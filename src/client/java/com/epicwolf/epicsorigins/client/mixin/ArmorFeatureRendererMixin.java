package com.epicwolf.epicsorigins.client.mixin;

import com.epicwolf.epicsorigins.power.AbstractLookPower;
import com.epicwolf.epicsorigins.util.ModelTypes;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {

    @Unique
    private boolean shouldRender;

    @Inject(method = "renderArmor", at = @At("HEAD"))
    private void setShouldRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        for (AbstractLookPower power : PowerHolderComponent.getPowers(entity, AbstractLookPower.class)) {
            shouldRender = !(power.isShouldRender() && entity.isSubmergedInWater() && power.getModelType().equals(ModelTypes.MERMAID_TAIL));
        }
    }

    @Inject(method = "setVisible", at = @At("TAIL"))
    private void renderArmor(A model, EquipmentSlot slot, CallbackInfo ci) {
        model.leftLeg.visible = shouldRender;
        model.rightLeg.visible = shouldRender;
        if (slot == EquipmentSlot.LEGS) model.body.visible = shouldRender;
    }
}
