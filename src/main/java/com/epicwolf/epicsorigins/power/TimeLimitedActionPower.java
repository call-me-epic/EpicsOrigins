package com.epicwolf.epicsorigins.power;

import com.epicwolf.epicsorigins.Epicsorigins;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.function.Consumer;

public class TimeLimitedActionPower extends TimeLimitedPower {

    private final Consumer<Entity> activeFunction;

    public TimeLimitedActionPower(PowerType<?> type, LivingEntity entity, int interval, int ticks, boolean ignoresCondition, Consumer<Entity> activeFunction) {
        super(type, entity, interval, ticks, ignoresCondition);
        this.activeFunction = activeFunction;
    }

    @Override
    public void onInterval() {
        activeFunction.accept(this.entity);
    }

    public static PowerFactory<Power> createFactory() {
        return new PowerFactory<>(Epicsorigins.identifier("time_limited_action"),
                addTimeLimitedSerializable(new SerializableData()
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION)),
                data ->
                        (type, player) -> new TimeLimitedActionPower(type, player, data.getInt("interval"), data.getInt("ticks"), data.getBoolean("ignores_condition"), data.get("entity_action")))
                .allowCondition();
    }
}
