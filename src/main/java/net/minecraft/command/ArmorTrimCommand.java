package net.minecraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.TrimMaterial;
import net.minecraft.item.equipment.trim.TrimPattern;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

public class ArmorTrimCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("trim")
                        .requires(cs -> cs.hasPermission(2))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("item", ItemArgument.item())
                                        .then(Commands.argument("pattern", ResourceLocationArgument.id())
                                                .suggests(SuggestionProviders.TRIM_PATTERNS)
                                                .then(Commands.argument("material", ResourceLocationArgument.id())
                                                        .suggests(SuggestionProviders.TRIM_MATERIALS)
                                                        .executes(ctx -> giveTrimmedArmor(ctx, 1))
                                                        .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                                                .executes(ctx -> giveTrimmedArmor(ctx, IntegerArgumentType.getInteger(ctx, "count")))
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }

    private static int giveTrimmedArmor(CommandContext<CommandSource> ctx, int count) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgument.getPlayers(ctx, "targets");
        ItemInput itemInput = ItemArgument.getItem(ctx, "item");
        ResourceLocation patternRL = ResourceLocationArgument.getId(ctx, "pattern");
        ResourceLocation materialRL = ResourceLocationArgument.getId(ctx, "material");

        if (!ArmorTrim.TRIMMABLE_ARMOR.contains(itemInput.getItem())) {
            ctx.getSource().sendFailure(new StringTextComponent("Item is not a valid armor piece for trim!"));
            return 0;
        }

        TrimPattern pattern = Registry.TRIM_PATTERN.get(patternRL);
        TrimMaterial material = Registry.TRIM_MATERIAL.get(materialRL);

        if (pattern == null || material == null) {
            ctx.getSource().sendFailure(new StringTextComponent("Invalid trim pattern or material"));
            return 0;
        }

        for (ServerPlayerEntity player : targets) {
            int remaining = count;
            while (remaining > 0) {
                int batch = Math.min(itemInput.getItem().getMaxStackSize(), remaining);
                remaining -= batch;
                ItemStack stack = itemInput.createItemStack(batch, false);

                ArmorTrim.setTrim(null, stack, new ArmorTrim(material, pattern));

                boolean addedToInventory = player.inventory.add(stack);
                if (!addedToInventory) {
                    ItemEntity dropped = player.drop(stack, false);
                    if (dropped != null) {
                        dropped.setNoPickUpDelay();
                        dropped.setOwner(player.getUUID());
                    }
                }
            }
        }

        ctx.getSource().sendSuccess(
                new TranslationTextComponent("commands.armortrim.success", count, itemInput.createItemStack(count, false).getDisplayName(), targets.size()),
                true
        );
        return targets.size();
    }
}
