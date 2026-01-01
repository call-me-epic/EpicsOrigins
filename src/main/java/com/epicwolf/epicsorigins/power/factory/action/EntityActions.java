package com.epicwolf.epicsorigins.power.factory.action;

import com.epicwolf.epicsorigins.Epicsorigins;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registry;

public class EntityActions {

    public static void register() {
        register(new ActionFactory<>(Epicsorigins.identifier("grant_ability"), new SerializableData()
                .add("ability", ApoliDataTypes.PLAYER_ABILITY)
                .add("source", SerializableDataTypes.IDENTIFIER),
                (data, entity) -> {
            AbilitySource source = Pal.getAbilitySource(data.getId("source"));
            source.grantTo((PlayerEntity)entity, data.get("ability"));
        }));

        register(new ActionFactory<>(Epicsorigins.identifier("revoke_ability"), new SerializableData()
                .add("ability", ApoliDataTypes.PLAYER_ABILITY)
                .add("source", SerializableDataTypes.IDENTIFIER),
                (data, entity) -> {
            AbilitySource source = Pal.getAbilitySource(data.getId("source"));
            source.revokeFrom((PlayerEntity)entity, data.get("ability"));
        }));
    }

    private static void register(ActionFactory<Entity> actionFactory) {
        Registry.register(ApoliRegistries.ENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
    }
}
