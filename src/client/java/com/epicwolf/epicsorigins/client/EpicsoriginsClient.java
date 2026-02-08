package com.epicwolf.epicsorigins.client;

import com.epicwolf.epicsorigins.Epicsorigins;
import com.epicwolf.epicsorigins.client.command.Commands;
import com.epicwolf.epicsorigins.client.networking.ModPacketsS2C;
import com.epicwolf.epicsorigins.client.render.feature.PlayerLookFeatureRenderer;
import com.epicwolf.epicsorigins.client.render.model.PlayerLookModel;
import com.epicwolf.epicsorigins.networking.ModPackets;
import io.github.apace100.apoli.ApoliClient;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EpicsoriginsClient implements ClientModInitializer {

    public static final EntityModelLayer PLAYER_ATTRIBUTES = new EntityModelLayer(
            Epicsorigins.identifier("player_attributes"), "main");

    public static final Map<UUID, Identifier> PLAYER_TEXTURES = new HashMap<>();

    public static KeyBinding useChangeLookPower;

    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(PLAYER_ATTRIBUTES, PlayerLookModel::getTexturedModelData);

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer instanceof PlayerEntityRenderer playerEntityRenderer) {
                registrationHelper.register(new PlayerLookFeatureRenderer<>(playerEntityRenderer, context));
            }
        });
        ClientPlayConnectionEvents.JOIN.register((handler,  sender, minecraftClient) -> {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer().writeBoolean(false));
            buf.writeString("");
            ClientPlayNetworking.send(ModPackets.SEND_PLAYER_LOOK_TEXTURE, buf);
        });

        Commands.register();
        ModPacketsS2C.register();

        useChangeLookPower = new KeyBinding("key.epicsorigins.change_look", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category." + Epicsorigins.MOD_ID);
        ApoliClient.registerPowerKeybinding("key.epicsorigins.change_look", useChangeLookPower);
        ApoliClient.registerPowerKeybinding("changelook", useChangeLookPower);

        KeyBindingHelper.registerKeyBinding(useChangeLookPower);
    }
}
