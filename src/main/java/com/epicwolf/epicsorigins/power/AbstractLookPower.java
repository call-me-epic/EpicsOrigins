package com.epicwolf.epicsorigins.power;

import com.epicwolf.epicsorigins.Epicsorigins;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

public class AbstractLookPower extends Power implements Active {

    private boolean shouldRender;

    private Identifier textureLocation;

    private final Identifier modelType;

    private Key key;

    public AbstractLookPower(PowerType<?> type, LivingEntity entity, boolean shouldRender, Identifier textureLocation, Identifier modelType) {
        super(type, entity);
        this.shouldRender = shouldRender;
        this.textureLocation = textureLocation;
        this.modelType = modelType;
    }

    public static PowerFactory<Power> createFactory() {
        Key baseKey = new Key();
        baseKey.key = "key.epicsorigins.change_look";
        return new PowerFactory<>(Epicsorigins.identifier("look_attributes"), new SerializableData()
                .add("should_render", SerializableDataTypes.BOOLEAN, true)
                .add("texture_location", SerializableDataTypes.IDENTIFIER, null)
                .add("model_type", SerializableDataTypes.IDENTIFIER)
                .add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, baseKey),
                data -> (type, player) -> {
                    AbstractLookPower power = new AbstractLookPower(type, player, data.getBoolean("should_render"), data.getId("texture_location"), data.getId("model_type"));
                    power.setKey(data.get("key"));
                    return power;
                }).allowCondition();
    }


    @Override
    public void onUse() {
        this.shouldRender = !this.shouldRender;
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public void setKey(Key key) {
        this.key = key;
    }

    @Override
    public NbtElement toTag() {
        return NbtByte.of(shouldRender);
    }

    @Override
    public void fromTag(NbtElement tag) {
        shouldRender = tag.equals(NbtByte.ONE);
    }

    public Identifier getTextureLocation() {
        return textureLocation;
    }

    public Identifier getModelType() {return modelType;}

    public Pair<Identifier, Identifier> getModelAndTexture() {return new Pair<>(modelType, textureLocation);}

    public boolean isShouldRender() {return shouldRender;}
}
