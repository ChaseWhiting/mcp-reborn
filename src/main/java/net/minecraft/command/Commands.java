package net.minecraft.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.command.impl.*;
import net.minecraft.command.impl.data.DataCommand;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.groovy.GroovyCommand;
import net.minecraft.network.play.server.SCommandListPacket;
import net.minecraft.test.TestCommand;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Commands {
   private static final Logger LOGGER = LogManager.getLogger();
   private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();

   public Commands(Commands.EnvironmentType p_i232148_1_) {
      AdvancementCommand.register(this.dispatcher);
      AttributeCommand.register(this.dispatcher);
      ExecuteCommand.register(this.dispatcher);
      BossBarCommand.register(this.dispatcher);
      ClearCommand.register(this.dispatcher);
      CloneCommand.register(this.dispatcher);
      DataCommand.register(this.dispatcher);
      DataPackCommand.register(this.dispatcher);
      DebugCommand.register(this.dispatcher);
      DefaultGameModeCommand.register(this.dispatcher);
      DifficultyCommand.register(this.dispatcher);
      EffectCommand.register(this.dispatcher);
      MeCommand.register(this.dispatcher);
      EnchantCommand.register(this.dispatcher);
      ExperienceCommand.register(this.dispatcher);
      FillCommand.register(this.dispatcher);
      ForceLoadCommand.register(this.dispatcher);
      FunctionCommand.register(this.dispatcher);
      GameModeCommand.register(this.dispatcher);
      GameRuleCommand.register(this.dispatcher);
      GiveCommand.register(this.dispatcher);
      HelpCommand.register(this.dispatcher);
      KickCommand.register(this.dispatcher);
      KillCommand.register(this.dispatcher);
      ListCommand.register(this.dispatcher);
      LocateCommand.register(this.dispatcher);
      LocateBiomeCommand.register(this.dispatcher);
      LootCommand.register(this.dispatcher);
      MessageCommand.register(this.dispatcher);
      ParticleCommand.register(this.dispatcher);
      PlaySoundCommand.register(this.dispatcher);
      ReloadCommand.register(this.dispatcher);
      RecipeCommand.register(this.dispatcher);
      ReplaceItemCommand.register(this.dispatcher);
      SayCommand.register(this.dispatcher);
      ScheduleCommand.register(this.dispatcher);
      ScoreboardCommand.register(this.dispatcher);
      SeedCommand.register(this.dispatcher, p_i232148_1_ != Commands.EnvironmentType.INTEGRATED);
      SetBlockCommand.register(this.dispatcher);
      SpawnPointCommand.register(this.dispatcher);
      SetWorldSpawnCommand.register(this.dispatcher);
      SpectateCommand.register(this.dispatcher);
      SpreadPlayersCommand.register(this.dispatcher);
      StopSoundCommand.register(this.dispatcher);
      SummonCommand.register(this.dispatcher);
      TagCommand.register(this.dispatcher);
      TeamCommand.register(this.dispatcher);
      TeamMsgCommand.register(this.dispatcher);
      TeleportCommand.register(this.dispatcher);
      TellRawCommand.register(this.dispatcher);
      TimeCommand.register(this.dispatcher);
      TitleCommand.register(this.dispatcher);
      TriggerCommand.register(this.dispatcher);
      WeatherCommand.register(this.dispatcher);
      WorldBorderCommand.register(this.dispatcher);
      RaidCommand.register(this.dispatcher);
      TestCommand.register(this.dispatcher);
      DrainCommand.register(this.dispatcher);
      FindBlockCommand.register(this.dispatcher);
      HealCommand.register(this.dispatcher);
      FleeCommand.register(this.dispatcher);
      InvasionCommand.register(this.dispatcher);
      GroovyCommand.register(this.dispatcher);
      FakePlayerCommand.register(this.dispatcher);
      QuestCommand.register(this.dispatcher);
//      if (SharedConstants.IS_RUNNING_IN_IDE) {
//         TestCommand.register(this.dispatcher);
//      }

      if (p_i232148_1_.includeDedicated) {
         BanIpCommand.register(this.dispatcher);
         BanListCommand.register(this.dispatcher);
         BanCommand.register(this.dispatcher);
         DeOpCommand.register(this.dispatcher);
         OpCommand.register(this.dispatcher);
         PardonCommand.register(this.dispatcher);
         PardonIpCommand.register(this.dispatcher);
         SaveAllCommand.register(this.dispatcher);
         SaveOffCommand.register(this.dispatcher);
         SaveOnCommand.register(this.dispatcher);
         SetIdleTimeoutCommand.register(this.dispatcher);
         StopCommand.register(this.dispatcher);
         WhitelistCommand.register(this.dispatcher);

      }

      if (p_i232148_1_.includeIntegrated) {
         PublishCommand.register(this.dispatcher);
      }

      this.dispatcher.findAmbiguities((p_201302_1_, p_201302_2_, p_201302_3_, p_201302_4_) -> {
         LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", this.dispatcher.getPath(p_201302_2_), this.dispatcher.getPath(p_201302_3_), p_201302_4_);
      });
      this.dispatcher.setConsumer((p_197058_0_, p_197058_1_, p_197058_2_) -> {
         p_197058_0_.getSource().onCommandComplete(p_197058_0_, p_197058_1_, p_197058_2_);
      });
   }

   public int performCommand(CommandSource p_197059_1_, String p_197059_2_) {
      StringReader stringreader = new StringReader(p_197059_2_);
      if (stringreader.canRead() && stringreader.peek() == '/') {
         stringreader.skip();
      }

      p_197059_1_.getServer().getProfiler().push(p_197059_2_);

      try {
         try {
            return this.dispatcher.execute(stringreader, p_197059_1_);
         } catch (CommandException commandexception) {
            p_197059_1_.sendFailure(commandexception.getComponent());
            return 0;
         } catch (CommandSyntaxException commandsyntaxexception) {
            p_197059_1_.sendFailure(TextComponentUtils.fromMessage(commandsyntaxexception.getRawMessage()));
            if (commandsyntaxexception.getInput() != null && commandsyntaxexception.getCursor() >= 0) {
               int j = Math.min(commandsyntaxexception.getInput().length(), commandsyntaxexception.getCursor());
               IFormattableTextComponent iformattabletextcomponent1 = (new StringTextComponent("")).withStyle(TextFormatting.GRAY).withStyle((p_211705_1_) -> {
                  return p_211705_1_.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, p_197059_2_));
               });
               if (j > 10) {
                  iformattabletextcomponent1.append("...");
               }

               iformattabletextcomponent1.append(commandsyntaxexception.getInput().substring(Math.max(0, j - 10), j));
               if (j < commandsyntaxexception.getInput().length()) {
                  ITextComponent itextcomponent = (new StringTextComponent(commandsyntaxexception.getInput().substring(j))).withStyle(new TextFormatting[]{TextFormatting.RED, TextFormatting.UNDERLINE});
                  iformattabletextcomponent1.append(itextcomponent);
               }

               iformattabletextcomponent1.append((new TranslationTextComponent("command.context.here")).withStyle(new TextFormatting[]{TextFormatting.RED, TextFormatting.ITALIC}));
               p_197059_1_.sendFailure(iformattabletextcomponent1);
            }
         } catch (Exception exception) {
            IFormattableTextComponent iformattabletextcomponent = new StringTextComponent(exception.getMessage() == null ? exception.getClass().getName() : exception.getMessage());
            if (LOGGER.isDebugEnabled()) {
               LOGGER.error("Command exception: {}", p_197059_2_, exception);
               StackTraceElement[] astacktraceelement = exception.getStackTrace();

               for(int i = 0; i < Math.min(astacktraceelement.length, 3); ++i) {
                  iformattabletextcomponent.append("\n\n").append(astacktraceelement[i].getMethodName()).append("\n ").append(astacktraceelement[i].getFileName()).append(":").append(String.valueOf(astacktraceelement[i].getLineNumber()));
               }
            }

            p_197059_1_.sendFailure((new TranslationTextComponent("command.failed")).withStyle((p_211704_1_) -> {
               return p_211704_1_.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, iformattabletextcomponent));
            }));
            if (SharedConstants.IS_RUNNING_IN_IDE) {
               p_197059_1_.sendFailure(new StringTextComponent(Util.describeError(exception)));
               LOGGER.error("'" + p_197059_2_ + "' threw an exception", (Throwable)exception);
            }

            return 0;
         }

         return 0;
      } finally {
         p_197059_1_.getServer().getProfiler().pop();
      }
   }

   public void sendCommands(ServerPlayerEntity p_197051_1_) {
      Map<CommandNode<CommandSource>, CommandNode<ISuggestionProvider>> map = Maps.newHashMap();
      RootCommandNode<ISuggestionProvider> rootcommandnode = new RootCommandNode<>();
      map.put(this.dispatcher.getRoot(), rootcommandnode);
      this.fillUsableCommands(this.dispatcher.getRoot(), rootcommandnode, p_197051_1_.createCommandSourceStack(), map);
      p_197051_1_.connection.send(new SCommandListPacket(rootcommandnode));
   }

   private void fillUsableCommands(CommandNode<CommandSource> p_197052_1_, CommandNode<ISuggestionProvider> p_197052_2_, CommandSource p_197052_3_, Map<CommandNode<CommandSource>, CommandNode<ISuggestionProvider>> p_197052_4_) {
      for(CommandNode<CommandSource> commandnode : p_197052_1_.getChildren()) {
         if (commandnode.canUse(p_197052_3_)) {
            ArgumentBuilder<ISuggestionProvider, ?> argumentbuilder = (ArgumentBuilder) commandnode.createBuilder();
            argumentbuilder.requires((p_197060_0_) -> {
               return true;
            });
            if (argumentbuilder.getCommand() != null) {
               argumentbuilder.executes((p_197053_0_) -> {
                  return 0;
               });
            }

            if (argumentbuilder instanceof RequiredArgumentBuilder) {
               RequiredArgumentBuilder<ISuggestionProvider, ?> requiredargumentbuilder = (RequiredArgumentBuilder)argumentbuilder;
               if (requiredargumentbuilder.getSuggestionsProvider() != null) {
                  requiredargumentbuilder.suggests(SuggestionProviders.safelySwap(requiredargumentbuilder.getSuggestionsProvider()));
               }
            }

            if (argumentbuilder.getRedirect() != null) {
               argumentbuilder.redirect(p_197052_4_.get(argumentbuilder.getRedirect()));
            }

            CommandNode<ISuggestionProvider> commandnode1 = argumentbuilder.build();
            p_197052_4_.put(commandnode, commandnode1);
            p_197052_2_.addChild(commandnode1);
            if (!commandnode.getChildren().isEmpty()) {
               this.fillUsableCommands(commandnode, commandnode1, p_197052_3_, p_197052_4_);
            }
         }
      }

   }

   public static LiteralArgumentBuilder<CommandSource> literal(String literalString) {
      return LiteralArgumentBuilder.literal(literalString);
   }

   public static <T> RequiredArgumentBuilder<CommandSource, T> argument(String argumentName, ArgumentType<T> type) {
      return RequiredArgumentBuilder.argument(argumentName, type);
   }

   public static Predicate<String> createValidator(Commands.IParser parser) {
      return (reader) -> {
         try {
            parser.parse(new StringReader(reader));
            return true;
         } catch (CommandSyntaxException commandsyntaxexception) {
            return false;
         }
      };
   }

   public CommandDispatcher<CommandSource> getDispatcher() {
      return this.dispatcher;
   }

   @Nullable
   public static <S> CommandSyntaxException getParseException(ParseResults<S> results) {
      if (!results.getReader().canRead()) {
         return null;
      } else if (results.getExceptions().size() == 1) {
         return results.getExceptions().values().iterator().next();
      } else {
         return results.getContext().getRange().isEmpty() ? CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(results.getReader()) : CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(results.getReader());
      }
   }

   public static void validate() {
      RootCommandNode<CommandSource> rootcommandnode = (new Commands(Commands.EnvironmentType.ALL)).getDispatcher().getRoot();
      Set<ArgumentType<?>> set = ArgumentTypes.findUsedArgumentTypes(rootcommandnode);
      Set<ArgumentType<?>> set1 = set.stream().filter((p_242987_0_) -> {
         return !ArgumentTypes.isTypeRegistered(p_242987_0_);
      }).collect(Collectors.toSet());
      if (!set1.isEmpty()) {
         LOGGER.warn("Missing type registration for following arguments:\n {}", set1.stream().map((p_242985_0_) -> {
            return "\t" + p_242985_0_;
         }).collect(Collectors.joining(",\n")));
         throw new IllegalStateException("Unregistered argument types");
      }
   }

   public static enum EnvironmentType {
      ALL(true, true),
      DEDICATED(false, true),
      INTEGRATED(true, false);

      private final boolean includeIntegrated;
      private final boolean includeDedicated;

      private EnvironmentType(boolean p_i232149_3_, boolean p_i232149_4_) {
         this.includeIntegrated = p_i232149_3_;
         this.includeDedicated = p_i232149_4_;
      }
   }

   @FunctionalInterface
   public interface IParser {
      void parse(StringReader p_parse_1_) throws CommandSyntaxException;
   }
}
