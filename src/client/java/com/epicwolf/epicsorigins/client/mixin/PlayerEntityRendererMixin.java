package com.epicwolf.epicsorigins.client.mixin;

import com.epicwolf.epicsorigins.power.AbstractLookPower;
import com.epicwolf.epicsorigins.util.ModelTypes;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin extends LivingEntityRenderer {
    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, EntityModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "setModelPose", at=@At("TAIL"))
    private void renderLegs(AbstractClientPlayerEntity player, CallbackInfo ci) {
        PlayerEntityModel model = (PlayerEntityModel) this.getModel();
        for (AbstractLookPower power : PowerHolderComponent.getPowers(player, AbstractLookPower.class)) {
            if (power.isShouldRender() && player.isSubmergedInWater()) setVisibleLegs(!power.getModelType().equals(ModelTypes.MERMAID_TAIL), model);
        }
    }

    @Unique
    @Override
    public Identifier getTexture(Entity entity) {
        return null;
    }

    @Unique
    public void setVisibleLegs(boolean visible, PlayerEntityModel model) {
        model.leftLeg.visible = visible;
        model.rightLeg.visible = visible;
        model.leftPants.visible = visible;
        model.rightPants.visible = visible;
    }
}
