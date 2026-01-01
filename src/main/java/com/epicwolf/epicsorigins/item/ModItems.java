package com.epicwolf.epicsorigins.item;

import com.epicwolf.epicsorigins.Epicsorigins;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {

    public static final Item ORB_OF_EVOLUTION = new OrbOfEvolutionItem();

    public static void register() {
        Registry.register(Registries.ITEM, Epicsorigins.identifier("orb_of_evolution"), ORB_OF_EVOLUTION);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register((content) -> content.add(ORB_OF_EVOLUTION));
    }
}
