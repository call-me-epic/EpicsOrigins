package com.epicwolf.epicsorigins.client.networking;

import com.epicwolf.epicsorigins.Epicsorigins;
import com.epicwolf.epicsorigins.client.EpicsoriginsClient;
import com.epicwolf.epicsorigins.client.gui.PlayerLookScreen;
import com.epicwolf.epicsorigins.networking.ModPackets;
import io.github.apace100.origins.screen.ViewOriginScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;


public class ModPacketsS2C {

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.OPEN_VIEW_ORIGIN_SCREEN, ModPacketsS2C::OpenViewOriginScreen);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.OPEN_PLAYER_LOOK_SCREEN, ModPacketsS2C::OpenPlayerLookScreen);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.UPDATE_PLAYER_LOOK_TEXTURE, ModPacketsS2C::UpdatePlayerLookTexture);
    }

    public static void OpenViewOriginScreen(MinecraftClient minecraftClient, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        minecraftClient.execute(() -> {
            if (!(minecraftClient.currentScreen instanceof ViewOriginScreen)) {
                minecraftClient.setScreen(new ViewOriginScreen());
            }
        });
    }
    public static void OpenPlayerLookScreen(MinecraftClient minecraftClient, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        minecraftClient.execute(() -> {
            if (!(minecraftClient.currentScreen instanceof PlayerLookScreen)) {
                minecraftClient.setScreen(new PlayerLookScreen(Text.translatable("ui.epicsorigins.look_screen.title")));
            }
        });
    }
    public static void UpdatePlayerLookTexture(MinecraftClient minecraftClient, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        UUID uuid = buf.readUuid();
        String textureData = buf.readString();
        boolean shouldRemove = buf.readBoolean();
        minecraftClient.execute(() -> {
            Identifier lookTexture = Epicsorigins.identifier("look_texture/" + uuid.toString());
            if (shouldRemove) {
                EpicsoriginsClient.PLAYER_TEXTURES.remove(uuid);
                minecraftClient.getTextureManager().destroyTexture(lookTexture);
            }
            else {
                try {
                    byte[] byteTexture = Base64.getDecoder().decode(textureData);
                    NativeImage image = NativeImage.read(new ByteArrayInputStream(byteTexture));
                    NativeImageBackedTexture texture = new NativeImageBackedTexture(image);

                    minecraftClient.getTextureManager().registerTexture(lookTexture, texture);
                    EpicsoriginsClient.PLAYER_TEXTURES.put(uuid, lookTexture);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
