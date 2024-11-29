package net.minecraft.entity.monster;

import com.google.common.collect.Maps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

public class PillagerCaptainEntity extends PillagerEntity implements ICrossbowUser {
    private static final DataParameter<Boolean> IS_CHARGING_CROSSBOW = EntityDataManager.defineId(PillagerCaptainEntity.class, DataSerializers.BOOLEAN);
    private final Inventory inventory = new Inventory(5);


    public PillagerCaptainEntity(EntityType<? extends PillagerCaptainEntity> pillager, World world) {
        super(pillager, world);
        this.xpReward = 10;
    }

    @Override
    public float getCreakingRunDistance() {
        return 2f;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isChargingCrossbow() {
        return this.entityData.get(IS_CHARGING_CROSSBOW);
    }

    public void setChargingCrossbow(boolean value) {
        this.entityData.set(IS_CHARGING_CROSSBOW, value);
    }

    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    public void performRangedAttack(LivingEntity entity, float v) {
        this.performCrossbowAttack(this, 1.6F);
    }

    public void shootCrossbowProjectile(LivingEntity entity, ItemStack stack, ProjectileEntity projectileEntity, float v) {
        this.shootCrossbowProjectile(this, entity, projectileEntity, v, 1.75F);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(2, new AbstractRaiderEntity.FindTargetGoal(this, 15.0F));
        this.goalSelector.addGoal(3, new RangedCrossbowAttackGoal<>(this, 1.0D, 30.0F));
        this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 30.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, Mob.class, 15.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setAlertOthers());

    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CHARGING_CROSSBOW, false);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return PillagerEntity.createAttributes().add(Attributes.ARMOR, 8D).add(Attributes.ARMOR_TOUGHNESS, 6D).add(Attributes.KNOCKBACK_RESISTANCE, 0.1F).add(Attributes.FOLLOW_RANGE, 64.0D).add(Attributes.MAX_HEALTH, 32F).add(Attributes.MOVEMENT_SPEED, 0.4F);
    }


    protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance) {
        ItemStack crossbow = new ItemStack(Items.CROSSBOW);
        if (random.nextFloat() < 0.1F) {
            int level = random.nextInt(4);
            int enchantmentLevel = (level == 0) ? 1 : level;
            crossbow.enchant(Enchantments.PIERCING, enchantmentLevel);
        }
        if(random.nextBoolean()) {
            ItemStack gildedCrossbow = new ItemStack(Items.GILDED_CROSSBOW);
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(crossbow);
            EnchantmentHelper.setEnchantments(enchantments, gildedCrossbow);
            crossbow = gildedCrossbow;
            this.setDropChance(EquipmentSlotType.MAINHAND, 0.02F);
        }
        this.setItemSlot(EquipmentSlotType.MAINHAND, crossbow);
    }

    public boolean isAlliedTo(Entity entity) {
        if (super.isAlliedTo(entity)) {
            return true;
        } else if (entity instanceof LivingEntity && ((LivingEntity)entity).getMobType() == CreatureAttribute.ILLAGER) {
            return this.getTeam() == null && entity.getTeam() == null;
        } else {
            return false;
        }
    }

    protected float getVoicePitch() {
        return super.getVoicePitch() - 0.2F;
    }

    public void applyRaidBuffs(int level, boolean value) {
        Raid raid = this.getCurrentRaid();
        boolean flag = this.random.nextFloat() <= raid.getEnchantOdds();
        if (flag) {
            ItemStack itemstack = new ItemStack(Items.CROSSBOW);
            Map<Enchantment, Integer> map = Maps.newHashMap();
            if (level > raid.getNumGroups(Difficulty.NORMAL)) {
                map.put(Enchantments.QUICK_CHARGE, 3);
            } else if (level > raid.getNumGroups(Difficulty.EASY)) {
                map.put(Enchantments.QUICK_CHARGE, 2);
            }

            map.put(Enchantments.MULTISHOT, 2);
            EnchantmentHelper.setEnchantments(map, itemstack);
            this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack);
            ItemStack itemstack2 = new ItemStack(Items.FIREWORK_ARROW, 13);
            ItemStack itemstack3 = new ItemStack(Items.BONE_ARROW, 18);
            if (this.random.nextBoolean()) {
                if (this.random.nextBoolean()) {
                    this.setItemSlot(EquipmentSlotType.OFFHAND, itemstack3);
                } else {
                    this.setItemSlot(EquipmentSlotType.OFFHAND, itemstack2);
                }
            }
        }

    }


}
