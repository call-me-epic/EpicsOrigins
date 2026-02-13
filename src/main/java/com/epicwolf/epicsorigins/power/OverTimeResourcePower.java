package com.epicwolf.epicsorigins.power;

import com.epicwolf.epicsorigins.Epicsorigins;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ResourcePower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.function.Consumer;

public class OverTimeResourcePower extends ResourcePower {

    private final Consumer<Entity> actionOnGain;
    private final Consumer<Entity> actionOnLost;
    private final int decrementSpeed;
    private final int incrementSpeed;

    public OverTimeResourcePower(PowerType<?> type, LivingEntity entity, HudRender hudRender, int startValue, int min, int max, int decrementSpeed, int incrementSpeed, Consumer<Entity> actionOnMin, Consumer<Entity> actionOnMax, Consumer<Entity> actionOnGain, Consumer<Entity> actionOnLost) {
        super(type, entity, hudRender, startValue, min, max, actionOnMin, actionOnMax);
        this.decrementSpeed = decrementSpeed;
        this.incrementSpeed = incrementSpeed;
        this.actionOnGain = actionOnGain;
        this.actionOnLost = actionOnLost;
        this.setTicking(true);
    }
    @Override
    public void tick() {
        if (this.isActive()) this.decrement();
        else this.increment();
        PowerHolderComponent.syncPower(entity, this.type);
    }

    @Override
    public void onLost() {
        if(actionOnLost != null) this.actionOnLost.accept(entity);
    }
    @Override
    public void onGained() {
        if(actionOnGain != null) this.actionOnGain.accept(entity);
    }
    @Override
    public void onRespawn() {
        onGained();
    }

    @Override
    public int increment() {
        return setValue(getValue() + this.incrementSpeed);
    }

    @Override
    public int decrement() {
        return setValue(getValue() - this.decrementSpeed);
    }

    public static PowerFactory<Power> createFactory() {
        return new PowerFactory<>(Epicsorigins.identifier("over_time_resource"),
                new SerializableData()
                        .add("min", SerializableDataTypes.INT)
                        .add("max", SerializableDataTypes.INT)
                        .addFunctionedDefault("start_value", SerializableDataTypes.INT, data -> data.getInt("min"))
                        .add("decrement_speed", SerializableDataTypes.INT, 1)
                        .addFunctionedDefault("increment_speed", SerializableDataTypes.INT, data -> data.getInt("decrement_speed"))
                        .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
                        .add("min_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("max_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("lost_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("gain_action", ApoliDataTypes.ENTITY_ACTION, null),
                data ->
                        (type, player) ->
                                new OverTimeResourcePower(type, player,
                                        data.get("hud_render"),
                                        data.getInt("start_value"),
                                        data.getInt("min"),
                                        data.getInt("max"),
                                        data.getInt("decrement_speed"),
                                        data.getInt("increment_speed"),
                                        data.get("min_action"),
                                        data.get("max_action"),
                                        data.get("gain_action"),
                                        data.get("lost_action")))
                .allowCondition();
    }
}
