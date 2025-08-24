package net.minecraft.item.equipment.trim;

import com.mojang.serialization.Codec;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ArmorTrim {

    public static final List<Item> TRIMMABLE_ARMOR = Util.make(new ArrayList<>(), list -> {
        for (Item item : Registry.ITEM) {
            if (item instanceof ArmorItem) {
                list.add(item);
            }
        }
    });

    public static final List<Item> TRIM_MATERIALS = Util.make(new ArrayList<>(), list -> {
        list.add(Items.QUARTZ);
        list.add(Items.IRON_INGOT);
        list.add(Items.NETHERITE_INGOT);
        list.add(Items.REDSTONE);
        list.add(Items.COPPER_INGOT);
        list.add(Items.GOLD_INGOT);
        list.add(Items.EMERALD);
        list.add(Items.DIAMOND);
        list.add(Items.LAPIS_LAZULI);
        list.add(Items.AMETHYST_SHARD);
        list.add(Items.RESIN_BRICK);
        list.add(Items.ECHO_SHARD);
        list.add(Items.ROSE_GOLD_INGOT);
    });

    public static <T> Codec<T> makeRegistryCodec(Registry<T> registry) {
        return ResourceLocation.CODEC.xmap(
                registry::get, // from ID to object
                registry::getKey // from object to ID
        );
    }

//    public static final Codec<ArmorTrim> CODEC = RecordCodecBuilder.create(instance ->
//            instance.group(
//                    TrimMaterial.CODEC.fieldOf("material").forGetter(ArmorTrim::getMaterial),
//                    TrimPattern.CODEC.fieldOf("pattern").forGetter(ArmorTrim::getPattern)
//            ).apply(instance, (material, pattern) -> new ArmorTrim(() -> material, () -> pattern)));



    private static final Logger LOGGER = LogManager.getLogger();
    public static final String TAG_TRIM_ID = "Trim";
    private static final ITextComponent UPGRADE_TITLE = new TranslationTextComponent(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.upgrade"))).withStyle(TextFormatting.GRAY);
    private final Supplier<TrimMaterial> material;
    private final Supplier<TrimPattern> pattern;
    public final boolean glow;
    private final Function<ArmorMaterial, ResourceLocation> innerTexture;
    private final Function<ArmorMaterial, ResourceLocation> outerTexture;

    public ArmorTrim(Supplier<TrimMaterial> holder, Supplier<TrimPattern> holder2, boolean glow) {
        this.material = holder;
        this.pattern = holder2;
        this.innerTexture = Util.memoize(armorMaterial -> {
            ResourceLocation resourceLocation = (holder2.get()).getAssetId();
            String string = this.getColorPaletteSuffix((ArmorMaterial)armorMaterial);
            return resourceLocation.withPath(string2 -> "trims/models/armor/" + string2 + "_leggings_" + string);
        });
        this.outerTexture = Util.memoize(armorMaterial -> {
            ResourceLocation resourceLocation = (holder2.get()).getAssetId();
            String string = this.getColorPaletteSuffix((ArmorMaterial)armorMaterial);
            return resourceLocation.withPath(string2 -> "trims/models/armor/" + string2 + "_" + string);
        });
        this.glow = glow;
    }

    public boolean glowing() {
        return glow || this.getMaterial() == Registry.TRIM_MATERIAL.get(TrimMaterials.SCULK);
    }

    public ArmorTrim(Supplier<TrimMaterial> holder, Supplier<TrimPattern> holder2) {
        this(holder, holder2, false);
    }

    public ArmorTrim(TrimMaterial material, TrimPattern pattern) {
        this(() -> material, () -> pattern, false);
    }

    public ArmorTrim(TrimMaterial material, TrimPattern pattern, boolean glow) {
        this(() -> material, () -> pattern, glow);
    }

    private String getColorPaletteSuffix(ArmorMaterial armorMaterial) {
        Map<ArmorMaterial, String> map = this.material.get().getOverrideArmorMaterials();
        if (armorMaterial != null && map.containsKey(armorMaterial)) {
            return map.get(armorMaterial);
        }
        return this.material.get().getAssetName();
    }

    public boolean hasPatternAndMaterial(Supplier<TrimPattern> holder, Supplier<TrimMaterial> holder2) {
        return holder == this.pattern && holder2 == this.material;
    }

    public boolean hasPatternAndMaterial(TrimPattern holder, TrimMaterial holder2) {
        return holder == this.pattern.get() && holder2 == this.material.get();
    }

    public Supplier<TrimPattern> pattern() {
        return this.pattern;
    }

    public Supplier<TrimMaterial> material() {
        return this.material;
    }

    public TrimPattern getPattern() {
        return this.pattern.get();
    }

    public TrimMaterial getMaterial() {
        return this.material.get();
    }

    public ResourceLocation innerTexture(ArmorMaterial armorMaterial) {
        return this.innerTexture.apply(armorMaterial);
    }

    public ResourceLocation outerTexture(ArmorMaterial armorMaterial) {
        return this.outerTexture.apply(armorMaterial);
    }

    public boolean equals(Object object) {
        if (!(object instanceof ArmorTrim)) {
            return false;
        }
        ArmorTrim armorTrim = (ArmorTrim)object;
        return armorTrim.pattern == this.pattern && armorTrim.material == this.material;
    }



    public static boolean setTrim(DynamicRegistries registry, ItemStack itemStack, ArmorTrim trim) {
        if (TRIMMABLE_ARMOR.contains(itemStack.getItem())) {
            CompoundNBT trimTag = new CompoundNBT();
            trimTag.putString("pattern", Registry.TRIM_PATTERN.getKey(trim.getPattern()).toString());
            trimTag.putString("material", Registry.TRIM_MATERIAL.getKey(trim.getMaterial()).toString());
            trimTag.putBoolean("glow", trim.glow);

            CompoundNBT tag = itemStack.getOrCreateTag();
            tag.put(TAG_TRIM_ID, trimTag);
            itemStack.setTag(tag);
            return true;
        }

        return false;
    }


    public static Optional<ArmorTrim> getTrim(DynamicRegistries dynamicRegistries, ItemStack itemStack) {
        if (TRIMMABLE_ARMOR.contains(itemStack.getItem()) && itemStack.hasTag() && itemStack.getTag().contains(TAG_TRIM_ID, 10)) {
            CompoundNBT trimTag = itemStack.getTag().getCompound(TAG_TRIM_ID);

            String patternId = trimTag.getString("pattern");
            String materialId = trimTag.getString("material");
            boolean glow = trimTag.getBoolean("glow");

            TrimPattern pattern = Registry.TRIM_PATTERN.get(new ResourceLocation(patternId));
            TrimMaterial material = Registry.TRIM_MATERIAL.get(new ResourceLocation(materialId));

            if (pattern != null && material != null) {
                return Optional.of(new ArmorTrim(material, pattern, glow));
            }
        }
        return Optional.empty();
    }



    public static void appendUpgradeHoverText(ItemStack itemStack, DynamicRegistries registryAccess, List<ITextComponent> list) {
        Optional<ArmorTrim> optional = ArmorTrim.getTrim(registryAccess, itemStack);
        if (optional.isPresent()) {
            ArmorTrim armorTrim = optional.get();
            list.add(UPGRADE_TITLE);
            list.add(new StringTextComponent(" ").append(armorTrim.pattern().get().copyWithStyle(armorTrim.material())));
            list.add(new StringTextComponent(" ").append(armorTrim.material().get().getDescription()));
            if (armorTrim.glowing()) {
                list.add(new StringTextComponent(" ").append(new TranslationTextComponent("trim.minecraft.glow")).withStyle(armorTrim.material.get().getDescription().getStyle()));
            }
        }
    }
}
