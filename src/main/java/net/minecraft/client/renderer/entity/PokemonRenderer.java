package net.minecraft.client.renderer.entity;

import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.pokemon.entity.Pokemon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PokemonRenderer<T extends Pokemon> extends MobRenderer<T, HierarchicalModel<T>> {
   private final ResourceLocation REGULAR_POKEMON;
   private final ResourceLocation SHINY;

   public PokemonRenderer(EntityRendererManager manager, HierarchicalModel<T> model, String pokemonName, float shadowRadius) {
      super(manager, model, shadowRadius);
      this.REGULAR_POKEMON = new ResourceLocation("textures/entity/pokemon/" + pokemonName + ".png");
      this.SHINY = new ResourceLocation("textures/entity/pokemon/" + pokemonName + "_shiny.png");
   }

   public ResourceLocation getTextureLocation(Pokemon pokemon) {
      return pokemon.isShiny() ? SHINY : REGULAR_POKEMON;
   }
}