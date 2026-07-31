package de.rettichlp.therettingtoncompanion.gui;

import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

import static de.rettichlp.therettingtoncompanion.utils.ChatUtils.isValidPattern;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.lang.String.valueOf;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;

public class PatternEditBox extends EditBox {

    private final Consumer<String> onChange;

    public PatternEditBox(Font font, @Nullable String patternString, Consumer<String> onChange) {
        super(font, empty());
        this.onChange = onChange;
        setMaxLength(256);
        setValue(valueOf(patternString));
        setHint(literal(valueOf(patternString)));
        setResponder(_ -> {});
        moveCursorToStart(false);
    }

    // prevent overriding the responder
    @Override
    public void setResponder(@NonNull Consumer<String> responder) {
        super.setResponder(value -> {
            if (isValidPattern(value)) {
                setTextColor(WHITE.getRGB());
                this.onChange.accept(value);
            } else {
                setTextColor(RED.getRGB());
            }
        });
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(@NonNull FocusNavigationEvent navigationEvent) {
        if (navigationEvent instanceof FocusNavigationEvent.ArrowNavigation) {
            return null;
        }

        return super.nextFocusPath(navigationEvent);
    }
}
