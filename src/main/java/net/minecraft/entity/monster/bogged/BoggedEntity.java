package net.minecraft.entity.monster.bogged;

import com.google.common.collect.Lists;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class BoggedEntity extends AbstractSkeletonEntity implements IShearable, IDropsCustomLoot {
    private static final DataParameter<Integer> BOGGED_TYPE = EntityDataManager.defineId(BoggedEntity.class, DataSerializers.INT);
    private static final DataParameter<Boolean> IS_SHEARED = EntityDataManager.defineId(BoggedEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Optional<UUID>> DATA_TRUSTED_PLAYER = EntityDataManager.defineId(BoggedEntity.class, DataSerializers.OPTIONAL_UUID);
    private boolean alwaysGrowsBackMushrooms = false;
    private UUID lastLightningBoltUUID;


    public boolean canAlwaysBeSheared() {
        return this.alwaysGrowsBackMushrooms && this.hasHeadAccessory();
    }

    private static final Predicate<Entity> TRUSTED_TARGET_SELECTOR = (entity) -> {
        if (!(entity instanceof LivingEntity)) {
            return false;
        } else {
            LivingEntity livingentity = (LivingEntity) entity;
            return livingentity.getLastHurtMob() != null && livingentity.getLastHurtMobTimestamp() < livingentity.tickCount + 600;
        }
    };
    protected int mushroomRegrowTime = -1;
    final BoggedAttackGoal<BoggedEntity> bowGoalAdvanced = new BoggedAttackGoal<>(this, 1.1F, 20, 16.0F);


    public BoggedEntity(EntityType<? extends BoggedEntity> type, World world) {
        super(type, world);
        this.xpReward = 12;
    }

    protected void registerGoals() {
        //this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(2, new SearchForTargetGoal<>(this, PlayerEntity.class, 1.13D, 30, EntityPredicates.NO_CREATIVE_OR_SPECTATOR::test, true));
        this.goalSelector.addGoal(3, new SearchForTargetGoal<>(this, VillagerEntity.class, 1.13D, 30, null, true));

        //this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, WolfEntity.class, 6.0F, 1.0D, 1.2D, entity -> {
            return this.getTrustedPlayer().get(0) == null;
        }));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, IronGolemEntity.class, 4.5F, 1.2D, 1.0D, entity -> {
            return !this.getMainHandItem().isEmpty();
        }));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, (entity) -> {
            return !this.trusts(entity.getUUID());
        }));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 15, true, false, (entity) -> {

            return !entity.getPassengers().isEmpty() && entity.getPassengers().get(0) != null && this.getAttackableEntities().contains(entity.getPassengers().get(0).getClass());
        }));
        this.targetSelector.addGoal(3, new DefendPlayerGoal(this, LivingEntity.class, false, false, (p_234193_1_) -> {
            return TRUSTED_TARGET_SELECTOR.test(p_234193_1_) && !this.trusts(p_234193_1_.getUUID());
        }));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, VillagerEntity.class, 10, true, false, entity -> {
            for (UUID uuid : this.getTrustedPlayer()) {
                if (uuid != null) {
                    return false;
                }
            }
            return true;
        }));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_ON_LAND_SELECTOR));
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return AbstractSkeletonEntity.createAttributes().add(Attributes.MAX_HEALTH, 16.0D).add(Attributes.MOVEMENT_SPEED, 0.22D);
    }

    public boolean burnsInDay() {
        return this.getBoggedType() == BoggedType.FESTERED;
    }

    public int[] getSkeletonConversionTime() {
        return new int[]{10 * 20, 5 * 20};
    }

    public boolean convertsInWater() {
        return BoggedUtils.Entity.convertsInWater(this);
    }

    protected void convertToSkeletonType(EntityType<? extends AbstractSkeletonEntity> mob) {
        BoggedEntity skeleton = this.convertTo(EntityType.BOGGED, true);
        if (skeleton != null) {
            skeleton.setBoggedType(getTypeToConvertTo());
            skeleton.setSheared(this.isSheared());
            skeleton.alwaysGrowsBackMushrooms = this.alwaysGrowsBackMushrooms;
            skeleton.mushroomRegrowTime = this.mushroomRegrowTime;
            BoggedUtils.Entity.setAttributes(skeleton);
            if (this.getTrustedPlayer().get(0) != null) {
                if (skeleton.getAttribute(Attributes.MAX_HEALTH) != null) {
                    skeleton.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30);
                }
                skeleton.setHealth(this.getHealth());
                skeleton.setTrustedPlayer(this.getTrustedPlayer().get(0));
            }
        }

    }

    public BoggedType getTypeToConvertTo() {
        return switch (this.getBoggedType()) {
            case BOGGED, FROSTED -> BoggedType.PARCHED;  // Can convert to PARCHED
            case BLOSSOMED -> BoggedType.BOGGED;         // BLOSSOMED converts back to BOGGED
            case WITHERED -> random.nextBoolean() ? BoggedType.FESTERED : BoggedType.FESTERED_BROWN;  // WITHERED stays unreachable, only conditional
            case PARCHED -> BoggedType.BLOSSOMED;        // PARCHED leads to BLOSSOMED
            case FESTERED -> BoggedType.FESTERED_BROWN;  // FESTERED to FESTERED_BROWN or BLOSSOMED
            case FESTERED_BROWN -> BoggedType.BLOSSOMED; // FESTERED_BROWN leads to BLOSSOMED to keep cycle
        };
    }



    public RangedInteger getMushroomRegrowTicks() {
        return switch (this.getBoggedType()) {
            case BOGGED -> TickRangeConverter.rangeOfTicks(2400, 4600);
            case BLOSSOMED -> TickRangeConverter.rangeOfTicks(600, 1800);
            case WITHERED -> TickRangeConverter.rangeOfTicks(3800, 6700);
            case PARCHED -> TickRangeConverter.rangeOfTicks(1200, 2400);
            case FESTERED, FESTERED_BROWN -> TickRangeConverter.rangeOfTicks(600, 1200);
            case FROSTED -> null;
        };
    }

    public void die(DamageSource source) {
        super.die(source);
        if (this.getTrustedPlayer().get(0) != null) {
            PlayerEntity player = this.level.getPlayerByUUID(this.getTrustedPlayer().get(0));
            if (player != null) {
                String name = this.getName().getString();
                player.sendMessage(new StringTextComponent(name + " was killed. (" + this.blockPosition().toShortString() + ")"), this.getTrustedPlayer().get(0));
            }
        }
    }

    public boolean doHurtTarget(Entity entity) {
        if (this.getBoggedType() == BoggedType.WITHERED) {
            if (!super.doHurtTarget(entity)) {
                return false;
            } else {
                if (entity instanceof LivingEntity) {
                    entity.as(LivingEntity.class).addEffect(new EffectInstance(Effects.WITHER, 200));
                }
                return true;
            }
        }

        return super.doHurtTarget(entity);
    }


    public void aiStep() {
        super.aiStep();
        if (this.getTarget() != null && !this.getTarget().isAlive()) {
            this.setTarget(null);
        }
        if (this.getTrustedPlayer().get(0) != null && this.level.isServerSide) {
            UUID trustedUUID = this.getTrustedPlayer().get(0);

            if (trustedUUID != null) {
                PlayerEntity trustedPlayer = ((ServerWorld) this.level).getPlayerByUUID(trustedUUID);

                if (trustedPlayer != null) {
                    BoggedUtils.followEntity(this, trustedPlayer);
                    if (this.tick(5)) {
                        this.heal(0.0625F);
                    }
                }
            }


        }

        if (this.mushroomRegrowTime > 0) {
            --mushroomRegrowTime;
        }
        if (this.mushroomRegrowTime == 0) {
            mushroomRegrowTime = -1;
            this.regrowMushrooms();
        }
    }

    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficultyInstance, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT nbt) {
        this.setBoggedType(BoggedType.BY_BIOME.getOrDefault(this.level.getBiomeName(this.blockPosition()).orElse(Biomes.SWAMP), BoggedType.BOGGED));
        BoggedUtils.Entity.setAttributes(this);
        return super.finalizeSpawn(world, difficultyInstance, reason, data, nbt);
    }

    public void setBoggedType(BoggedType type) {
        this.entityData.set(BOGGED_TYPE, type.getId());
    }

    public void regrowMushrooms() {
        this.setSheared(false);
        // Logically reset mushroom regrow timer (to prevent overwriting issues)
        if (this.level.isServerSide) {
            this.mushroomRegrowTime = -1;
        }
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    public List<UUID> getTrustedPlayer() {
        List<UUID> list = Lists.newArrayList();
        list.add(this.entityData.get(DATA_TRUSTED_PLAYER).orElse((UUID) null));
        return list;
    }

    void setTrustedPlayer(@Nullable UUID p_213465_1_) {
        this.entityData.set(DATA_TRUSTED_PLAYER, Optional.ofNullable(p_213465_1_));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TRUSTED_PLAYER, Optional.empty());
        this.entityData.define(IS_SHEARED, false);
        this.entityData.define(BOGGED_TYPE, 0);
    }

    public boolean isSheared() {
        if (this.alwaysGrowsBackMushrooms) return false;

        return this.entityData.get(IS_SHEARED);
    }

    public void setSheared(boolean sheared) {
        this.entityData.set(IS_SHEARED, sheared);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.setSheared(compound.getBoolean("sheared"));
        this.alwaysGrowsBackMushrooms = compound.getBoolean("CanAlwaysBeSheared");
        if (compound.contains("mushroomGrowTime")) {
            this.mushroomRegrowTime = compound.getInt("mushroomGrowTime");
        }
        ListNBT listnbt = compound.getList("Trusted", 11);

        for (INBT inbt : listnbt) {
            this.setTrustedPlayer(NBTUtil.loadUUID(inbt));
        }
        this.setBoggedType(BoggedType.byName(compound.getString("BoggedType")));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        List<UUID> list = this.getTrustedPlayer();
        ListNBT listnbt = new ListNBT();

        for (UUID uuid : list) {
            if (uuid != null) {
                listnbt.add(NBTUtil.createUUID(uuid));
            }
        }
        compound.put("Trusted", listnbt);
        compound.putBoolean("CanAlwaysBeSheared", this.alwaysGrowsBackMushrooms);
        compound.putBoolean("sheared", this.isSheared());
        compound.putString("BoggedType", this.getBoggedType().getName());
        compound.putInt("mushroomGrowTime", this.mushroomRegrowTime);
    }

    public BoggedType getBoggedType() {
        return BoggedType.byId(this.entityData.get(BOGGED_TYPE));
    }

    protected SoundEvent getAmbientSound() {


        return SoundEvents.BOGGED_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource source) {

        return SoundEvents.BOGGED_HURT;
    }

    protected SoundEvent getDeathSound() {

        return SoundEvents.BOGGED_DEATH;
    }

    public SoundEvent getStepSound() {

        return SoundEvents.BOGGED_STEP;
    }

    @Override
    protected AbstractArrowEntity getArrow(ItemStack arrowStack, float velocity) {
        AbstractArrowEntity arrow = super.getArrow(arrowStack, velocity);
        if (arrow instanceof ArrowEntity) {
            BoggedUtils.addEffectPerType((ArrowEntity) arrow, this);
        }
        return arrow;
    }


    public boolean readyForShearing() {
        if (alwaysGrowsBackMushrooms) {
            return true;
        }

        return !this.isSheared() && this.isAlive() && !this.hasItemInSlot(EquipmentSlotType.HEAD) && this.hasHeadAccessory();
    }

    public boolean isAlliedTo(Entity entity) {
        return this.trusts(entity.getUUID());
    }

    public void shear(SoundCategory soundSource) {
        this.level.playSound((PlayerEntity) null, this, SoundEvents.SHEEP_SHEAR, soundSource, 1.0F, 1.0F);

        this.spawnShearedItems();

        if (!this.alwaysGrowsBackMushrooms && this.level.isServerSide) {
            this.setSheared(true);
            this.mushroomRegrowTime = this.getMushroomRegrowTicks().randomValue(this.level.getRandom());
        }
    }

    private void spawnShearedItems() {
        World world = this.level;
        if (world instanceof ServerWorld) {
            this.dropLoot(typeDrops(), 1.65F, null);
        }
    }


    public List<WeightedItemStack> typeDrops() {
        BoggedType boggedType = this.getBoggedType();

        return switch (boggedType) {
            case BOGGED -> BoggedUtils.ItemUtil.createMushroomList(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM, 10, 1, 3);
            case BLOSSOMED -> BoggedUtils.ItemUtil.createRandomFlowerList(1, 3);
            case WITHERED -> BoggedUtils.ItemUtil.createSingleItemStack(Items.NETHER_WART, 10, 2, 6);
            case PARCHED -> BoggedUtils.ItemUtil.createSingleItemStack(Items.CACTUS, 10, 1, 4);
            case FESTERED -> BoggedUtils.ItemUtil.createSingleItemStack(Items.RED_MUSHROOM, 10, 3, 7);
            case FESTERED_BROWN -> BoggedUtils.ItemUtil.createSingleItemStack(Items.BROWN_MUSHROOM, 10, 3, 7);
            case FROSTED -> List.of();
            default -> List.of();
        };
    }

    public boolean multipleMushrooms() {
        return this.getBoggedType() == BoggedType.FESTERED || this.getBoggedType() == BoggedType.FESTERED_BROWN;
    }

    public boolean extraHealth() {
        return false;
    }

    public ActionResultType mobInteract(Hand hand, PlayerEntity player) {
        return super.mobInteract(player, hand);
    }

    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        return BoggedUtils.itemUsedOnBogged(itemInHand, player, hand, this);
    }


    public static boolean checkBoggedRules(EntityType<BoggedEntity> type, IServerWorld world, SpawnReason reason, BlockPos pos, Random random) {
        boolean nether = world.getLevel().dimension() == World.NETHER;
        return checkMonsterSpawnRules(type, world, reason, pos, random) && (reason == SpawnReason.SPAWNER || (world.canSeeSky(pos) || pos.getY() > 45 || nether));
    }


    @Nullable
    public Potion getPotionForType() {
        return switch (this.getBoggedType()) {
            case BOGGED, BLOSSOMED -> Potions.POISON;

            case WITHERED -> null;
            case PARCHED, FESTERED, FESTERED_BROWN, FROSTED -> Potions.SLOWNESS;
        };
    }

    public void thunderHit(ServerWorld p_241841_1_, LightningBoltEntity p_241841_2_) {
        UUID uuid = p_241841_2_.getUUID();
        if (this.getBoggedType() == BoggedType.FESTERED || getBoggedType() == BoggedType.FESTERED_BROWN) {
            if (!uuid.equals(this.lastLightningBoltUUID)) {
                this.setBoggedType(this.getBoggedType() == BoggedType.FESTERED_BROWN ? BoggedType.FESTERED : BoggedType.FESTERED_BROWN);
                this.lastLightningBoltUUID = uuid;
                this.playSound(SoundEvents.MOOSHROOM_CONVERT, 2.0F, 1.0F);
            }
        }

    }

    public boolean hasHeadAccessory() {
        return this.getBoggedType().hasMushrooms;
    }


    public List<WeightedItemStack> getDropItems() {
        List<WeightedItemStack> weightedItemStackList = new ArrayList<>();
        // Adding the arrow drops with default condition and no additional function


        WeightedItemStack.Builder builder = new WeightedItemStack.Builder()
                .addWeightedItem(new ItemStack(Items.ARROW), 12, 0, 3)
                .addWeightedItem(new ItemStack(Items.VINE), 2, 0, 2)
                .addWeightedItem(new ItemStack(Items.BONE), 7, 0, 3)
                .setRolls(0, 1)
                .withCondition(context -> context.hasParam(LootParameters.LAST_DAMAGE_PLAYER))
                .setBonusRolls(new WeightedItemStack.RandomValueRange(0, 1));


        ItemStack tippedArrowStack = new ItemStack(Items.TIPPED_ARROW);
        Potion potion = getPotionForType(); // Method to get the specific potion
        if (potion != null) {
            PotionUtils.setPotion(tippedArrowStack, potion);
            builder.addWeightedItem(tippedArrowStack, 3, 0, 2);
        }

        weightedItemStackList.add(builder.build());

        return weightedItemStackList;
    }

    public void reassessWeaponGoal() {
        BoggedUtils.reassessWeapon(this);
    }

    public int getAttackInterval() {
        return this.getBoggedType().getArrowCooldown();
    }

    public boolean canBeLeashed(PlayerEntity player) {
        return this.getTrustedPlayer().contains(player.getUUID());
    }

    private boolean trusts(UUID uuid) {
        return this.getTrustedPlayer().contains(uuid);
    }


}
