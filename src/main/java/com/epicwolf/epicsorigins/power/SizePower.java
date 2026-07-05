package com.epicwolf.epicsorigins.power;

import com.epicwolf.epicsorigins.Epicsorigins;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;

public class SizePower extends Power {

    private final float size, width, height;

    public SizePower(PowerType<?> type, LivingEntity entity, float size, float width, float height) {
        super(type, entity);
        this.size = size;
        this.width = width;
        this.height = height;
    }

    public float getSize() {
        return size;
    }

    public float getWidth() {
        return width;
    }
    public float getHeight() {
        return height;
    }

    public static PowerFactory<Power> createFactory() {
        return new PowerFactory<>(Epicsorigins.identifier("size"),
                new SerializableData()
                        .add("size", SerializableDataTypes.FLOAT, 1f)
                        .add("width", SerializableDataTypes.FLOAT, 0f)
                        .add("height", SerializableDataTypes.FLOAT, 0f),
                data -> (type, player) -> new SizePower(type, player, data.getFloat("size"), data.getFloat("width"), data.getFloat("height"))).allowCondition();
    }
}
