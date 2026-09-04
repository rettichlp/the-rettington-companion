package de.rettichlp.therettingtoncompanion.gui;

import de.rettichlp.therettingtoncompanion.chat.AbstractChatTab;
import de.rettichlp.therettingtoncompanion.chat.AddChatTab;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.FOCUSED_CHAT_TAB;
import static java.lang.Integer.MIN_VALUE;
import static java.lang.Math.clamp;
import static java.lang.Math.min;
import static java.lang.String.valueOf;
import static net.minecraft.client.gui.GuiGraphicsExtractor.HoveredTextEffects.NONE;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.TextColor.RED;
import static net.minecraft.util.ARGB.as8BitChannel;
import static net.minecraft.util.ARGB.color;

public class ChatTabButton extends Button.Plain {

    @Getter
    private final @NonNull AbstractChatTab chatTab;

    private ChatTabButton(@NonNull Font font, @NonNull AbstractChatTab chatTab, Button.OnPress onPress) {
        super(0, 0, computeWidth(font, chatTab), 14, buildLabel(chatTab), onPress, DEFAULT_NARRATION);
        this.chatTab = chatTab;
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(@NonNull FocusNavigationEvent navigationEvent) {
        if (navigationEvent instanceof FocusNavigationEvent.ArrowNavigation) {
            return null;
        }

        return super.nextFocusPath(navigationEvent);
    }

    /**
     * Re-applies the label (name and unread badge) and resizes the button accordingly, since the unread count can change while the
     * button already exists. Call this before laying out the tab button row.
     */
    public void refresh(@NonNull Font font) {
        setMessage(buildLabel(this.chatTab));
        setWidth(computeWidth(font, this.chatTab));
    }

    /**
     * Buttons take focus after being clicked by default, which would steal keyboard focus away from the chat input box and make typing
     * appear to do nothing. Tab buttons are toggles, not text inputs, so they never need to hold focus.
     */
    @Override
    public boolean shouldTakeFocusAfterInteraction() {
        return false;
    }

    /**
     * Renders this button outside its normal Screen widget lifecycle, e.g. for the non-interactive tab row shown in the passive chat
     * preview.
     */
    public void draw(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractContents(graphics, mouseX, mouseY, a);
    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        Options options = Minecraft.getInstance().options;

        // semi-transparent panel styled like the chat box itself, deliberately not the vanilla button sprite
        graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), options.getBackgroundColor(MIN_VALUE));

        boolean isSelected = this.chatTab == FOCUSED_CHAT_TAB;
        if (isSelected) {
            int color = as8BitChannel(options.textBackgroundOpacity().get().floatValue()) << 24 | configuration.visuals().getExperienceLevelColor() & 16777215;
            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), color);
        }

        boolean isFilterTriggered = !isSelected && this.chatTab.isFilterTriggered();
        if (isFilterTriggered) {
            int color = color(as8BitChannel(options.textBackgroundOpacity().get().floatValue()), 255, 100, 100);
            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), color);
        }

        extractDefaultLabel(graphics.textRendererForWidget(this, NONE));
    }

    public static @NonNull ChatTabButton forTab(@NonNull Font font, @NonNull AbstractChatTab chatTab, Button.OnPress onPress) {
        return new ChatTabButton(font, chatTab, onPress);
    }

    private static @NonNull Component buildLabel(@NonNull AbstractChatTab chatTab) {
        MutableComponent label = empty().append(chatTab.getDisplayName());

        if (chatTab.getUnreadCount() > 0) {
            String badgeText = valueOf(min(99, chatTab.getUnreadCount()));
            label.append(literal(" " + badgeText).withColor(RED));
        }

        return label;
    }

    private static int computeWidth(@NonNull Font font, @NonNull AbstractChatTab chatTab) {
        if (chatTab instanceof AddChatTab) {
            return 16;
        }

        return clamp(font.width(buildLabel(chatTab)) + 10, 20, 80);
    }
}
