package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KnowledgeBookItem extends Item {
   private static final Logger LOGGER = LogManager.getLogger();

   public KnowledgeBookItem(Item.Properties p_i48485_1_) {
      super(p_i48485_1_);
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
      CompoundNBT compoundnbt = itemstack.getTag();
      if (!p_77659_2_.abilities.instabuild) {
         p_77659_2_.setItemInHand(p_77659_3_, ItemStack.EMPTY);
      }

      if (compoundnbt != null && compoundnbt.contains("Recipes", 9)) {
         if (!p_77659_1_.isClientSide) {
            ListNBT listnbt = compoundnbt.getList("Recipes", 8);
            List<IRecipe<?>> list = Lists.newArrayList();
            RecipeManager recipemanager = p_77659_1_.getServer().getRecipeManager();

            for(int i = 0; i < listnbt.size(); ++i) {
               String s = listnbt.getString(i);
               Optional<? extends IRecipe<?>> optional = recipemanager.byKey(new ResourceLocation(s));
               if (!optional.isPresent()) {
                  LOGGER.error("Invalid recipe: {}", (Object)s);
                  return ActionResult.fail(itemstack);
               }

               list.add(optional.get());
            }

            p_77659_2_.awardRecipes(list);
            p_77659_2_.awardStat(Stats.ITEM_USED.get(this));
         }

         return ActionResult.sidedSuccess(itemstack, p_77659_1_.isClientSide());
      } else {
         LOGGER.error("Tag not valid: {}", (Object)compoundnbt);
         return ActionResult.fail(itemstack);
      }
   }

   public static void addRecipe(ItemStack knowledgeBook, String recipe) {
      if (knowledgeBook.getItem() == Items.KNOWLEDGE_BOOK) {
         CompoundNBT nbt = knowledgeBook.getOrCreateTag();

         // Check if the "Recipes" tag already exists
         if (nbt.contains("Recipes", 9)) {
            // Get the existing ListNBT of recipes
            ListNBT listNBT = nbt.getList("Recipes", 8);

            // Create a new StringNBT entry for the new recipe
            listNBT.add(StringNBT.valueOf(recipe));

            // Set the updated list back into the knowledge book NBT
            nbt.put("Recipes", listNBT);
         } else {
            // If no recipes list exists, create a new one
            ListNBT newListNBT = new ListNBT();

            // Add the recipe to the new list
            newListNBT.add(StringNBT.valueOf(recipe));

            // Add the list to the NBT under the "Recipes" tag
            nbt.put("Recipes", newListNBT);
         }
      }
   }

   public static void addRecipes(ItemStack knowledgeBook, String... recipes) {
      for (String recipe : recipes) {
         KnowledgeBookItem.addRecipe(knowledgeBook, recipe);
      }
   }
}