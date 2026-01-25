package com.epicwolf.epicsorigins.power;

import com.epicwolf.epicsorigins.Epicsorigins;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

public class EntityTransformationPower extends Power implements Active {

    private boolean isTransformed;

    private boolean wasTransformed;

    private final Identifier entityType;

    private final boolean isChangeable;

    private Key key;

    public EntityTransformationPower(PowerType<?> type, LivingEntity entity, boolean isTransformed, Identifier entityType, boolean changeable) {
        super(type, entity);
        this.isTransformed = isTransformed;
        this.entityType = entityType;
        this.isChangeable = changeable;
        this.wasTransformed = false;
    }

    public static PowerFactory<Power> createFactory() {
        return new PowerFactory<>(Epicsorigins.identifier("entity_transformation"),
                new SerializableData()
                        .add("active_by_default", SerializableDataTypes.BOOLEAN, false)
                        .add("changeable", SerializableDataTypes.BOOLEAN, true)
                        .add("entity_type", SerializableDataTypes.IDENTIFIER)
                        .add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Key()),
                data -> (type, player) -> {
                    EntityTransformationPower power = new EntityTransformationPower(type, player, data.getBoolean("active_by_default"), data.getId("entity_type"), data.getBoolean("changeable"));
                    power.setKey(data.get("key"));
                    return power;
                }).allowCondition();
    }

    @Override
    public void onUse() {
        if (isChangeable) this.isTransformed = !this.isTransformed;
        PowerHolderComponent.sync(this.entity);
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
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("is_transformed", isTransformed);
        nbt.putBoolean("was_transformed", wasTransformed);
        return nbt;
    }

    @Override
    public void fromTag(NbtElement tag) {
        if(tag instanceof NbtCompound nbt) {
            isTransformed = nbt.getBoolean("is_transformed");
            wasTransformed = nbt.getBoolean("was_transformed");
        }
    }

    public Identifier getEntityType() {return entityType;}

    public boolean isTransformed() {return isTransformed;}

    public boolean wasTransformed() {return wasTransformed;}

    public void setWasTransformed(boolean transformed) {wasTransformed = transformed;}
}
