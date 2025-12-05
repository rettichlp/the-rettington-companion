package de.rettichlp.therettingtoncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.LocalDateTime;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.common.utils.TextUtils.getHighlightColor;
import static de.rettichlp.therettingtoncompanion.common.utils.TextUtils.getString;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Math.max;
import static java.time.format.DateTimeFormatter.ofPattern;
import static net.minecraft.client.gui.hud.ChatHud.getHeight;
import static net.minecraft.client.gui.hud.ChatHud.getWidth;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.math.ColorHelper.withAlpha;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract boolean isChatFocused();

    /**
     * Modifies the {@code addMessage} method by intercepting at the HEAD position to prepend a timestamp to the beginning of the chat
     * message. The timestamp is formatted in "HH:mm:ss" and styled in dark gray.
     *
     * @param originalMessage the original {@link Text} object representing the message being added. This parameter will have its style
     *                        applied to the resulting modified {@link Text}.
     *
     * @return a new {@link Text} object that includes the formatted timestamp followed by the original message, maintaining the style
     *         of the original message.
     */
    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
                    at = @At("HEAD"),
                    argsOnly = true,
                    ordinal = 0)
    private Text trc$addMessageHead(@NotNull Text originalMessage) {
        if (!configuration.chat().isChatTime()) {
            return originalMessage;
        }

        LocalDateTime now = LocalDateTime.now();
        String timeString = now.format(ofPattern("HH:mm:ss "));
        String dateString = now.format(ofPattern("dd.MM.yyyy"));

        return empty().setStyle(originalMessage.getStyle())
                .append(literal(timeString).styled(style -> style
                        .withFormatting(DARK_GRAY)
                        .withHoverEvent(new HoverEvent.ShowText(literal(dateString)))))
                .append(originalMessage);
    }

    /**
     * Redirects the invocation of the {@code fill} method in the {@link DrawContext} class during the execution of the
     * {@code method_71992} in the {@link ChatHud} class. This method adjusts the background color for chat lines by applying custom
     * highlight colors when applicable.
     *
     * @param drawContext the {@link DrawContext} used for rendering, provided by the redirected invocation.
     * @param x1          the x-coordinate of the first corner of the rectangle to fill.
     * @param y1          the y-coordinate of the first corner of the rectangle to fill.
     * @param x2          the x-coordinate of the opposite corner of the rectangle to fill.
     * @param y2          the y-coordinate of the opposite corner of the rectangle to fill.
     * @param color       the original color to use for the background of the rectangle.
     * @param line        the {@link ChatHudLine.Visible} instance containing the content of the current chat line being rendered.
     */
    @Redirect(method = "method_71992",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 0))
    public void trc$method_71992Invoke(DrawContext drawContext,
                                       int x1,
                                       int y1,
                                       int x2,
                                       int y2,
                                       int color,
                                       @Local(argsOnly = true) ChatHudLine.Visible line) {
        int backgroundColor = color;

        Formatting highlightColor = getHighlightColor(getString(line.content()));
        if (highlightColor != null && highlightColor.getColorValue() != null) {
            backgroundColor = withAlpha(100, 0xFF000000 | highlightColor.getColorValue());
        }

        drawContext.fill(x1, y1, x2, y2, backgroundColor);
    }

    /**
     * Intercepts the {@code clear} method at the HEAD position to determine whether the chat history should be cleared based on the
     * current configuration. If the configuration specifies that messages should be kept on disconnect and {@code clearHistory} is
     * true, the method execution is canceled.
     *
     * @param clearHistory specifies whether the chat history should be cleared. If true, the operation was triggered by changing
     *                     worlds or disconnecting. If false, it was manually triggered by the player.
     * @param ci           a {@link CallbackInfo} instance that allows canceling the method execution if specific conditions are met.
     */
    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    public void trc$clearHead(boolean clearHistory, CallbackInfo ci) {
        if (configuration.chat().isKeepMessagesOnDisconnect() && clearHistory) {
            ci.cancel();
        }
    }

    /**
     * Modifies the integer constant value used in the {@code addMessage} and {@code addVisibleMessage} methods to allow for dynamic
     * adjustments based on the current chat configuration. If the configuration permits more messages to be displayed, a maximum value
     * is returned; otherwise, the default value of 100 is used.
     *
     * @param value the original integer constant value passed to the method. This is typically 100.
     *
     * @return the modified integer value based on the chat configuration. Returns a maximum value if the configuration allows more
     *         messages, otherwise returns 100.
     */
    @ModifyExpressionValue(method = { "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", "addVisibleMessage" },
                           at = @At(value = "CONSTANT", args = "intValue=100"))
    public int trc$addMessageConstant(int value) {
        return configuration.chat().isMoreMessages() ? MAX_VALUE : 100;
    }

    /**
     * Modifies the return value of the {@code getWidth()} method to dynamically adjust the width based on specific conditions. The
     * width is optimized if the chat size optimization is enabled; otherwise, the original width is returned.
     *
     * @param width the original width value returned by the {@code getWidth()} method.
     *
     * @return the modified width value if chat size optimization is enabled, otherwise the original width.
     */
    @ModifyReturnValue(method = "getWidth()I", at = @At("RETURN"))
    private int trc$getWidthReturn(int width) {
        if (!configuration.chat().isOptimizedChatSize()) {
            return width;
        }

        // from x = 0 to hotbar (length = 182)
        int chatWidth = this.client.getWindow().getScaledWidth() / 2 - 91 - 12; // for some reason there is a 12px offset
        double minecraftChatWidth = getWidth(this.client.options.getChatWidth().getValue());

        return (int) max(chatWidth, minecraftChatWidth);
    }

    /**
     * Modifies the return value of the {@code getHeight()} method to dynamically adjust the height based on specific conditions. The
     * height is optimized if the chat size optimization is enabled; otherwise, the original height is returned.
     *
     * @param height the original height value returned by the {@code getHeight()} method.
     *
     * @return the modified height value if chat size optimization is enabled, otherwise the original height.
     */
    @ModifyReturnValue(method = "getHeight()I", at = @At("RETURN"))
    private int trc$getHeightReturn(int height) {
        if (!configuration.chat().isOptimizedChatSize()) {
            return height;
        }

        // half of the screen height
        int chatHeight = this.client.getWindow().getScaledHeight() / 2;
        double minecraftChatHeight = getHeight(this.client.options.getChatHeightFocused().getValue());

        return isChatFocused() ? ((int) max(chatHeight, minecraftChatHeight)) : height;
    }
}
