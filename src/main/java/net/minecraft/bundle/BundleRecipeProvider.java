package net.minecraft.bundle;


import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;

import net.minecraft.item.Items;

import java.nio.file.Path;

public class BundleRecipeProvider extends RecipeProvider {
   private final DataGenerator generator;
   public BundleRecipeProvider(DataGenerator p_248813_) {
      super(p_248813_);
      this.generator = p_248813_;
   }


//   public static void buildShapelessRecipes(RecipeProvider p_297760_) {
//      ShapedRecipeBuilder.shaped(Items.BUNDLE, 1).define('#', Items.LEATHER).define('-', Items.STRING).pattern("-#-").pattern("# #").pattern("###").unlockedBy("has_string", has(Items.STRING)).save((iFinishedRecipe) -> {
//
//         Path path = generator.getOutputFolder();
//            saveRecipe(iFinishedRecipe, iFinishedRecipe.serializeRecipe(), path.resolve("data/" + iFinishedRecipe.getId().getNamespace() + "/recipes/" + iFinishedRecipe.getId().getPath() + ".json"));
//            JsonObject jsonobject = iFinishedRecipe.serializeAdvancement();
//            if (jsonobject != null) {
//               saveAdvancement(iFinishedRecipe, jsonobject, path.resolve("data/" + iFinishedRecipe.getId().getNamespace() + "/advancements/" + iFinishedRecipe.getAdvancementId().getPath() + ".json"));
//            }
//      });
//   }
}