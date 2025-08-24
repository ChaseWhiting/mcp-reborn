package net.minecraft.entity.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.bundle.QuiverItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.happy_ghast.HappyGhastEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.creaking.CreakingEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.GrapplingHookEntity;
import net.minecraft.entity.villager.data.quest.Quest;
import net.minecraft.entity.villager.data.quest.QuestManager;
import net.minecraft.entity.warden.WardenSpawnTracker;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.fallout.Addiction;
import net.minecraft.fallout.Skills;
import net.minecraft.fallout.Special;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.food.FoodData;
import net.minecraft.item.tool.BatItem;
import net.minecraft.item.tool.SwordItem;
import net.minecraft.item.tool.terraria.AccessoryHolderItem;
import net.minecraft.item.tool.terraria.MoltenSkullRoseItem;
import net.minecraft.item.tool.terraria.ObsidianRoseItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.random.RandomSource;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.villager.data.quest.QuestTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class PlayerEntity extends LivingEntity {
    public static final Vector3d DEFAULT_VEHICLE_ATTACHMENT = new Vector3d(0.0, 0.6, 0.0);
    public static final EntitySize STANDING_DIMENSIONS = EntitySize.scalable(0.6F, 1.8F).withAttachments(EntityAttachments.builder().attach(EntityAttachment.VEHICLE, DEFAULT_VEHICLE_ATTACHMENT));
    private static final Map<Pose, EntitySize> POSES = ImmutableMap.<Pose, EntitySize>builder().put(Pose.STANDING, STANDING_DIMENSIONS).put(Pose.SLEEPING, SLEEPING_DIMENSIONS).put(Pose.FALL_FLYING, EntitySize.scalable(0.6F, 0.6F)).put(Pose.SWIMMING, EntitySize.scalable(0.6F, 0.6F)).put(Pose.SPIN_ATTACK, EntitySize.scalable(0.6F, 0.6F)).put(Pose.CROUCHING, EntitySize.scalable(0.6F, 1.5F)).put(Pose.DYING, EntitySize.fixed(0.2F, 0.2F)).build();
    private static final DataParameter<Float> DATA_PLAYER_ABSORPTION_ID = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> DATA_SCORE_ID = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.INT);
    protected static final DataParameter<Byte> DATA_PLAYER_MODE_CUSTOMISATION = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.BYTE);
    protected static final DataParameter<Byte> DATA_PLAYER_MAIN_HAND = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.BYTE);
    protected static final DataParameter<CompoundNBT> DATA_SHOULDER_LEFT = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.COMPOUND_TAG);
    protected static final DataParameter<CompoundNBT> DATA_SHOULDER_RIGHT = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.COMPOUND_TAG);
    private long timeEntitySatOnShoulder;
    public final PlayerInventory inventory = new PlayerInventory(this);
    protected EnderChestInventory enderChestInventory = new EnderChestInventory();
    public final PlayerContainer inventoryMenu;
    public Container containerMenu;
    protected FoodStats foodData = new FoodStats();
    protected int jumpTriggerTime;
    public float oBob;
    public float bob;
    public int takeXpDelay;
    public double xCloakO;
    public double yCloakO;
    public double zCloakO;
    public double xCloak;
    public double yCloak;
    public double zCloak;
    private Map<EntityType<?>, Integer> killCounts;
    private QuestManager questManager;
    private List<Quest> quests;
    private int sleepCounter;
    protected boolean wasUnderwater;
    public final PlayerAbilities abilities = new PlayerAbilities();
    public int experienceLevel;
    public int totalExperience;
    public float experienceProgress;
    protected int enchantmentSeed;
    protected final float defaultFlySpeed = 0.02F;
    private int lastLevelUpTime;
    private final GameProfile gameProfile;
    @OnlyIn(Dist.CLIENT)
    private boolean reducedDebugInfo;
    private ItemStack lastItemInMainHand = ItemStack.EMPTY;
    private final CooldownTracker cooldowns = this.createItemCooldowns();
    @Nullable
    public FishingBobberEntity fishing;
    public Addiction.AddictionManager addictions;
    private GrapplingHookEntity grapplingHook;
    public Special.SpecialManager specialManager;
    public Skills skills;
    private static final UUID INVENTORY_WEIGHT_MOD = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");

    public boolean isScoping() {
        return this.isUsingItem() && this.getUseItem().is(Items.SPYGLASS);
    }

    @Override
    public Vector3d getVehicleAttachmentPoint(Entity vehicle) {
        if (vehicle instanceof AbstractHorseEntity) {
            return new Vector3d(0.0, 0.723, 0.0);
        }

        return new Vector3d(0.0, 0.6, 0.0); // TEMP: forces it to be flush with the vehicle
    }


    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return !this.abilities.flying && (!this.onGround || !this.isDiscrete()) ? Entity.MovementEmission.ALL : Entity.MovementEmission.NONE;
    }


    public float getReachDistance() {
        return (float) this.getAttributeValue(Attributes.REACH_DISTANCE) + (this.isCreative() ? 0.5F : 0F);
    }

    public double getEntityReachDistance() {
        return this.getAttributeValue(Attributes.ENTITY_REACH_DISTANCE) + (this.isCreative() ? 2D : 0D);
    }

    public Optional<WardenSpawnTracker> getWardenSpawnTracker() {
        return Optional.empty();
    }


    protected boolean doesEmitEquipEvent(EquipmentSlotType equipmentSlot) {
        return equipmentSlot.getType() == EquipmentSlotType.Group.ARMOR;
    }

    public void assignSlownessBasedOnItemWeightInVeryHardMode() {
        ModifiableAttributeInstance movementSpeed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed == null) {
            return; // No movement speed attribute, exit early
        }

        // Check if the player is in very hard mode
        if (this.veryHardmode()) {
            // Calculate the total weight of items in the player's inventory
            int totalWeight = 0;
            for (List<ItemStack> list : this.inventory.compartments) {
                for (ItemStack itemStack : list) {
                    if (!itemStack.isEmpty()) {
                        totalWeight += itemStack.getWeight(itemStack, new ItemStack(Items.AIR, 0));
                    }
                }
            }

            // Calculate the slowness modifier based on weight
            double slownessValue = (totalWeight / 5) * -0.0005; // Negative to reduce speed

            // Check if the modifier already exists
            AttributeModifier existingModifier = movementSpeed.getModifier(INVENTORY_WEIGHT_MOD);

            if (existingModifier != null) {
                // If the modifier exists, update it only if the value has changed
                if (existingModifier.getAmount() != slownessValue) {
                    movementSpeed.removeModifier(INVENTORY_WEIGHT_MOD);
                    movementSpeed.addTransientModifier(new AttributeModifier(
                            INVENTORY_WEIGHT_MOD,
                            "Inventory weight slowness",
                            slownessValue,
                            AttributeModifier.Operation.ADDITION
                    ));
                }
            } else {
                // If no modifier exists, add a new one
                movementSpeed.addTransientModifier(new AttributeModifier(
                        INVENTORY_WEIGHT_MOD,
                        "Inventory weight slowness",
                        slownessValue,
                        AttributeModifier.Operation.ADDITION
                ));
            }
        } else {
            // If not in very hard mode, ensure the modifier is removed
            if (movementSpeed.getModifier(INVENTORY_WEIGHT_MOD) != null) {
                movementSpeed.removeModifier(INVENTORY_WEIGHT_MOD);
            }
        }
    }


    public PlayerEntity(World p_i241920_1_, BlockPos p_i241920_2_, float p_i241920_3_, GameProfile p_i241920_4_) {
        super(EntityType.PLAYER, p_i241920_1_);
        this.setUUID(createPlayerUUID(p_i241920_4_));
        this.gameProfile = p_i241920_4_;
        this.killCounts = new HashMap<>();
        this.questManager = new QuestManager(this);
        this.quests = new ArrayList<>();
        this.addictions = new Addiction.AddictionManager(this);
        this.specialManager = new Special.SpecialManager(this);
        this.skills = new Skills(this, specialManager);
        this.questManager.addQuest(QuestTypes.defeatAllMob);
        this.inventoryMenu = new PlayerContainer(this.inventory, !p_i241920_1_.isClientSide, this);
        this.containerMenu = this.inventoryMenu;
        this.moveTo((double) p_i241920_2_.getX() + 0.5D, (double) (p_i241920_2_.getY() + 1), (double) p_i241920_2_.getZ() + 0.5D, p_i241920_3_, 0.0F);
        this.rotOffs = 180.0F;
    }

    public void addAddiction(Addiction addiction) {
        this.addictions.addAddiction(addiction);
    }

    public boolean hasAddiction(Addiction addiction) {
        return addictions.hasAddiction(addiction);
    }

    public void removeAddiction(Addiction addiction) {
        addictions.removeAddiction(addiction);
    }

    public boolean blockActionRestricted(World p_223729_1_, BlockPos p_223729_2_, Gamemode p_223729_3_) {
        if (!p_223729_3_.isBlockPlacingRestricted()) {
            return false;
        } else if (p_223729_3_ == Gamemode.SPECTATOR) {
            return true;
        } else if (this.mayBuild()) {
            return false;
        } else {
            ItemStack itemstack = this.getMainHandItem();
            return itemstack.isEmpty() || !itemstack.hasAdventureModeBreakTagForBlock(p_223729_1_.getTagManager(), new CachedBlockInfo(p_223729_1_, p_223729_2_, false));
        }
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.ENTITY_REACH_DISTANCE, 3D).add(Attributes.REACH_DISTANCE, 4.5D).add(Attributes.MOVEMENT_SPEED, (double) 0.1F).add(Attributes.ATTACK_SPEED).add(Attributes.LUCK);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_PLAYER_ABSORPTION_ID, 0.0F);
        this.entityData.define(DATA_SCORE_ID, 0);
        this.entityData.define(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0);
        this.entityData.define(DATA_PLAYER_MAIN_HAND, (byte) 1);
        this.entityData.define(DATA_SHOULDER_LEFT, new CompoundNBT());
        this.entityData.define(DATA_SHOULDER_RIGHT, new CompoundNBT());
    }

    public CompoundNBT writeQuestsToNBT(CompoundNBT compound) {
        ListNBT questsNBT = new ListNBT();
        for (Quest quest : quests) {
            questsNBT.add(quest.toNBT());
        }
        compound.put("Quests", questsNBT);
        return compound;
    }

    public void readQuestsFromNBT(CompoundNBT compound) {
        ListNBT questsNBT = compound.getList("Quests", 10);
        quests.clear();
        for (INBT inbt : questsNBT) {
            quests.add(Quest.fromNBT((CompoundNBT) inbt));
        }
    }

    public GrapplingHookEntity getGrapplingHook() {
        return this.grapplingHook;
    }

    public void setGrapplingHook(GrapplingHookEntity grapplingHook) {
        this.grapplingHook = grapplingHook;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public void addQuest(Quest quest) {
        quests.add(quest);
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public void tickQuests() {
        List<Quest> activeQuestsCopy;
        synchronized (this.questManager.getActiveQuests()) {
            activeQuestsCopy = new ArrayList<>(this.questManager.getActiveQuests());
        }

        for (Quest quest : activeQuestsCopy) {
            quest.tick();
            if (questManager.tryGiveReward(quest, this) && !quest.isCompleted()) {
                synchronized (this.questManager.getActiveQuests()) {
                    questManager.completeQuest(quest);
                    this.questManager.getActiveQuests().remove(quest);
                }
            }
        }
    }

    public void tick() {
        tickQuests();
        addictions.tick();


        this.noPhysics = this.isSpectator();
        if (this.isSpectator()) {
            this.onGround = false;
        }

        if (this.takeXpDelay > 0) {
            --this.takeXpDelay;
        }

        if (this.isSleeping()) {
            ++this.sleepCounter;
            if (this.sleepCounter > 100) {
                this.sleepCounter = 100;
            }

            if (!this.level.isClientSide && this.level.isDay() && !this.level.getGameRules().getBoolean(GameRules.RULE_SLEEP_DAY)) {
                this.stopSleepInBed(false, true);
            }
        } else if (this.sleepCounter > 0) {
            ++this.sleepCounter;
            if (this.sleepCounter >= 110) {
                this.sleepCounter = 0;
            }
        }

        this.updateIsUnderwater();
        super.tick();
        if (!this.level.isClientSide && this.containerMenu != null && !this.containerMenu.stillValid(this)) {
            this.closeContainer();
            this.containerMenu = this.inventoryMenu;
        }

        this.moveCloak();
        if (!this.level.isClientSide) {
            this.foodData.tick(this);
            this.awardStat(Stats.PLAY_ONE_MINUTE);
            if (this.isAlive()) {
                this.awardStat(Stats.TIME_SINCE_DEATH);
            }

            if (this.isDiscrete()) {
                this.awardStat(Stats.CROUCH_TIME);
            }

            if (!this.isSleeping()) {
                this.awardStat(Stats.TIME_SINCE_REST);
            }
        }

        int i = 29999999;
        double d0 = MathHelper.clamp(this.getX(), -2.9999999E7D, 2.9999999E7D);
        double d1 = MathHelper.clamp(this.getZ(), -2.9999999E7D, 2.9999999E7D);
        if (d0 != this.getX() || d1 != this.getZ()) {
            this.setPos(d0, this.getY(), d1);
        }

        ++this.attackStrengthTicker;
        ItemStack itemstack = this.getMainHandItem();
        if (!ItemStack.matches(this.lastItemInMainHand, itemstack)) {
            if (!ItemStack.isSameIgnoreDurability(this.lastItemInMainHand, itemstack)) {
                this.resetAttackStrengthTicker();
            }

            this.lastItemInMainHand = itemstack.copy();
        }

        this.turtleHelmetTick();
        this.cooldowns.tick();
        this.updatePlayerPose();
    }

    public boolean isSecondaryUseActive() {
        return this.isShiftKeyDown();
    }

    protected boolean wantsToStopRiding() {
        return this.isShiftKeyDown();
    }

    protected boolean isStayingOnGroundSurface() {
        return this.isShiftKeyDown();
    }

    protected boolean updateIsUnderwater() {
        this.wasUnderwater = this.isEyeInFluid(FluidTags.WATER);
        return this.wasUnderwater;
    }

    private void turtleHelmetTick() {
        ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.HEAD);
        ItemStack itemstack2 = this.getItemBySlot(EquipmentSlotType.FEET);
        ItemStack itemstack3 = this.getItemBySlot(EquipmentSlotType.LEGS);
        ItemStack itemstack4 = this.getItemBySlot(EquipmentSlotType.CHEST);

        boolean helmet = itemstack.getItem() == Items.TURTLE_HELMET || itemstack.getItem() == Items.BURNT_TURTLE_HELMET;
        boolean chestplate = itemstack4.getItem() == Items.TURTLE_CHESTPLATE || itemstack4.getItem() == Items.BURNT_TURTLE_CHESTPLATE;
        boolean legs = itemstack3.getItem() == Items.TURTLE_LEGGINGS || itemstack3.getItem() == Items.BURNT_TURTLE_LEGGINGS;
        boolean boots = itemstack2.getItem() == Items.TURTLE_BOOTS || itemstack2.getItem() == Items.BURNT_TURTLE_BOOTS;

        if (itemstack.get() == Items.FLOWER_CROWN) {
            this.addEffect(new EffectInstance(Effects.LUCK, 10 * 20, 4, false, false, true));
            if (!this.hasEffect(Effects.REGENERATION)) {
                this.addEffect(new EffectInstance(Effects.REGENERATION, 10 * 20, 0, false, false, true));
            }
        }

        if (helmet && !this.isEyeInFluid(FluidTags.WATER)) {
            this.addEffect(new EffectInstance(Effects.WATER_BREATHING, 200, 0, false, false, true));
        }

        if (boots && legs && this.isInWater()) {
            this.addEffect(new EffectInstance(Effects.TURTLES_GRACE, 200, 0, false, false, true));
        }


        if (boots && legs && chestplate && helmet && this.isInWaterRainOrBubble()) {
            this.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 200, 2, false, false, true));
            this.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 200, 3, false, false, true));
        }

    }

    protected CooldownTracker createItemCooldowns() {
        return new CooldownTracker();
    }

    public int getKillCount(EntityType<?> entityType) {
        return killCounts.getOrDefault(entityType, 0);
    }

    public void incrementKillCount(EntityType<?> entityType) {
        killCounts.put(entityType, killCounts.getOrDefault(entityType, 0) + 1);
    }

    public void resetKillCount(EntityType<?> entityType) {
        killCounts.put(entityType, 0);
    }

    public void resetAllKillCounts() {
        killCounts.clear();
    }


    private void moveCloak() {
        this.xCloakO = this.xCloak;
        this.yCloakO = this.yCloak;
        this.zCloakO = this.zCloak;
        double d0 = this.getX() - this.xCloak;
        double d1 = this.getY() - this.yCloak;
        double d2 = this.getZ() - this.zCloak;
        double d3 = 10.0D;
        if (d0 > 10.0D) {
            this.xCloak = this.getX();
            this.xCloakO = this.xCloak;
        }

        if (d2 > 10.0D) {
            this.zCloak = this.getZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 > 10.0D) {
            this.yCloak = this.getY();
            this.yCloakO = this.yCloak;
        }

        if (d0 < -10.0D) {
            this.xCloak = this.getX();
            this.xCloakO = this.xCloak;
        }

        if (d2 < -10.0D) {
            this.zCloak = this.getZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 < -10.0D) {
            this.yCloak = this.getY();
            this.yCloakO = this.yCloak;
        }

        this.xCloak += d0 * 0.25D;
        this.zCloak += d2 * 0.25D;
        this.yCloak += d1 * 0.25D;
    }

    protected void updatePlayerPose() {
        if (this.canEnterPose(Pose.SWIMMING)) {
            Pose pose;
            if (this.isFallFlying()) {
                pose = Pose.FALL_FLYING;
            } else if (this.isSleeping()) {
                pose = Pose.SLEEPING;
            } else if (this.isSwimming()) {
                pose = Pose.SWIMMING;
            } else if (this.isAutoSpinAttack()) {
                pose = Pose.SPIN_ATTACK;
            } else if (this.isShiftKeyDown() && !this.abilities.flying) {
                pose = Pose.CROUCHING;
            } else {
                pose = Pose.STANDING;
            }

            Pose pose1;
            if (!this.isSpectator() && !this.isPassenger() && !this.canEnterPose(pose)) {
                if (this.canEnterPose(Pose.CROUCHING)) {
                    pose1 = Pose.CROUCHING;
                } else {
                    pose1 = Pose.SWIMMING;
                }
            } else {
                pose1 = pose;
            }

            this.setPose(pose1);
        }
    }

    public int getPortalWaitTime() {
        return this.abilities.invulnerable ? 1 : 80;
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.PLAYER_SWIM;
    }

    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.PLAYER_SPLASH;
    }

    protected SoundEvent getSwimHighSpeedSplashSound() {
        return SoundEvents.PLAYER_SPLASH_HIGH_SPEED;
    }

    public int getDimensionChangingDelay() {
        return 10;
    }

    public void playSound(SoundEvent p_184185_1_, float volume, float pitch) {
        this.level.playSound(this, this.getX(), this.getY(), this.getZ(), p_184185_1_, this.getSoundSource(), volume, pitch);
    }

    public void playNotifySound(SoundEvent p_213823_1_, SoundCategory p_213823_2_, float p_213823_3_, float p_213823_4_) {
    }

    public SoundCategory getSoundSource() {
        return SoundCategory.PLAYERS;
    }

    protected int getFireImmuneTicks() {
        return 20;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte p_70103_1_) {
        if (p_70103_1_ == 9) {
            this.completeUsingItem();
        } else if (p_70103_1_ == 23) {
            this.reducedDebugInfo = false;
        } else if (p_70103_1_ == 22) {
            this.reducedDebugInfo = true;
        } else if (p_70103_1_ == 43) {
            this.addParticlesAroundSelf(ParticleTypes.CLOUD);
        } else {
            super.handleEntityEvent(p_70103_1_);
        }

    }

    @OnlyIn(Dist.CLIENT)
    private void addParticlesAroundSelf(IParticleData p_213824_1_) {
        for (int i = 0; i < 5; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(p_213824_1_, this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), d0, d1, d2);
        }

    }

    protected void closeContainer() {
        this.containerMenu = this.inventoryMenu;
    }

    public void rideTick() {
        if (this.wantsToStopRiding() && this.isPassenger()) {
            this.stopRiding();
            this.setShiftKeyDown(false);
        } else {
            double d0 = this.getX();
            double d1 = this.getY();
            double d2 = this.getZ();
            super.rideTick();
            this.oBob = this.bob;
            this.bob = 0.0F;
            this.checkRidingStatistics(this.getX() - d0, this.getY() - d1, this.getZ() - d2);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void resetPos() {
        this.setPose(Pose.STANDING);
        super.resetPos();
        this.setHealth(this.getMaxHealth());
        this.deathTime = 0;
    }

    protected void serverAiStep() {
        super.serverAiStep();
        this.updateSwingTime();
        this.yHeadRot = this.yRot;
    }

    public void aiStep() {
        if (this.jumpTriggerTime > 0) {
            --this.jumpTriggerTime;
        }

        if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
            if (this.getHealth() < this.getMaxHealth() && this.tickCount % 20 == 0) {
                this.heal(1.0F);
            }

            if (this.foodData.needsFood() && this.tickCount % 10 == 0) {
                this.foodData.setFoodLevel(this.foodData.getFoodLevel() + 1);
            }
        }

        this.inventory.tick();
        this.oBob = this.bob;
        super.aiStep();
        this.flyingSpeed = 0.02F;
        if (this.isSprinting()) {
            this.flyingSpeed = (float) ((double) this.flyingSpeed + 0.005999999865889549D);
        }

        this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
        float f;
        if (this.onGround && !this.isDeadOrDying() && !this.isSwimming()) {
            f = Math.min(0.1F, MathHelper.sqrt(getHorizontalDistanceSqr(this.getDeltaMovement())));
        } else {
            f = 0.0F;
        }

        this.bob += (f - this.bob) * 0.4F;
        if (this.getHealth() > 0.0F && !this.isSpectator()) {
            AxisAlignedBB axisalignedbb;
            if (this.isPassenger() && !this.getVehicle().removed) {
                axisalignedbb = this.getBoundingBox().minmax(this.getVehicle().getBoundingBox()).inflate(1.0D, 0.0D, 1.0D);
            } else {
                axisalignedbb = this.getBoundingBox().inflate(1.0D, 0.5D, 1.0D);
            }

            List<Entity> list = this.level.getEntities(this, axisalignedbb);

            for (int i = 0; i < list.size(); ++i) {
                Entity entity = list.get(i);
                if (!entity.removed) {
                    this.touch(entity);
                }
            }
        }

        this.playShoulderEntityAmbientSound(this.getShoulderEntityLeft());
        this.playShoulderEntityAmbientSound(this.getShoulderEntityRight());
        if (!this.level.isClientSide && (this.fallDistance > 0.5F || this.isInWater()) || this.abilities.flying || this.isSleeping()) {
            this.removeEntitiesOnShoulder();
        }

    }

    private void playShoulderEntityAmbientSound(@Nullable CompoundNBT p_192028_1_) {
        if (p_192028_1_ != null && (!p_192028_1_.contains("Silent") || !p_192028_1_.getBoolean("Silent")) && this.level.random.nextInt(200) == 0) {
            String s = p_192028_1_.getString("id");
            EntityType.byString(s).filter((p_213830_0_) -> {
                return p_213830_0_ == EntityType.PARROT;
            }).ifPresent((p_213834_1_) -> {
                if (!ParrotEntity.imitateNearbyMobs(this.level, this)) {
                    this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), ParrotEntity.getAmbient(this.level, this.level.random), this.getSoundSource(), 1.0F, ParrotEntity.getPitch(this.level.random));
                }

            });
        }

    }

    private void touch(Entity p_71044_1_) {
        p_71044_1_.playerTouch(this);
    }

    public int getScore() {
        return this.entityData.get(DATA_SCORE_ID);
    }

    public void setScore(int p_85040_1_) {
        this.entityData.set(DATA_SCORE_ID, p_85040_1_);
    }

    public void increaseScore(int p_85039_1_) {
        int i = this.getScore();
        this.entityData.set(DATA_SCORE_ID, i + p_85039_1_);
    }

    public void die(DamageSource p_70645_1_) {
        super.die(p_70645_1_);
        this.reapplyPosition();
        if (!this.isSpectator()) {
            this.dropAllDeathLoot(p_70645_1_);
        }

        if (p_70645_1_ != null) {
            this.setDeltaMovement((double) (-MathHelper.cos((this.hurtDir + this.yRot) * ((float) Math.PI / 180F)) * 0.1F), (double) 0.1F, (double) (-MathHelper.sin((this.hurtDir + this.yRot) * ((float) Math.PI / 180F)) * 0.1F));
        } else {
            this.setDeltaMovement(0.0D, 0.1D, 0.0D);
        }

        this.awardStat(Stats.DEATHS);
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        this.clearFire();
        this.setSharedFlag(0, false);
    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            this.destroyVanishingCursedItems();
            this.inventory.dropAll();
        }

    }

    protected void destroyVanishingCursedItems() {
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (!itemstack.isEmpty() && EnchantmentHelper.hasVanishingCurse(itemstack)) {
                this.inventory.removeItemNoUpdate(i);
            }
        }

    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        if (p_184601_1_ == DamageSource.ON_FIRE) {
            return SoundEvents.PLAYER_HURT_ON_FIRE;
        } else if (p_184601_1_ == DamageSource.DROWN) {
            return SoundEvents.PLAYER_HURT_DROWN;
        } else {
            return p_184601_1_ == DamageSource.SWEET_BERRY_BUSH ? SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH : SoundEvents.PLAYER_HURT;
        }
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }

    public boolean drop(boolean p_225609_1_) {
        return this.drop(this.inventory.removeItem(this.inventory.selected, p_225609_1_ && !this.inventory.getSelected().isEmpty() ? this.inventory.getSelected().getCount() : 1), false, true) != null;
    }

    @Nullable
    public ItemEntity drop(ItemStack p_71019_1_, boolean p_71019_2_) {
        return this.drop(p_71019_1_, false, p_71019_2_);
    }

    @Nullable
    public ItemEntity drop(ItemStack p_146097_1_, boolean p_146097_2_, boolean p_146097_3_) {
        if (p_146097_1_.isEmpty()) {
            return null;
        } else {
            if (this.level.isClientSide) {
                this.swing(Hand.MAIN_HAND);
            }

            double d0 = this.getEyeY() - (double) 0.3F;
            ItemEntity itementity = new ItemEntity(this.level, this.getX(), d0, this.getZ(), p_146097_1_);
            itementity.setPickUpDelay(40);
            if (p_146097_3_) {
                itementity.setThrower(this.getUUID());
            }

            if (p_146097_2_) {
                float f = this.random.nextFloat() * 0.5F;
                float f1 = this.random.nextFloat() * ((float) Math.PI * 2F);
                itementity.setDeltaMovement((double) (-MathHelper.sin(f1) * f), (double) 0.2F, (double) (MathHelper.cos(f1) * f));
            } else {
                float f7 = 0.3F;
                float f8 = MathHelper.sin(this.xRot * ((float) Math.PI / 180F));
                float f2 = MathHelper.cos(this.xRot * ((float) Math.PI / 180F));
                float f3 = MathHelper.sin(this.yRot * ((float) Math.PI / 180F));
                float f4 = MathHelper.cos(this.yRot * ((float) Math.PI / 180F));
                float f5 = this.random.nextFloat() * ((float) Math.PI * 2F);
                float f6 = 0.02F * this.random.nextFloat();
                itementity.setDeltaMovement((double) (-f3 * f2 * 0.3F) + Math.cos((double) f5) * (double) f6, (double) (-f8 * 0.3F + 0.1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double) (f4 * f2 * 0.3F) + Math.sin((double) f5) * (double) f6);
            }

            return itementity;
        }
    }

    public float getDestroySpeed(BlockState p_184813_1_) {
        float f = this.inventory.getDestroySpeed(p_184813_1_);
        if (f > 1.0F) {
            int i = EnchantmentHelper.getBlockEfficiency(this);
            ItemStack itemstack = this.getMainHandItem();
            if (i > 0 && !itemstack.isEmpty()) {
                f += (float) (i * i + 1);
            }
        }

        if (EffectUtils.hasDigSpeed(this)) {
            f *= 1.0F + (float) (EffectUtils.getDigSpeedAmplification(this) + 1) * 0.2F;
        }

        float multiplier = 5.0F;

        if (this.hasEffect(Effects.DIG_SLOWDOWN)) {
            float f1;
            switch (this.getEffect(Effects.DIG_SLOWDOWN).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;
                case 1:
                    f1 = 0.09F;
                    break;
                case 2:
                    f1 = 0.0027F;
                    break;
                case 3:
                default:
                    f1 = 8.1E-4F;
            }

            f *= f1;
        }

        if (this.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) {
            f /= multiplier;
        }

        if (!this.onGround) {


            if (isPassenger() && getRootVehicle() instanceof Mob mob) {
                if (mob.getBodyRotationControl().isMovingAtAll()) {
                    f /= ( !(mob instanceof HappyGhastEntity) ? (isInWaterOrBubble() ? 5.0F : 2.5F) : 5.0F);
                }
            } else if (isPassenger()) {
                if (getRootVehicle() instanceof BoatEntity || getRootVehicle() instanceof AbstractMinecartEntity) {
                    Entity rv = this.getRootVehicle();

                    double d0 = rv.getX() - rv.xo;
                    double d1 = rv.getZ() - rv.zo;
                    if (d0 * d0 + d1 * d1 > (double) 2.5000003E-7F) {
                        f /= (rv.isInWaterOrBubble() ? (rv instanceof BoatEntity ? 3.5F : 5.0F) : 2.5F);
                    } else if (rv.isInWaterOrBubble()) {
                        if (!(rv instanceof BoatEntity) && rv.isInWaterOrBubble()) {
                            f /= 5.0F;
                        }
                    }
                } else {
                    f /= 5.0F;
                }
            } else {
                f /= 5.0F;
            }


        }

        return f;
    }

    public boolean hasCorrectToolForDrops(BlockState p_234569_1_) {
        return !p_234569_1_.requiresCorrectToolForDrops() || this.inventory.getSelected().isCorrectToolForDrops(p_234569_1_);
    }

    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        addictions.readAdditionalSaveData(compoundNBT);
        skills.readAdditionalSaveData(compoundNBT);
        this.setUUID(createPlayerUUID(this.gameProfile));
        ListNBT listnbt = compoundNBT.getList("Inventory", 10);
        this.inventory.load(listnbt);
        this.inventory.selected = compoundNBT.getInt("SelectedItemSlot");
        this.sleepCounter = compoundNBT.getShort("SleepTimer");
        this.experienceProgress = compoundNBT.getFloat("XpP");
        this.experienceLevel = compoundNBT.getInt("XpLevel");
        this.totalExperience = compoundNBT.getInt("XpTotal");
        this.enchantmentSeed = compoundNBT.getInt("XpSeed");
        if (this.enchantmentSeed == 0) {
            this.enchantmentSeed = this.random.nextInt();
        }

        this.setScore(compoundNBT.getInt("Score"));
        this.foodData.readAdditionalSaveData(compoundNBT);
        this.abilities.loadSaveData(compoundNBT);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double) this.abilities.getWalkingSpeed());
        if (compoundNBT.contains("EnderItems", 9)) {
            this.enderChestInventory.fromTag(compoundNBT.getList("EnderItems", 10));
        }

        if (compoundNBT.contains("ShoulderEntityLeft", 10)) {
            this.setShoulderEntityLeft(compoundNBT.getCompound("ShoulderEntityLeft"));
        }

        if (compoundNBT.contains("ShoulderEntityRight", 10)) {
            this.setShoulderEntityRight(compoundNBT.getCompound("ShoulderEntityRight"));
        }

        // Read quests data
        if (compoundNBT.contains("Quests", 9)) {
            this.questManager.loadQuestData(compoundNBT);
        }

    }

    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        addictions.addAdditionalSaveData(compoundNBT);
        skills.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        compoundNBT.put("Inventory", this.inventory.save(new ListNBT()));
        compoundNBT.putInt("SelectedItemSlot", this.inventory.selected);
        compoundNBT.putShort("SleepTimer", (short) this.sleepCounter);
        compoundNBT.putFloat("XpP", this.experienceProgress);
        compoundNBT.putInt("XpLevel", this.experienceLevel);
        compoundNBT.putInt("XpTotal", this.totalExperience);
        compoundNBT.putInt("XpSeed", this.enchantmentSeed);
        compoundNBT.putInt("Score", this.getScore());
        this.foodData.addAdditionalSaveData(compoundNBT);
        this.abilities.addSaveData(compoundNBT);
        compoundNBT.put("EnderItems", this.enderChestInventory.createTag());
        if (!this.getShoulderEntityLeft().isEmpty()) {
            compoundNBT.put("ShoulderEntityLeft", this.getShoulderEntityLeft());
        }

        if (!this.getShoulderEntityRight().isEmpty()) {
            compoundNBT.put("ShoulderEntityRight", this.getShoulderEntityRight());
        }
        this.questManager.saveQuestData(compoundNBT);

    }

    public boolean isInvulnerableTo(DamageSource p_180431_1_) {


        if (super.isInvulnerableTo(p_180431_1_)) {
            return true;
        } else if (p_180431_1_ == DamageSource.DROWN) {
            return !this.level.getGameRules().getBoolean(GameRules.RULE_DROWNING_DAMAGE);
        } else if (p_180431_1_ == DamageSource.FALL) {
            return !this.level.getGameRules().getBoolean(GameRules.RULE_FALL_DAMAGE);
        } else if (p_180431_1_.isFire()) {
            return !this.level.getGameRules().getBoolean(GameRules.RULE_FIRE_DAMAGE);
        } else {
            return false;
        }
    }

    public boolean hurt(DamageSource p_70097_1_, float damage) {
        if (this.isInvulnerableTo(p_70097_1_)) {
            return false;
        } else if (this.abilities.invulnerable && !p_70097_1_.isBypassInvul()) {
            return false;
        } else {
            this.noActionTime = 0;
            if (this.isDeadOrDying()) {
                return false;
            } else {
                this.removeEntitiesOnShoulder();
                if (p_70097_1_.scalesWithDifficulty()) {
                    if (this.level.getDifficulty() == Difficulty.PEACEFUL && !this.veryHardmode()) {
                        damage = 0.0F;
                    }

                    if (this.level.getDifficulty() == Difficulty.EASY) {
                        damage = Math.min(damage / 2.0F + 1.0F, damage);
                    }

                    if (this.level.getDifficulty() == Difficulty.HARD) {
                        damage = damage * 3.0F / 2.0F;
                    }

                    // Add scaling for very hardmode
                    if (this.veryHardmode()) {
                        damage *= 2.0F;
                    }
                }

                boolean hasObsidianRose = false;

                ItemStack stack = hasAccessoryHolder() ? inventory.getItem(inventory.findSlotMatching(Items.ACCESSORY_HOLDER)) : null;

                if (stack != null) {
                    for (ItemStack item : AccessoryHolderItem.getContents(stack).collect(Collectors.toList())) {
                        if (item.getItem() instanceof ObsidianRoseItem || item.getItem() instanceof MoltenSkullRoseItem) {
                            hasObsidianRose = true;
                        }
                    }
                }

                if (hasObsidianRose && (p_70097_1_ == DamageSource.LAVA || p_70097_1_ == DamageSource.ON_FIRE || p_70097_1_ == DamageSource.IN_FIRE || p_70097_1_ == DamageSource.HOT_FLOOR)) {
                    damage *= 0.4375;
                }

                if (this.getHealth() < (this.getMaxHealth() / 2) && this.getAccessoryHolder() != null && this.hasItemInHolder(getAccessoryHolder(), Items.FROZEN_SHIELD)) {
                    damage *= 0.75;
                    AccessoryHolderItem.getContents(this.getAccessoryHolder()).filter(item -> item.get() == Items.FROZEN_SHIELD).forEach(item -> {
                        item.hurt(1, this);
                    });
                }

                if (this.getAccessoryHolder() != null && this.hasItemInHolder(getAccessoryHolder(), Items.WORM_SCARF)) {
                    damage *= 0.83;
                    AccessoryHolderItem.getContents(this.getAccessoryHolder()).filter(item -> item.get() == Items.WORM_SCARF).forEach(item -> {
                        item.hurt(1, this);
                    });
                }

                if (this.getAccessoryHolder() != null && this.hasItemInHolder(getAccessoryHolder(), Items.SHIELD_OF_CTHULHU)) {
                    damage *= 0.98;
                }


                return damage == 0.0F ? false : super.hurt(p_70097_1_, damage);
            }
        }
    }

    public boolean hasAccessoryHolder() {
        return inventory.findSlotMatching(Items.ACCESSORY_HOLDER) != -1;
    }

    @Nullable
    public ItemStack getAccessoryHolder() {
        return hasAccessoryHolder() ? inventory.getItem(inventory.findSlotMatching(Items.ACCESSORY_HOLDER)) : null;
    }

    public boolean hasItemInHolder(ItemStack holder, Item item) {
        for (ItemStack stack : AccessoryHolderItem.getContents(holder).collect(Collectors.toList())) {
            if (stack.getItem() == item) return true;
        }
        return false;
    }

    protected void blockUsingShield(LivingEntity p_190629_1_) {
        super.blockUsingShield(p_190629_1_);
        if (p_190629_1_.canDisableShield()) {
            this.disableShield(true, p_190629_1_);
        }

    }

    public boolean canHarmPlayer(PlayerEntity p_96122_1_) {
        Team team = this.getTeam();
        Team team1 = p_96122_1_.getTeam();
        if (team == null) {
            return true;
        } else {
            return !team.isAlliedTo(team1) ? true : team.isAllowFriendlyFire();
        }
    }

    protected void hurtArmor(DamageSource p_230294_1_, float p_230294_2_) {
        this.inventory.hurtArmor(p_230294_1_, p_230294_2_);
    }

    protected void hurtCurrentlyUsedShield(float p_184590_1_) {
        if (this.useItem.getItem() == Items.SHIELD || this.useItem.getItem() == Items.NETHERITE_SHIELD || this.useItem.get() == Items.SHIELD_OF_CTHULHU) {
            if (!this.level.isClientSide) {
                this.awardStat(Stats.ITEM_USED.get(this.useItem.getItem()));
            }

            if (p_184590_1_ >= 3.0F) {
                int i = 1 + MathHelper.floor(p_184590_1_);
                Hand hand = this.getUsedItemHand();
                this.useItem.hurtAndBreak(i, this, (p_213833_1_) -> {
                    p_213833_1_.broadcastBreakEvent(hand);
                });
                if (this.useItem.isEmpty()) {
                    if (hand == Hand.MAIN_HAND) {
                        this.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
                    } else {
                        this.setItemSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
                    }

                    this.useItem = ItemStack.EMPTY;
                    this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level.random.nextFloat() * 0.4F);
                }
            }

        }
    }

    protected void actuallyHurt(DamageSource p_70665_1_, float p_70665_2_) {
        if (!this.isInvulnerableTo(p_70665_1_)) {
            p_70665_2_ = this.getDamageAfterArmorAbsorb(p_70665_1_, p_70665_2_);
            p_70665_2_ = this.getDamageAfterMagicAbsorb(p_70665_1_, p_70665_2_);
            float f2 = Math.max(p_70665_2_ - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (p_70665_2_ - f2));
            float f = p_70665_2_ - f2;
            if (f > 0.0F && f < 3.4028235E37F) {
                this.awardStat(Stats.DAMAGE_ABSORBED, Math.round(f * 10.0F));
            }

            if (f2 != 0.0F) {
                this.causeFoodExhaustion(p_70665_1_.getFoodExhaustion());
                float f1 = this.getHealth();
                this.setHealth(this.getHealth() - f2);
                this.getCombatTracker().recordDamage(p_70665_1_, f1, f2);
                if (f2 < 3.4028235E37F) {
                    this.awardStat(Stats.DAMAGE_TAKEN, Math.round(f2 * 10.0F));
                }
                this.gameEvent(GameEvent.ENTITY_DAMAGE);
            }
        }
    }

    protected boolean onSoulSpeedBlock() {
        return !this.abilities.flying && super.onSoulSpeedBlock();
    }

    public void openTextEdit(SignTileEntity p_175141_1_) {
    }

    public void openMinecartCommandBlock(CommandBlockLogic p_184809_1_) {
    }

    public void openCommandBlock(CommandBlockTileEntity p_184824_1_) {
    }

    public void openStructureBlock(StructureBlockTileEntity p_189807_1_) {
    }

    public void openJigsawBlock(JigsawTileEntity p_213826_1_) {
    }

    public void openHorseInventory(AbstractHorseEntity p_184826_1_, IInventory p_184826_2_) {
    }

    public OptionalInt openMenu(@Nullable INamedContainerProvider p_213829_1_) {
        return OptionalInt.empty();
    }

    public void sendMerchantOffers(int p_213818_1_, MerchantOffers p_213818_2_, int p_213818_3_, int p_213818_4_, boolean p_213818_5_, boolean p_213818_6_) {
    }

    public void openItemGui(ItemStack p_184814_1_, Hand p_184814_2_) {
    }

    public ActionResultType interactOn(Entity p_190775_1_, Hand p_190775_2_) {
        if (this.isSpectator()) {
            if (p_190775_1_ instanceof INamedContainerProvider) {
                this.openMenu((INamedContainerProvider) p_190775_1_);
            }

            return ActionResultType.PASS;
        } else {
            ItemStack itemstack = this.getItemInHand(p_190775_2_);
            ItemStack itemstack1 = itemstack.copy();
            ActionResultType actionresulttype = p_190775_1_.interact(this, p_190775_2_);
            if (actionresulttype.consumesAction()) {
                if (this.abilities.instabuild && itemstack == this.getItemInHand(p_190775_2_) && itemstack.getCount() < itemstack1.getCount()) {
                    itemstack.setCount(itemstack1.getCount());
                }

                return actionresulttype;
            } else {
                if (!itemstack.isEmpty() && p_190775_1_ instanceof LivingEntity) {
                    if (this.abilities.instabuild) {
                        itemstack = itemstack1;
                    }

                    ActionResultType actionresulttype1 = itemstack.interactLivingEntity(this, (LivingEntity) p_190775_1_, p_190775_2_);
                    if (actionresulttype1.consumesAction()) {
                        if (itemstack.isEmpty() && !this.abilities.instabuild) {
                            this.setItemInHand(p_190775_2_, ItemStack.EMPTY);
                        }

                        return actionresulttype1;
                    }
                }

                return ActionResultType.PASS;
            }
        }
    }

    public double getMyRidingOffset() {
        return -0.35D;
    }

    public void removeVehicle() {
        super.removeVehicle();
        this.boardingCooldown = 0;
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.isSleeping();
    }

    public boolean isAffectedByFluids() {
        return !this.abilities.flying;
    }

    protected Vector3d maybeBackOffFromEdge(Vector3d p_225514_1_, MoverType p_225514_2_) {
        if (!this.abilities.flying && (p_225514_2_ == MoverType.SELF || p_225514_2_ == MoverType.PLAYER) && this.isStayingOnGroundSurface() && this.isAboveGround()) {
            double d0 = p_225514_1_.x;
            double d1 = p_225514_1_.z;
            double d2 = 0.05D;

            while (d0 != 0.0D && this.level.noCollision(this, this.getBoundingBox().move(d0, (double) (-this.maxUpStep), 0.0D))) {
                if (d0 < 0.05D && d0 >= -0.05D) {
                    d0 = 0.0D;
                } else if (d0 > 0.0D) {
                    d0 -= 0.05D;
                } else {
                    d0 += 0.05D;
                }
            }

            while (d1 != 0.0D && this.level.noCollision(this, this.getBoundingBox().move(0.0D, (double) (-this.maxUpStep), d1))) {
                if (d1 < 0.05D && d1 >= -0.05D) {
                    d1 = 0.0D;
                } else if (d1 > 0.0D) {
                    d1 -= 0.05D;
                } else {
                    d1 += 0.05D;
                }
            }

            while (d0 != 0.0D && d1 != 0.0D && this.level.noCollision(this, this.getBoundingBox().move(d0, (double) (-this.maxUpStep), d1))) {
                if (d0 < 0.05D && d0 >= -0.05D) {
                    d0 = 0.0D;
                } else if (d0 > 0.0D) {
                    d0 -= 0.05D;
                } else {
                    d0 += 0.05D;
                }

                if (d1 < 0.05D && d1 >= -0.05D) {
                    d1 = 0.0D;
                } else if (d1 > 0.0D) {
                    d1 -= 0.05D;
                } else {
                    d1 += 0.05D;
                }
            }

            p_225514_1_ = new Vector3d(d0, p_225514_1_.y, d1);
        }

        return p_225514_1_;
    }

    private boolean isAboveGround() {
        return this.onGround || this.fallDistance < this.maxUpStep && !this.level.noCollision(this, this.getBoundingBox().move(0.0D, (double) (this.fallDistance - this.maxUpStep), 0.0D));
    }

    public void attack(Entity p_71059_1_) {
        if (p_71059_1_.isAttackable()) {
            if (!p_71059_1_.skipAttackInteraction(this)) {
                float damageAttribute = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float f1;
                if (p_71059_1_ instanceof LivingEntity) {
                    f1 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity) p_71059_1_).getMobType());
                } else {
                    f1 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), CreatureAttribute.UNDEFINED);
                }

                float f2 = this.getAttackStrengthScale(0.5F);
                damageAttribute = damageAttribute * (0.2F + f2 * f2 * 0.8F);
                f1 = f1 * f2;
                this.resetAttackStrengthTicker();
                if (damageAttribute > 0.0F || f1 > 0.0F) {
                    boolean flag = f2 > 0.9F;
                    boolean flag1 = false;
                    int i = 0;
                    i = i + EnchantmentHelper.getKnockbackBonus(this);
                    if (this.isSprinting() && flag) {
                        this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0F, 1.0F);
                        ++i;
                        flag1 = true;
                    }

                    boolean flag2 = flag && this.fallDistance > 0.0F && !this.onGround && !this.onClimbable() && !this.isInWater() && !this.hasEffect(Effects.BLINDNESS) && !this.isPassenger() && p_71059_1_ instanceof LivingEntity && !(this.getItemInHand(Hand.MAIN_HAND).get() instanceof BatItem);
                    flag2 = flag2 && !this.isSprinting();
                    if (flag2) {
                        damageAttribute *= 1.5F;
                    }

                    damageAttribute = damageAttribute + f1;
                    boolean doSweeping = false;
                    double d0 = (double) (this.walkDist - this.walkDistO);
                    if (flag && !flag2 && !flag1 && this.onGround && d0 < (double) this.getSpeed()) {
                        ItemStack itemstack = this.getItemInHand(Hand.MAIN_HAND);
                        if (itemstack.getItem() instanceof SwordItem) {
                            doSweeping = true;
                        }
                    }

                    float f4 = 0.0F;
                    boolean flag4 = false;
                    int j = EnchantmentHelper.getFireAspect(this);
                    if (p_71059_1_ instanceof LivingEntity) {
                        f4 = ((LivingEntity) p_71059_1_).getHealth();
                        if (j > 0 && !p_71059_1_.isOnFire()) {
                            flag4 = true;
                            p_71059_1_.setSecondsOnFire(1);
                        }
                    }

                    Vector3d vector3d = p_71059_1_.getDeltaMovement();
                    boolean flag5 = p_71059_1_.hurt(DamageSource.playerAttack(this), damageAttribute);

                    if (this.getItemInHand(Hand.MAIN_HAND).getItem() == Items.WITHER_BONE_CUTLASS) {
                        float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(this) * damageAttribute;
                        int sweepingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, this);
                        double dd;


                        f3 *= 1.5F;
                        sweepingLevel += RandomSource.create(this.random.nextLong()).nextInt((int) Math.min(MathHelper.clamp(sweepingLevel, MathHelper.randomBetween(random, 2, 5), 5), MathHelper.randomBetweenInclusive(random, 3, 6)));
                        dd = 12.0D + MathHelper.randomBetweenInclusive(random, random.nextInt(Math.max(1, sweepingLevel) + 1), random.nextInt(sweepingLevel + 1, sweepingLevel + 5));


                        for (LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, p_71059_1_.getBoundingBox().inflate(1.0D + (0.05 * sweepingLevel), 0.25D + (0.05 * sweepingLevel), 1.0D + (0.05 * sweepingLevel)))) {
                            if (livingentity != this && livingentity != p_71059_1_ && !this.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity) livingentity).isMarker()) && this.distanceToSqr(livingentity) < dd) {
                                livingentity.knockback(0.4F, (double) MathHelper.sin(this.yRot * ((float) Math.PI / 180F)), (double) (-MathHelper.cos(this.yRot * ((float) Math.PI / 180F))));
                                livingentity.hurt(DamageSource.playerAttack(this), f3);
                            }
                        }

                        this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0F, 1.0F);
                        this.multiSweepAttack();
                    }

                    if (flag5) {
                        if (this.getItemInHand(Hand.MAIN_HAND).get() instanceof BatItem) {
                            i += 1;
                        }
                        if (i > 0) {
                            if (p_71059_1_ instanceof LivingEntity) {
                                ((LivingEntity) p_71059_1_).knockback((float) i * 0.5F, (double) MathHelper.sin(this.yRot * ((float) Math.PI / 180F)), (double) (-MathHelper.cos(this.yRot * ((float) Math.PI / 180F))));
                            } else {
                                p_71059_1_.push((double) (-MathHelper.sin(this.yRot * ((float) Math.PI / 180F)) * (float) i * 0.5F), 0.1D, (double) (MathHelper.cos(this.yRot * ((float) Math.PI / 180F)) * (float) i * 0.5F));
                            }

                            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                            this.setSprinting(false);
                        }

                        if (doSweeping && this.getItemInHand(Hand.MAIN_HAND).getItem() != Items.WITHER_BONE_CUTLASS) {
                            float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(this) * damageAttribute;
                            int sweepingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, this);
                            double dd = 9.0D;

                            for (LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, p_71059_1_.getBoundingBox().inflate(1.0D + (0.05 * sweepingLevel), 0.25D + (0.05 * sweepingLevel), 1.0D + (0.05 * sweepingLevel)))) {
                                if (livingentity != this && livingentity != p_71059_1_ && !this.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity) livingentity).isMarker()) && this.distanceToSqr(livingentity) < dd) {
                                    livingentity.knockback(0.4F, (double) MathHelper.sin(this.yRot * ((float) Math.PI / 180F)), (double) (-MathHelper.cos(this.yRot * ((float) Math.PI / 180F))));
                                    livingentity.hurt(DamageSource.playerAttack(this), f3);
                                }
                            }

                            this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0F, 1.0F);
                            this.sweepAttack();
                        }

                        if (p_71059_1_ instanceof ServerPlayerEntity && p_71059_1_.hurtMarked) {
                            ((ServerPlayerEntity) p_71059_1_).connection.send(new SEntityVelocityPacket(p_71059_1_));
                            p_71059_1_.hurtMarked = false;
                            p_71059_1_.setDeltaMovement(vector3d);
                        }

                        if (flag2) {
                            this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, this.getSoundSource(), 1.0F, 1.0F);
                            this.crit(p_71059_1_);
                        }

                        if (!flag2 && !doSweeping) {
                            if (flag) {
                                this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, this.getSoundSource(), 1.0F, 1.0F);
                            } else {
                                this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, this.getSoundSource(), 1.0F, 1.0F);
                            }
                        }

                        if (f1 > 0.0F) {
                            this.magicCrit(p_71059_1_);
                        }

                        this.setLastHurtMob(p_71059_1_);
                        if (p_71059_1_ instanceof LivingEntity) {
                            EnchantmentHelper.doPostHurtEffects((LivingEntity) p_71059_1_, this);
                        }

                        EnchantmentHelper.doPostDamageEffects(this, p_71059_1_);
                        ItemStack itemstack1 = this.getMainHandItem();
                        Entity entity = p_71059_1_;
                        if (p_71059_1_ instanceof EnderDragonPartEntity) {
                            entity = ((EnderDragonPartEntity) p_71059_1_).parentMob;
                        }

                        if (!this.level.isClientSide && !itemstack1.isEmpty() && entity instanceof LivingEntity) {
                            itemstack1.hurtEnemy((LivingEntity) entity, this);
                            if (itemstack1.isEmpty()) {
                                this.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                            }
                        }

                        if (p_71059_1_ instanceof LivingEntity) {
                            float f5 = f4 - ((LivingEntity) p_71059_1_).getHealth();
                            this.awardStat(Stats.DAMAGE_DEALT, Math.round(f5 * 10.0F));
                            if (j > 0) {
                                p_71059_1_.setSecondsOnFire(j * 4);
                            }

                            if (this.level instanceof ServerWorld && f5 > 2.0F) {
                                int k = (int) ((double) f5 * 0.5D);
                                ((ServerWorld) this.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, p_71059_1_.getX(), p_71059_1_.getY(0.5D), p_71059_1_.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }

                        this.causeFoodExhaustion(0.1F);
                    } else {
                        if (!(p_71059_1_ instanceof CreakingEntity)) {
                            this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource(), 1.0F, 1.0F);
                        }
                        if (flag4) {
                            p_71059_1_.clearFire();
                        }
                    }
                }

            }
        }
    }

    protected void doAutoAttackOnTouch(LivingEntity p_204804_1_) {
        this.attack(p_204804_1_);
    }

    public void disableShield(boolean p_190777_1_, LivingEntity entity) {
        float f = 0.25F + (float) EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
        if (p_190777_1_) {
            f += 0.75F;
        }

        if (this.random.nextFloat() < f) {
            int b = entity.getDisableShieldTime();

            this.getCooldowns().addCooldown(Items.SHIELD, b);
            this.getCooldowns().addCooldown(Items.NETHERITE_SHIELD, entity.canDisableShield() ? b : 30);
            this.getCooldowns().addCooldown(Items.SHIELD_OF_CTHULHU, entity.canDisableShield() ? b : 45);
            this.stopUsingItem();
            this.level.broadcastEntityEvent(this, (byte) 30);
        }

    }


    public void crit(Entity p_71009_1_) {
    }

    public void magicCrit(Entity p_71047_1_) {
    }

    public void sweepAttack() {
        double d0 = (double) (-MathHelper.sin(this.yRot * ((float) Math.PI / 180F)));
        double d1 = (double) MathHelper.cos(this.yRot * ((float) Math.PI / 180F));
        if (this.level instanceof ServerWorld) {
            ((ServerWorld) this.level).sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX() + d0, this.getY(0.5D), this.getZ() + d1, 0, d0, 0.0D, d1, 0.0D);
        }

    }

    public void multiSweepAttack() {
        if (this.level instanceof ServerWorld serverWorld) {
            int sweeps = random.nextInt(1, 3);
            float baseYaw = this.yRot;

            for (int i = 0; i < sweeps; i++) {
                float angleStep = 0F + random.nextInt(-10, 10);

                float currentYaw = baseYaw + (i - (sweeps / 2.0F)) * angleStep; // center around baseYaw
                double radians = currentYaw * (Math.PI / 180F);
                double d0 = -MathHelper.sin((float) radians);
                double d1 = MathHelper.cos((float) radians);

                serverWorld.sendParticles(
                        ParticleTypes.SWEEP_ATTACK,
                        this.getX() + d0,
                        this.getY(random.nextDouble(0.5D, 0.8D)),
                        this.getZ() + d1,
                        0, d0, 0.0D, d1, 0.0D
                );
            }
        }
    }


    @OnlyIn(Dist.CLIENT)
    public void respawn() {
    }

    public void remove() {
        super.remove();
        this.inventoryMenu.removed(this);
        if (this.containerMenu != null) {
            this.containerMenu.removed(this);
        }

    }

    public boolean isLocalPlayer() {
        return false;
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public Either<SleepResult, Unit> startSleepInBed(BlockPos p_213819_1_) {
        this.startSleeping(p_213819_1_);
        this.sleepCounter = 0;
        return Either.right(Unit.INSTANCE);
    }

    public void stopSleepInBed(boolean p_225652_1_, boolean p_225652_2_) {
        super.stopSleeping();
        if (this.level instanceof ServerWorld && p_225652_2_) {
            ((ServerWorld) this.level).updateSleepingPlayerList();
        }

        this.sleepCounter = p_225652_1_ ? 0 : 100;
    }

    public void stopSleeping() {
        this.stopSleepInBed(true, true);
    }

    public static Optional<Vector3d> findRespawnPositionAndUseSpawnBlock(ServerWorld p_242374_0_, BlockPos p_242374_1_, float p_242374_2_, boolean p_242374_3_, boolean p_242374_4_) {
        BlockState blockstate = p_242374_0_.getBlockState(p_242374_1_);
        Block block = blockstate.getBlock();
        if (block instanceof RespawnAnchorBlock && blockstate.getValue(RespawnAnchorBlock.CHARGE) > 0 && RespawnAnchorBlock.canSetSpawn(p_242374_0_)) {
            Optional<Vector3d> optional = RespawnAnchorBlock.findStandUpPosition(EntityType.PLAYER, p_242374_0_, p_242374_1_);
            if (!p_242374_4_ && optional.isPresent()) {
                p_242374_0_.setBlock(p_242374_1_, blockstate.setValue(RespawnAnchorBlock.CHARGE, Integer.valueOf(blockstate.getValue(RespawnAnchorBlock.CHARGE) - 1)), 3);
            }

            return optional;
        } else if (block instanceof BedBlock && BedBlock.canSetSpawn(p_242374_0_)) {
            return BedBlock.findStandUpPosition(EntityType.PLAYER, p_242374_0_, p_242374_1_, p_242374_2_);
        } else if (!p_242374_3_) {
            return Optional.empty();
        } else {
            boolean flag = block.isPossibleToRespawnInThis();
            boolean flag1 = p_242374_0_.getBlockState(p_242374_1_.above()).getBlock().isPossibleToRespawnInThis();
            return flag && flag1 ? Optional.of(new Vector3d((double) p_242374_1_.getX() + 0.5D, (double) p_242374_1_.getY() + 0.1D, (double) p_242374_1_.getZ() + 0.5D)) : Optional.empty();
        }
    }

    public boolean isSleepingLongEnough() {
        return this.isSleeping() && this.sleepCounter >= 100;
    }

    public int getSleepTimer() {
        return this.sleepCounter;
    }

    public void displayClientMessage(ITextComponent p_146105_1_, boolean p_146105_2_) {
    }

    public void awardStat(ResourceLocation p_195066_1_) {
        this.awardStat(Stats.CUSTOM.get(p_195066_1_));
    }

    public void awardStat(ResourceLocation location, int i) {
        this.awardStat(Stats.CUSTOM.get(location), i);
    }

    public void awardStat(Stat<?> p_71029_1_) {
        this.awardStat(p_71029_1_, 1);
    }

    public void awardStat(Stat<?> p_71064_1_, int p_71064_2_) {
    }

    public void resetStat(Stat<?> p_175145_1_) {
    }

    public int awardRecipes(Collection<IRecipe<?>> p_195065_1_) {
        return 0;
    }

    public void awardRecipesByKey(ResourceLocation[] p_193102_1_) {
    }

    public int resetRecipes(Collection<IRecipe<?>> p_195069_1_) {
        return 0;
    }

    public void jumpFromGround() {
        super.jumpFromGround();
        this.awardStat(Stats.JUMP);
        if (this.isSprinting()) {
            this.causeFoodExhaustion(0.2F);
        } else {
            this.causeFoodExhaustion(0.05F);
        }

    }

    public void travel(Vector3d p_213352_1_) {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        if (this.isSwimming() && !this.isPassenger()) {
            double d3 = this.getLookAngle().y;
            double d4 = d3 < -0.2D ? 0.085D : 0.06D;
            if (d3 <= 0.0D || this.jumping || !this.level.getBlockState(new BlockPos(this.getX(), this.getY() + 1.0D - 0.1D, this.getZ())).getFluidState().isEmpty()) {
                Vector3d vector3d1 = this.getDeltaMovement();
                this.setDeltaMovement(vector3d1.add(0.0D, (d3 - vector3d1.y) * d4, 0.0D));
            }
        }

        if (this.abilities.flying && !this.isPassenger()) {
            double d5 = this.getDeltaMovement().y;
            float f = this.flyingSpeed;
            this.flyingSpeed = this.abilities.getFlyingSpeed() * (float) (this.isSprinting() ? 2 : 1);
            super.travel(p_213352_1_);
            Vector3d vector3d = this.getDeltaMovement();
            this.setDeltaMovement(vector3d.x, d5 * 0.6D, vector3d.z);
            this.flyingSpeed = f;
            this.fallDistance = 0.0F;
            this.setSharedFlag(7, false);
        } else {
            super.travel(p_213352_1_);
        }

        this.checkMovementStatistics(this.getX() - d0, this.getY() - d1, this.getZ() - d2);
    }

    public void updateSwimming() {
        if (this.abilities.flying) {
            this.setSwimming(false);
        } else {
            super.updateSwimming();
        }

    }

    protected boolean freeAt(BlockPos p_207401_1_) {
        return !this.level.getBlockState(p_207401_1_).isSuffocating(this.level, p_207401_1_);
    }

    public float getSpeed() {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    public void checkMovementStatistics(double p_71000_1_, double p_71000_3_, double p_71000_5_) {
        if (!this.isPassenger()) {
            if (this.isSwimming()) {
                int i = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_3_ * p_71000_3_ + p_71000_5_ * p_71000_5_) * 100.0F);
                if (i > 0) {
                    this.awardStat(Stats.SWIM_ONE_CM, i);
                    this.causeFoodExhaustion(0.01F * (float) i * 0.01F);
                }
            } else if (this.isEyeInFluid(FluidTags.WATER)) {
                int j = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_3_ * p_71000_3_ + p_71000_5_ * p_71000_5_) * 100.0F);
                if (j > 0) {
                    this.awardStat(Stats.WALK_UNDER_WATER_ONE_CM, j);
                    this.causeFoodExhaustion(0.01F * (float) j * 0.01F);
                }
            } else if (this.isInWater()) {
                int k = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0F);
                if (k > 0) {
                    this.awardStat(Stats.WALK_ON_WATER_ONE_CM, k);
                    this.causeFoodExhaustion(0.01F * (float) k * 0.01F);
                }
            } else if (this.onClimbable()) {
                if (p_71000_3_ > 0.0D) {
                    this.awardStat(Stats.CLIMB_ONE_CM, (int) Math.round(p_71000_3_ * 100.0D));
                }
            } else if (this.onGround) {
                int l = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0F);
                if (l > 0) {
                    if (this.isSprinting()) {
                        this.awardStat(Stats.SPRINT_ONE_CM, l);
                        this.causeFoodExhaustion(0.1F * (float) l * 0.01F);
                    } else if (this.isCrouching()) {
                        this.awardStat(Stats.CROUCH_ONE_CM, l);
                        this.causeFoodExhaustion(0.0F * (float) l * 0.01F);
                    } else {
                        this.awardStat(Stats.WALK_ONE_CM, l);
                        this.causeFoodExhaustion(0.0F * (float) l * 0.01F);
                    }
                }
            } else if (this.isFallFlying()) {
                int i1 = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_3_ * p_71000_3_ + p_71000_5_ * p_71000_5_) * 100.0F);
                this.awardStat(Stats.AVIATE_ONE_CM, i1);
            } else {
                int j1 = Math.round(MathHelper.sqrt(p_71000_1_ * p_71000_1_ + p_71000_5_ * p_71000_5_) * 100.0F);
                if (j1 > 25) {
                    this.awardStat(Stats.FLY_ONE_CM, j1);
                }
            }

        }
    }

    private void checkRidingStatistics(double p_71015_1_, double p_71015_3_, double p_71015_5_) {
        if (this.isPassenger()) {
            int i = Math.round(MathHelper.sqrt(p_71015_1_ * p_71015_1_ + p_71015_3_ * p_71015_3_ + p_71015_5_ * p_71015_5_) * 100.0F);
            if (i > 0) {
                Entity entity = this.getVehicle();
                if (entity instanceof AbstractMinecartEntity) {
                    this.awardStat(Stats.MINECART_ONE_CM, i);
                } else if (entity instanceof BoatEntity) {
                    this.awardStat(Stats.BOAT_ONE_CM, i);
                } else if (entity instanceof PigEntity) {
                    this.awardStat(Stats.PIG_ONE_CM, i);
                } else if (entity instanceof AbstractHorseEntity) {
                    this.awardStat(Stats.HORSE_ONE_CM, i);
                } else if (entity instanceof StriderEntity) {
                    this.awardStat(Stats.STRIDER_ONE_CM, i);
                } else if (entity instanceof HappyGhastEntity) {
                    this.awardStat(Stats.HAPPY_GHAST_ONE_CM, i);
                }
            }
        }

    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
        if (this.abilities.mayfly) {
            return false;
        } else {
            if (p_225503_1_ >= 2.0F) {
                this.awardStat(Stats.FALL_ONE_CM, (int) Math.round((double) p_225503_1_ * 100.0D));
            }

            return super.causeFallDamage(p_225503_1_, p_225503_2_);
        }
    }

    public boolean tryToStartFallFlying() {
        if (!this.onGround && !this.isFallFlying() && !this.isInWater() && !this.hasEffect(Effects.LEVITATION)) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.CHEST);
            if (itemstack.getItem() == Items.ELYTRA && ElytraItem.isFlyEnabled(itemstack)) {
                this.startFallFlying();
                return true;
            }
        }

        return false;
    }

    public void startFallFlying() {
        this.setSharedFlag(7, true);
    }

    public void stopFallFlying() {
        this.setSharedFlag(7, true);
        this.setSharedFlag(7, false);
    }

    protected void doWaterSplashEffect() {
        if (!this.isSpectator()) {
            super.doWaterSplashEffect();
        }

    }

    protected SoundEvent getFallDamageSound(int p_184588_1_) {
        return p_184588_1_ > 4 ? SoundEvents.PLAYER_BIG_FALL : SoundEvents.PLAYER_SMALL_FALL;
    }

    public void killed(ServerWorld world, LivingEntity entity) {
        EntityType<?> entityType = entity.getType();
        incrementKillCount(entityType);
        this.awardStat(Stats.ENTITY_KILLED.get(entityType));

        if (random.nextFloat() < 0.75f) {
            if (entity instanceof VillagerEntity && this.getOffhandItem() != null && this.getOffhandItem().getItem() == Items.CREAKING_HEART_ITEM) {
                if (CreakingHeartItem.hasRoom(this.getOffhandItem())) {
                    CreakingHeartItem.addEntityToCreakingHeart(this.getOffhandItem(), entity);
                }
            } else if (entity instanceof VillagerEntity && this.getMainHandItem() != null && this.getMainHandItem().getItem() == Items.CREAKING_HEART_ITEM) {
                if (CreakingHeartItem.hasRoom(this.getMainHandItem())) {
                    CreakingHeartItem.addEntityToCreakingHeart(this.getMainHandItem(), entity);
                }
            }
        }

    }

    public void makeStuckInBlock(BlockState p_213295_1_, Vector3d p_213295_2_) {
        if (!this.abilities.flying) {
            super.makeStuckInBlock(p_213295_1_, p_213295_2_);
        }

    }

    public void giveExperiencePoints(int p_195068_1_) {
        this.increaseScore(p_195068_1_);
        this.experienceProgress += (float) p_195068_1_ / (float) this.getXpNeededForNextLevel();
        this.totalExperience = MathHelper.clamp(this.totalExperience + p_195068_1_, 0, Integer.MAX_VALUE);

        while (this.experienceProgress < 0.0F) {
            float f = this.experienceProgress * (float) this.getXpNeededForNextLevel();
            if (this.experienceLevel > 0) {
                this.giveExperienceLevels(-1);
                this.experienceProgress = 1.0F + f / (float) this.getXpNeededForNextLevel();
            } else {
                this.giveExperienceLevels(-1);
                this.experienceProgress = 0.0F;
            }
        }

        while (this.experienceProgress >= 1.0F) {
            this.experienceProgress = (this.experienceProgress - 1.0F) * (float) this.getXpNeededForNextLevel();
            this.giveExperienceLevels(1);
            this.experienceProgress /= (float) this.getXpNeededForNextLevel();
        }

    }

    public int getEnchantmentSeed() {
        return this.enchantmentSeed;
    }

    public void onEnchantmentPerformed(ItemStack p_192024_1_, int p_192024_2_) {
        this.experienceLevel -= p_192024_2_;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0F;
            this.totalExperience = 0;
        }

        this.enchantmentSeed = this.random.nextInt();
    }

    public void giveExperienceLevels(int p_82242_1_) {
        this.experienceLevel += p_82242_1_;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0F;
            this.totalExperience = 0;
        }

        if (p_82242_1_ > 0 && this.experienceLevel % 5 == 0 && (float) this.lastLevelUpTime < (float) this.tickCount - 100.0F) {
            float f = this.experienceLevel > 30 ? 1.0F : (float) this.experienceLevel / 30.0F;
            this.level.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_LEVELUP, this.getSoundSource(), f * 0.75F, 1.0F);
            this.lastLevelUpTime = this.tickCount;
        }

    }

    public int getXpNeededForNextLevel() {
        if (this.experienceLevel >= 30) {
            return 112 + (this.experienceLevel - 30) * 9;
        } else {
            return this.experienceLevel >= 15 ? 37 + (this.experienceLevel - 15) * 5 : 7 + this.experienceLevel * 2;
        }
    }

    public void causeFoodExhaustion(float exhaustionAmount) {
        if (!this.abilities.invulnerable) {
            if (!this.level.isClientSide) {
                if (this.veryHardmode()) {
                    exhaustionAmount *= 3;
                }
                if (this.hasEffect(Effects.ROOTED)) {
                    exhaustionAmount *= Math.min(6, this.getEffect(Effects.ROOTED).getAmplifier() + 1);
                }

                exhaustionAmount *= this.getExhaustionRate();


                this.foodData.addExhaustion(exhaustionAmount);
            }
        }
    }

//    public float getExhaustionRate() {
//        if (level.isClientSide) return 1.0F;
//
//        AtomicReference<Float> rate = new AtomicReference<>(0.3F);
//
//        this.getArmorSlots().forEach(stack -> {
//            if (stack.getItem() instanceof Equipable equipable) {
//                if (stack.getItem() instanceof ArmorItem armorItem) {
//                    ArmorMaterial material = armorItem.material1;
//
//                    if (material != ArmorMaterial.LEATHER && material != ArmorMaterial.CHAIN) {
//                        if (rate.get() == 0.9F) {
//                            rate.set(1.0F);
//                        } else {
//                            rate.set(rate.get() + 0.2F);
//                        }
//                    }
//                }
//                if (equipable instanceof ElytraItem) {
//                    if (ElytraItem.isFlyEnabled(stack)) {
//                        float s = rate.get() - 0.15F;
//                        if (s > 0.0F) {
//                            rate.set(s);
//                        }
//                    }
//                }
//            }
//        });
//
//        return rate.get();
//    }

    public float getExhaustionRate() {
        if (level.isClientSide) return 1.0F;

        float rate = 0.3F;

        for (ItemStack armorSlot : this.getArmorSlots()) {
            if (armorSlot.getItem() instanceof Equipable equipable) {
                if (armorSlot.getItem() instanceof ArmorItem armorItem) {
                    rate += (armorItem.getWeight(null) / 100);
                }

                if (equipable instanceof ElytraItem elytraItem) {
                    if (ElytraItem.isFlyEnabled(armorSlot)) {
                        rate -= 0.15F;
                    }
                }
            }
        }

        return MathHelper.clamp(rate, 0.0F, 1.0F);
    }

    public FoodStats getFoodData() {
        return this.foodData;
    }

    public boolean canEat(boolean p_71043_1_) {
        return this.abilities.invulnerable || p_71043_1_ || this.foodData.needsFood();
    }

    public boolean isHurt() {
        return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
    }

    public boolean mayBuild() {
        return this.abilities.mayBuild;
    }

    public boolean mayUseItemAt(BlockPos p_175151_1_, Direction p_175151_2_, ItemStack p_175151_3_) {
        if (this.abilities.mayBuild) {
            return true;
        } else {
            BlockPos blockpos = p_175151_1_.relative(p_175151_2_.getOpposite());
            CachedBlockInfo cachedblockinfo = new CachedBlockInfo(this.level, blockpos, false);
            return p_175151_3_.hasAdventureModePlaceTagForBlock(this.level.getTagManager(), cachedblockinfo);
        }
    }

    protected int getExperienceReward(PlayerEntity p_70693_1_) {
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && !this.isSpectator()) {
            int i = this.experienceLevel * 7;
            return i > 100 ? 100 : i;
        } else {
            return 0;
        }
    }

    protected boolean isAlwaysExperienceDropper() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldShowName() {
        return true;
    }

    protected boolean isMovementNoisy() {
        return !this.abilities.flying && (!this.onGround || !this.isDiscrete());
    }

    public void onUpdateAbilities() {
    }

    public void setGameMode(Gamemode p_71033_1_) {
    }

    public ITextComponent getName() {
        return new StringTextComponent(this.gameProfile.getName());
    }

    public EnderChestInventory getEnderChestInventory() {
        return this.enderChestInventory;
    }

    public ItemStack getItemBySlot(EquipmentSlotType p_184582_1_) {
        if (p_184582_1_ == EquipmentSlotType.MAINHAND) {
            return this.inventory.getSelected();
        } else if (p_184582_1_ == EquipmentSlotType.OFFHAND) {
            return this.inventory.offhand.get(0);
        } else {
            return p_184582_1_.getType() == EquipmentSlotType.Group.ARMOR ? this.inventory.armor.get(p_184582_1_.getIndex()) : ItemStack.EMPTY;
        }
    }

//    public void setItemSlot(EquipmentSlotType p_184201_1_, ItemStack p_184201_2_) {
//        if (p_184201_1_ == EquipmentSlotType.MAINHAND) {
//            this.playEquipSound(p_184201_2_);
//            this.inventory.items.set(this.inventory.selected, p_184201_2_);
//        } else if (p_184201_1_ == EquipmentSlotType.OFFHAND) {
//            this.playEquipSound(p_184201_2_);
//            this.inventory.offhand.set(0, p_184201_2_);
//        } else if (p_184201_1_.getType() == EquipmentSlotType.Group.ARMOR) {
//            this.playEquipSound(p_184201_2_);
//            this.gameEvent(GameEvent.EQUIP);
//            this.inventory.armor.set(p_184201_1_.getIndex(), p_184201_2_);
//        }
//
//    }

    protected void verifyEquippedItem(ItemStack itemStack) {
        CompoundNBT compoundTag = itemStack.getTag();
        if (compoundTag != null) {
            itemStack.getItem().verifyTagAfterLoad(compoundTag);
        }
    }

    @Override
    public void setItemSlot(EquipmentSlotType equipmentSlot, ItemStack itemStack) {
        this.verifyEquippedItem(itemStack);
        if (equipmentSlot == EquipmentSlotType.MAINHAND) {
            this.onEquipItem(equipmentSlot, this.inventory.items.set(this.inventory.selected, itemStack), itemStack);
        } else if (equipmentSlot == EquipmentSlotType.OFFHAND) {
            this.onEquipItem(equipmentSlot, this.inventory.offhand.set(0, itemStack), itemStack);
        } else if (equipmentSlot.getType() == EquipmentSlotType.Group.ARMOR) {
            this.onEquipItem(equipmentSlot, this.inventory.armor.set(equipmentSlot.getIndex(), itemStack), itemStack);
        }
    }

    public void onEquipItem(EquipmentSlotType equipmentSlot, ItemStack itemStack, ItemStack itemStack2) {
        boolean bl;
        boolean bl2 = bl = itemStack2.isEmpty() && itemStack.isEmpty();
        if (bl || ItemStack.isSameItemSameTags(itemStack, itemStack2) || this.firstTick) {
            return;
        }
        Equipable equipable = Equipable.get(itemStack2);
        if (equipable != null && !this.isSpectator() && equipable.getEquipmentSlot() == equipmentSlot) {
            if (!this.level().isClientSide() && !this.isSilent()) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), equipable.getEquipSound(), this.getSoundSource(), 1.0f, 1.0f);
            }
            if (this.doesEmitEquipEvent(equipmentSlot)) {
                this.gameEvent(GameEvent.EQUIP);
            }
        }
    }

    public boolean addItem(ItemStack p_191521_1_) {
        this.playEquipSound(p_191521_1_);
        boolean add = this.inventory.add(p_191521_1_);

        if (add) {
//            for (Pair<Set<Item>, List<String>> group : RecipeManager.ITEM_UNLOCK_RECIPE_MAP) {
//                for (Item item : group.getFirst()) {
//                    if (item == p_191521_1_.getItem()) {
//                        // unlock all recipes in group.recipesToUnlock for the player
//                        awardRecipesByKey(Arrays.stream(group.getSecond().toArray(new String[0])).map(ResourceLocation::new).toList().toArray(new ResourceLocation[0]));
//                        // You probably want to mark these as unlocked for this player so you don't repeat!
//                        break; // No need to check more items in this group for this player
//                    }
//                }
//            }
        }

        return add;
    }

    public boolean addOrDrop(ItemStack stack) {
        if (addItem(stack)) {
            return true;  // Item was successfully added
        } else {
            drop(stack, true);  // Item wasn't added, so we drop it
            return false;  // Indicate that the item was not added
        }
    }

    public boolean addItem(ItemStack stack, boolean sound) {
        if (sound) {
            this.playEquipSound(stack);
        }
        boolean add = this.inventory.add(stack);

        if (add) {
//            for (Pair<Set<Item>, List<String>> group : RecipeManager.ITEM_UNLOCK_RECIPE_MAP) {
//                for (Item item : group.getFirst()) {
//                    if (item == stack.getItem()) {
//                        // unlock all recipes in group.recipesToUnlock for the player
//                        awardRecipesByKey(Arrays.stream(group.getSecond().toArray(new String[0])).map(ResourceLocation::new).toList().toArray(new ResourceLocation[0]));
//                        // You probably want to mark these as unlocked for this player so you don't repeat!
//                        break; // No need to check more items in this group for this player
//                    }
//                }
//            }
        }

        return add;
    }

    public Iterable<ItemStack> getHandSlots() {
        return Lists.newArrayList(this.getMainHandItem(), this.getOffhandItem());
    }

    public Iterable<ItemStack> getArmorSlots() {
        return this.inventory.armor;
    }

    public Iterable<ItemStack> getArmorAndHandSlots() {
        List<ItemStack> l = Lists.newArrayList();
        l.addAll(this.inventory.armor);
        l.add(this.getMainHandItem());
        l.add(this.getOffhandItem());

        return l;
    }

    public boolean setEntityOnShoulder(CompoundNBT p_192027_1_) {
        if (!this.isPassenger() && this.onGround && !this.isInWater()) {
            if (this.getShoulderEntityLeft().isEmpty()) {
                this.setShoulderEntityLeft(p_192027_1_);
                this.timeEntitySatOnShoulder = this.level.getGameTime();
                return true;
            } else if (this.getShoulderEntityRight().isEmpty()) {
                this.setShoulderEntityRight(p_192027_1_);
                this.timeEntitySatOnShoulder = this.level.getGameTime();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected void removeEntitiesOnShoulder() {
        if (this.timeEntitySatOnShoulder + 20L < this.level.getGameTime()) {
            this.respawnEntityOnShoulder(this.getShoulderEntityLeft());
            this.setShoulderEntityLeft(new CompoundNBT());
            this.respawnEntityOnShoulder(this.getShoulderEntityRight());
            this.setShoulderEntityRight(new CompoundNBT());
        }

    }

    private void respawnEntityOnShoulder(CompoundNBT p_192026_1_) {
        if (!this.level.isClientSide && !p_192026_1_.isEmpty()) {
            EntityType.create(p_192026_1_, this.level).ifPresent((p_226562_1_) -> {
                if (p_226562_1_ instanceof TameableEntity) {
                    ((TameableEntity) p_226562_1_).setOwnerUUID(this.uuid);
                }

                p_226562_1_.setPos(this.getX(), this.getY() + (double) 0.7F, this.getZ());
                ((ServerWorld) this.level).addWithUUID(p_226562_1_);
            });
        }

    }

    public abstract boolean isSpectator();

    public boolean isSwimming() {
        return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
    }

    public abstract boolean isCreative();

    public boolean isPushedByFluid() {
        return !this.abilities.flying;
    }

    public Scoreboard getScoreboard() {
        return this.level.getScoreboard();
    }

    public ITextComponent getDisplayName() {
        IFormattableTextComponent iformattabletextcomponent = ScorePlayerTeam.formatNameForTeam(this.getTeam(), this.getName());
        return this.decorateDisplayNameComponent(iformattabletextcomponent);
    }

    private IFormattableTextComponent decorateDisplayNameComponent(IFormattableTextComponent p_208016_1_) {
        String s = this.getGameProfile().getName();
        return p_208016_1_.withStyle((p_234565_2_) -> {
            return p_234565_2_.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + s + " ")).withHoverEvent(this.createHoverEvent()).withInsertion(s);
        });
    }

    public String getScoreboardName() {
        return this.getGameProfile().getName();
    }

    public float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
        switch (p_213348_1_) {
            case SWIMMING:
            case FALL_FLYING:
            case SPIN_ATTACK:
                return 0.4F;
            case CROUCHING:
                return 1.27F;
            default:
                return 1.62F;
        }
    }

    public void setAbsorptionAmount(float p_110149_1_) {
        if (p_110149_1_ < 0.0F) {
            p_110149_1_ = 0.0F;
        }

        this.getEntityData().set(DATA_PLAYER_ABSORPTION_ID, p_110149_1_);
    }

    public float getAbsorptionAmount() {
        return this.getEntityData().get(DATA_PLAYER_ABSORPTION_ID);
    }

    public static UUID createPlayerUUID(GameProfile p_146094_0_) {
        UUID uuid = p_146094_0_.getId();
        if (uuid == null) {
            uuid = createPlayerUUID(p_146094_0_.getName());
        }

        return uuid;
    }

    public static UUID createPlayerUUID(String p_175147_0_) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + p_175147_0_).getBytes(StandardCharsets.UTF_8));
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isModelPartShown(PlayerModelPart p_175148_1_) {
        return (this.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION) & p_175148_1_.getMask()) == p_175148_1_.getMask();
    }

    public boolean setSlot(int p_174820_1_, ItemStack p_174820_2_) {
        if (p_174820_1_ >= 0 && p_174820_1_ < this.inventory.items.size()) {
            this.inventory.setItem(p_174820_1_, p_174820_2_);
            return true;
        } else {
            EquipmentSlotType equipmentslottype;
            if (p_174820_1_ == 100 + EquipmentSlotType.HEAD.getIndex()) {
                equipmentslottype = EquipmentSlotType.HEAD;
            } else if (p_174820_1_ == 100 + EquipmentSlotType.CHEST.getIndex()) {
                equipmentslottype = EquipmentSlotType.CHEST;
            } else if (p_174820_1_ == 100 + EquipmentSlotType.LEGS.getIndex()) {
                equipmentslottype = EquipmentSlotType.LEGS;
            } else if (p_174820_1_ == 100 + EquipmentSlotType.FEET.getIndex()) {
                equipmentslottype = EquipmentSlotType.FEET;
            } else {
                equipmentslottype = null;
            }

            if (p_174820_1_ == 98) {
                this.setItemSlot(EquipmentSlotType.MAINHAND, p_174820_2_);
                return true;
            } else if (p_174820_1_ == 99) {
                this.setItemSlot(EquipmentSlotType.OFFHAND, p_174820_2_);
                return true;
            } else if (equipmentslottype == null) {
                int i = p_174820_1_ - 200;
                if (i >= 0 && i < this.enderChestInventory.getContainerSize()) {
                    this.enderChestInventory.setItem(i, p_174820_2_);
                    return true;
                } else {
                    return false;
                }
            } else {
                if (!p_174820_2_.isEmpty()) {
                    if (!(p_174820_2_.getItem() instanceof ArmorItem) && !(p_174820_2_.getItem() instanceof ElytraItem)) {
                        if (equipmentslottype != EquipmentSlotType.HEAD) {
                            return false;
                        }
                    } else if (Mob.getEquipmentSlotForItem(p_174820_2_) != equipmentslottype) {
                        return false;
                    }
                }

                this.inventory.setItem(equipmentslottype.getIndex() + this.inventory.items.size(), p_174820_2_);
                return true;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    @OnlyIn(Dist.CLIENT)
    public void setReducedDebugInfo(boolean p_175150_1_) {
        this.reducedDebugInfo = p_175150_1_;
    }

    public void setRemainingFireTicks(int p_241209_1_) {
        super.setRemainingFireTicks(this.abilities.invulnerable ? Math.min(p_241209_1_, 1) : p_241209_1_);
    }

    public HandSide getMainArm() {
        return this.entityData.get(DATA_PLAYER_MAIN_HAND) == 0 ? HandSide.LEFT : HandSide.RIGHT;
    }

    public void setMainArm(HandSide p_184819_1_) {
        this.entityData.set(DATA_PLAYER_MAIN_HAND, (byte) (p_184819_1_ == HandSide.LEFT ? 0 : 1));
    }

    public CompoundNBT getShoulderEntityLeft() {
        return this.entityData.get(DATA_SHOULDER_LEFT);
    }

    protected void setShoulderEntityLeft(CompoundNBT p_192029_1_) {
        this.entityData.set(DATA_SHOULDER_LEFT, p_192029_1_);
    }

    public CompoundNBT getShoulderEntityRight() {
        return this.entityData.get(DATA_SHOULDER_RIGHT);
    }

    protected void setShoulderEntityRight(CompoundNBT p_192031_1_) {
        this.entityData.set(DATA_SHOULDER_RIGHT, p_192031_1_);
    }

    public float getCurrentItemAttackStrengthDelay() {
        return (float) (1.0D / this.getAttributeValue(Attributes.ATTACK_SPEED) * 20.0D);
    }

    public float getAttackStrengthScale(float p_184825_1_) {
        return MathHelper.clamp(((float) this.attackStrengthTicker + p_184825_1_) / this.getCurrentItemAttackStrengthDelay(), 0.0F, 1.0F);
    }

    public void resetAttackStrengthTicker() {
        this.attackStrengthTicker = 0;
    }

    public CooldownTracker getCooldowns() {
        return this.cooldowns;
    }

    protected float getBlockSpeedFactor() {
        return !this.abilities.flying && !this.isFallFlying() ? super.getBlockSpeedFactor() : 1.0F;
    }

    public float getLuck() {
        return (float) this.getAttributeValue(Attributes.LUCK);
    }

    public boolean canUseGameMasterBlocks() {
        return this.abilities.instabuild && this.getPermissionLevel() >= 2;
    }

    public boolean canTakeItem(ItemStack p_213365_1_) {
        EquipmentSlotType equipmentslottype = Mob.getEquipmentSlotForItem(p_213365_1_);
        return this.getItemBySlot(equipmentslottype).isEmpty();
    }

    public EntitySize getDimensions(Pose p_213305_1_) {
        return POSES.getOrDefault(p_213305_1_, STANDING_DIMENSIONS);
    }

    public ImmutableList<Pose> getDismountPoses() {
        return ImmutableList.of(Pose.STANDING, Pose.CROUCHING, Pose.SWIMMING);
    }

    public ItemStack getProjectile(ItemStack shootableItemStack) {
        if (!(shootableItemStack.getItem() instanceof ShootableItem)) {
            return ItemStack.EMPTY;
        } else {
            Predicate<ItemStack> predicate = ((ShootableItem) shootableItemStack.getItem()).getSupportedHeldProjectiles();
            ItemStack projectile = ShootableItem.getHeldProjectile(this, predicate);

            // Check if a projectile is held directly
            if (!projectile.isEmpty()) {
                return projectile;
            } else {
                predicate = ((ShootableItem) shootableItemStack.getItem()).getAllSupportedProjectiles();

                // First, search in the player's inventory for a quiver containing arrows
                for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
                    ItemStack inventoryStack = this.inventory.getItem(i);

                    // Check if the item is a quiver
                    if (inventoryStack.getItem() instanceof QuiverItem) {
                        // Retrieve arrows from the quiver
                        List<ItemStack> quiverContents = QuiverItem.getContents(inventoryStack).collect(Collectors.toList());

                        for (ItemStack quiverItemStack : quiverContents) {
                            if (predicate.test(quiverItemStack)) {
                                return quiverItemStack;
                            }
                        }
                    }
                }

                // If no quiver was found or it contained no valid projectiles, search the inventory directly
                for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
                    ItemStack inventoryStack = this.inventory.getItem(i);
                    if (predicate.test(inventoryStack)) {
                        return inventoryStack;
                    }
                }

                // If instabuild mode is enabled, return a single arrow by default
                return this.abilities.instabuild ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
            }
        }
    }

    public ItemStack eat(World p_213357_1_, ItemStack p_213357_2_) {
        this.getFoodData().eat(p_213357_2_.getItem(), p_213357_2_);
        this.awardStat(Stats.ITEM_USED.get(p_213357_2_.getItem()));

        SoundEvent sound = SoundEvents.PLAYER_BURP;
        float volume = 0.5F;
        float pitch = p_213357_1_.random.nextFloat() * 0.1F + 0.9F;

        if (p_213357_2_.getOrCreateTag().contains("FoodData", 10)) {
            FoodData data = FoodData.load(p_213357_2_.getTag());

            if (data != null) {
                sound = data.doneEating().soundWrapper().event();
                if (data.doneEating().volume() != -1.0F) {
                    volume = data.doneEating().volume();
                }
                if (data.doneEating().pitch() != -1.0F) {
                    pitch = data.doneEating().pitch();
                }
            }
        }

        p_213357_1_.playSound((PlayerEntity) null, this.getX(), this.getY(), this.getZ(), sound, SoundCategory.PLAYERS, volume, pitch);
        if (this instanceof ServerPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity) this, p_213357_2_);
        }

        return super.eat(p_213357_1_, p_213357_2_);
    }

    protected boolean shouldRemoveSoulSpeed(BlockState p_230295_1_) {
        return this.abilities.flying || super.shouldRemoveSoulSpeed(p_230295_1_);
    }

    @OnlyIn(Dist.CLIENT)
    public Vector3d getRopeHoldPosition(float p_241843_1_) {
        double d0 = 0.22D * (this.getMainArm() == HandSide.RIGHT ? -1.0D : 1.0D);
        float f = MathHelper.lerp(p_241843_1_ * 0.5F, this.xRot, this.xRotO) * ((float) Math.PI / 180F);
        float f1 = MathHelper.lerp(p_241843_1_, this.yBodyRotO, this.yBodyRot) * ((float) Math.PI / 180F);
        if (!this.isFallFlying() && !this.isAutoSpinAttack()) {
            if (this.isVisuallySwimming()) {
                return this.getPosition(p_241843_1_).add((new Vector3d(d0, 0.2D, -0.15D)).xRot(-f).yRot(-f1));
            } else {
                double d5 = this.getBoundingBox().getYsize() - 1.0D;
                double d6 = this.isCrouching() ? -0.2D : 0.07D;
                return this.getPosition(p_241843_1_).add((new Vector3d(d0, d5, d6)).yRot(-f1));
            }
        } else {
            Vector3d vector3d = this.getViewVector(p_241843_1_);
            Vector3d vector3d1 = this.getDeltaMovement();
            double d1 = Entity.getHorizontalDistanceSqr(vector3d1);
            double d2 = Entity.getHorizontalDistanceSqr(vector3d);
            float f2;
            if (d1 > 0.0D && d2 > 0.0D) {
                double d3 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d1 * d2);
                double d4 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;
                f2 = (float) (Math.signum(d4) * Math.acos(d3));
            } else {
                f2 = 0.0F;
            }

            return this.getPosition(p_241843_1_).add((new Vector3d(d0, -0.11D, 0.85D)).zRot(-f2).xRot(-f).yRot(-f1));
        }
    }

    public static enum SleepResult {
        NOT_POSSIBLE_HERE,
        NOT_POSSIBLE_NOW(new TranslationTextComponent("block.minecraft.bed.no_sleep")),
        TOO_FAR_AWAY(new TranslationTextComponent("block.minecraft.bed.too_far_away")),
        OBSTRUCTED(new TranslationTextComponent("block.minecraft.bed.obstructed")),
        OTHER_PROBLEM,
        NOT_SAFE(new TranslationTextComponent("block.minecraft.bed.not_safe"));

        @Nullable
        private final ITextComponent message;

        private SleepResult() {
            this.message = null;
        }

        private SleepResult(ITextComponent p_i50668_3_) {
            this.message = p_i50668_3_;
        }

        @Nullable
        public ITextComponent getMessage() {
            return this.message;
        }
    }
}