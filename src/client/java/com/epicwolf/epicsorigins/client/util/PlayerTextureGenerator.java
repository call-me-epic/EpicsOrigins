package com.epicwolf.epicsorigins.client.util;

import com.epicwolf.epicsorigins.Epicsorigins;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.DynamicTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTextureGenerator {
    private static final Map<UUID, Identifier> GENERATED_TEXTURES = new HashMap<>();
    
    public static void generatePlayerTexture(UUID playerId, String base64Data) {
        try {
            byte[] imageData = Base64.getDecoder().decode(base64Data);
            NativeImage image = NativeImage.read(new ByteArrayInputStream(imageData));


            Identifier textureId = Epicsorigins.identifier(
                "dynamic_player_" + playerId.toString());
            
//            MinecraftClient.getInstance().getTextureManager()
//                .registerTexture(textureId, new DynamicTexture(image));
            
            GENERATED_TEXTURES.put(playerId, textureId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean hasTexture(UUID playerId) {
        return GENERATED_TEXTURES.containsKey(playerId);
    }
    
    public static Identifier getPlayerTextureIdentifier(UUID playerId) {
        return GENERATED_TEXTURES.get(playerId);
    }
}