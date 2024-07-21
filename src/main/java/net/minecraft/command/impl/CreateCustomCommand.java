package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.EntityBuilder;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

public class CreateCustomCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("createcustom")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("entity", StringArgumentType.string())
                                .suggests(CreateCustomCommand::suggestEntityTypes)
                                .then(Commands.argument("health", FloatArgumentType.floatArg())
                                        .then(Commands.argument("name", StringArgumentType.string())
                                                .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                                        .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                                                .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                                        .then(Commands.argument("headItem", StringArgumentType.string())
                                                                                .then(Commands.argument("chestItem", StringArgumentType.string())
                                                                                        .then(Commands.argument("legsItem", StringArgumentType.string())
                                                                                                .then(Commands.argument("feetItem", StringArgumentType.string())
                                                                                                        .then(Commands.argument("mainHandItem", StringArgumentType.string())
                                                                                                                .then(Commands.argument("offHandItem", StringArgumentType.string())
                                                                                                                        .executes(context -> spawnCustomEntity(context,
                                                                                                                                StringArgumentType.getString(context, "entity"),
                                                                                                                                FloatArgumentType.getFloat(context, "health"),
                                                                                                                                StringArgumentType.getString(context, "name"),
                                                                                                                                DoubleArgumentType.getDouble(context, "x"),
                                                                                                                                DoubleArgumentType.getDouble(context, "y"),
                                                                                                                                DoubleArgumentType.getDouble(context, "z"),
                                                                                                                                StringArgumentType.getString(context, "headItem"),
                                                                                                                                StringArgumentType.getString(context, "chestItem"),
                                                                                                                                StringArgumentType.getString(context, "legsItem"),
                                                                                                                                StringArgumentType.getString(context, "feetItem"),
                                                                                                                                StringArgumentType.getString(context, "mainHandItem"),
                                                                                                                                StringArgumentType.getString(context, "offHandItem")
                                                                                                                        )))))))))))))));
    }

    private static CompletableFuture<Suggestions> suggestEntityTypes(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        StreamSupport.stream(Registry.ENTITY_TYPE.spliterator(), false)
                .map(EntityType::getRegistryName)
                .map(ResourceLocation::toString)
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    private static int spawnCustomEntity(CommandContext<CommandSource> context, String entityName, float health, String customName, double x, double y, double z, String headItem, String chestItem, String legsItem, String feetItem, String mainHandItem, String offHandItem) {
        CommandSource source = context.getSource();
        ServerWorld world = source.getLevel();

        ResourceLocation entityLocation = new ResourceLocation(entityName);
        EntityType<?> entityType = Registry.ENTITY_TYPE.getOptional(entityLocation).orElse(null);
        if (entityType == null) {
            source.sendFailure(new StringTextComponent("Entity type not found: " + entityName));
            return 0;
        }

        EntityBuilder<Mob, ServerWorld> builder = new EntityBuilder<>((EntityType<Mob>) entityType, world);

        builder.setPos(x, y, z)
                .setHealth(health)
                .setCustomName(customName)
                .setItem(EquipmentSlotType.HEAD, getItemFromName(headItem))
                .setItem(EquipmentSlotType.CHEST, getItemFromName(chestItem))
                .setItem(EquipmentSlotType.LEGS, getItemFromName(legsItem))
                .setItem(EquipmentSlotType.FEET, getItemFromName(feetItem))
                .setItem(EquipmentSlotType.MAINHAND, getItemFromName(mainHandItem))
                .setItem(EquipmentSlotType.OFFHAND, getItemFromName(offHandItem))
                .setAttribute(Attributes.MAX_HEALTH, 100.0)
                .addEffect(new EffectInstance(Effects.REGENERATION, 200, 1));

        Mob customEntity = builder.build();
        world.addFreshEntity(customEntity);

        source.sendSuccess(new StringTextComponent("Spawned custom entity: " + entityName), true);
        return 1;
    }

    private static Item getItemFromName(String itemName) {
        ResourceLocation itemLocation = new ResourceLocation(itemName);
        return Registry.ITEM.getOptional(itemLocation).orElse(Items.AIR);
    }
}
