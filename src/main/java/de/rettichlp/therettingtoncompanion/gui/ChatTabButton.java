package de.rettichlp.therettingtoncompanion.gui;

import de.rettichlp.therettingtoncompanion.configuration.ChatTab;
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
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.network.chat.TextColor.RED;
import static net.minecraft.util.ARGB.as8BitChannel;
import static net.minecraft.util.ARGB.color;

public class ChatTabButton extends Button.Plain {

    @Getter
    private final @Nullable ChatTab chatTab; // null for the "add tab" and "default tab" buttons
    private final boolean defaultTab; // represents the unfiltered main chat, distinct from the "add tab" button

    private ChatTabButton(@NonNull Font font, @Nullable ChatTab chatTab, boolean defaultTab, Button.OnPress onPress) {
        super(0, 0, computeWidth(font, chatTab, defaultTab), 14, buildLabel(chatTab, defaultTab), onPress, DEFAULT_NARRATION);
        this.chatTab = chatTab;
        this.defaultTab = defaultTab;
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
        setMessage(buildLabel(this.chatTab, this.defaultTab));
        setWidth(computeWidth(font, this.chatTab, this.defaultTab));
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

        boolean isSelected = this.defaultTab ? FOCUSED_CHAT_TAB == null : this.chatTab != null && this.chatTab == FOCUSED_CHAT_TAB;
        if (isSelected) {
            int color = as8BitChannel(options.textBackgroundOpacity().get().floatValue()) << 24 | configuration.visuals().getExperienceLevelColor() & 16777215;
            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), color);
        }

        boolean isFilterTriggered = !isSelected && this.chatTab != null && this.chatTab.isFilterTriggered();
        if (isFilterTriggered) {
            int color = color(as8BitChannel(options.textBackgroundOpacity().get().floatValue()), 255, 100, 100);
            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), color);
        }

        extractDefaultLabel(graphics.textRendererForWidget(this, NONE));
    }

    public static @NonNull ChatTabButton forTab(@NonNull Font font, @NonNull ChatTab chatTab, Button.OnPress onPress) {
        return new ChatTabButton(font, chatTab, false, onPress);
    }

    public static @NonNull ChatTabButton forDefaultTab(@NonNull Font font, Button.OnPress onPress) {
        return new ChatTabButton(font, null, true, onPress);
    }

    public static @NonNull ChatTabButton forAddButton(@NonNull Font font, Button.OnPress onPress) {
        return new ChatTabButton(font, null, false, onPress);
    }

    private static @NonNull Component buildLabel(@Nullable ChatTab chatTab, boolean defaultTab) {
        if (chatTab == null) {
            return defaultTab ? translatable("trc.chat_screen.chat_tabs.default_tab_name") : literal("+");
        }

        String name = chatTab.getName();
        MutableComponent label = empty().append(literal(name == null || name.isBlank() ? "?" : name));

        if (chatTab.getUnreadCount() > 0) {
            String badgeText = valueOf(min(99, chatTab.getUnreadCount()));
            label.append(literal(" " + badgeText).withColor(RED));
        }

        return label;
    }

    private static int computeWidth(@NonNull Font font, @Nullable ChatTab chatTab, boolean defaultTab) {
        if (chatTab == null && !defaultTab) {
            return 16;
        }

        return clamp(font.width(buildLabel(chatTab, defaultTab)) + 10, 20, 80);
    }
}
