package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TargetCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("target")
                .requires((source) -> source.hasPermission(2))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("target", EntityArgument.entity())
                                .executes((context) -> {
                                    Collection<? extends Entity> entities = EntityArgument.getEntities(context, "targets");
                                    Entity target = EntityArgument.getEntity(context, "target");
                                    return targetEntity(context.getSource(), entities, target, null, false);
                                }))
                        .then(Commands.literal("self")
                                .executes((context) -> {
                                    Collection<? extends Entity> entities = EntityArgument.getEntities(context, "targets");
                                    return targetSelf(context.getSource(), null, true, entities);
                                })))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("targets2", EntityArgument.entities())
                                .executes((context) -> {
                                    Collection<? extends Entity> entities = EntityArgument.getEntities(context, "targets");
                                    Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets2");
                                    return targetEntity(context.getSource(), entities, null, targets, true);
                                })))
                .then(Commands.argument("target", EntityArgument.entity())
                        .then(Commands.literal("self")
                                .executes((context) -> {
                                    Entity target = EntityArgument.getEntity(context, "target");
                                    return targetSelf(context.getSource(), target, false, null);
                                })))
        );
    }

    private static int targetSelf(CommandSource source, @Nullable Entity entity, boolean multiple, @Nullable Collection<? extends Entity> multipleTarget) {
        if (!multiple) {
            if (!(entity instanceof Mob)) {
                source.sendFailure(new StringTextComponent("The entity is not a mob."));
                return 0;
            }

            Mob mob = (Mob) entity;
            try {
                mob.setTarget(mob);
                String formatted = String.format("Successfully set the target of %s to itself.", mob.getName().getString());
                source.sendSuccess(new StringTextComponent(formatted), true);
                return 1;
            } catch (Exception e) {
                Logger.getLogger(TargetCommand.class.getName()).warning("Failed to set target to self: " + e.getMessage());
                return 0;
            }
        } else {
            int mobTarget = 0;
            List<Mob> mobList = multipleTarget.stream()
                    .filter(Mob.class::isInstance)
                    .map(Mob.class::cast)
                    .collect(Collectors.toList());

            for (Mob mob : mobList) {
                try {
                    mob.setTarget(mob);
                    mobTarget++;
                } catch (Exception e) {
                    Logger.getLogger(TargetCommand.class.getName()).warning("Failed to set target to self for mob: " + e.getMessage());
                }
            }

            String formatted = String.format("Successfully set the target of %s mobs to themselves.", mobTarget);
            source.sendSuccess(new StringTextComponent(formatted), true);
            return 1;
        }
    }

    private static int targetEntity(CommandSource source, Collection<? extends Entity> entities, @Nullable Entity target, @Nullable Collection<? extends Entity> targets, boolean multipleTargets) throws CommandSyntaxException {
        if (target != null && !(target instanceof LivingEntity)) {
            source.sendFailure(new StringTextComponent("Target provided is not a living entity."));
            return 0;
        }
        int mobTarget = 0;

        List<Mob> entityList = entities.stream()
                .filter(Mob.class::isInstance)
                .map(Mob.class::cast)
                .collect(Collectors.toList());

        if (!multipleTargets && target != null) {
            mobTarget = setOne((LivingEntity) target, entityList);
            String formatted = String.format("Successfully set the target of %s mobs to %s", mobTarget, target.getName().getString());
            source.sendSuccess(new StringTextComponent(formatted), true);
        } else if (targets != null) {
            List<LivingEntity> validTargets = targets.stream()
                    .filter(LivingEntity.class::isInstance)
                    .map(LivingEntity.class::cast)
                    .collect(Collectors.toList());
            for (Mob mob : entityList) {
                validTargets = validTargets.stream().filter(entity -> entity != mob).collect(Collectors.toList());
                if (validTargets.isEmpty()) {
                    Logger.getLogger(TargetCommand.class.getName()).warning("No valid targets available for mob: " + mob.getName().getString());
                    continue;
                }
                Random random = mob.getRandom();
                int index = random.nextInt(validTargets.size());
                LivingEntity entity = validTargets.get(index);
                try {
                    mob.setTarget(entity);
                    mobTarget++;
                } catch (Exception e) {
                    Logger.getLogger(TargetCommand.class.getName()).warning("Failed to set target: " + e.getMessage());
                }
            }

            String formatted = String.format("Out of %s mobs, %s set their target to random ones in the specified list", entityList.size(), mobTarget);
            source.sendSuccess(new StringTextComponent(formatted), true);
        }
        return 1;
    }

    public static int setOne(LivingEntity target, List<Mob> entityList) {
        int mobTarget = 0;
        for (Mob entity : entityList) {
            try {
                entity.setTarget(target);
                mobTarget++;
            } catch (Exception e) {
                Logger.getLogger(TargetCommand.class.getName()).warning("Failed to set target for mob: " + e.getMessage());
            }
        }
        return mobTarget;
    }
}
