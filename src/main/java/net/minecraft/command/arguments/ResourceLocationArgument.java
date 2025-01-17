package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.Advancement;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.loot.LootPredicateManager;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.structure.Structure;

public class ResourceLocationArgument implements ArgumentType<ResourceLocation> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_ADVANCEMENT = new DynamicCommandExceptionType((p_208676_0_) -> {
      return new TranslationTextComponent("advancement.advancementNotFound", p_208676_0_);
   });
   private static final DynamicCommandExceptionType ERROR_INVALID_FEATURE = new DynamicCommandExceptionType((p_208676_0_) -> {
      return new TranslationTextComponent("feature.invalid", p_208676_0_);
   });
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_RECIPE = new DynamicCommandExceptionType((p_208677_0_) -> {
      return new TranslationTextComponent("recipe.notFound", p_208677_0_);
   });
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_PREDICATE = new DynamicCommandExceptionType((p_208674_0_) -> {
      return new TranslationTextComponent("predicate.unknown", p_208674_0_);
   });
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_ATTRIBUTE = new DynamicCommandExceptionType((p_239091_0_) -> {
      return new TranslationTextComponent("attribute.unknown", p_239091_0_);
   });

   public static ResourceLocationArgument id() {
      return new ResourceLocationArgument();
   }

   public static Advancement getAdvancement(CommandContext<CommandSource> p_197198_0_, String p_197198_1_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = p_197198_0_.getArgument(p_197198_1_, ResourceLocation.class);
      Advancement advancement = p_197198_0_.getSource().getServer().getAdvancements().getAdvancement(resourcelocation);
      if (advancement == null) {
         throw ERROR_UNKNOWN_ADVANCEMENT.create(resourcelocation);
      } else {
         return advancement;
      }
   }

   public static IRecipe<?> getRecipe(CommandContext<CommandSource> p_197194_0_, String p_197194_1_) throws CommandSyntaxException {
      RecipeManager recipemanager = p_197194_0_.getSource().getServer().getRecipeManager();
      ResourceLocation resourcelocation = p_197194_0_.getArgument(p_197194_1_, ResourceLocation.class);
      return recipemanager.byKey(resourcelocation).orElseThrow(() -> {
         return ERROR_UNKNOWN_RECIPE.create(resourcelocation);
      });
   }

   public static ILootCondition getPredicate(CommandContext<CommandSource> p_228259_0_, String p_228259_1_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = p_228259_0_.getArgument(p_228259_1_, ResourceLocation.class);
      LootPredicateManager lootpredicatemanager = p_228259_0_.getSource().getServer().getPredicateManager();
      ILootCondition ilootcondition = lootpredicatemanager.get(resourcelocation);
      if (ilootcondition == null) {
         throw ERROR_UNKNOWN_PREDICATE.create(resourcelocation);
      } else {
         return ilootcondition;
      }
   }

   public static Attribute getAttribute(CommandContext<CommandSource> p_239094_0_, String p_239094_1_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = p_239094_0_.getArgument(p_239094_1_, ResourceLocation.class);
      return Registry.ATTRIBUTE.getOptional(resourcelocation).orElseThrow(() -> {
         return ERROR_UNKNOWN_ATTRIBUTE.create(resourcelocation);
      });
   }

   public static ResourceLocation getId(CommandContext<CommandSource> p_197195_0_, String p_197195_1_) {
      return p_197195_0_.getArgument(p_197195_1_, ResourceLocation.class);
   }

//   public static ConfiguredFeature<?, ?> getConfiguredFeature(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
//      ResourceLocation resourceLocation = context.getArgument(name, ResourceLocation.class);
//      return context.getSource().getLevel().structureFeatureManager().getConfiguredFeature(resourceLocation)
//              .orElseThrow(() -> ERROR_INVALID_FEATURE.create(resourceLocation));
//   }

   public static Structure getStructure(CommandContext<CommandSource> context, ResourceLocation key) {
      Structure structure = Registry.STRUCTURE_FEATURE.get(key);
      if (structure == null) {
         // Handle the case where the structure is not found
         throw new IllegalArgumentException("Structure not found for key: " + key);
      }
      return structure;
   }

   public ResourceLocation parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return ResourceLocation.read(p_parse_1_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}