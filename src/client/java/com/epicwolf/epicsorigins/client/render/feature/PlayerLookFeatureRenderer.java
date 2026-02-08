package com.epicwolf.epicsorigins.client.render.feature;

import com.epicwolf.epicsorigins.Epicsorigins;
import com.epicwolf.epicsorigins.client.EpicsoriginsClient;
import com.epicwolf.epicsorigins.client.render.model.PlayerLookModel;
import com.epicwolf.epicsorigins.client.util.PlayerLookModelManager;
import com.epicwolf.epicsorigins.client.util.PlayerLookModelManagerRegistry;
import com.epicwolf.epicsorigins.power.AbstractLookPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.List;

import static com.epicwolf.epicsorigins.util.ModelTypes.*;

public class PlayerLookFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {

    private final Identifier BaseTextureLocation = Epicsorigins.identifier("textures/entity/player/none_attribute.png");

    private final PlayerLookModel<T> playerLookModel;


    public PlayerLookFeatureRenderer(FeatureRendererContext<T, M> context, EntityRendererFactory.Context ctx) {
        super(context);
        this.playerLookModel = new PlayerLookModel<>(ctx.getPart(EpicsoriginsClient.PLAYER_ATTRIBUTES));
    }

    public List<AbstractLookPower> getLookPowers(T entity) {
        return PowerHolderComponent.getPowers(entity, AbstractLookPower.class);
    }

    public Identifier getTexture(T entity, AbstractLookPower power) {
        Identifier textureLocation = BaseTextureLocation;
        if (power.getTextureLocation() != null) textureLocation = power.getTextureLocation();
        if (EpicsoriginsClient.PLAYER_TEXTURES.containsKey(entity.getUuid())) textureLocation = EpicsoriginsClient.PLAYER_TEXTURES.get(entity.getUuid());
        return textureLocation;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity.isInvisible()) return;
        playerLookModel.riding = entity.hasVehicle();
        playerLookModel.child = entity.isBaby();
        playerLookModel.sneaking = entity.isInSneakingPose();
        playerLookModel.animateModel(entity, limbAngle, limbDistance, tickDelta);
        playerLookModel.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        for (AbstractLookPower power : getLookPowers(entity)) {
            if (power.isShouldRender()) {
                Identifier type = power.getModelType();
                RenderLayer layer = playerLookModel.getLayer(getTexture(entity, power));
                for (Pair<Identifier, List<String>> modelType : IDENTIFIERS) {
                    if (type.equals(MERMAID_TAIL)) {
                        if (entity.isSubmergedInWater()) playerLookModel.renderMermaidTail(matrixStack, vertexConsumerProvider.getBuffer(layer), light, OverlayTexture.DEFAULT_UV);
                        break;
                    }
                    if (type.equals(modelType.getLeft())) {
                        boolean shouldRender = true;
                        if (!PlayerLookModelManagerRegistry.modelManagers.isEmpty()) {
                            for (PlayerLookModelManager manager: PlayerLookModelManagerRegistry.modelManagers) {
                                if (!manager.onRender(modelType, entity, playerLookModel)) {
                                    shouldRender = false;
                                    break;
                                }
                            }
                        }
                        if (shouldRender) playerLookModel.renderPart(matrixStack, vertexConsumerProvider.getBuffer(layer), light, OverlayTexture.DEFAULT_UV, modelType.getRight());
                        break;
                    }
                }
            }
        }
    }
}