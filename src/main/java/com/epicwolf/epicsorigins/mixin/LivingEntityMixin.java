package com.epicwolf.epicsorigins.mixin;

import com.epicwolf.epicsorigins.Epicsorigins;
import com.epicwolf.epicsorigins.power.EntityTransformationPower;
import com.epicwolf.epicsorigins.power.SlimeMovementPower;
import com.epicwolf.epicsorigins.power.WolfFearPower;
import io.github.apace100.apoli.access.MovingEntity;
import io.github.apace100.apoli.access.SubmergableEntity;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements MovingEntity, SubmergableEntity {

    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }
    @Inject(method = "tick", at = @At("HEAD"))
    public void onEntityLand(CallbackInfo ci) {
        if (PowerHolderComponent.hasPower(this, SlimeMovementPower.class) && this.isInFluid()) {
            this.addVelocity(0, 0.075, 0);
        }
    }

    @Unique
    public boolean isInFluid() {
        BlockPos pos = this.getBlockPos();
        FluidState fluidState = this.getWorld().getFluidState(pos);
        return !fluidState.isEmpty();
    }
    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    public void TransformedDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        for (EntityTransformationPower power : PowerHolderComponent.getPowers(this, EntityTransformationPower.class)) {
            if (power.isTransformed()) {
                cir.setReturnValue(EntityType.get(power.getEntityType().toString()).get().getDimensions());
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (PowerHolderComponent.hasPower(this, WolfFearPower.class)) {
            Box checkbox = new Box(this.getPos().add(-5, -5, -5), this.getPos().add(5, 5, 5));
            List<WolfEntity> wolfs = this.getWorld().getEntitiesByType(TypeFilter.instanceOf(WolfEntity.class), checkbox, wolfEntity -> {
                wolfEntity.setTarget((LivingEntity)(Object)this);
                return true;
            });
            if (!wolfs.isEmpty()) this.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 0));
        }
    }
}
