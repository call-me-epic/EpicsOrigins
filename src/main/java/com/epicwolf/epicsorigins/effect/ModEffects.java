package com.epicwolf.epicsorigins.effect;

import com.epicwolf.epicsorigins.Epicsorigins;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

public class ModEffects {
    public static final RegistryEntry<StatusEffect> CHARM =
            Registry.registerReference(Registries.STATUS_EFFECT, Epicsorigins.identifier("charm"), new CharmEffect(15568345));

    public static void register() {}
}
