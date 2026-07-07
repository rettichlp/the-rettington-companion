package de.rettichlp.therettingtoncompanion.gui.options.tabs;

import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.gui.screens.TRCOptionsScreen;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;

@Getter
@RequiredArgsConstructor
public abstract class AbstractTRCOptionsTab {

    protected final Minecraft minecraft = Minecraft.getInstance();

    private final String id;

    public abstract Component title();

    public abstract void populateOptionsList(@NonNull TRCOptionsList optionsList);

    public Button getTabButton(Screen lastScreen) {
        Button button = Button.builder(title(), _ -> {
            this.minecraft.setScreen(new TRCOptionsScreen(this.id, lastScreen, true));
            configuration.saveToFile();
        }).size(100, 20).build();

        boolean isActive = this.minecraft.screen instanceof TRCOptionsScreen trcOptionsScreen && trcOptionsScreen.getSelectedTabId().equals(this.id);
        button.setFocused(isActive);

        return button;
    }

    public TRCOptionsList getOptionsList(TRCOptionsScreen screen) {
        TRCOptionsList optionsList = new TRCOptionsList(this.minecraft, screen);
        populateOptionsList(optionsList);
        return optionsList;
    }
}
