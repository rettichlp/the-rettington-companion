package de.rettichlp.therettingtoncompanion.mixin;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.therettingtoncompanion.TheRettingtonCompanion.configuration;
import static net.minecraft.client.render.entity.equipment.EquipmentModel.LayerType.WINGS;

@Mixin(EquipmentRenderer.class)
public abstract class EquipmentRendererMixin {

    @Inject(method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V",
            at = @At("HEAD"),
            cancellable = true)
    private <S> void trc$renderHead(EquipmentModel.LayerType layerType,
                                    RegistryKey<EquipmentAsset> assetKey,
                                    Model<? super S> model,
                                    S object,
                                    ItemStack itemStack,
                                    MatrixStack matrixStack,
                                    OrderedRenderCommandQueue orderedRenderCommandQueue,
                                    int i,
                                    Identifier identifier,
                                    int j,
                                    int k,
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
