package net.minecraft.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.equipment.trim.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

public class SpawnArmorTrimsCommand {
    private static final Map<Pair<ArmorMaterial, EquipmentSlotType>, Item> MATERIAL_AND_SLOT_TO_ITEM = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(Pair.of(ArmorMaterial.CHAIN, EquipmentSlotType.HEAD), Items.CHAINMAIL_HELMET);
        hashMap.put(Pair.of(ArmorMaterial.CHAIN, EquipmentSlotType.CHEST), Items.CHAINMAIL_CHESTPLATE);
        hashMap.put(Pair.of(ArmorMaterial.CHAIN, EquipmentSlotType.LEGS), Items.CHAINMAIL_LEGGINGS);
        hashMap.put(Pair.of(ArmorMaterial.CHAIN, EquipmentSlotType.FEET), Items.CHAINMAIL_BOOTS);
        hashMap.put(Pair.of(ArmorMaterial.IRON, (EquipmentSlotType.HEAD)), Items.IRON_HELMET);
        hashMap.put(Pair.of(ArmorMaterial.IRON, (EquipmentSlotType.CHEST)), Items.IRON_CHESTPLATE);
        hashMap.put(Pair.of(ArmorMaterial.IRON, (EquipmentSlotType.LEGS)), Items.IRON_LEGGINGS);
        hashMap.put(Pair.of(ArmorMaterial.IRON, (EquipmentSlotType.FEET)), Items.IRON_BOOTS);
        hashMap.put(Pair.of(ArmorMaterial.GOLD, (EquipmentSlotType.HEAD)), Items.GOLDEN_HELMET);
        hashMap.put(Pair.of(ArmorMaterial.GOLD, (EquipmentSlotType.CHEST)), Items.GOLDEN_CHESTPLATE);
        hashMap.put(Pair.of(ArmorMaterial.GOLD, (EquipmentSlotType.LEGS)), Items.GOLDEN_LEGGINGS);
        hashMap.put(Pair.of(ArmorMaterial.GOLD, (EquipmentSlotType.FEET)), Items.GOLDEN_BOOTS);
        hashMap.put(Pair.of(ArmorMaterial.NETHERITE, (EquipmentSlotType.HEAD)), Items.NETHERITE_HELMET);
        hashMap.put(Pair.of(ArmorMaterial.NETHERITE, (EquipmentSlotType.CHEST)), Items.NETHERITE_CHESTPLATE);
        hashMap.put(Pair.of(ArmorMaterial.NETHERITE, (EquipmentSlotType.LEGS)), Items.NETHERITE_LEGGINGS);
        hashMap.put(Pair.of(ArmorMaterial.NETHERITE, (EquipmentSlotType.FEET)), Items.NETHERITE_BOOTS);
        hashMap.put(Pair.of(ArmorMaterial.DIAMOND, (EquipmentSlotType.HEAD)), Items.DIAMOND_HELMET);
        hashMap.put(Pair.of(ArmorMaterial.DIAMOND, (EquipmentSlotType.CHEST)), Items.DIAMOND_CHESTPLATE);
        hashMap.put(Pair.of(ArmorMaterial.DIAMOND, (EquipmentSlotType.LEGS)), Items.DIAMOND_LEGGINGS);
        hashMap.put(Pair.of(ArmorMaterial.DIAMOND, (EquipmentSlotType.FEET)), Items.DIAMOND_BOOTS);
        hashMap.put(Pair.of(ArmorMaterial.TURTLE, (EquipmentSlotType.HEAD)), Items.TURTLE_HELMET);
    });
    private static final List<RegistryKey<TrimPattern>> VANILLA_TRIM_PATTERNS = List.of(TrimPatterns.SENTRY, TrimPatterns.DUNE, TrimPatterns.COAST, TrimPatterns.WILD, TrimPatterns.WARD, TrimPatterns.EYE, TrimPatterns.VEX, TrimPatterns.TIDE, TrimPatterns.SNOUT, TrimPatterns.RIB, TrimPatterns.SPIRE, TrimPatterns.WAYFINDER, TrimPatterns.SHAPER, TrimPatterns.SILENCE, TrimPatterns.RAISER, TrimPatterns.HOST);
    private static final List<RegistryKey<TrimMaterial>> VANILLA_TRIM_MATERIALS = List.of(TrimMaterials.QUARTZ, TrimMaterials.IRON, TrimMaterials.NETHERITE, TrimMaterials.REDSTONE, TrimMaterials.COPPER, TrimMaterials.GOLD, TrimMaterials.EMERALD, TrimMaterials.DIAMOND, TrimMaterials.LAPIS, TrimMaterials.AMETHYST);
    private static final ToIntFunction<RegistryKey<TrimPattern>> TRIM_PATTERN_ORDER = Util.createIndexLookup(VANILLA_TRIM_PATTERNS);
    private static final ToIntFunction<RegistryKey<TrimMaterial>> TRIM_MATERIAL_ORDER = Util.createIndexLookup(VANILLA_TRIM_MATERIALS);

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {
        commandDispatcher.register((Commands.literal("spawn_armor_trims").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).executes(commandContext -> SpawnArmorTrimsCommand.spawnArmorTrims(commandContext.getSource(), (commandContext.getSource()).getPlayerOrException())));
    }

    private static int spawnArmorTrims(CommandSource commandSourceStack, PlayerEntity player) {
        World level = player.level();
        NonNullList<ArmorTrim> nonNullList = NonNullList.create();
        Registry<TrimPattern> registry = Registry.TRIM_PATTERN;
        Registry<TrimMaterial> registry2 = Registry.TRIM_MATERIAL;
        registry.stream().sorted(Comparator.comparing(trimPattern -> TRIM_PATTERN_ORDER.applyAsInt(registry.getResourceKey((TrimPattern)trimPattern).orElse(null)))).forEachOrdered(trimPattern -> registry2.stream().sorted(Comparator.comparing(trimMaterial -> TRIM_MATERIAL_ORDER.applyAsInt(registry2.getResourceKey(trimMaterial).orElse(null)))).forEachOrdered(trimMaterial -> nonNullList.add(new ArmorTrim((trimMaterial), (trimPattern)))));
        BlockPos blockPos = player.blockPosition().relative(player.getDirection(), 5);
        int n = ArmorMaterial.values().length - 1;
        int n2 = 0;
        int n3 = 0;
        for (ArmorTrim armorTrim : nonNullList) {
            if (armorTrim.getMaterial() == TrimMaterials.DUMMY_TRIM_MATERIAL || armorTrim.getPattern() == TrimPatterns.DUMMY_TRIM_PATTERN) continue;
            for (ArmorMaterial armorMaterials : ArmorMaterial.values()) {
                if (armorMaterials == ArmorMaterial.LEATHER) continue;
                double d2 = (double)blockPos.getX() + 0.5 - (double)(n2 % (int) registry2.stream().count()) * 3.0;
                double d3 = (double)blockPos.getY() + 0.5 + (double)(n3 % n) * 3.0;
                double d4 = (double)blockPos.getZ() + 0.5 + (double)(n2 / registry2.stream().count() * 10);
                ArmorStandEntity armorStand = new ArmorStandEntity(level, d2, d3, d4);
                armorStand.yRot = (180.0f);
                armorStand.setNoGravity(true);
                for (EquipmentSlotType equipmentSlot : EquipmentSlotType.values()) {
                    ArmorItem armorItem;
                    Item item = MATERIAL_AND_SLOT_TO_ITEM.get(Pair.of(armorMaterials, equipmentSlot));
                    if (item == null) continue;
                    ItemStack itemStack = new ItemStack(item);
                    ArmorTrim.setTrim(level.registryAccess(), itemStack, armorTrim);
                    armorStand.setItemSlot(equipmentSlot, itemStack);
                    if (item instanceof ArmorItem && (armorItem = (ArmorItem)item).getMaterial() == ArmorMaterial.TURTLE) {
                        armorStand.setCustomName(armorTrim.pattern().get().copyWithStyle(armorTrim.material()).copy().append(" ").append(armorTrim.material().get().getDescription()));
                        armorStand.setCustomNameVisible(true);
                        continue;
                    }
                    armorStand.setInvisible(true);
                }
                level.addFreshEntity(armorStand);
                ++n3;
            }
            ++n2;
        }
        commandSourceStack.sendSuccess(new StringTextComponent("Armorstands with trimmed armor spawned around you"), true);
        return 1;
    }
}
