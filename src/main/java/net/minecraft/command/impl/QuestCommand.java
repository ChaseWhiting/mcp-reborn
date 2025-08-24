package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.villager.data.quest.Quest;
import net.minecraft.entity.villager.data.quest.QuestTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class QuestCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("addquest")
                .requires((commandSource) -> commandSource.hasPermission(2))
                .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("quest", StringArgumentType.greedyString())
                                .suggests(QuestCommand::suggestQuests)
                                .executes((context) -> addQuest(context, EntityArgument.getPlayers(context, "targets"), StringArgumentType.getString(context, "quest"))))));
    }

    private static CompletableFuture<Suggestions> suggestQuests(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        return CompletableFuture.supplyAsync(() -> {
            QuestTypes.quests.stream()
                    .map(Quest::getName)
                    .forEach(builder::suggest);
            return builder.build();
        });
    }

    private static int addQuest(CommandContext<CommandSource> context, Collection<ServerPlayerEntity> targets, String questName) {
        int count = 0;
        Quest quest = QuestTypes.getQuestByName(questName);

        if (quest == null) {
            context.getSource().sendFailure(new TranslationTextComponent("commands.addquest.failed", questName));
            return 0;
        }

        quest.reset();
        quest.setCompleted(false);
        for(ItemStack item : QuestTypes.getQuestByName(questName).getRequiredItems()) {
            quest.addReward(item);
        }

        for (ServerPlayerEntity player : targets) {
            player.getQuestManager().removeQuest(quest);
            player.getQuestManager().addQuest(quest);
            player.sendMessage(new TranslationTextComponent("commands.addquest.success", quest.getName()), player.getUUID());
            System.out.println("Quest added with rewards: " + quest.getRewards()); // Debugging statement
            count++;
        }

        if (count == 1) {
            context.getSource().sendSuccess(new TranslationTextComponent("commands.addquest.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            context.getSource().sendSuccess(new TranslationTextComponent("commands.addquest.success.multiple", count), true);
        }

        return count;
    }

}
