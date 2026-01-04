package com.epicwolf.epicsorigins.mixin;

import com.epicwolf.epicsorigins.power.EntityTransformationPower;
import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import io.github.apace100.apoli.access.MovingEntity;
import io.github.apace100.apoli.access.SubmergableEntity;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity implements MovingEntity, SubmergableEntity, ComponentProvider {

    @Unique
    private boolean isInitialized;

    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    public void TransformedDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (PowerHolderComponent.hasPower(this, EntityTransformationPower.class)) {
            EntityTransformationPower power = PowerHolderComponent.getPowers(this, EntityTransformationPower.class).get(0);
            if (power.isTransformed()) {
                EntityDimensions dimensions = EntityType.get(power.getEntityType().toString()).get().getDimensions();
                if (Objects.requireNonNull(pose) == EntityPose.CROUCHING) {
                    cir.setReturnValue(new EntityDimensions(dimensions.width, (float) (dimensions.height * 0.8), false));
                } else {
                    cir.setReturnValue(dimensions);
                }
            }
        }
    }

    @Inject(method = "getActiveEyeHeight", at = @At("HEAD"), cancellable = true)
    public void TransformedEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        if (isInitialized) {
            if (PowerHolderComponent.hasPower(this, EntityTransformationPower.class)) {
                EntityTransformationPower power = PowerHolderComponent.getPowers(this, EntityTransformationPower.class).get(0);
                if (power.isTransformed()) {
                    float height = EntityType.get(power.getEntityType().toString()).get().getDimensions().height;
                    if (power.isTransformed()) {
                        switch (pose) {
                            case SWIMMING:
                            case FALL_FLYING:
                            case SPIN_ATTACK:
                                cir.setReturnValue(height * 0.4F);
                                return;
                            case CROUCHING:
                                cir.setReturnValue(height * 0.65F);
                                return;
                            default:
                                cir.setReturnValue(height * 0.85F);
                        }
                    }
                }
            }
        }
        else {
            isInitialized = true;
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (PowerHolderComponent.hasPower(this, EntityTransformationPower.class)) {
            EntityTransformationPower power = PowerHolderComponent.getPowers(this, EntityTransformationPower.class).get(0);
            if (power.isTransformed() != power.wasTransformed()) {
                power.setWasTransformed(power.isTransformed());
                this.calculateDimensions();
            }
        }
    }
}