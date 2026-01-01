package com.epicwolf.epicsorigins.client;

import com.epicwolf.epicsorigins.Epicsorigins;
import com.epicwolf.epicsorigins.client.command.Commands;
import com.epicwolf.epicsorigins.client.networking.ModPacketsS2C;
import com.epicwolf.epicsorigins.client.render.feature.PlayerLookFeatureRenderer;
import com.epicwolf.epicsorigins.client.render.model.PlayerLookModel;
import io.github.apace100.apoli.ApoliClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EpicsoriginsClient implements ClientModInitializer {

    public static final EntityModelLayer PLAYER_ATTRIBUTES = new EntityModelLayer(
            Epicsorigins.identifier("player_attributes"), "main");

    public static final Map<UUID, PlayerTextureData> CLIENT_PLAYER_TEXTURES = new HashMap<>();

    public static KeyBinding useChangeLookPower;

    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(PLAYER_ATTRIBUTES, PlayerLookModel::getTexturedModelData);

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer instanceof PlayerEntityRenderer playerEntityRenderer) {
                registrationHelper.register(new PlayerLookFeatureRenderer<>(playerEntityRenderer, context));
            }
        });

        Commands.register();
        ModPacketsS2C.register();

        useChangeLookPower = new KeyBinding("key.epicsorigins.change_look", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category." + Epicsorigins.MOD_ID);
        ApoliClient.registerPowerKeybinding("key.epicsorigins.change_look", useChangeLookPower);
        ApoliClient.registerPowerKeybinding("changelook", useChangeLookPower);

        KeyBindingHelper.registerKeyBinding(useChangeLookPower);

        // Регистрация пакетов
//        registerClientPackets();

        // Регистрация слушателя ресурсов
//        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
//                .registerReloadListener(new PlayerTextureReloadListener());

//        ClientTickEvents.END_CLIENT_TICK.register(client -> {
//            if (client.world != null && client.player != null) {
//                requestPlayerTextures();
//            }
//        });
    }

    /*TODO*/
    private void registerClientPackets() {
//        ClientPlayNetworking.registerGlobalReceiver(
//                Epicsorigins.identifier("player_texture_data"),
//                (client, handler, buf, responseSender) -> {
//                    UUID playerUuid = buf.readUuid();
//                    String textureData = buf.readString();
//                    boolean hasWings = buf.readBoolean();
//
//                    client.execute(() -> {
//                        CLIENT_PLAYER_TEXTURES.put(playerUuid,
//                                new PlayerTextureData(textureData, hasWings));
//                        PlayerTextureGenerator.generatePlayerTexture(playerUuid, textureData);
//                    });
//                }
//        );
    }

    /*TODO*/
    private void requestPlayerTextures() {
//        MinecraftClient client = MinecraftClient.getInstance();
//        if (client.world != null) {
//            client.world.getPlayers().forEach(player -> {
//                if (!player.getUuid().equals(client.player.getUuid()) &&
//                        !CLIENT_PLAYER_TEXTURES.containsKey(player.getUuid())) {
//
//                    // Запрашиваем данные игрока
//                    PacketByteBuf buf = PacketByteBufs.create();
//                    buf.writeUuid(player.getUuid());
//                    ClientPlayNetworking.send(
//                            Epicsorigins.identifier("request_player_data"), buf);
//                }
//            });
//        }
    }


    /*TODO*/
    public static class PlayerTextureData {
//        public String textureData;
//        public boolean hasWings;
//
//        public PlayerTextureData(String textureData, boolean hasWings) {
//            this.textureData = textureData;
//            this.hasWings = hasWings;
//        }
    }
}
