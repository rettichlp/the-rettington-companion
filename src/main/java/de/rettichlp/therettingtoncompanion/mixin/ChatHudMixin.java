package de.rettichlp.therettingtoncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.rettichlp.therettingtoncompanion.models.ChatRegex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
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

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.player;
import static de.rettichlp.therettingtoncompanion.utils.TextUtils.getHighestPriorityMatchingChatRegex;
import static java.lang.Math.max;
import static java.time.format.DateTimeFormatter.ofPattern;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.client.gui.components.ChatComponent.getHeight;
import static net.minecraft.client.gui.components.ChatComponent.getWidth;
import static net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;

@Mixin(ChatComponent.class)
public abstract class ChatHudMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    public abstract boolean isChatFocused();

    @Inject(method = "clearMessages", at = @At("HEAD"), cancellable = true)
    public void trc$clearHead(boolean history, CallbackInfo ci) {
        if (configuration.chat().isKeepMessagesOnDisconnect() && history) {
            ci.cancel();
        }
    }

    @Inject(method = "addMessageToDisplayQueue",
            at = @At(value = "INVOKE", target = "Ljava/util/List;removeLast()Ljava/lang/Object;"),
            cancellable = true)
    public void trc$addMessageToDisplayQueueInvoke(GuiMessage message, CallbackInfo ci) {
        ChatRegex highestPriorityMatchingChatRegex = getHighestPriorityMatchingChatRegex(message.content().getString());
        if (highestPriorityMatchingChatRegex != null) {
            Identifier chatRegexSoundIdentifier = highestPriorityMatchingChatRegex.getSoundIdentifier();
            SoundEvent soundEvent = SOUND_EVENT.getValue(chatRegexSoundIdentifier);
            player.playSound(soundEvent, 1, 1.5f);
        }

        if (configuration.chat().isMoreMessages()) {
            ci.cancel();
        }
    }

    @Inject(method = "addMessageToQueue",
            at = @At(value = "INVOKE", target = "Ljava/util/List;removeLast()Ljava/lang/Object;"),
            cancellable = true)
    public void trc$addMessageToQueueInvoke(GuiMessage message, CallbackInfo ci) {
        ChatRegex highestPriorityMatchingChatRegex = getHighestPriorityMatchingChatRegex(message.content().getString());
        if (highestPriorityMatchingChatRegex != null) {
            Identifier chatRegexSoundIdentifier = highestPriorityMatchingChatRegex.getSoundIdentifier();
            SoundEvent soundEvent = SOUND_EVENT.getValue(chatRegexSoundIdentifier);
            player.playSound(soundEvent, 1, 1.5f);
        }

        if (configuration.chat().isMoreMessages()) {
            ci.cancel();
        }
    }

    @Inject(method = "addRecentChat",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ArrayListDeque;removeFirst()Ljava/lang/Object;"),
            cancellable = true)
    public void trc$addRecentChatInvoke(String message, CallbackInfo ci) {
        ChatRegex highestPriorityMatchingChatRegex = getHighestPriorityMatchingChatRegex(message);
        if (highestPriorityMatchingChatRegex != null) {
            Identifier chatRegexSoundIdentifier = highestPriorityMatchingChatRegex.getSoundIdentifier();
            SoundEvent soundEvent = SOUND_EVENT.getValue(chatRegexSoundIdentifier);
            player.playSound(soundEvent, 1, 1.5f);
        }

        if (configuration.chat().isMoreMessages()) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "addMessage",
                    at = @At("HEAD"),
                    argsOnly = true,
                    name = "contents")
    private Component trc$addMessageHead(Component contents) {
        if (!configuration.chat().isChatTime()) {
            return contents;
        }

        LocalDateTime now = LocalDateTime.now();
        String timeString = now.format(ofPattern("HH:mm:ss "));
        String dateString = now.format(ofPattern("dd.MM.yyyy"));

        return empty()
                .append(literal(timeString).withStyle(style -> style
                        .applyFormat(DARK_GRAY)
                        .withHoverEvent(new HoverEvent.ShowText(literal(dateString)))))
                .append(contents);
    }

    @ModifyReturnValue(method = "getWidth()I", at = @At("RETURN"))
    private int trc$getWidthReturn(int width) {
        if (!configuration.chat().isOptimizedChatSize()) {
            return width;
        }

        // from x = 0 to hotbar (length = 182)
        int chatWidth = this.minecraft.getWindow().getGuiScaledWidth() / 2 - 91 - 12; // for some reason there is a 12px offset
        double minecraftChatWidth = getWidth(this.minecraft.options.chatWidth().get());

        return (int) max(chatWidth, minecraftChatWidth);
    }

    @ModifyReturnValue(method = "getHeight()I", at = @At("RETURN"))
    private int trc$getHeightReturn(int height) {
        if (!configuration.chat().isOptimizedChatSize()) {
            return height;
        }

        // half of the screen height
        int chatHeight = this.minecraft.getWindow().getGuiScaledHeight() / 2;
        double minecraftChatHeight = getHeight(this.minecraft.options.chatHeightFocused().get());

        return isChatFocused() ? ((int) max(chatHeight, minecraftChatHeight)) : height;
    }

    @ModifyArgs(method = "lambda$extractRenderState$1(IILnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IFLnet/minecraft/client/multiplayer/chat/GuiMessage$Line;IF)V",
                at = @At(value = "INVOKE",
                         target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;fill(IIIII)V"))
    private static void trc$method_75802Invoke(Args args, @Local(argsOnly = true, name = "arg5") GuiMessage.Line arg5) {
        int originalColor = args.get(4);

        // extract alpha value
        int alpha = (originalColor >> 24) & 0xFF;

        ChatRegex highestPriorityMatchingChatRegex = getHighestPriorityMatchingChatRegex(arg5.parent().content().getString());

        if (highestPriorityMatchingChatRegex == null) {
            return;
        }

        Color chatRegexColor = highestPriorityMatchingChatRegex.getColor();
        int highlightColorWithAlpha = (alpha << 24) | (chatRegexColor.getRed() << 16) | (chatRegexColor.getGreen() << 8) | chatRegexColor.getBlue();
        args.set(4, highlightColorWithAlpha);
    }
}
