package net.minecraft.item.equipment.trim;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SmithingTemplateItem
extends Item {
    private static final TextFormatting TITLE_FORMAT = TextFormatting.GRAY;
    private static final TextFormatting DESCRIPTION_FORMAT = TextFormatting.BLUE;
    private static final String DESCRIPTION_ID = Util.makeDescriptionId("item", new ResourceLocation("smithing_template"));
    private static final ITextComponent INGREDIENTS_TITLE = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.ingredients"))).withStyle(TITLE_FORMAT);
    private static final ITextComponent SMITHING_TEMPLATE_SUFFIX = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template"))).withStyle(TITLE_FORMAT);
    private static final ITextComponent APPLIES_TO_TITLE = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.applies_to"))).withStyle(TITLE_FORMAT);
    private static final ITextComponent NETHERITE_UPGRADE = Component.translatable(Util.makeDescriptionId("upgrade", new ResourceLocation("netherite_upgrade"))).withStyle(TITLE_FORMAT);

    private static final ITextComponent ARMOR_TRIM_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.armor_trim.applies_to"))).withStyle(DESCRIPTION_FORMAT);
    private static final ITextComponent ARMOR_TRIM_INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.armor_trim.ingredients"))).withStyle(DESCRIPTION_FORMAT);
    private static final ITextComponent ARMOR_TRIM_BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.armor_trim.base_slot_description")));
    private static final ITextComponent ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.armor_trim.additions_slot_description")));
    private static final ITextComponent NETHERITE_UPGRADE_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.netherite_upgrade.applies_to"))).withStyle(DESCRIPTION_FORMAT);
    private static final ITextComponent NETHERITE_UPGRADE_INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.netherite_upgrade.ingredients"))).withStyle(DESCRIPTION_FORMAT);
    private static final ITextComponent NETHERITE_UPGRADE_BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.netherite_upgrade.base_slot_description")));
    private static final ITextComponent NETHERITE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.netherite_upgrade.additions_slot_description")));

    private static final ITextComponent DIAMOND_UPGRADE = Component.translatable(Util.makeDescriptionId("upgrade", new ResourceLocation("netherite_upgrade"))).withStyle(TITLE_FORMAT);
    private static final ITextComponent DIAMOND_UPGRADE_APPLIES_TO = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.diamond_upgrade.applies_to"))).withStyle(DESCRIPTION_FORMAT);
    private static final ITextComponent DIAMOND_UPGRADE_INGREDIENTS = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.diamond_upgrade.ingredients"))).withStyle(DESCRIPTION_FORMAT);
    private static final ITextComponent DIAMOND_UPGRADE_BASE_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.diamond_upgrade.base_slot_description")));
    private static final ITextComponent DIAMOND_UPGRADE_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.diamond_upgrade.additions_slot_description")));

    private static final ResourceLocation EMPTY_SLOT_HELMET = new ResourceLocation("textures/item/empty_armor_slot_helmet.png");
    private static final ResourceLocation EMPTY_SLOT_CHESTPLATE = new ResourceLocation("textures/item/empty_armor_slot_chestplate.png");
    private static final ResourceLocation EMPTY_SLOT_LEGGINGS = new ResourceLocation("textures/item/empty_armor_slot_leggings.png");
    private static final ResourceLocation EMPTY_SLOT_BOOTS = new ResourceLocation("textures/item/empty_armor_slot_boots.png");
    private static final ResourceLocation EMPTY_SLOT_HOE = new ResourceLocation("textures/item/empty_slot_hoe.png");
    private static final ResourceLocation EMPTY_SLOT_AXE = new ResourceLocation("textures/item/empty_slot_axe.png");
    private static final ResourceLocation EMPTY_SLOT_SWORD = new ResourceLocation("textures/item/empty_slot_sword.png");
    private static final ResourceLocation EMPTY_SLOT_SHOVEL = new ResourceLocation("textures/item/empty_slot_shovel.png");
    private static final ResourceLocation EMPTY_SLOT_PICKAXE = new ResourceLocation("textures/item/empty_slot_pickaxe.png");
    private static final ResourceLocation EMPTY_SLOT_INGOT = new ResourceLocation("textures/item/empty_slot_ingot.png");
    private static final ResourceLocation EMPTY_SLOT_REDSTONE_DUST = new ResourceLocation("textures/item/empty_slot_redstone_dust.png");
    private static final ResourceLocation EMPTY_SLOT_QUARTZ = new ResourceLocation("textures/item/empty_slot_quartz.png");
    private static final ResourceLocation EMPTY_SLOT_EMERALD = new ResourceLocation("textures/item/empty_slot_emerald.png");
    private static final ResourceLocation EMPTY_SLOT_DIAMOND = new ResourceLocation("textures/item/empty_slot_diamond.png");
    private static final ResourceLocation EMPTY_SLOT_LAPIS_LAZULI = new ResourceLocation("textures/item/empty_slot_lapis_lazuli.png");
    private static final ResourceLocation EMPTY_SLOT_AMETHYST_SHARD = new ResourceLocation("textures/item/empty_slot_amethyst_shard.png");
    private static final ResourceLocation EMPTY_SLOT_ECHO_SHARD = new ResourceLocation("textures/item/empty_slot_echo_shard.png");

    private final ITextComponent appliesTo;
    private final ITextComponent ingredients;
    private final ITextComponent upgradeDescription;
    private final ITextComponent baseSlotDescription;
    private final ITextComponent additionsSlotDescription;
    private final List<ResourceLocation> baseSlotEmptyIcons;
    private final List<ResourceLocation> additionalSlotEmptyIcons;
    private final Item duplicationItem;

    public TrimPattern getPattern() {
        return pattern;
    }

    private TrimPattern pattern;

    public ToolTrimPattern getToolPattern() {
        return toolPattern;
    }

    private ToolTrimPattern toolPattern;

    public SmithingTemplateItem(Item dupeItem, Rarity properties, ITextComponent component, ITextComponent component2, ITextComponent component3, ITextComponent component4, ITextComponent component5, List<ResourceLocation> list, List<ResourceLocation> list2) {
        super(new Item.Properties().rarity(properties));
        this.appliesTo = component;
        this.ingredients = component2;
        this.upgradeDescription = component3;
        this.baseSlotDescription = component4;
        this.additionsSlotDescription = component5;
        this.baseSlotEmptyIcons = list;
        this.additionalSlotEmptyIcons = list2;
        this.duplicationItem = dupeItem;
    }


    public Item getDuplicationItem() {
        return this.duplicationItem;
    }

    public static SmithingTemplateItem createArmorTrimTemplate(Rarity properties, RegistryKey<TrimPattern> resourceKey, Item duplicationItem) {


        return SmithingTemplateItem.createArmorTrimTemplate(properties, resourceKey.location(), duplicationItem).setTrimPattern(resourceKey);
    }


    public static SmithingTemplateItem createToolTrimTemplate(Rarity properties, RegistryKey<ToolTrimPattern> resourceKey, Item duplicationItem) {


        return SmithingTemplateItem.createArmorTrimTemplate(properties, resourceKey.location(), duplicationItem).setToolTrimPattern(resourceKey);
    }

    public static SmithingTemplateItem createToolTrimTemplate(Rarity properties, ResourceLocation resourceLocation, Item duplicationItem) {
        return new SmithingTemplateItem(duplicationItem, properties, ARMOR_TRIM_APPLIES_TO, ARMOR_TRIM_INGREDIENTS, Component.translatable(Util.makeDescriptionId("trim_pattern", resourceLocation)).withStyle(TITLE_FORMAT), ARMOR_TRIM_BASE_SLOT_DESCRIPTION, ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION, SmithingTemplateItem.createNetheriteUpgradeIconList(), SmithingTemplateItem.createTrimmableMaterialIconList());
    }

    public SmithingTemplateItem setTrimPattern(RegistryKey<TrimPattern> registryKey) {
        this.pattern = Registry.TRIM_PATTERN.get(registryKey);
        return this;
    }

    public SmithingTemplateItem setToolTrimPattern(RegistryKey<ToolTrimPattern> registryKey) {
        this.toolPattern = Registry.TOOL_TRIM_PATTERN.get(registryKey);
        return this;
    }

    public static SmithingTemplateItem createArmorTrimTemplate(Rarity properties, ResourceLocation resourceLocation, Item duplicationItem) {
        return new SmithingTemplateItem(duplicationItem, properties, ARMOR_TRIM_APPLIES_TO, ARMOR_TRIM_INGREDIENTS, Component.translatable(Util.makeDescriptionId("trim_pattern", resourceLocation)).withStyle(TITLE_FORMAT), ARMOR_TRIM_BASE_SLOT_DESCRIPTION, ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION, SmithingTemplateItem.createTrimmableArmorIconList(), SmithingTemplateItem.createTrimmableMaterialIconList());
    }

    public static SmithingTemplateItem createNetheriteUpgradeTemplate() {
        return new SmithingTemplateItem(Items.NETHERRACK, Rarity.UNCOMMON, NETHERITE_UPGRADE_APPLIES_TO, NETHERITE_UPGRADE_INGREDIENTS, NETHERITE_UPGRADE, NETHERITE_UPGRADE_BASE_SLOT_DESCRIPTION, NETHERITE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION, SmithingTemplateItem.createNetheriteUpgradeIconList(), SmithingTemplateItem.createNetheriteUpgradeMaterialList());
    }

    public static SmithingTemplateItem createDiamondUpgradeTemplate() {
        return new SmithingTemplateItem(Items.IRON_INGOT, Rarity.UNCOMMON, DIAMOND_UPGRADE_APPLIES_TO, DIAMOND_UPGRADE_INGREDIENTS, DIAMOND_UPGRADE, DIAMOND_UPGRADE_BASE_SLOT_DESCRIPTION, DIAMOND_UPGRADE_ADDITIONS_SLOT_DESCRIPTION, SmithingTemplateItem.createNetheriteUpgradeIconList(), SmithingTemplateItem.createNetheriteUpgradeIconList());
    }

    private static List<ResourceLocation> createTrimmableArmorIconList() {
        return List.of(EMPTY_SLOT_HELMET, EMPTY_SLOT_CHESTPLATE, EMPTY_SLOT_LEGGINGS, EMPTY_SLOT_BOOTS);
    }

    private static List<ResourceLocation> createTrimmableMaterialIconList() {
        return List.of(EMPTY_SLOT_INGOT, EMPTY_SLOT_REDSTONE_DUST, EMPTY_SLOT_LAPIS_LAZULI, EMPTY_SLOT_QUARTZ, EMPTY_SLOT_DIAMOND, EMPTY_SLOT_EMERALD, EMPTY_SLOT_AMETHYST_SHARD, EMPTY_SLOT_ECHO_SHARD);
    }

    private static List<ResourceLocation> createNetheriteUpgradeIconList() {
        return List.of(EMPTY_SLOT_HELMET, EMPTY_SLOT_SWORD, EMPTY_SLOT_CHESTPLATE, EMPTY_SLOT_PICKAXE, EMPTY_SLOT_LEGGINGS, EMPTY_SLOT_AXE, EMPTY_SLOT_BOOTS, EMPTY_SLOT_HOE, EMPTY_SLOT_SHOVEL);
    }

    private static List<ResourceLocation> createNetheriteUpgradeMaterialList() {
        return List.of(EMPTY_SLOT_INGOT);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World level, List<ITextComponent> list, ITooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        list.add(SMITHING_TEMPLATE_SUFFIX);
        list.add(new StringTextComponent(""));
        list.add(APPLIES_TO_TITLE);
        list.add(new StringTextComponent(" ").append(this.appliesTo));
        list.add(INGREDIENTS_TITLE);
        list.add(new StringTextComponent(" ").append(this.ingredients));
    }

    public ITextComponent getBaseSlotDescription() {
        return this.baseSlotDescription;
    }

    public ITextComponent getAdditionSlotDescription() {
        return this.additionsSlotDescription;
    }

    public List<ResourceLocation> getBaseSlotEmptyIcons() {
        return this.baseSlotEmptyIcons;
    }

    public List<ResourceLocation> getAdditionalSlotEmptyIcons() {
        return this.additionalSlotEmptyIcons;
    }

    @Override
    public String getDescriptionId() {
        return super.getDescriptionId() + ".new";
    }
}