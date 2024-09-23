package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class SetSeedCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher, boolean dedicated) {
      dispatcher.register(Commands.literal("setseed")
              .requires((source) -> !dedicated || source.hasPermission(2)) // Permission level 2 required
              .then(Commands.argument("seed", LongArgumentType.longArg()) // Argument for the seed value
                      .executes((context) -> {
                         long newSeed = LongArgumentType.getLong(context, "seed"); // Get the seed from the command argument
                         context.getSource().getLevel().setSeed(newSeed); // Set the world seed

                         ITextComponent successMessage = TextComponentUtils.wrapInSquareBrackets(
                                 (new StringTextComponent(String.valueOf(newSeed)))
                                         .withStyle((style) -> style.withColor(TextFormatting.GREEN)
                                                 .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(newSeed)))
                                                 .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.copy.click")))
                                                 .withInsertion(String.valueOf(newSeed))
                                         )
                         );

                         context.getSource().sendSuccess(new TranslationTextComponent("commands.setseed.success", successMessage), true);
                         return 1; // Return success
                      })
              )
      );
   }
}
