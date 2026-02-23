package de.rettichlp.therettingtoncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.rettichlp.therettingtoncompanion.common.models.ChatRegex;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static de.rettichlp.therettingtoncompanion.common.utils.TextUtils.getHighestPriorityMatchingChatRegex;
import static de.rettichlp.therettingtoncompanion.common.utils.TextUtils.getString;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Math.max;
import static java.time.format.DateTimeFormatter.ofPattern;
import static net.minecraft.client.gui.hud.ChatHud.getHeight;
import static net.minecraft.client.gui.hud.ChatHud.getWidth;
import static net.minecraft.registry.Registries.SOUND_EVENT;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.math.ColorHelper.withAlpha;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @Unique
    private static final Collection<ChatHudLine.Visible> NOTIFIED_LINES = new ArrayList<>();

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract boolean isChatFocused();

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

    /**
     * Redirects the call to the original {@code fill} method in {@link ChatHud.Backend}, modifying the background color based on the
     * highlight information from the provided chat line. If a highlight color is determined for the message, it is applied with a
     * specific transparency; otherwise, the original color is used.
     *
     * @param instance the {@link ChatHud.Backend} instance on which the {@code fill} method is called.
     * @param x1       the x-coordinate of the top-left corner of the rectangle.
     * @param y1       the y-coordinate of the top-left corner of the rectangle.
     * @param x2       the x-coordinate of the bottom-right corner of the rectangle.
     * @param y2       the y-coordinate of the bottom-right corner of the rectangle.
     * @param color    the original background color passed to the {@code fill} method.
     * @param line     the visible chat line being rendered, from which the message content is extracted to determine the background
     *                 color.
     */
    @Redirect(method = "method_75802",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud$Backend;fill(IIIII)V", ordinal = 0))
    private static void trc$method_75802Invoke(ChatHud.Backend instance,
                                               int x1,
                                               int y1,
                                               int x2,
                                               int y2,
                                               int color,
                                               @Local(argsOnly = true) ChatHudLine.Visible line) {
        int backgroundColor = color;

        ChatRegex highestPriorityMatchingChatRegex = getHighestPriorityMatchingChatRegex(getString(line.content()));

        if (highestPriorityMatchingChatRegex != null) {
            // colour for chat line highlight
            Formatting chatRegexColor = highestPriorityMatchingChatRegex.getColor();
            assert chatRegexColor.getColorValue() != null;
            backgroundColor = withAlpha(100, 0xFF000000 | chatRegexColor.getColorValue());

            // play sound
            if (!NOTIFIED_LINES.contains(line)) {
                NOTIFIED_LINES.add(line);
                Identifier chatRegexSoundIdentifier = highestPriorityMatchingChatRegex.getSoundIdentifier();
                SoundEvent soundEvent = SOUND_EVENT.get(chatRegexSoundIdentifier);
                player.playSound(soundEvent, 1, 1.5f);
            }
        }

        instance.fill(x1, y1, x2, y2, backgroundColor);
    }
}
