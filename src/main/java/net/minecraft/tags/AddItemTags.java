package net.minecraft.tags;

import net.minecraft.item.HarnessItem;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.Map;

public class AddItemTags {

    public static void addItemTags(Map<ResourceLocation, ITag.Builder> itemTagBuilders) {
        // --- SWORDS ---
        ResourceLocation swords = new ResourceLocation("minecraft", "swords");
        ITag.Builder swordsBuilder = ITag.Builder.tag();
        swordsBuilder.addItem(Items.WITHER_BONE_CUTLASS).addItem(Items.ROSE_GOLD_SWORD).addItem(Registry.ITEM.getKey(Items.WOODEN_SWORD), "hardcoded").addItem(Registry.ITEM.getKey(Items.STONE_SWORD), "hardcoded").addItem(Registry.ITEM.getKey(Items.IRON_SWORD), "hardcoded").addItem(Registry.ITEM.getKey(Items.GOLDEN_SWORD), "hardcoded").addItem(Registry.ITEM.getKey(Items.DIAMOND_SWORD), "hardcoded").addItem(Registry.ITEM.getKey(Items.NETHERITE_SWORD), "hardcoded");
        itemTagBuilders.put(swords, swordsBuilder);

        // --- PICKAXES ---
        ResourceLocation pickaxes = new ResourceLocation("minecraft", "pickaxes");
        ITag.Builder pickaxesBuilder = ITag.Builder.tag();
        pickaxesBuilder.addItem(Registry.ITEM.getKey(Items.WOODEN_PICKAXE));
        pickaxesBuilder.addItem(Registry.ITEM.getKey(Items.STONE_PICKAXE));
        pickaxesBuilder.addItem(Registry.ITEM.getKey(Items.IRON_PICKAXE));
        pickaxesBuilder.addItem(Registry.ITEM.getKey(Items.GOLDEN_PICKAXE));
        pickaxesBuilder.addItem(Registry.ITEM.getKey(Items.DIAMOND_PICKAXE));
        pickaxesBuilder.addItem(Registry.ITEM.getKey(Items.NETHERITE_PICKAXE));
        pickaxesBuilder.addItem(Items.ROSE_GOLD_PICKAXE);
        itemTagBuilders.put(pickaxes, pickaxesBuilder);

        // --- AXES ---
        ResourceLocation axes = new ResourceLocation("minecraft", "axes");
        ITag.Builder axesBuilder = ITag.Builder.tag();
        axesBuilder.addItem(Registry.ITEM.getKey(Items.WOODEN_AXE), "hardcoded");
        axesBuilder.addItem(Registry.ITEM.getKey(Items.STONE_AXE), "hardcoded");
        axesBuilder.addItem(Registry.ITEM.getKey(Items.IRON_AXE), "hardcoded");
        axesBuilder.addItem(Registry.ITEM.getKey(Items.GOLDEN_AXE), "hardcoded");
        axesBuilder.addItem(Registry.ITEM.getKey(Items.DIAMOND_AXE), "hardcoded");
        axesBuilder.addItem(Registry.ITEM.getKey(Items.NETHERITE_AXE), "hardcoded");
        axesBuilder.addItem(Items.ROSE_GOLD_AXE);
        itemTagBuilders.put(axes, axesBuilder);

        // --- SHOVELS ---
        ResourceLocation shovels = new ResourceLocation("minecraft", "shovels");
        ITag.Builder shovelsBuilder = ITag.Builder.tag();
        shovelsBuilder.addItem(Registry.ITEM.getKey(Items.WOODEN_SHOVEL), "hardcoded");
        shovelsBuilder.addItem(Registry.ITEM.getKey(Items.STONE_SHOVEL), "hardcoded");
        shovelsBuilder.addItem(Registry.ITEM.getKey(Items.IRON_SHOVEL), "hardcoded");
        shovelsBuilder.addItem(Registry.ITEM.getKey(Items.GOLDEN_SHOVEL), "hardcoded");
        shovelsBuilder.addItem(Registry.ITEM.getKey(Items.DIAMOND_SHOVEL), "hardcoded");
        shovelsBuilder.addItem(Registry.ITEM.getKey(Items.NETHERITE_SHOVEL), "hardcoded");
        shovelsBuilder.addItem(Items.ROSE_GOLD_SHOVEL);
        itemTagBuilders.put(shovels, shovelsBuilder);

        // --- HOES ---
        ResourceLocation hoes = new ResourceLocation("minecraft", "hoes");
        ITag.Builder hoesBuilder = ITag.Builder.tag();
        hoesBuilder.addItem(Registry.ITEM.getKey(Items.WOODEN_HOE), "hardcoded");
        hoesBuilder.addItem(Registry.ITEM.getKey(Items.STONE_HOE), "hardcoded");
        hoesBuilder.addItem(Registry.ITEM.getKey(Items.IRON_HOE), "hardcoded");
        hoesBuilder.addItem(Registry.ITEM.getKey(Items.GOLDEN_HOE), "hardcoded");
        hoesBuilder.addItem(Registry.ITEM.getKey(Items.DIAMOND_HOE), "hardcoded");
        hoesBuilder.addItem(Registry.ITEM.getKey(Items.NETHERITE_HOE), "hardcoded");
        hoesBuilder.addItem(Items.ROSE_GOLD_HOE);
        itemTagBuilders.put(hoes, hoesBuilder);

        // --- DESTROYS_DECORATED_POTS (empty as example) ---
        ResourceLocation destroysPots = new ResourceLocation("minecraft", "destroys_decorated_pots");
        ITag.Builder destroysPotsBuilder = ITag.Builder.tag();
        // Add items to destroysPotsBuilder as needed
        destroysPotsBuilder.addTag(new ResourceLocation("minecraft", "swords"), "hardcoded");
        destroysPotsBuilder.addTag(new ResourceLocation("minecraft", "axes"), "hardcoded");
        destroysPotsBuilder.addTag(new ResourceLocation("minecraft", "pickaxes"), "hardcoded");
        destroysPotsBuilder.addTag(new ResourceLocation("minecraft", "shovels"), "hardcoded");
        destroysPotsBuilder.addTag(new ResourceLocation("minecraft", "hoes"), "hardcoded");
        destroysPotsBuilder.addItem(Registry.ITEM.getKey(Items.TRIDENT), "hardcoded");
        itemTagBuilders.put(destroysPots, destroysPotsBuilder);

        itemTagBuilders.get(new ResourceLocation("minecraft", "planks")).addItem(new ResourceLocation("minecraft", "pale_oak_planks"));


        //todo add the rest


        ITag.Builder builder = ITag.Builder.tag();
        for (HarnessItem harnessItem : Registry.ITEM.stream().filter(item -> item instanceof HarnessItem).map(item -> (HarnessItem) item).toList()) {
            builder = builder.addItem(harnessItem);
        }
        itemTagBuilders.put(new ResourceLocation("minecraft", "harnesses"), builder);

        itemTagBuilders.get(new ResourceLocation("piglin_loved")).addItem(Items.COPPER_INGOT).addItem(Items.RAW_GOLD);

        itemTagBuilders.get(new ResourceLocation("minecraft", "rails")).remove(new ResourceLocation("minecraft", "powered_rail")).addItem(Items.GOLDEN_POWERED_RAIL);

    }
}
