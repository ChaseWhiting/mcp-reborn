package net.minecraft.command.impl;


import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.util.text.StringTextComponent;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static net.minecraft.command.CommandSource.feedbackTypes.SUCCESS;
import static net.minecraft.command.CommandSource.feedbackTypes.FAILURE;


public class HealCommand {
    private static final SimpleCommandExceptionType ENTITY_NOT_FOUND = new SimpleCommandExceptionType(new StringTextComponent("Entity not found"));
    private static final SimpleCommandExceptionType EXCEEDS_MAX_HEALTH = new SimpleCommandExceptionType(new StringTextComponent("Healing amount exceeds maximum health"));


    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("heal")
                        .requires(cs -> cs.hasPermission(2))
                        .executes(context -> healEntity(context.getSource(), context.getSource().getEntity(), -1))
                        .then(Commands.argument("target", EntityArgument.entity())
                                .executes(context -> healEntity(context.getSource(), EntityArgument.getEntity(context, "target"), -1))
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                        .executes(context -> healEntity(context.getSource(), EntityArgument.getEntity(context, "target"), IntegerArgumentType.getInteger(context, "amount")))
                                )
                        )
        );
    }

    private static int healEntity(CommandSource source, Entity entity, int amount) throws CommandSyntaxException {
        if (!(entity instanceof LivingEntity)) {
            throw ENTITY_NOT_FOUND.create();
        }
        LivingEntity livingEntity = (LivingEntity) entity;
        if (amount < 0) {  // Heal to full health
            livingEntity.setHealth(livingEntity.getMaxHealth());
            if(livingEntity instanceof PlayerEntity) {
                ((PlayerEntity)livingEntity).getFoodData().setFoodLevel(20);
                ((PlayerEntity)livingEntity).getFoodData().setSaturation(10);
            }
            source.sendFeedback(SUCCESS, new StringTextComponent("Healed " + entity.getName().getString() + " to full health."), false);
        } else {
            float newHealth = livingEntity.getHealth() + amount;
            if (newHealth > livingEntity.getMaxHealth()) {
                livingEntity.setHealth(livingEntity.getMaxHealth());
                source.sendFeedback(SUCCESS, new StringTextComponent("Set the health of " + entity.getName().getString() + " to max health (" + livingEntity.getMaxHealth() + ")."), false);
            } else {
                livingEntity.setHealth(newHealth);
                source.sendFeedback(SUCCESS, new StringTextComponent("Added " + amount + " health to " + entity.getName().getString() + "."), false);
            }
            if (livingEntity instanceof PlayerEntity) {
                ((PlayerEntity)livingEntity).getFoodData().setFoodLevel(20);
                ((PlayerEntity)livingEntity).getFoodData().setSaturation(10);
            }
        }
        return 1;
    }
}
