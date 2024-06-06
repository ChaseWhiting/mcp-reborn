package net.minecraft.tags;

import java.util.stream.Collectors;

public class TagCollectionManager {
   private static volatile ITagCollectionSupplier instance = ITagCollectionSupplier.of(ITagCollection.of(BlockTags.getWrappers().stream().collect(Collectors.toMap(ITag.INamedTag::getName, (p_242183_0_) -> {
      return p_242183_0_;
   }))), ITagCollection.of(ItemTags.getWrappers().stream().collect(Collectors.toMap(ITag.INamedTag::getName, (p_242182_0_) -> {
      return p_242182_0_;
   }))), ITagCollection.of(FluidTags.getWrappers().stream().collect(Collectors.toMap(ITag.INamedTag::getName, (p_242181_0_) -> {
      return p_242181_0_;
   }))), ITagCollection.of(EntityTypeTags.getWrappers().stream().collect(Collectors.toMap(ITag.INamedTag::getName, (p_242179_0_) -> {
      return p_242179_0_;
   }))));

   public static ITagCollectionSupplier getInstance() {
      return instance;
   }

   public static void bind(ITagCollectionSupplier p_242180_0_) {
      instance = p_242180_0_;
   }
}