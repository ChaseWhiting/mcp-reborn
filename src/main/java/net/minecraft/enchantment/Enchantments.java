package net.minecraft.enchantment;

import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.passive.Animal;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.registry.Registry;

public class Enchantments {
   private static final EquipmentSlotType[] ARMOR_SLOTS = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};
   public static final Enchantment ALL_DAMAGE_PROTECTION = register("protection", new ProtectionEnchantment(Enchantment.Rarity.COMMON, ProtectionEnchantment.Type.ALL, ARMOR_SLOTS));
   public static final Enchantment FIRE_PROTECTION = register("fire_protection", new ProtectionEnchantment(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.Type.FIRE, ARMOR_SLOTS));
   public static final Enchantment FALL_PROTECTION = register("feather_falling", new ProtectionEnchantment(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.Type.FALL, ARMOR_SLOTS));
   public static final Enchantment BLAST_PROTECTION = register("blast_protection", new ProtectionEnchantment(Enchantment.Rarity.RARE, ProtectionEnchantment.Type.EXPLOSION, ARMOR_SLOTS));
   public static final Enchantment PROJECTILE_PROTECTION = register("projectile_protection", new ProtectionEnchantment(Enchantment.Rarity.UNCOMMON, ProtectionEnchantment.Type.PROJECTILE, ARMOR_SLOTS));
   public static final Enchantment RESPIRATION = register("respiration", new RespirationEnchantment(Enchantment.Rarity.RARE, ARMOR_SLOTS));
   public static final Enchantment AQUA_AFFINITY = register("aqua_affinity", new AquaAffinityEnchantment(Enchantment.Rarity.RARE, ARMOR_SLOTS));
   public static final Enchantment THORNS = register("thorns", new ThornsEnchantment(Enchantment.Rarity.VERY_RARE, ARMOR_SLOTS));
   public static final Enchantment DEPTH_STRIDER = register("depth_strider", new DepthStriderEnchantment(Enchantment.Rarity.RARE, ARMOR_SLOTS));
   public static final Enchantment FROST_WALKER = register("frost_walker", new FrostWalkerEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.FEET));
   public static final Enchantment LAVA_WALKER = register("lava_walker", new FrostWalkerEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.FEET));

   public static final Enchantment BINDING_CURSE = register("binding_curse", new BindingCurseEnchantment(Enchantment.Rarity.VERY_RARE, ARMOR_SLOTS));
   public static final Enchantment WEIGHT_CURSE = register("weight_curse", new WeightCurseEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.values()));
   public static final Enchantment SOUL_SPEED = register("soul_speed", new SoulSpeedEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.FEET));
   public static final Enchantment SHARPNESS = register("sharpness", new DamageEnchantment(Enchantment.Rarity.COMMON, 0, EquipmentSlotType.MAINHAND));
   public static final Enchantment SMITE = register("smite", new DamageEnchantment(Enchantment.Rarity.UNCOMMON, 1, EquipmentSlotType.MAINHAND));
   public static final Enchantment BANE_OF_ARTHROPODS = register("bane_of_arthropods", new DamageEnchantment(Enchantment.Rarity.UNCOMMON, 2, EquipmentSlotType.MAINHAND));
   public static final Enchantment PILLAGING = register("pillaging", new DamageEnchantment(Enchantment.Rarity.UNCOMMON, 3, EquipmentSlotType.MAINHAND));
   public static final Enchantment KNOCKBACK = register("knockback", new KnockbackEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlotType.MAINHAND));
   public static final Enchantment FIRE_ASPECT = register("fire_aspect", new FireAspectEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment MOB_LOOTING = register("looting", new LootBonusEnchantment(Enchantment.Rarity.RARE, EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND));
   public static final Enchantment SWEEPING_EDGE = register("sweeping", new SweepingEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment BLOCK_EFFICIENCY = register("efficiency", new EfficiencyEnchantment(Enchantment.Rarity.COMMON, EquipmentSlotType.MAINHAND));
   public static final Enchantment SILK_TOUCH = register("silk_touch", new SilkTouchEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment UNBREAKING = register("unbreaking", new UnbreakingEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlotType.MAINHAND));
   public static final Enchantment BLOCK_FORTUNE = register("fortune", new LootBonusEnchantment(Enchantment.Rarity.RARE, EnchantmentType.DIGGER, EquipmentSlotType.MAINHAND));
   public static final Enchantment POWER_ARROWS = register("power", new PowerEnchantment(Enchantment.Rarity.COMMON, EquipmentSlotType.MAINHAND));
   public static final Enchantment MARROW_QUIVER = register("marrow_quiver", new MarrowQuiverEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment PUNCH_ARROWS = register("punch", new PunchEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment FLAMING_ARROWS = register("flame", new FlameEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment INFINITY_ARROWS = register("infinity", new InfinityEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment FISHING_LUCK = register("luck_of_the_sea", new LootBonusEnchantment(Enchantment.Rarity.RARE, EnchantmentType.FISHING_ROD, EquipmentSlotType.MAINHAND));
   public static final Enchantment FISHING_SPEED = register("lure", new LureEnchantment(Enchantment.Rarity.RARE, EnchantmentType.FISHING_ROD, EquipmentSlotType.MAINHAND));
   public static final Enchantment LOYALTY = register("loyalty", new LoyaltyEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlotType.MAINHAND));
   public static final Enchantment IMPALING = register("impaling", new ImpalingEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment RIPTIDE = register("riptide", new RiptideEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment CHANNELING = register("channeling", new ChannelingEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment MULTISHOT = register("multishot", new MultishotEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment QUICK_CHARGE = register("quick_charge", new QuickChargeEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlotType.MAINHAND));
   public static final Enchantment RICOCHET = register("reflective_bolt", new ReflectiveBoltEnchantment());
   public static final Enchantment GRAVITY = register("gravity_well", new GravityWellEnchantment());
   public static final Enchantment PIERCING = register("piercing", new PiercingEnchantment(Enchantment.Rarity.COMMON, EquipmentSlotType.MAINHAND));
   public static final Enchantment MENDING = register("mending", new MendingEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.values()));
   public static final Enchantment VANISHING_CURSE = register("vanishing_curse", new VanishingCurseEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.values()));
   public static final Enchantment DEEP_POCKETS = register("deep_pockets", new DeepPocketsEnchantment());
   public static final Enchantment LIGHTWEIGHT = register("lightweight", new LightweightEnchantment());

   public static final Enchantment RETURNING = register("pathfinder", new PathfinderEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment RADIANCE = register("radiance", new RadianceEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment SPECTRAL_THROW = register("spectral_throw", new SpectralThrowEnchantment(Enchantment.Rarity.RARE, EquipmentSlotType.MAINHAND));
   public static final Enchantment HOMING = register("homing", new HomingEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlotType.MAINHAND));
   public static final HomingModuleEnchantment HOMING_MONSTER = (HomingModuleEnchantment) register("homing_monster_module", new HomingModuleEnchantment(Enchantment.Rarity.VERY_RARE, Monster.class));
   public static final HomingModuleEnchantment HOMING_ANIMAL = (HomingModuleEnchantment) register("homing_animal_module", new HomingModuleEnchantment(Enchantment.Rarity.VERY_RARE, Animal.class));





   private static Enchantment register(String name, Enchantment type) {
      return Registry.register(Registry.ENCHANTMENT, name, type);
   }
}