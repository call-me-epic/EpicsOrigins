package com.epicwolf.epicsorigins.util;

import com.epicwolf.epicsorigins.Epicsorigins;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTextureManager {
    private static final Map<UUID, PlayerTextureData> PLAYER_TEXTURES = new HashMap<>();
    
    public static class PlayerTextureData {
        public String textureData;
        public boolean hasWings;
        public long lastUpdate;
        
        public PlayerTextureData(String textureData, boolean hasWings) {
            this.textureData = textureData;
            this.hasWings = hasWings;
            this.lastUpdate = System.currentTimeMillis();
        }
    }
    
    public static void savePlayerTexture(UUID playerId, String textureData, boolean hasWings) {
        PLAYER_TEXTURES.put(playerId, new PlayerTextureData(textureData, hasWings));
    }
    
    public static PlayerTextureData getPlayerTexture(UUID playerId) {
        return PLAYER_TEXTURES.get(playerId);
    }
    
    public static void syncPlayerData(ServerPlayerEntity player) {
        PlayerTextureData data = getPlayerTexture(player.getUuid());
        if (data != null) {
            // Отправляем данные всем игрокам в радиусе
            player.getServerWorld().getPlayers().forEach(otherPlayer -> {
                if (!otherPlayer.getUuid().equals(player.getUuid())) {
                    sendPlayerDataToClient(otherPlayer, player.getUuid());
                }
            });
        }
    }
    
    public static void sendPlayerDataToClient(ServerPlayerEntity targetPlayer, UUID playerUuid) {
        PlayerTextureData data = getPlayerTexture(playerUuid);
        if (data != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeUuid(playerUuid);
            buf.writeString(data.textureData);
            buf.writeBoolean(data.hasWings);
            
            ServerPlayNetworking.send(targetPlayer,
                    Epicsorigins.identifier("player_texture_data"), buf);
        }
    }
    
    public static Identifier getPlayerTextureIdentifier(UUID playerId) {
        return Epicsorigins.identifier(
            "textures/entity/player/custom_" + playerId.toString() + ".png");
    }
}