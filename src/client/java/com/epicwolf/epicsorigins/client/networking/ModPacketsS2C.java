package com.epicwolf.epicsorigins.client.networking;

import com.epicwolf.epicsorigins.networking.ModPackets;
import io.github.apace100.origins.screen.ViewOriginScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;


public class ModPacketsS2C {

    public static void register() {
        ClientPlayConnectionEvents.INIT.register(((clientPlayNetworkHandler, minecraftClient) -> ClientPlayNetworking.registerReceiver(ModPackets.OPEN_VIEW_ORIGIN_SCREEN, ModPacketsS2C::OpenViewOriginScreen)));
    }

    public static void OpenViewOriginScreen(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        minecraftClient.execute(() -> {
            if (!(minecraftClient.currentScreen instanceof ViewOriginScreen)) {
                minecraftClient.setScreen(new ViewOriginScreen());
            }
        });
    }
}
