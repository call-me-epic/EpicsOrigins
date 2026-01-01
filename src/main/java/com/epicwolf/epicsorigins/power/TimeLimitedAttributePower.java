package com.epicwolf.epicsorigins.power;

import com.epicwolf.epicsorigins.Epicsorigins;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.AttributedEntityAttributeModifier;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;

import java.util.LinkedList;
import java.util.List;

public class TimeLimitedAttributePower extends TimeLimitedPower {

    private final List<AttributedEntityAttributeModifier> modifiers = new LinkedList<>();
    private final boolean updateHealth;

    public TimeLimitedAttributePower(PowerType<?> type, LivingEntity entity, int interval, int ticks, boolean ignoresCondition, boolean updateHealth) {
        super(type, entity, interval, ticks, ignoresCondition);
        this.updateHealth = updateHealth;
    }

    @Override
    public void onInterval() {
        if(this.isActive()) {
            addMods();
        } else {
            removeMods();
        }
    }

    @Override
    public void onRemoved() {
        removeMods();
    }

    public TimeLimitedAttributePower addModifier(AttributedEntityAttributeModifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public void addMods() {
        float previousMaxHealth = entity.getMaxHealth();
        float previousHealthPercent = entity.getHealth() / previousMaxHealth;
        modifiers.forEach(mod -> {
            if(entity.getAttributes().hasAttribute(mod.getAttribute())) {
                EntityAttributeInstance instance = entity.getAttributeInstance(mod.getAttribute());
                if(instance != null) {
                    if(!instance.hasModifier(mod.getModifier())) {
                        instance.addTemporaryModifier(mod.getModifier());
                    }
                }
            }
        });
        float afterMaxHealth = entity.getMaxHealth();
        if(updateHealth && afterMaxHealth != previousMaxHealth) {
            entity.setHealth(afterMaxHealth * previousHealthPercent);
        }
    }

    public void removeMods() {
        float previousMaxHealth = entity.getMaxHealth();
        float previousHealthPercent = entity.getHealth() / previousMaxHealth;
        modifiers.forEach(mod -> {
            if (entity.getAttributes().hasAttribute(mod.getAttribute())) {
                EntityAttributeInstance instance = entity.getAttributeInstance(mod.getAttribute());
                if(instance != null) {
                    if(instance.hasModifier(mod.getModifier())) {
                        instance.removeModifier(mod.getModifier());
                    }
                }
            }
        });
        float afterMaxHealth = entity.getMaxHealth();
        if(updateHealth && afterMaxHealth != previousMaxHealth) {
            entity.setHealth(afterMaxHealth * previousHealthPercent);
        }
    }

    public static PowerFactory<Power> createFactory() {
        return new PowerFactory<>(Epicsorigins.identifier("time_limited_attribute"),
                addTimeLimitedSerializable(new SerializableData()
                        .add("modifier", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIER, null)
                        .add("modifiers", ApoliDataTypes.ATTRIBUTED_ATTRIBUTE_MODIFIERS, null)
                        .add("update_health", SerializableDataTypes.BOOLEAN)),
                data ->
                        (type, player) -> {
            TimeLimitedAttributePower ap = new TimeLimitedAttributePower(type, player, data.getInt("interval"), data.getInt("ticks"), data.getBoolean("ignores_condition"), data.getBoolean("update_health"));
            if(data.isPresent("modifier")) {
                ap.addModifier(data.get("modifier"));
            }
            if(data.isPresent("modifiers")) {
                List<AttributedEntityAttributeModifier> modifierList = data.get("modifiers");
                modifierList.forEach(ap::addModifier);
            }
            return ap;
        }).allowCondition();
    }
}
