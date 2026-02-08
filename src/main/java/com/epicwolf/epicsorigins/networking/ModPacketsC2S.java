package com.epicwolf.epicsorigins.networking;

import com.epicwolf.epicsorigins.Epicsorigins;
import com.epicwolf.epicsorigins.power.AbstractLookPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class ModPacketsC2S {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ModPackets.TOGGLE_PLAYER_LOOK_POWER, ModPacketsC2S::ToggleLook);
        ServerPlayNetworking.registerGlobalReceiver(ModPackets.SEND_PLAYER_LOOK_TEXTURE,
                (minecraftServer, serverPlayer, handler, buf, sender) -> {
            boolean shouldRemove = buf.readBoolean();
            String url = buf.readString();
            try {
                if (!shouldRemove && !url.isBlank()) Epicsorigins.config.downloadTexture(serverPlayer.getUuid().toString(), url);
                Epicsorigins.config.loadTextureToPlayers(serverPlayer, shouldRemove);
            } catch (Exception e) {
                Epicsorigins.LOGGER.error("Could not download texture from url of player clipboard");
            }
        });
    }

    public static void ToggleLook(MinecraftServer minecraftServer, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        int count = buf.readInt();
        Identifier[] powerIds = new Identifier[count];
        for(int i = 0; i < count; i++) {
            powerIds[i] = buf.readIdentifier();
        }
        minecraftServer.execute(() -> {
            PowerHolderComponent component = PowerHolderComponent.KEY.get(playerEntity);
            boolean shouldRender = ((AbstractLookPower) component.getPower(PowerTypeRegistry.get(powerIds[0]))).isShouldRender();
            for(Identifier id : powerIds) {
                PowerType<?> type = PowerTypeRegistry.get(id);
                Power power = component.getPower(type);
                if(power instanceof AbstractLookPower lookPower) {
                    lookPower.setShouldRender(!shouldRender);
                }
                PowerHolderComponent.sync(playerEntity);
            }
        });
    }
}
