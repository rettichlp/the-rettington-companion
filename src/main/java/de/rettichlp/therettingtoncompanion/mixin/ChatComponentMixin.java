package de.rettichlp.therettingtoncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.rettichlp.therettingtoncompanion.gui.options.list.FilteredMessageEntry;
import de.rettichlp.therettingtoncompanion.gui.options.list.HiddenMessageEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.Optional;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.LOGGER;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.gui.options.list.FilteredMessageEntry.FilteredMessage.getBestMatchingFilteredMessage;
import static de.rettichlp.therettingtoncompanion.gui.options.list.HiddenMessageEntry.HiddenMessage.shouldBeHidden;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.getChatBottomHeight;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.getChatHeight;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.getChatWidth;
import static java.lang.Integer.MAX_VALUE;
import static java.time.format.DateTimeFormatter.ofPattern;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.TextColor.DARK_GRAY;
import static net.minecraft.sounds.SoundEvent.createVariableRangeEvent;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "clearMessages", at = @At("HEAD"), cancellable = true)
    public void trc$clearHead(boolean history, CallbackInfo ci) {
        if (configuration.chat().isKeepMessagesOnDisconnect() && history) {
            ci.cancel();
        }
    }

    @Inject(method = "addMessage", at = @At("HEAD"), cancellable = true)
    private void trc$addMessageHead(@NonNull Component contents,
                                    MessageSignature signature,
                                    GuiMessageSource source,
                                    GuiMessageTag tag,
                                    CallbackInfo ci) {
        Optional<HiddenMessageEntry.HiddenMessage> shouldBeHidden = shouldBeHidden(contents.getString());
        shouldBeHidden.ifPresent(hiddenMessage -> {
            ci.cancel();
            LOGGER.info("Hidden following message (commissioned by {}): {} ", hiddenMessage.getProviderModId(), contents.getString());
        });

        FilteredMessageEntry.FilteredMessage bestMatchingFilteredMessage = getBestMatchingFilteredMessage(contents.getString());
        if (bestMatchingFilteredMessage != null && this.minecraft.player != null) {
            Identifier chatRegexSoundIdentifier = bestMatchingFilteredMessage.getSoundIdentifier();
            this.minecraft.player.playSound(createVariableRangeEvent(chatRegexSoundIdentifier), 1.0f, 1.5f);
        }
    }

    @ModifyVariable(method = "addMessage", at = @At("HEAD"), argsOnly = true, name = "contents")
    private @NonNull Component trc$addMessageHead(@NonNull Component contents) {
        if (!configuration.chat().isChatTime()) {
            return contents;
        }

        LocalDateTime now = LocalDateTime.now();
        String timeString = now.format(ofPattern("HH:mm:ss "));
        String dateString = now.format(ofPattern("dd.MM.yyyy"));

        return empty()
                .append(literal(timeString).withStyle(style -> style
                        .withColor(DARK_GRAY)
                        .withHoverEvent(new HoverEvent.ShowText(literal(dateString)))))
                .append(contents);
    }

    @ModifyExpressionValue(method = { "addMessageToDisplayQueue", "addMessageToQueue", "addRecentChat" },
                           at = @At(value = "CONSTANT", args = "intValue=100"))
    private int moreMessages(int hundred) {
        return configuration.chat().isMoreMessages() ? MAX_VALUE : 100;
    }

    @ModifyReturnValue(method = "getWidth()I", at = @At("RETURN"))
    private int trc$getWidthReturn(int width) {
        return getChatWidth() - 12; // I don't know why, but 12px offset'
    }

    @ModifyReturnValue(method = "getHeight()I", at = @At("RETURN"))
    private int trc$getHeightReturn(int height) {
        return getChatHeight();
    }

    @ModifyExpressionValue(method = "extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V",
                           at = @At(value = "INVOKE",
                                    target = "Lnet/minecraft/util/Mth;floor(F)I"))
    private int trc$extractRenderStateExpressionValue(int original) {
        return getChatBottomHeight();
    }

    @ModifyArgs(method = "lambda$extractRenderState$1(IILnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IFLnet/minecraft/client/multiplayer/chat/GuiMessage$Line;IF)V",
                at = @At(value = "INVOKE",
                         target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;fill(IIIII)V"))
    private static void trc$method_75802Invoke(@NonNull Args args,
                                               @Local(argsOnly = true, name = "arg5") GuiMessage.@NonNull Line arg5) {
        // check for filtered message
        FilteredMessageEntry.FilteredMessage bestMatchingFilteredMessage = getBestMatchingFilteredMessage(arg5.parent().content().getString());
        if (bestMatchingFilteredMessage != null) {
            int originalColor = args.get(4);

            // extract alpha value
            int alpha = (originalColor >> 24) & 0xFF;

            Color chatRegexColor = bestMatchingFilteredMessage.getColor();
            int highlightColorWithAlpha = (alpha << 24) | (chatRegexColor.getRed() << 16) | (chatRegexColor.getGreen() << 8) | chatRegexColor.getBlue();
            args.set(4, highlightColorWithAlpha);
        }
    }
}
