package com.epicwolf.epicsorigins.client.gui;

import com.epicwolf.epicsorigins.networking.ModPackets;
import com.epicwolf.epicsorigins.power.AbstractLookPower;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;

public class PlayerLookScreen extends Screen {
    protected int x;
    protected int y;

    public PlayerLookScreen(Text title) {
        super(title);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.x = this.width / 2;
        this.y = this.height / 2;
        int size = 90-(7*this.client.options.getGuiScale().getValue());
        renderBackground(context);
        drawEntity(context, x, y+40, size, x + 51 - mouseX, y + 25 - mouseY, this.client.player);
        super.render(context, mouseX, mouseY, delta);
    }

    public static void drawEntity(DrawContext context, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        float f = (float)Math.atan(mouseX / 40.0F);
        float g = (float)Math.atan(mouseY / 40.0F);
        Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(g * 20.0F * (float) (Math.PI / 180.0));
        quaternionf.mul(quaternionf2);
        float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        entity.bodyYaw = 180.0F + f * 20.0F;
        entity.setYaw(180.0F + f * 40.0F);
        entity.setPitch(-g * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        drawEntity(context, x, y, size, quaternionf, quaternionf2, entity);
        entity.bodyYaw = h;
        entity.setYaw(i);
        entity.setPitch(j);
        entity.prevHeadYaw = k;
        entity.headYaw = l;
    }

    public static void drawEntity(DrawContext context, int x, int y, int size, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, LivingEntity entity) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 100.0);
        context.getMatrices().multiplyPositionMatrix(new Matrix4f().scaling(size, size, -size));
        context.getMatrices().multiply(quaternionf);
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        if (quaternionf2 != null) {
            quaternionf2.conjugate();
            entityRenderDispatcher.setRotation(quaternionf2);
        }

        entityRenderDispatcher.setRenderShadows(false);
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, context.getMatrices(), context.getVertexConsumers(), 15728880));
        context.draw();
        entityRenderDispatcher.setRenderShadows(true);
        context.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
    }

    @Override
    protected void init() {
        this.x = this.width / 2;
        this.y = this.height / 2;
        if (PowerHolderComponent.hasPower(this.client.player, AbstractLookPower.class)) {
            int x = 80;
            int y = -125;
            List<AbstractLookPower> powers = PowerHolderComponent.getPowers(this.client.player, AbstractLookPower.class);

            this.addDrawableChild(
                    ButtonWidget.builder(Text.translatable("ui.epicsorigins.look_screen.toggle_all_looks"), button -> usePowers(powers))
                            .dimensions(this.x+x, this.y+y, 140, 20)
                            .build()
            );
            if (powers.size() > 1) {
                y = y + 25;
                for (AbstractLookPower power : powers) {
                    this.addDrawableChild(
                            ButtonWidget.builder(Text.translatable("ui.epicsorigins.look_screen.toggle_look").append(Text.translatable(power.getModelType().toString())), button -> usePowers(List.of(power)))
                                    .dimensions(this.x + x, this.y + y, 140, 20)
                                    .build()
                    );
                    y = y + 25;
                }
            }
        }
    }
    @Override
    public boolean shouldPause() {
        return false;
    }

    private void usePowers(List<AbstractLookPower> powers) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(powers.size());
        boolean shouldRender = powers.get(0).isShouldRender();
        for (AbstractLookPower power : powers) {
            buf.writeIdentifier(power.getType().getIdentifier());
            power.setShouldRender(!shouldRender);
        }
        ClientPlayNetworking.send(ModPackets.TOGGLE_PLAYER_LOOK_POWER, buf);
    }
}
