package de.rettichlp.therettingtoncompanion.gui.options.tabs;

import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.gui.options.TRCOptionsScreen;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

@Getter
@RequiredArgsConstructor
public abstract class AbstractTRCOptionsTab {

    protected final Minecraft minecraft = Minecraft.getInstance();

    private final String id;

    public abstract Component title();

    public abstract void populateOptionsList(@NonNull TRCOptionsList optionsList);

    public Button getTabButton(Screen lastScreen) {
        TRCOptionsScreen trcOptionsScreen = new TRCOptionsScreen(this.id, lastScreen, true);
        return Button.builder(title(), _ -> this.minecraft.setScreen(trcOptionsScreen))
                .size(100, 20)
                .build();
    }

    public TRCOptionsList getOptionsList(TRCOptionsScreen screen) {
        TRCOptionsList optionsList = new TRCOptionsList(this.minecraft, screen);
        populateOptionsList(optionsList);
        return optionsList;
    }
}
