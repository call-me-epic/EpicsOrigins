package com.epicwolf.epicsorigins.client.render.model;

import com.epicwolf.epicsorigins.client.util.PlayerLookModelManager;
import com.epicwolf.epicsorigins.client.util.PlayerLookModelManagerRegistry;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Objects;

public class PlayerLookModel<T extends LivingEntity> extends BipedEntityModel<T> {

    public final ModelPart wings;

    public final ModelPart fox_tail;

    public final ModelPart demon_tail;

    public final ModelPart mermaid_tail;

    public final ModelPart fox_ears;

    public final ModelPart demon_horns;

    public final ModelPart root;

    public PlayerLookModel(ModelPart root) {
        super(root);
        this.root = root;
        this.wings = root.getChild(EntityModelPartNames.BODY).getChild("wings");
        this.fox_tail = root.getChild(EntityModelPartNames.BODY).getChild("fox_tail");
        this.mermaid_tail = root.getChild("mermaid_tail");
        this.demon_tail = root.getChild(EntityModelPartNames.BODY).getChild("demon_tail");
        this.fox_ears = root.getChild(EntityModelPartNames.HEAD).getChild("fox_ears");
        this.demon_horns = root.getChild(EntityModelPartNames.HEAD).getChild("demon_horns");
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j) {
        super.setAngles(entity, f, g, h, i, j);
        this.fox_ears.copyTransform(this.head);
        this.demon_horns.copyTransform(this.head);
        this.fox_tail.setTransform(ModelTransform.of(this.body.getTransform().pivotX,
                this.body.getTransform().pivotY, this.body.getTransform().pivotZ,
                this.body.getTransform().pitch-0.3927F, this.body.getTransform().yaw, this.body.getTransform().roll));
        this.demon_tail.setTransform(ModelTransform.of(this.body.getTransform().pivotX,
                this.body.getTransform().pivotY, this.body.getTransform().pivotZ,
                this.body.getTransform().pitch-0.5F, this.body.getTransform().yaw, this.body.getTransform().roll));
        this.wings.copyTransform(this.body);
        ModelPart left_wing = this.wings.getChild("left_wing");
        ModelPart right_wing = this.wings.getChild("right_wing");

        ModelPart mermaid_tail2 = this.mermaid_tail.getChild("tail2");

        boolean bl = entity.getRoll() > 4;
        float k = 1.0f;
        if (bl) {
            k = (float)entity.getVelocity().lengthSquared();
            k /= 0.2f;
            k *= k * k;
        }
        if (k < 1.0f) {
            k = 1.0f;
        }
        this.mermaid_tail.pitch = (MathHelper.cos((float)(f * 0.6662f)) + 0.4f)* g / k / 8;
        mermaid_tail2.pitch = MathHelper.cos((float)(f * 0.6662f))* g / k / 4;
        this.mermaid_tail.yaw = 0.005f;
        this.mermaid_tail.roll = 0.005f;
        if (this.riding) {
            this.mermaid_tail.pitch = -1.4137167f;
            this.mermaid_tail.yaw = 0;
            this.mermaid_tail.roll = 0;
        }
        if (this.sneaking) {
            this.mermaid_tail.pivotZ = 4.0f;
            this.mermaid_tail.pivotY = 12.2f;
        } else {
            this.mermaid_tail.pivotZ = 0.0f;
            this.mermaid_tail.pivotY = 12.0f;
        }
        if (this.leaningPitch > 0.0f) {
            this.mermaid_tail.pitch = MathHelper.lerp((float)this.leaningPitch, (float)this.mermaid_tail.pitch, (float)(0.15f * MathHelper.cos((float)(f * 0.33333334f))));
            mermaid_tail2.pitch = MathHelper.lerp((float)this.leaningPitch, (float)mermaid_tail2.pitch, (float)(0.4f * MathHelper.cos((float)(f * 0.33333334f))));
        }
        for (PlayerLookModelManager manager : PlayerLookModelManagerRegistry.modelManagers) {
            manager.setAngles(entity, f, g, h, i, j, this);
        }

    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.wings.visible = visible;
        this.fox_tail.visible = visible;
        this.mermaid_tail.visible = visible;
        this.demon_tail.visible = visible;
        this.fox_ears.visible = visible;
        this.demon_horns.visible = visible;
        for (PlayerLookModelManager manager : PlayerLookModelManagerRegistry.modelManagers) {
            manager.setVisible(visible, this);
        }
    }

    public void renderWings(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.wings.render(matrices, vertices, light, overlay);
    }
    public void renderFoxTail(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.fox_tail.render(matrices, vertices, light, overlay);
    }
    public void renderMermaidTail(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.mermaid_tail.render(matrices, vertices, light, overlay);
    }
    public void renderDemonTail(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.demon_tail.render(matrices, vertices, light, overlay);
    }
    public void renderFoxEars(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.fox_ears.render(matrices, vertices, light, overlay);
    }
    public void renderDemonHorns(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.demon_horns.render(matrices, vertices, light, overlay);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData body = modelPartData.getChild(EntityModelPartNames.BODY);
        ModelPartData head = modelPartData.getChild(EntityModelPartNames.HEAD);
        ModelPartData wings = body.addChild("wings", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        ModelPartData left_wing = wings.addChild("left_wing", ModelPartBuilder.create().uv(0, 16).cuboid(0.0F, 10.0F, 0.0F, 16.0F, 16.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -12.0F, 2.0F, 0.0F, -0.5236F, 0.0F));
        ModelPartData right_wing = wings.addChild("right_wing", ModelPartBuilder.create().uv(0, 0).cuboid(-16.0F, 10.0F, 0.0F, 16.0F, 16.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -12.0F, 2.0F, 0.0F, 0.5236F, 0.0F));

        ModelPartData fox_tail = body.addChild("fox_tail", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, 7.0F, 5.0F, 4.0F, 4.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 11.0F, 1.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData demon_tail = body.addChild("demon_tail", ModelPartBuilder.create().uv(0, 10).cuboid(0.0F, 7.0F, 4.0F, 0.0F, 6.0F, 16.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 11.0F, 2.0F, -0.7854F, 0.0F, 0.0F));

        ModelPartData mermaid_tail = modelPartData.addChild("mermaid_tail", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 12.0F, 0.0F));
        ModelPartData mermaid_tail2 = mermaid_tail.addChild("tail2", ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, 0.0F, 0.0F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 12.0F, 0.0F));


        ModelPartData fox_ears = head.addChild("fox_ears", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        ModelPartData left_ear = fox_ears.addChild("left_ear", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -8.0F, 0.0F));
        ModelPartData left_ear_r1 = left_ear.addChild("left_ear_r1", ModelPartBuilder.create().uv(16, 0).cuboid(-3.0F, -6.0F, 1.0F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, 2.0F, -2.0F, 0.0F, 0.1745F, 0.0873F));
        ModelPartData right_ear = fox_ears.addChild("right_ear", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -8.0F, 0.0F));
        ModelPartData right_ear_r1 = right_ear.addChild("right_ear_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -6.0F, 1.0F, 8.0F, 8.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, 2.0F, -2.0F, 0.0F, -0.1745F, -0.0873F));


        ModelPartData demon_horns = head.addChild("demon_horns", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        ModelPartData left_horn = demon_horns.addChild("left_horn", ModelPartBuilder.create(), ModelTransform.pivot(-3.0F, -7.5F, -3.0F));
        ModelPartData left_horn_r1 = left_horn.addChild("left_horn_r1", ModelPartBuilder.create().uv(8, 0).cuboid(0.0F, -6.0F, -3.0F, 0.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, 1.0F, 0.0F, 0.1745F, -0.1745F, -0.0873F));
        ModelPartData right_horn = demon_horns.addChild("right_horn", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, -7.5F, -3.0F));
        ModelPartData right_horn_r1 = right_horn.addChild("right_horn_r1", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -6.0F, -3.0F, 0.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.5F, 1.0F, 0.0F, 0.1745F, 0.1745F, 0.0873F));


        for (PlayerLookModelManager manager : PlayerLookModelManagerRegistry.modelManagers) {
            manager.addTexturedModelData(modelData);
        }

        return TexturedModelData.of(modelData, 32, 32);
    }

    public void renderPart(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, List<String> root) {
        getPart(root).render(matrices, vertices, light, overlay);
    }

    public ModelPart getPart(List<String> root) {
        ModelPart final_part = null;
        for (String parent : root) {
            final_part = Objects.requireNonNullElse(final_part, this.root).getChild(parent);
        }
        return final_part;
    }
}
