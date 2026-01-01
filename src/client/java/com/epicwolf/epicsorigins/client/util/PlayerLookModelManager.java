package com.epicwolf.epicsorigins.client.util;

import com.epicwolf.epicsorigins.client.render.model.PlayerLookModel;
import net.minecraft.client.model.ModelData;
import net.minecraft.entity.LivingEntity;

public interface PlayerLookModelManager {

    void setAngles(LivingEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, PlayerLookModel<?> model);

    void addTexturedModelData(ModelData modelData);

    void setVisible(boolean visible, PlayerLookModel<?> model);
}
