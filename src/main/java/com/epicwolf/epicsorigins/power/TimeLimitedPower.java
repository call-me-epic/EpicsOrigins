package com.epicwolf.epicsorigins.power;

import com.google.gson.JsonObject;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.action.ActionTypes;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.function.Consumer;

public class TimeLimitedPower extends Power {

    private int ticks;
    private final int interval;
    private final boolean ignoresCondition;
    private final Consumer<Entity> deleteFunction;

    public TimeLimitedPower(PowerType<?> type, LivingEntity entity, int interval, int ticks, boolean ignoresCondition) {
        super(type, entity);
        if(interval <= 0) interval = 1;
        this.interval = interval;
        this.ticks = ticks;
        this.ignoresCondition = ignoresCondition;
        this.setTicking();
        JsonObject js = new JsonObject();
        js.addProperty("type", "origins:revoke_power");
        js.addProperty("power", String.valueOf(this.type.getIdentifier()));
        js.addProperty("source", "epicsorigins:evolved/asura");
        this.deleteFunction = ActionTypes.ENTITY.read(js);
    }

    @Override
    public void tick() {
        if(this.isActive() || this.ignoresCondition) {
            --ticks;
        }
        if (entity.age % interval == 0) this.onInterval();
        if (ticks <= 0) {
            deleteFunction.accept(entity);
        }
    }

    public void onInterval() {}

    @Override
    public NbtElement toTag() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("ticks", ticks);
        return nbt;
    }

    @Override
    public void fromTag(NbtElement tag) {
        if(tag instanceof NbtCompound nbt) {
            this.ticks = nbt.getInt("ticks");
        }
    }

    public static SerializableData addTimeLimitedSerializable(SerializableData serializableData) {
        return serializableData.add("interval", SerializableDataTypes.INT, 1).add("ticks", SerializableDataTypes.INT, 20).add("ignores_condition", SerializableDataTypes.BOOLEAN, false);
    }
}