package com.epicwolf.epicsorigins.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.MobEntity;

public class CharmEffect extends StatusEffect {
    protected CharmEffect(int color) {
        super(StatusEffectCategory.HARMFUL, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.age-entity.getLastAttackedTime() <= 1 && entity.getAttacker() != null) entity.removeStatusEffect(this);
        if (entity instanceof MobEntity mob) {
            mob.setTarget(null);
            mob.setAttacker(null);
            mob.setAttacking(false);
        }
    }
}
