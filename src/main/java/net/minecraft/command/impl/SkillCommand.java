package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fallout.Skills;
import net.minecraft.util.text.StringTextComponent;

import java.util.concurrent.CompletableFuture;

public class SkillCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("skill")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("action", StringArgumentType.string())
                                .suggests(SkillCommand::suggestActions)
                                .then(Commands.argument("skill", StringArgumentType.string())
                                        .suggests(SkillCommand::suggestSkills)
                                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0))
                                                .executes(context -> manageSkill(context,
                                                        StringArgumentType.getString(context, "action"),
                                                        StringArgumentType.getString(context, "skill"),
                                                        DoubleArgumentType.getDouble(context, "amount")))))));

        dispatcher.register(
                Commands.literal("addPoints")
                        .requires(source -> source.getEntity() != null
                                && source.getEntity() instanceof PlayerEntity
                                && ((PlayerEntity) source.getEntity()).isCreative())
                        .then(Commands.argument("points", DoubleArgumentType.doubleArg(0))
                                .executes(context -> addPoints(context,
                                        DoubleArgumentType.getDouble(context, "points")))));
    }


    private static CompletableFuture<Suggestions> suggestActions(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        builder.suggest("increase");
        builder.suggest("decrease");
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestSkills(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        for (Skills.SkillType skill : Skills.SkillType.values()) {
            builder.suggest(skill.name().toLowerCase());
        }
        return builder.buildFuture();
    }

    private static int manageSkill(CommandContext<CommandSource> context, String action, String skillName, double amount) {
        CommandSource source = context.getSource();
        Skills skills = ((PlayerEntity) source.getEntity()).skills;
        Skills.SkillType skillType;

        try {
            skillType = Skills.SkillType.valueOf(skillName.toUpperCase());
        } catch (IllegalArgumentException e) {
            source.sendFailure(new StringTextComponent("Invalid skill type: " + skillName));
            return 0;
        }

        if ("increase".equalsIgnoreCase(action)) {
            if (skills.skillPoints >= amount) {
                skills.increaseSkill(skillType, amount);
                skills.skillPoints -= amount;
                source.sendSuccess(new StringTextComponent("Increased " + skillType.name() + " by " + amount), true);
            } else {
                source.sendFailure(new StringTextComponent("Not enough skill points. You have " + skills.skillPoints + " points."));
                return 0;
            }
        } else if ("decrease".equalsIgnoreCase(action)) {
            double currentSkill = skills.getSkill(skillType);
            if (currentSkill >= amount) {
                skills.decreaseSkill(skillType, amount);
                skills.skillPoints += amount;
                source.sendSuccess(new StringTextComponent("Decreased " + skillType.name() + " by " + amount), true);
            } else {
                source.sendFailure(new StringTextComponent("Cannot decrease skill below 0. Current " + skillType.name() + " level: " + currentSkill));
                return 0;
            }
        } else {
            source.sendFailure(new StringTextComponent("Invalid action: " + action));
            return 0;
        }

        return 1;
    }

    private static int addPoints(CommandContext<CommandSource> context, double points) {
        CommandSource source = context.getSource();
        Skills skills = ((PlayerEntity) source.getEntity()).skills;

        if (points > 0) {
            skills.skillPoints += points;
            source.sendSuccess(new StringTextComponent("Added " + points + " skill points. You now have " + skills.skillPoints + " points."), true);
        } else {
            source.sendFailure(new StringTextComponent("Invalid points value: " + points));
            return 0;
        }

        return 1;
    }
}
