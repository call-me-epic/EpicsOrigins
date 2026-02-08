package com.epicwolf.epicsorigins;

import com.epicwolf.epicsorigins.effect.ModEffects;
import com.epicwolf.epicsorigins.item.ModItems;
import com.epicwolf.epicsorigins.networking.ModPacketsC2S;
import com.epicwolf.epicsorigins.power.factory.PowerFactories;
import com.epicwolf.epicsorigins.power.factory.action.EntityActions;
import com.epicwolf.epicsorigins.util.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Epicsorigins implements ModInitializer {

    public static final String MOD_ID = "epicsorigins";

    public static final Logger LOGGER = LogManager.getLogger(Epicsorigins.class);

    public static ModConfig config;

    public static String VERSION = "";

    @Override
    public void onInitialize() {
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent((modContainer) -> {
            VERSION = modContainer.getMetadata().getVersion().getFriendlyString();
            if (VERSION.contains("+")) {
                VERSION = VERSION.split("\\+")[0];
            }

            if (VERSION.contains("-")) {
                VERSION = VERSION.split("-")[0];
            }

        });
        LOGGER.info("Epic`s Origins {} is initializing", VERSION);
        config = new ModConfig();
        config.modConfigDir.mkdirs();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            Epicsorigins.config.loadTexturesToPlayer(handler.getPlayer());
        });

        ModPacketsC2S.register();
        PowerFactories.register();
        EntityActions.register();
        ModItems.register();
        ModEffects.register();
    }

    public static Identifier identifier(String id) {
        return Identifier.of(MOD_ID, id);
    }
}
