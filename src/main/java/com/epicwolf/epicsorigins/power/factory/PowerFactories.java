package com.epicwolf.epicsorigins.power.factory;

import com.epicwolf.epicsorigins.Epicsorigins;
import com.epicwolf.epicsorigins.power.*;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PlayerAbilityPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.PowerFactorySupplier;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.registry.Registry;

public class PowerFactories {

    public static void register() {
        register(Power.createSimpleFactory(SlimeMovementPower::new, Epicsorigins.identifier("slime_movement")));
        register(Power.createSimpleFactory(WolfFearPower::new, Epicsorigins.identifier("wolf_fear")));
        register(ActionHudOverTimePower::createFactory);
        register(AbstractLookPower::createFactory);
        register(EntityTransformationPower::createFactory);
        register(TimeLimitedActionPower::createFactory);
        register(TimeLimitedAttributePower::createFactory);
        register(ActionOnCollidePower::createFactory);
        register(PowerFactories::createAbilityFactory);
    }

    private static void register(PowerFactory<?> powerFactory) {
        Registry.register(ApoliRegistries.POWER_FACTORY, powerFactory.getSerializerId(), powerFactory);
    }
    private static void register(PowerFactorySupplier<?> factorySupplier) {
        register(factorySupplier.createFactory());
    }

    private static PowerFactory<Power> createAbilityFactory() {
        return new PowerFactory<>(Apoli.identifier("player_ability"),
                new SerializableData()
                        .add("ability", ApoliDataTypes.PLAYER_ABILITY),
                data ->
                        (type, player) -> new PlayerAbilityPower(type, player, data.get("ability")))
                .allowCondition();
    }
}
