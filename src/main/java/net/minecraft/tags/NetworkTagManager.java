package net.minecraft.tags;

import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import net.minecraft.block.*;
import net.minecraft.block.family.WoodFamilies;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.HarnessItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class NetworkTagManager implements IFutureReloadListener {
   private final TagCollectionReader<Block> blocks = new TagCollectionReader<>(Registry.BLOCK::getOptional, "tags/blocks", "block");
   private final TagCollectionReader<Item> items = new TagCollectionReader<>(Registry.ITEM::getOptional, "tags/items", "item");
   private final TagCollectionReader<Fluid> fluids = new TagCollectionReader<>(Registry.FLUID::getOptional, "tags/fluids", "fluid");
   private final TagCollectionReader<EntityType<?>> entityTypes = new TagCollectionReader<>(Registry.ENTITY_TYPE::getOptional, "tags/entity_types", "entity_type");
   private ITagCollectionSupplier tags = ITagCollectionSupplier.EMPTY;

   public ITagCollectionSupplier getTags() {
      return this.tags;
   }

   public CompletableFuture<Void> reload(IFutureReloadListener.IStage p_215226_1_, IResourceManager p_215226_2_, IProfiler p_215226_3_, IProfiler p_215226_4_, Executor p_215226_5_, Executor p_215226_6_) {
      CompletableFuture<Map<ResourceLocation, ITag.Builder>> completablefuture = this.blocks.prepare(p_215226_2_, p_215226_5_);
      CompletableFuture<Map<ResourceLocation, ITag.Builder>> completablefuture1 = this.items.prepare(p_215226_2_, p_215226_5_);
      CompletableFuture<Map<ResourceLocation, ITag.Builder>> completablefuture2 = this.fluids.prepare(p_215226_2_, p_215226_5_);
      CompletableFuture<Map<ResourceLocation, ITag.Builder>> completablefuture3 = this.entityTypes.prepare(p_215226_2_, p_215226_5_);



      return CompletableFuture.allOf(completablefuture, completablefuture1, completablefuture2, completablefuture3).thenCompose(p_215226_1_::wait).thenAcceptAsync((p_232979_5_) -> {
         Map<ResourceLocation, ITag.Builder> itemTagBuilders = completablefuture1.join();
         Map<ResourceLocation, ITag.Builder> blockTagBuilders = completablefuture.join();
         Map<ResourceLocation, ITag.Builder> entityTagBuilders = completablefuture3.join();

// Inject your hardcoded tags
         addHardcodedItemTags(itemTagBuilders);
         addHardcodedBlockTags(blockTagBuilders);
         addHardcodedEntityTags(entityTagBuilders);
// Now load the tag collection from builders
         ITagCollection<Item> itagcollection1 = this.items.load(itemTagBuilders);
         ITagCollection<Block> itagcollection = this.blocks.load(blockTagBuilders);
         ITagCollection<Fluid> itagcollection2 = this.fluids.load(completablefuture2.join());


         ITagCollection<EntityType<?>> itagcollection3 = this.entityTypes.load(entityTagBuilders);

         ITagCollectionSupplier itagcollectionsupplier = ITagCollectionSupplier.of(itagcollection, itagcollection1, itagcollection2, itagcollection3);
         Multimap<ResourceLocation, ResourceLocation> multimap = TagRegistryManager.getAllMissingTags(itagcollectionsupplier);
         if (!multimap.isEmpty()) {
            throw new IllegalStateException("Missing required tags: " + (String)multimap.entries().stream().map((p_232978_0_) -> {
               return p_232978_0_.getKey() + ":" + p_232978_0_.getValue();
            }).sorted().collect(Collectors.joining(",")));
         } else {
            TagCollectionManager.bind(itagcollectionsupplier);
            this.tags = itagcollectionsupplier;
         }
      }, p_215226_6_);
   }

   private void addHardcodedItemTags(Map<ResourceLocation, ITag.Builder> itemTagBuilders) {
      AddItemTags.addItemTags(itemTagBuilders);
   }

   private void addHardcodedBlockTags(Map<ResourceLocation, ITag.Builder> blockTagBuilders) {
      AddBlockTags.addBlockTags(blockTagBuilders);
   }

   private void addHardcodedEntityTags(Map<ResourceLocation, ITag.Builder> entityTagBuilders) {
      AddEntityTags.addEntityTags(entityTagBuilders);
   }

}