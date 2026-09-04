package de.rettichlp.therettingtoncompanion.gui;

import de.rettichlp.therettingtoncompanion.chat.AbstractChatTab;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.FOCUSED_CHAT_TAB;
import static java.lang.Integer.MIN_VALUE;
import static net.minecraft.client.gui.GuiGraphicsExtractor.HoveredTextEffects.NONE;
import static net.minecraft.util.ARGB.as8BitChannel;
import static net.minecraft.util.ARGB.color;

public class ChatTabButton extends Button.Plain {

    @Getter
    private final @NonNull AbstractChatTab chatTab;

    public ChatTabButton(@NonNull Font font, @NonNull AbstractChatTab chatTab, Button.OnPress onPress) {
        super(0, 0, chatTab.getButtonWidth(font), 14, chatTab.getButtonLabel(), onPress, DEFAULT_NARRATION);
        this.chatTab = chatTab;
    }

    @Override
    public void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
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

    @Override
    public @Nullable ComponentPath nextFocusPath(@NonNull FocusNavigationEvent navigationEvent) {
        if (navigationEvent instanceof FocusNavigationEvent.ArrowNavigation) {
            return null;
        }

        return super.nextFocusPath(navigationEvent);
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
     * Re-applies the label (name and unread badge) and resizes the button accordingly, since the unread count can change while the
     * button already exists. Call this before laying out the tab button row.
     */
    public void refresh(@NonNull Font font) {
        setMessage(this.chatTab.getButtonLabel());
        setWidth(this.chatTab.getButtonWidth(font));
    }
}
