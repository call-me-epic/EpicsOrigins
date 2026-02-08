package com.epicwolf.epicsorigins.util;

import com.epicwolf.epicsorigins.Epicsorigins;
import com.epicwolf.epicsorigins.networking.ModPackets;
import io.netty.buffer.Unpooled;
import me.shedaniel.autoconfig.annotation.Config;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

@Config(name = "epicsorigins_server")
public class ModConfig {
    public File baseConfigDir;
    public File modConfigDir;

    public ModConfig() {
        baseConfigDir = FabricLoader.getInstance().getConfigDir().toFile();
        modConfigDir = new File(baseConfigDir, "epicsorigins");
    }

    public void loadTextureToPlayers(ServerPlayerEntity player, boolean shouldRemove) {
        String uuid = player.getUuid().toString();
        if (hasTexture(uuid)) {
            for (PlayerEntity pl : player.getWorld().getPlayers()) sendTextureToPlayer((ServerPlayerEntity) pl, player.getUuid(), player.getUuid().toString() + ".png", shouldRemove);
        }
    }

    public void loadTexturesToPlayer(ServerPlayerEntity player) {
        for (File file : Arrays.stream(Objects.requireNonNull(modConfigDir.listFiles())).filter(File::isFile).toList()) {
            sendTextureToPlayer(player, UUID.fromString(file.getName().split("\\.")[0]), file.getName(), false);
        }
    }
    public void sendTextureToPlayer(ServerPlayerEntity player, UUID uuid, String name, boolean shouldRemove) {
        try {
            if (!player.getWorld().isClient()) {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeUuid(uuid);
                buf.writeString(getTexture(name));
                buf.writeBoolean(shouldRemove);
                ServerPlayNetworking.send(player, ModPackets.UPDATE_PLAYER_LOOK_TEXTURE, buf);
                if (shouldRemove) this.deleteTexture(player.getUuid().toString());
            }
        }
        catch (Exception e) {
            Epicsorigins.LOGGER.error("Could not load player`s custom look texture: {}", String.valueOf(e));
        }
    }

    public boolean hasTexture(String uuid) {
        File file = new File(modConfigDir, uuid + ".png");
        return file.exists();
    }
    public String getTexture(String uuid) throws IOException {
        byte[] imageData = Files.readAllBytes(new File(modConfigDir, uuid).toPath());
        return Base64.getEncoder().encodeToString(imageData);
    }

    public void downloadTexture(String uuid, String url) throws IOException, InterruptedException {
        File file = new File(modConfigDir, uuid + ".png");
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(60))
                .GET()
                .build();
        HttpClient.newHttpClient().send(request,
                        HttpResponse.BodyHandlers.ofFile(file.toPath()));
    }

    public void deleteTexture(String uuid) {
        new File(modConfigDir, uuid + ".png").delete();
    }
}
