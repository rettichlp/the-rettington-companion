package de.rettichlp.therettingtoncompanion.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static net.minecraft.client.resources.model.EquipmentClientInfo.LayerType.WINGS;

@Mixin(EquipmentLayerRenderer.class)
public abstract class EquipmentLayerRendererMixin {

    @Inject(method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;II)V",
            at = @At("HEAD"),
            cancellable = true)
    private <S> void trc$renderLayersHead(EquipmentClientInfo.LayerType layerType,
                                          ResourceKey<EquipmentAsset> equipmentAssetId,
                                          Model<? super S> model,
                                          S state,
                                          ItemStack itemStack,
                                          PoseStack poseStack,
                                          SubmitNodeCollector submitNodeCollector,
                                          int lightCoords,
                                          int outlineColor,
                                          CallbackInfo ci) {
        switch (configuration.visuals().getEquipmentModelVisibility()) {
            case ALL -> {
            } // do nothing, render all equipment
            case NONE -> ci.cancel(); // cancel rendering of all equipment
            case ONLY_WINGS -> {
                // cancel rendering of non-wing equipment
                if (layerType != WINGS) {
                    ci.cancel();
                }
            }
        }
    }
}
