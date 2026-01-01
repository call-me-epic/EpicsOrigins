package com.epicwolf.epicsorigins.client.mixin;

import com.epicwolf.epicsorigins.power.EntityTransformationPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {


    @Unique
    private EntityRenderer<?> entityRenderer = null;
    @Unique
    private Entity entity = null;
    
    @Inject(method = "render*", at = @At("HEAD"), cancellable = true)
    private void render(T livingEntity, float f, float g, MatrixStack matrixStack,
                        VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (livingEntity instanceof PlayerEntity player) {
            List<EntityTransformationPower> powers = PowerHolderComponent.getPowers(player, EntityTransformationPower.class);
            if (!powers.isEmpty()) {
                EntityTransformationPower power = powers.get(0);
                if (power.isTransformed()) {
                    ci.cancel();
                    renderEntityModel(
                            player, power, matrixStack,
                            vertexConsumerProvider, i, f, g
                    );
                }
                else {
                    entityRenderer = null;
                    entity = null;
                }
            }
        }
    }

    @Unique
    private void renderEntityModel(PlayerEntity player, EntityTransformationPower power,
                                   MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                   int light, float f, float g) {

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.getEntityRenderDispatcher() == null) return;

        setEntity(EntityType.get(power.getEntityType().toString()).get(), client);

        if (entity instanceof LivingEntity livingEntity && entityRenderer instanceof LivingEntityRenderer<?, ?> livingRenderer) {
            syncEntity(player, livingEntity, f, g);
            renderLivingModel(livingRenderer, livingEntity, matrices, vertexConsumers, light, f, g);
        }
    }

    @Unique
    private <T extends LivingEntity, M extends EntityModel<T>> void renderLivingModel(
            LivingEntityRenderer<T, M> renderer, LivingEntity livingEntity,
            MatrixStack matrixStack, VertexConsumerProvider vertexConsumers,
            int light, float f, float g) {
        T castedEntity = (T) livingEntity;
        matrixStack.push();
        renderer.render(castedEntity, f, g, matrixStack, vertexConsumers, light);
        matrixStack.pop();
    }
    @Unique
    private void syncEntity(PlayerEntity player, LivingEntity entity, float f, float g) {
        entity.bodyYaw = player.bodyYaw;
        entity.headYaw = player.headYaw;
        entity.setYaw(player.getYaw());
        entity.setPitch(player.getPitch());

        entity.prevBodyYaw = player.prevBodyYaw;
        entity.prevHeadYaw = player.prevHeadYaw;
        entity.prevYaw = player.prevYaw;
        entity.prevPitch = player.prevPitch;

        entity.limbAnimator.setSpeed(player.limbAnimator.getSpeed(g)/2);
        entity.updateLimbs(false);

        entity.handSwinging = player.handSwinging;
        entity.handSwingTicks = player.handSwingTicks;
        entity.lastHandSwingProgress = player.lastHandSwingProgress;
        entity.handSwingProgress = player.handSwingProgress;

        entity.setPose(player.getPose());
        entity.setSneaking(player.isSneaking());

        entity.setOnFire(player.isOnFire());
        entity.hurtTime = player.hurtTime;
        entity.setStackInHand(Hand.MAIN_HAND, player.getMainHandStack());
        entity.setStackInHand(Hand.OFF_HAND, player.getOffHandStack());
    }

    @Unique
    private void setEntity(EntityType<?> entityType, MinecraftClient client) {
        try {
            if (entity == null) {
                entity = entityType.create(client.world);
                entityRenderer = client.getEntityRenderDispatcher().getRenderer(entity);
            }
        } catch (Exception ignored) {}
    }
}