package com.epicwolf.epicsorigins.item;

import com.epicwolf.epicsorigins.Epicsorigins;
import com.epicwolf.epicsorigins.networking.ModPackets;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayers;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Objects;


public class OrbOfEvolutionItem extends Item {

    public OrbOfEvolutionItem() {
        super(new Item.Settings().maxCount(1).rarity(Rarity.EPIC));
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (Origin.get(user).get(OriginLayers.getLayer(new Identifier(Origins.MODID, "origin"))).hasUpgrade()) {
            if (!world.isClient()) {
                ServerPlayerEntity player = (ServerPlayerEntity) user;
                Advancement advancement = Objects.requireNonNull(player.getServer()).getAdvancementLoader().get(Epicsorigins.identifier("end/draconic_evolution"));
                AdvancementProgress advancementProgress = player.getAdvancementTracker().getProgress(advancement);
                if (advancementProgress.isDone()) {
                    for (String criterion : advancementProgress.getObtainedCriteria()) {
                        player.getAdvancementTracker().revokeCriterion(advancement, criterion);
                    }
                }
                for (String criterion : advancementProgress.getUnobtainedCriteria()) {
                    player.getAdvancementTracker().grantCriterion(advancement, criterion);
                }
                ServerPlayNetworking.send((ServerPlayerEntity) user, ModPackets.OPEN_VIEW_ORIGIN_SCREEN, new PacketByteBuf(Unpooled.buffer()));
            }
            if (!user.isCreative()) {
                stack.decrement(1);
            }
            return TypedActionResult.consume(stack);
        }
        else {
            if (!world.isClient()) user.sendMessage(Text.translatable("message.epicsorigins.no_further_upgrades"));
            return TypedActionResult.fail(stack);
        }
    }
}
