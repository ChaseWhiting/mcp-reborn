package net.minecraft.util.component.components;

public class ItemEnchantments {
//implements TooltipProvider
//    public static final ItemEnchantments EMPTY = new ItemEnchantments((Object2IntOpenHashMap<Enchantment>)new Object2IntOpenHashMap());
//    private static final Codec<Integer> LEVEL_CODEC = Codec.intRange((int)1, (int)255);
//    public static final Codec<ItemEnchantments> CODEC = Codec.unboundedMap(Enchantment.CODEC, LEVEL_CODEC).xmap(map -> new ItemEnchantments((Object2IntOpenHashMap<Enchantment>)new Object2IntOpenHashMap(map)), itemEnchantments -> itemEnchantments.enchantments);
//    public static final StreamCodec<RegistryPacketBuffer, ItemEnchantments> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.map(Object2IntOpenHashMap::new, Enchantment.STREAM_CODEC, ByteBufCodecs.VAR_INT), itemEnchantments -> itemEnchantments.enchantments, ItemEnchantments::new);
//    final Object2IntOpenHashMap<Enchantment> enchantments;
//
//    ItemEnchantments(Object2IntOpenHashMap<Enchantment> object2IntOpenHashMap) {
//        this.enchantments = object2IntOpenHashMap;
//        for (Object2IntMap.Entry entry : object2IntOpenHashMap.object2IntEntrySet()) {
//            int n = entry.getIntValue();
//            if (n >= 0 && n <= 255) continue;
//            throw new IllegalArgumentException("Enchantment " + String.valueOf(entry.getKey()) + " has invalid level " + n);
//        }
//    }
//
//    public int getLevel(Enchantment holder) {
//        return this.enchantments.getInt(holder);
//    }
//
//    @Override
//    public void addToTooltip(Item.TooltipContext tooltipContext, Consumer<ITextComponent> consumer, TooltipFlag tooltipFlag, DataComponentGetter dataComponentGetter) {
//        HolderLookup.Provider provider = tooltipContext.registries();
//        HolderSet<Enchantment> holderSet = ItemEnchantments.getTagOrEmpty(provider, Registries.ENCHANTMENT, EnchantmentTags.TOOLTIP_ORDER);
//        for (Holder holder : holderSet) {
//            int n = this.enchantments.getInt((Object)holder);
//            if (n <= 0) continue;
//            consumer.accept(Enchantment.getFullname(holder, n));
//        }
//        for (Object2IntMap.Entry entry : this.enchantments.object2IntEntrySet()) {
//            Holder holder = (Holder)entry.getKey();
//            if (holderSet.contains(holder)) continue;
//            consumer.accept(Enchantment.getFullname((Holder)entry.getKey(), entry.getIntValue()));
//        }
//    }
//
//    private static <T> HolderSet<T> getTagOrEmpty(@Nullable HolderLookup.Provider provider, ResourceKey<Registry<T>> resourceKey, TagKey<T> tagKey) {
//        Optional<HolderSet.Named<T>> optional;
//        if (provider != null && (optional = provider.lookupOrThrow(resourceKey).get(tagKey)).isPresent()) {
//            return optional.get();
//        }
//        return HolderSet.direct(new Holder[0]);
//    }
//
//    public Set<Holder<Enchantment>> keySet() {
//        return Collections.unmodifiableSet(this.enchantments.keySet());
//    }
//
//    public Set<Object2IntMap.Entry<Holder<Enchantment>>> entrySet() {
//        return Collections.unmodifiableSet(this.enchantments.object2IntEntrySet());
//    }
//
//    public int size() {
//        return this.enchantments.size();
//    }
//
//    public boolean isEmpty() {
//        return this.enchantments.isEmpty();
//    }
//
//    public boolean equals(Object object) {
//        if (this == object) {
//            return true;
//        }
//        if (object instanceof ItemEnchantments) {
//            ItemEnchantments itemEnchantments = (ItemEnchantments)object;
//            return this.enchantments.equals(itemEnchantments.enchantments);
//        }
//        return false;
//    }
//
//    public int hashCode() {
//        return this.enchantments.hashCode();
//    }
//
//    public String toString() {
//        return "ItemEnchantments{enchantments=" + String.valueOf(this.enchantments) + "}";
//    }
//
//    public static class Mutable {
//        private final Object2IntOpenHashMap<Enchantment> enchantments = new Object2IntOpenHashMap();
//
//        public Mutable(ItemEnchantments itemEnchantments) {
//            this.enchantments.putAll(itemEnchantments.enchantments);
//        }
//
//        public void set(Enchantment holder, int n) {
//            if (n <= 0) {
//                this.enchantments.removeInt(holder);
//            } else {
//                this.enchantments.put(holder, Math.min(n, 255));
//            }
//        }
//
//        public void upgrade(Enchantment holder, int n) {
//            if (n > 0) {
//                this.enchantments.merge(holder, Math.min(n, 255), Integer::max);
//            }
//        }
//
//        public void removeIf(Predicate<Enchantment> predicate) {
//            this.enchantments.keySet().removeIf(predicate);
//        }
//
//        public int getLevel(Enchantment holder) {
//            return this.enchantments.getOrDefault(holder, 0);
//        }
//
//        public Set<Enchantment> keySet() {
//            return this.enchantments.keySet();
//        }
//
//        public ItemEnchantments toImmutable() {
//            return new ItemEnchantments(this.enchantments);
//        }
//    }
}
