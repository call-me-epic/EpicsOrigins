package com.epicwolf.epicsorigins.mixin;

import com.epicwolf.epicsorigins.power.EntityTransformationPower;
import com.epicwolf.epicsorigins.power.SizePower;
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

import java.util.List;
import java.util.Objects;

@Mixin(value = PlayerEntity.class, priority = 900)
public abstract class PlayerEntityMixin extends Entity implements MovingEntity, SubmergableEntity, ComponentProvider {

    @Unique
    private boolean isChangedDimensions;

    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    public void TransformedDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (PowerHolderComponent.hasPower(this, EntityTransformationPower.class)) {
            EntityTransformationPower power = PowerHolderComponent.getPowers(this, EntityTransformationPower.class).get(0);
            if (power.isTransformed()) {
                EntityDimensions dimensions = EntityType.get(power.getEntityType().toString()).get().getDimensions();
                if (List.of(EntityPose.FALL_FLYING, EntityPose.SWIMMING, EntityPose.SPIN_ATTACK).contains(Objects.requireNonNull(pose)) && dimensions.height > dimensions.width) {
                    cir.setReturnValue(new EntityDimensions(dimensions.width, dimensions.width, dimensions.fixed));
                }
                if (Objects.requireNonNull(pose) == EntityPose.CROUCHING) {
                    cir.setReturnValue(new EntityDimensions(dimensions.width, (float) (dimensions.height * 0.83), dimensions.fixed));
                } else {
                    cir.setReturnValue(dimensions);
                }
            }
        }
    }
    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void modifyDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (PowerHolderComponent.hasPower(this, SizePower.class)) {
            EntityDimensions dimensions = cir.getReturnValue();
            SizePower power = PowerHolderComponent.getPowers(this, SizePower.class).get(0);
            float width = power.getWidth() <= 0 ? dimensions.width : power.getWidth();
            float height = power.getHeight() <= 0 ? dimensions.height : power.getHeight();
            float size = power.getSize();
            cir.setReturnValue(new EntityDimensions(width*size, height*size, dimensions.fixed));
        }
    }

    @Inject(method = "getActiveEyeHeight", at = @At("HEAD"), cancellable = true)
    public void TransformedEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        try {
            if (PowerHolderComponent.hasPower(this, EntityTransformationPower.class)) {
                EntityTransformationPower power = PowerHolderComponent.getPowers(this, EntityTransformationPower.class).get(0);
                if (power.isTransformed()) {
                    float height = dimensions.height;
                    if (power.isTransformed()) {
                        cir.setReturnValue(applyEyeHeight(pose, height));
                    }
                }
            }
        } catch (Exception ignored) {}
    }
    @Inject(method = "getActiveEyeHeight", at = @At("RETURN"), cancellable = true)
    private void modifyEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        try {
            if (PowerHolderComponent.hasPower(this, SizePower.class)) {
                cir.setReturnValue(applyEyeHeight(pose, dimensions.height));
            }
        } catch (Exception ignored) {}
    }

    @Unique
    private float applyEyeHeight(EntityPose pose, float height) {
        return switch (pose) {
            case SWIMMING, FALL_FLYING, SPIN_ATTACK -> height * 2 / 3;
            case CROUCHING -> height * 0.846F;
            default -> height * 0.9F;
        };
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (PowerHolderComponent.hasPower(this, EntityTransformationPower.class) || PowerHolderComponent.hasPower(this, SizePower.class)) {
            if (PowerHolderComponent.hasPower(this, EntityTransformationPower.class)) {
                EntityTransformationPower power = PowerHolderComponent.getPowers(this, EntityTransformationPower.class).get(0);
                if (power.isTransformed() != power.wasTransformed()) {
                    power.setWasTransformed(power.isTransformed());
                    isChangedDimensions = false;
                }
            }
            if (!isChangedDimensions) {
                this.calculateDimensions();
                this.isChangedDimensions = true;
            }
        }
        else if (isChangedDimensions) {
            this.isChangedDimensions = false;
            this.calculateDimensions();
        }
    }
}