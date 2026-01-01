package com.epicwolf.epicsorigins.power;

import com.epicwolf.epicsorigins.Epicsorigins;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.HudRendered;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.*;

import java.util.function.Consumer;

public class ActionHudOverTimePower extends Power implements HudRendered {

    private final HudRender hudRender;
    private final float changeSpeed;
    private final float fillSpeed;
    private float fillValue;
    private boolean isOnCooldown;
    private final int interval;
    private final Consumer<Entity> entityAction;
    private final Consumer<Entity> entityFallAction;
    private final Consumer<Entity> risingAction;
    private final Consumer<Entity> fallingAction;

    private Integer initialTicks = null;

    public ActionHudOverTimePower(PowerType<?> type, LivingEntity entity, int interval, Consumer<Entity> entityAction, Consumer<Entity> entityFallAction, Consumer<Entity> risingAction, Consumer<Entity> fallingAction, float changeSpeed, float fillSpeed, HudRender hudRender) {
        super(type, entity);
        this.hudRender = hudRender;
        this.changeSpeed = changeSpeed;
        this.fillSpeed = (fillSpeed == 0) ? changeSpeed : fillSpeed;
        if(interval <= 0) interval = 1;
        this.interval = interval;
        this.entityAction = entityAction;
        this.entityFallAction = entityFallAction;
        this.risingAction = risingAction;
        this.fallingAction = fallingAction;
        this.setTicking(true);
    }
    public float getProgress() {
        float fill = 1.0f - fillValue;
        return Math.min(1F, Math.max(fill, 0F));
    }
    public boolean isOnCooldown() {
        return this.isOnCooldown;
    }

    @Override
    public HudRender getRenderSettings() {
        return this.hudRender;
    }

    @Override
    public float getFill() {
        return getProgress();
    }

    @Override
    public boolean shouldRender() {
        return this.hudRender.shouldRender();
    }
    @Override
    public void onAdded() {
        if (risingAction != null) {
            risingAction.accept(entity);
        }
    }

    @Override
    public void tick() {
        if (initialTicks == null) {
            initialTicks = entity.age % interval;
        }
        else if (entity.age % interval == initialTicks) {
            if (this.getProgress() > 0 && !this.isOnCooldown() && entityAction != null) {
                entityAction.accept(entity);
            }
            if (this.isOnCooldown() && entityFallAction != null) {
                entityFallAction.accept(entity);
            }
        }
        if (this.getProgress() == 1 && this.isOnCooldown()) {
            this.isOnCooldown = false;
            if (risingAction != null) {
                risingAction.accept(entity);
            }
        }
        if (this.getProgress() == 0 && !this.isOnCooldown()) {
            this.isOnCooldown = true;
            if (fallingAction != null) {
                fallingAction.accept(entity);
            }
        }
        if (this.isActive()) fillValue = Math.min(fillValue + (0.01f * changeSpeed), 1.0f);
        else fillValue = Math.max(fillValue - (0.01f * fillSpeed), 0.0f);
        if (entityAction == null || entityFallAction == null) PowerHolderComponent.sync(entity);
    }

    @Override
    public NbtElement toTag() {
        NbtCompound nbt = new NbtCompound();
        nbt.putFloat("fill_value", fillValue);
        nbt.putBoolean("is_on_cooldown", isOnCooldown);
        return nbt;
    }

    @Override
    public void fromTag(NbtElement tag) {
        if(tag instanceof NbtCompound nbt) {
            fillValue = nbt.getFloat("fill_value");
            isOnCooldown = nbt.getBoolean("is_on_cooldown");
        }
    }

    public static PowerFactory<Power> createFactory() {
        return new PowerFactory<>(Epicsorigins.identifier("action_hud_over_time"),
                new SerializableData()
                        .add("interval", SerializableDataTypes.INT, 1)
                        .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("entity_fall_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("rising_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("falling_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("change_speed", SerializableDataTypes.FLOAT, 1f)
                        .add("fill_speed", SerializableDataTypes.FLOAT, 0f)
                        .add("hud_render", ApoliDataTypes.HUD_RENDER, new HudRender(true, 0, Apoli.identifier("textures/gui/resource_bar.png"), null, false)),
                data ->
                        (type, player) -> new ActionHudOverTimePower(type, player, data.getInt("interval"),
                                data.get("entity_action"), data.get("entity_fall_action"), data.get("rising_action"), data.get("falling_action"), data.getFloat("change_speed"), data.getFloat("fill_speed"), data.get("hud_render")))
                .allowCondition();
    }
}
