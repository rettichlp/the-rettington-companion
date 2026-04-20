package de.rettichlp.therettingtoncompanion.common.registry;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public interface IHudRenderListener extends IListener {

    void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter);
}
