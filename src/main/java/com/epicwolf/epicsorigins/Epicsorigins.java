package com.epicwolf.epicsorigins;

import com.epicwolf.epicsorigins.effect.ModEffects;
import com.epicwolf.epicsorigins.item.ModItems;
import com.epicwolf.epicsorigins.networking.ModPacketsC2S;
import com.epicwolf.epicsorigins.power.factory.PowerFactories;
import com.epicwolf.epicsorigins.power.factory.action.EntityActions;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Epicsorigins implements ModInitializer {

    public static final String MOD_ID = "epicsorigins";

    public static final Logger LOGGER = LogManager.getLogger(Epicsorigins.class);

    @Override
    public void onInitialize() {
//        registerPackets();
        ModPacketsC2S.register();
        PowerFactories.register();
        EntityActions.register();
        ModItems.register();
        ModEffects.register();
    }

    public static Identifier identifier(String id) {
        return Identifier.of(MOD_ID, id);
    }

    /*TODO*/
    private void registerPackets() {
//        ServerPlayNetworking.registerGlobalReceiver(
//                new Identifier(MOD_ID, "update_player_texture"),
//                (server, player, handler, buf, responseSender) -> {
//                    String textureData = buf.readString();
//                    boolean hasWings = buf.readBoolean();
//                    server.execute(() -> {
//                        PlayerTextureManager.savePlayerTexture(player.getUuid(), textureData, hasWings);
//                        PlayerTextureManager.syncPlayerData(player);
//                    });
//                }
//        );
//
//        ServerPlayNetworking.registerGlobalReceiver(
//                new Identifier(MOD_ID, "request_player_data"),
//                (server, player, handler, buf, responseSender) -> {
//                    UUID targetUuid = buf.readUuid();
//                    server.execute(() -> {
//                        PlayerTextureManager.sendPlayerDataToClient(player, targetUuid);
//                    });
//                }
//        );
    }
}
