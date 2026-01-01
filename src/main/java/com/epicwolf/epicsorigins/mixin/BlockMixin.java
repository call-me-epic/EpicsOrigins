package com.epicwolf.epicsorigins.mixin;


import com.epicwolf.epicsorigins.power.SlimeMovementPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "onEntityLand", at = @At("HEAD"), cancellable = true)
    public void onEntityLand(BlockView world, Entity entity, CallbackInfo ci) {
        if (PowerHolderComponent.hasPower(entity, SlimeMovementPower.class)) {
            if (!entity.bypassesLandingEffects()) {
                Vec3d vec3d = entity.getVelocity();
                if (vec3d.y < (double)-0.1F) {
                    double d = entity instanceof LivingEntity ? (double)1.0F : 0.8;
                    entity.setVelocity(vec3d.x, -vec3d.y * d, vec3d.z);
                    ci.cancel();
                }
            }
        }
    }
}
