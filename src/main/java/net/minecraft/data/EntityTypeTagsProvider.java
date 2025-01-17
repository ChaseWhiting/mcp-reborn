package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class EntityTypeTagsProvider extends TagsProvider<EntityType<?>> {
   public EntityTypeTagsProvider(DataGenerator p_i50784_1_) {
      super(p_i50784_1_, Registry.ENTITY_TYPE);
   }

   protected void addTags() {
      this.tag(EntityTypeTags.SKELETONS).add(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON);
      this.tag(EntityTypeTags.RAIDERS).add(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH, EntityType.SHAMAN);
      this.tag(EntityTypeTags.BEEHIVE_INHABITORS).add(EntityType.BEE);
      this.tag(EntityTypeTags.ARROWS).add(EntityType.ARROW, EntityType.SPECTRAL_ARROW, EntityType.BONE_ARROW);
      this.tag(EntityTypeTags.IMPACT_PROJECTILES).addTag(EntityTypeTags.ARROWS).add(EntityType.SNOWBALL, EntityType.FIREBALL, EntityType.SMALL_FIREBALL, EntityType.EGG, EntityType.TRIDENT, EntityType.DRAGON_FIREBALL, EntityType.WITHER_SKULL, EntityType.FIREWORK_ROCKET);
   }

   protected Path getPath(ResourceLocation p_200431_1_) {
      return this.generator.getOutputFolder().resolve("data/" + p_200431_1_.getNamespace() + "/tags/entity_types/" + p_200431_1_.getPath() + ".json");
   }

   public String getName() {
      return "Entity Type Tags";
   }
}