package com.epicwolf.epicsorigins.client.util;

import com.epicwolf.epicsorigins.client.render.model.PlayerLookModel;
import net.minecraft.client.model.ModelData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.List;

public interface PlayerLookModelManager {

    void setAngles(LivingEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, PlayerLookModel<?> model);

    default boolean onRender(Pair<Identifier, List<String>> modelType, LivingEntity entity, PlayerLookModel<?> model) {return true;}

    void addTexturedModelData(ModelData modelData);

    void setVisible(boolean visible, PlayerLookModel<?> model);
}
