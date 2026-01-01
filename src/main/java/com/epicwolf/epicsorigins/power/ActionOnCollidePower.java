package com.epicwolf.epicsorigins.power;

import com.epicwolf.epicsorigins.Epicsorigins;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnCollidePower extends CooldownPower {

    private final Predicate<Pair<Entity, Entity>> bientityCondition;
    private final Consumer<Pair<Entity, Entity>> bientityAction;

    public ActionOnCollidePower(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender, Consumer<Pair<Entity, Entity>> bientityAction, Predicate<Pair<Entity, Entity>> bientityCondition) {
        super(type, entity, cooldownDuration, hudRender);
        this.bientityAction = bientityAction;
        this.bientityCondition = bientityCondition;
        this.setTicking(true);
    }

    @Override
    public void tick() {
        List<Entity> entityList = entity.getWorld().getOtherEntities(entity, entity.getBoundingBox().expand(1, 0.5, 1));
        if (!entityList.isEmpty()) {
            for (Entity target : entityList) {
                onCollide(target);
            }
        }
    }

    public void onCollide(Entity target) {
        if(canUse()) {
            if(bientityCondition == null || bientityCondition.test(new Pair<>(entity, target))) {
                this.bientityAction.accept(new Pair<>(entity, target));
                use();
            }
        }
    }

    public static PowerFactory<Power> createFactory() {
        return new PowerFactory<>(Epicsorigins.identifier("action_on_collide"),
            new SerializableData()
                .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION)
                .add("cooldown", SerializableDataTypes.INT, 1)
                .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
                .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
            data ->
                (type, player) -> new ActionOnCollidePower(type, player, data.getInt("cooldown"),
                    data.get("hud_render"), data.get("bientity_action"), data.get("bientity_condition")))
            .allowCondition();
    }
}
