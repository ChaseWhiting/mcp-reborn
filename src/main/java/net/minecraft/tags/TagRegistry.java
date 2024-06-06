package net.minecraft.tags;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TagRegistry<T> {
   private ITagCollection<T> source = ITagCollection.empty();
   private final List<TagRegistry.NamedTag<T>> wrappers = Lists.newArrayList();
   private final Function<ITagCollectionSupplier, ITagCollection<T>> collectionGetter;

   public TagRegistry(Function<ITagCollectionSupplier, ITagCollection<T>> p_i241894_1_) {
      this.collectionGetter = p_i241894_1_;
   }

   public ITag.INamedTag<T> bind(String p_232937_1_) {
      TagRegistry.NamedTag<T> namedtag = new TagRegistry.NamedTag<>(new ResourceLocation(p_232937_1_));
      this.wrappers.add(namedtag);
      return namedtag;
   }

   @OnlyIn(Dist.CLIENT)
   public void resetToEmpty() {
      this.source = ITagCollection.empty();
      ITag<T> itag = Tag.empty();
      this.wrappers.forEach((p_232933_1_) -> {
         p_232933_1_.rebind((p_232934_1_) -> {
            return itag;
         });
      });
   }

   public void reset(ITagCollectionSupplier p_242188_1_) {
      ITagCollection<T> itagcollection = this.collectionGetter.apply(p_242188_1_);
      this.source = itagcollection;
      this.wrappers.forEach((p_232936_1_) -> {
         p_232936_1_.rebind(itagcollection::getTag);
      });
   }

   public ITagCollection<T> getAllTags() {
      return this.source;
   }

   public List<? extends ITag.INamedTag<T>> getWrappers() {
      return this.wrappers;
   }

   public Set<ResourceLocation> getMissingTags(ITagCollectionSupplier p_242189_1_) {
      ITagCollection<T> itagcollection = this.collectionGetter.apply(p_242189_1_);
      Set<ResourceLocation> set = this.wrappers.stream().map(TagRegistry.NamedTag::getName).collect(Collectors.toSet());
      ImmutableSet<ResourceLocation> immutableset = ImmutableSet.copyOf(itagcollection.getAvailableTags());
      return Sets.difference(set, immutableset);
   }

   static class NamedTag<T> implements ITag.INamedTag<T> {
      @Nullable
      private ITag<T> tag;
      protected final ResourceLocation name;

      private NamedTag(ResourceLocation p_i231430_1_) {
         this.name = p_i231430_1_;
      }

      public ResourceLocation getName() {
         return this.name;
      }

      private ITag<T> resolve() {
         if (this.tag == null) {
            throw new IllegalStateException("Tag " + this.name + " used before it was bound");
         } else {
            return this.tag;
         }
      }

      void rebind(Function<ResourceLocation, ITag<T>> p_232943_1_) {
         this.tag = p_232943_1_.apply(this.name);
      }

      public boolean contains(T p_230235_1_) {
         return this.resolve().contains(p_230235_1_);
      }

      public List<T> getValues() {
         return this.resolve().getValues();
      }
   }
}