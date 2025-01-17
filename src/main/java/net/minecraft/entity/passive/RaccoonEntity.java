package net.minecraft.entity.passive;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.passive.fish.AbstractGroupFishEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.item.Item;

public class RaccoonEntity extends TameableEntity {
    private static final Logger logger = Logger.getLogger(RaccoonEntity.class.getName());
    private static final DataParameter<Integer> DATA_TYPE_ID = EntityDataManager.defineId(RaccoonEntity.class, DataSerializers.INT);
    private static final DataParameter<Byte> DATA_FLAGS_ID = EntityDataManager.defineId(RaccoonEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Optional<UUID>> DATA_TRUSTED_ID_0 = EntityDataManager.defineId(RaccoonEntity.class, DataSerializers.OPTIONAL_UUID);
    private static final DataParameter<Optional<UUID>> DATA_TRUSTED_ID_1 = EntityDataManager.defineId(RaccoonEntity.class, DataSerializers.OPTIONAL_UUID);
    private static final Predicate<ItemEntity> ALLOWED_ITEMS = (item) -> {
        return !item.hasPickUpDelay() && item.isAlive();
    };
    private static final DataParameter<Optional<UUID>> DATA_LEADER_ID = EntityDataManager.defineId(RaccoonEntity.class, DataSerializers.OPTIONAL_UUID);
    //private static final DataParameter<BlockPos> HOME_POS = EntityDataManager.defineId(RaccoonEntity.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<Boolean> HAS_HOME = EntityDataManager.defineId(RaccoonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CAN_HAVE_HOME = EntityDataManager.defineId(RaccoonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CAN_GO_HOME = EntityDataManager.defineId(RaccoonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COOLDOWN_BEFORE_GOING_HOME = EntityDataManager.defineId(RaccoonEntity.class, DataSerializers.INT);

    private List<UUID> homeMembers = new ArrayList<>();

    private static final Predicate<Entity> TRUSTED_TARGET_SELECTOR = (p_213470_0_) -> {
        if (!(p_213470_0_ instanceof LivingEntity)) {
            return false;
        } else {
            LivingEntity livingentity = (LivingEntity) p_213470_0_;
            return livingentity.getLastHurtMob() != null && livingentity.getLastHurtMobTimestamp() < livingentity.tickCount + 600;
        }
    };

    private static final Predicate<Entity> AVOID_PLAYERS = (p_213463_0_) -> {
        return !p_213463_0_.isDiscrete() && EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(p_213463_0_);
    };
    private Goal landTargetGoal;
    private Goal turtleEggTargetGoal;
    private Goal fishTargetGoal;
    private float interestedAngle;
    private float interestedAngleO;
    private float crouchAmount;
    private float crouchAmountO;
    private int ticksSinceEaten;


    private static final Map<Block, Item> CROP_DROP_MAP = new HashMap<>();

    static {
        CROP_DROP_MAP.put(Blocks.POTATOES, Items.POTATO);
        CROP_DROP_MAP.put(Blocks.WHEAT, Items.WHEAT);
        CROP_DROP_MAP.put(Blocks.CARROTS, Items.CARROT);
        CROP_DROP_MAP.put(Blocks.SWEET_BERRY_BUSH, Items.SWEET_BERRIES);
    }

    // dirty blocks, each block that is dirty has a float value of how dirty it is
    private static final Map<Block, Float> DIRTY_BLOCKS = new HashMap<>();

    static {
        DIRTY_BLOCKS.put(Blocks.GRASS_BLOCK, 0.1F);
        DIRTY_BLOCKS.put(Blocks.WHEAT, 0.1F);
        DIRTY_BLOCKS.put(Blocks.CARROTS, 0.2F);
        DIRTY_BLOCKS.put(Blocks.POTATOES, 0.2F);
        DIRTY_BLOCKS.put(Blocks.BEETROOTS, 0.3F);
        DIRTY_BLOCKS.put(Blocks.SWEET_BERRY_BUSH, 0.3F);
        DIRTY_BLOCKS.put(Blocks.DIRT, 0.5F);
        DIRTY_BLOCKS.put(Blocks.COARSE_DIRT, 0.5F);
        DIRTY_BLOCKS.put(Blocks.LILY_PAD, 0.35F);
        DIRTY_BLOCKS.put(Blocks.KELP, 0.37F);
        DIRTY_BLOCKS.put(Blocks.KELP_PLANT, 0.35F);
        DIRTY_BLOCKS.put(Blocks.SEAGRASS, 0.45F);
        DIRTY_BLOCKS.put(Blocks.TALL_SEAGRASS, 0.55F);
        DIRTY_BLOCKS.put(Blocks.SEA_PICKLE, 0.4F);
        DIRTY_BLOCKS.put(Blocks.VINE, 0.7F);
        DIRTY_BLOCKS.put(Blocks.PODZOL, 0.6F);
        DIRTY_BLOCKS.put(Blocks.GRAVEL, 0.45F);
        DIRTY_BLOCKS.put(Blocks.SAND, 0.6F);
        DIRTY_BLOCKS.put(Blocks.FARMLAND, 0.4F);
        DIRTY_BLOCKS.put(Blocks.GRASS_PATH, 0.3F);
        DIRTY_BLOCKS.put(Blocks.MYCELIUM, 0.6F);
        DIRTY_BLOCKS.put(Blocks.HONEY_BLOCK, 0.7F);
        //nether blocks:
        DIRTY_BLOCKS.put(Blocks.SOUL_SAND, 0.85F);
        DIRTY_BLOCKS.put(Blocks.SOUL_SOIL, 0.85F);
    }

    private static final Map<Block, Float> DIRTY_ENVIRONMENT = new HashMap<>();

    static {
        DIRTY_ENVIRONMENT.put(Blocks.LILY_PAD, 0.2F);
        DIRTY_ENVIRONMENT.put(Blocks.KELP, 0.27F);
        DIRTY_ENVIRONMENT.put(Blocks.KELP_PLANT, 0.25F);
        DIRTY_ENVIRONMENT.put(Blocks.SEAGRASS, 0.25F);
        DIRTY_ENVIRONMENT.put(Blocks.TALL_SEAGRASS, 0.25F);
        DIRTY_ENVIRONMENT.put(Blocks.SEA_PICKLE, 0.22F);
        DIRTY_ENVIRONMENT.put(Blocks.VINE, 0.2F);
        DIRTY_ENVIRONMENT.put(Blocks.WHEAT, 0.2F);
        DIRTY_ENVIRONMENT.put(Blocks.CARROTS, 0.22F);
        DIRTY_ENVIRONMENT.put(Blocks.POTATOES, 0.22F);
        DIRTY_ENVIRONMENT.put(Blocks.BEETROOTS, 0.23F);
        DIRTY_ENVIRONMENT.put(Blocks.SWEET_BERRY_BUSH, 0.23F);
    }

    private int getDirtyEnvironmentBonus() {
        int bonus = 1;
        int count = 0;
        List<Float> roundingBonus = new ArrayList<>();
        BlockPos currentPosition = new BlockPos.Mutable(this.getX(), this.getY(), this.getZ());

        for (int k = (int) this.getX() - 6; k < (int) this.getX() + 6 && count < 14; ++k) {
            for (int l = (int) this.getY() - 6; l < (int) this.getY() + 6 && count < 14; ++l) {
                for (int i1 = (int) this.getZ() - 6; i1 < (int) this.getZ() + 6 && count < 14; ++i1) {
                    Block block = this.level.getBlockState(currentPosition.offset(k, l, i1)).getBlock();
                    // Check if the block is in the DIRTY_ENVIRONMENT map
                    if (DIRTY_ENVIRONMENT.containsKey(block)) {
                        if (this.random.nextFloat() < DIRTY_ENVIRONMENT.get(block)) {
                            ++bonus;
                            roundingBonus.add(DIRTY_ENVIRONMENT.get(block));
                        }

                        ++count;
                    }
                }
            }
        }

        float sum = 0F;
        for (Float bonuses : roundingBonus) {
            sum += bonuses;
        }
        int roundedSum = (int) Math.ceil(sum);
        bonus += roundedSum;

        // limit the bonus to x8
        return Math.min(bonus, 8);
    }

    private static final List<RegistryKey<Biome>> DIRTY_BIOMES = Arrays.asList(
            Biomes.SWAMP,
            Biomes.SWAMP_HILLS,
            Biomes.ERODED_BADLANDS
    );


    private static final Item[] ALL_ITEMS = {
            Items.APPLE,
            Items.BREAD,
            Items.CARROT,
            Items.POTATO,
            Items.BEEF,
            Items.CHICKEN,
            Items.PORKCHOP,
            Items.COOKED_BEEF,
            Items.COOKED_CHICKEN,
            Items.COOKED_PORKCHOP,
            Items.GOLDEN_CARROT,
            Items.TROPICAL_FISH,
            Items.SWEET_BERRIES,
            Items.SALMON,
            Items.COOKED_SALMON,
            Items.MUTTON,
            Items.COOKED_MUTTON
    };

    private static final Optional<List<Class<? extends LivingEntity>>> entitiesToExclude = Optional.of(Arrays.asList(
            RaccoonEntity.class,
            WolfEntity.class,
            CatEntity.class,
            HorseEntity.class,
            FoxEntity.class,
            PolarBearEntity.class,
            BeeEntity.class,
            SnowGolemEntity.class,
            StriderEntity.class,
            TurtleEntity.class,
            IronGolemEntity.class,
            OwlEntity.class,
            BatEntity.class,
            OcelotEntity.class
    ));


    private static final List<Item> FOOD_ITEMS = Registry.ITEM.stream().filter(item -> item.isEdible()).collect(Collectors.toList());


    private static final Ingredient TEMPT_INGREDIENT = Ingredient.of(
            FOOD_ITEMS.stream().map(ItemStack::new).toArray(ItemStack[]::new)
    );

    public static boolean isFoodItem(ItemStack itemStack) {
        return itemStack.getItem().isEdible();
    }


    public double HOME_POS_X;

    public double HOME_POS_Y;
    public double HOME_POS_Z;

    private int thirst;

    public BlockPos HOME_POS = new BlockPos(BlockPos.ZERO);

    private static final int HUNGER_THRESHOLD = 10;
    private static final int TIME_THRESHOLD = 1200;

    private static final int MIN_HUNGER_INCREASE = 20;
    private static final int MAX_HUNGER_INCREASE = 45;
    private static final int SEARCH_RADIUS = 42;

    private static int timeBeforeRegroup;
    private int goHomeCooldown = 0;
    private boolean isDirty;
    private int dirtyCountdown = 600;

    public static boolean AlreadyChosenHome = false;
    public int hunger;
    public int TIME_WITHOUT_FINDING_TARGET;
    public int TIME_FOR_BREED = 0;
    public int TIME_FOR_SITTING = 200;

    public boolean isHome;

    private static final int MAX_HOME_MEMBERS = 5;
    private RaccoonDropOffItemsGoal dropOffItemsGoal;

    private static final List<Item> VALUABLE_ITEMS = Arrays.asList(
            Items.DIAMOND, Items.EMERALD, Items.GOLD_INGOT, Items.IRON_INGOT, Items.NETHERITE_INGOT,
            Items.DIAMOND_PICKAXE, Items.IRON_PICKAXE, Items.GOLDEN_PICKAXE, Items.STONE_PICKAXE, Items.WOODEN_PICKAXE,
            Items.DIAMOND_SWORD, Items.IRON_SWORD, Items.GOLDEN_SWORD, Items.STONE_SWORD, Items.WOODEN_SWORD,
            Items.DIAMOND_AXE, Items.IRON_AXE, Items.GOLDEN_AXE, Items.STONE_AXE, Items.WOODEN_AXE,
            Items.DIAMOND_SHOVEL, Items.IRON_SHOVEL, Items.GOLDEN_SHOVEL, Items.STONE_SHOVEL, Items.WOODEN_SHOVEL
    );

    public RaccoonEntity(EntityType<? extends RaccoonEntity> p_i50271_1_, World p_i50271_2_) {
        super(p_i50271_1_, p_i50271_2_);
        this.lookControl = new LookHelperController();
        this.moveControl = new MoveHelperController();
        this.setPathfindingMalus(PathNodeType.DANGER_OTHER, 0.0F);
        this.setPathfindingMalus(PathNodeType.DAMAGE_OTHER, 0.0F);
        this.setCanPickUpLoot(true);
        this.thirst = 100;
        this.hunger = 100;
        this.timeBeforeRegroup = 600;
        this.setCanHaveHome(true);
        this.setCanHome(true);
        this.setHome(0, 0, 0);
        this.isDirty = false;
        this.dirtyCountdown = 600;
    }

//    private static final Predicate<Entity> STALKABLE_PREY = (entity) -> {
//        if (entity instanceof ChickenEntity || entity instanceof RabbitEntity) {
//            return true;
//        }
//        if (entity instanceof PlayerEntity) {
//            return RaccoonEntity.this.getRaccoonType() == BoggedType.RABID;
//        }
//        return false;
//    };

    public Predicate<Entity> getStalkablePreyPredicate() {
        return (entity) -> {
            if (entity instanceof ChickenEntity || entity instanceof RabbitEntity) {
                return true;
            }
            if (entity instanceof PlayerEntity) {
                return this.getRaccoonType() == Type.RABID;
            }
            return false;
        };
    }

    public boolean isDirty() {
        return this.getRaccoonType() == Type.DIRTY;
    }

    public void setDirty(boolean value) {
        if (value = true) {
            this.setRaccoonType(Type.DIRTY);
        } else {
            this.setRaccoonType(Type.RED);
        }
    }

    public int getDirtiness() {
        return this.dirtyCountdown;
    }

    public void resetDirtyCountdown() {
        this.dirtyCountdown = 600;
    }

    public int getThirst() {
        return thirst;
    }

    public void setThirst(int thirst) {
        this.thirst = thirst;
    }

    public boolean isHome(double dis) {
        return close(this.position(), dis);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TRUSTED_ID_0, Optional.empty());
        this.entityData.define(DATA_TRUSTED_ID_1, Optional.empty());
        this.entityData.define(DATA_LEADER_ID, Optional.empty());

        this.entityData.define(HAS_HOME, false);
        this.entityData.define(CAN_HAVE_HOME, true);
        this.entityData.define(CAN_GO_HOME, true);
        this.entityData.define(COOLDOWN_BEFORE_GOING_HOME, 0);
        this.entityData.define(DATA_TYPE_ID, 0);
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
    }

    protected void registerGoals() {
        // Initialize specific goals
        this.landTargetGoal = new NearestAttackableTargetGoal<>(this, Animal.class, 10, false, false, (animal) -> {
            boolean flag = !(animal instanceof WolfEntity)
                    && !(animal instanceof CatEntity)
                    && !(animal instanceof BeeEntity)
                    && !(animal instanceof HorseEntity)
                    && !(animal instanceof BatEntity)
                    && !(animal instanceof OwlEntity)
                    && !(animal instanceof OcelotEntity)
                    && !(animal instanceof RaccoonEntity)
                    && !(animal instanceof FoxEntity);
            return RaccoonEntity.this.getHunger() <= 30 && flag;
        });
        this.turtleEggTargetGoal = new NearestAttackableTargetGoal<>(this, TurtleEntity.class, 10, false, false, TurtleEntity.BABY_ON_LAND_SELECTOR);
        this.fishTargetGoal = new NearestAttackableTargetGoal<>(this, AbstractFishEntity.class, 20, false, false, (p_213456_0_) -> {
            return p_213456_0_ instanceof AbstractGroupFishEntity;
        });

        // Target selector goals
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new FindFoodGoal());
        //this.goalSelector.addGoal(3, new RaccoonDropOffItemsGoal(this, 1.2));

        this.targetSelector.addGoal(3, new TemptGoal(this, 1D, TEMPT_INGREDIENT, true));
        this.targetSelector.addGoal(3, new RevengeGoal(LivingEntity.class, false, false, (p_234193_1_) -> {
            return TRUSTED_TARGET_SELECTOR.test(p_234193_1_) && !this.trusts(p_234193_1_.getUUID());
        }));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 20, false, false, (p_28600_) -> {
            return RaccoonEntity.this.getRaccoonType() == Type.RABID;
        }));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Mob.class, 20, false, true, entity -> {
            return this.getOwner() != null && entity.as(Mob.class).getTarget() == this.getOwner();
        }));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, WolfEntity.class, 20, false, false, (p_28600_) -> {
            return RaccoonEntity.this.getRaccoonType() == Type.RABID;
        }));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Animal.class, 20, false, false, (p_28600_) -> {
            return RaccoonEntity.this.getRaccoonType() == Type.RABID && !(p_28600_ instanceof RaccoonEntity) && this.random.nextFloat() < 0.2F;
        }));

        // Goal selector goals
        this.goalSelector.addGoal(3, new RaccoonRaidGardenGoal(this));

        this.goalSelector.addGoal(2, new RaccoonMoveToWaterGoal(this, 1.22F));
       // this.goalSelector.addGoal(4, new RaccoonPracticeGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RaccoonLieOnBedGoal(this, 1.3D, 16));
        this.goalSelector.addGoal(1, new RaccoonEntity.SwimGoal());
        this.goalSelector.addGoal(1, new RaccoonGoHomeGoal(this, 1.3F, 30, 30));
        this.goalSelector.addGoal(1, new RaccoonStealGoal(this, 1.1F, 16));
        this.goalSelector.addGoal(1, new FindHomeGoal(this));

        this.goalSelector.addGoal(1, new FollowLeaderGoal(this, 1.0, 7.0f, 6.0f));
        this.goalSelector.addGoal(1, new JumpGoal());
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.2D, 16.0F, 2.0F, false));
        this.goalSelector.addGoal(2, new MateGoal(1.1D));
        this.goalSelector.addGoal(2, new RaccoonSitGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(2.2D));
        this.goalSelector.addGoal(3, new RaccoonDefendBabyGoal(this));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, CatEntity.class, 8.0F, 0.8D, 1.33D, (cat) -> {
            return !((CatEntity) cat).isTame() && !(this.getRaccoonType() == Type.RABID);
        }));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, PlayerEntity.class, 16.0F, 1.1D, 1.55D, (player) -> {
            boolean flag = AVOID_PLAYERS.test(player) && !this.trusts(player.getUUID()) && !this.isDefending() && !(RaccoonEntity.this.getRaccoonType() == Type.RABID);
            boolean flag1 = this.getTarget() != null;
            boolean flag2 = !(this.getTarget() == player);
            if (flag1) {
                return flag2 && flag; // Run away if player is the target and flag is true
            } else {
                return flag; // Run away based on flag if player is not the current target
            }

        }));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, WolfEntity.class, 8.0F, 1.6D, 1.4D, (p_213469_1_) -> {
            return !((WolfEntity) p_213469_1_).isTame() && !this.isDefending() && !(RaccoonEntity.this.getRaccoonType() == Type.RABID);
        }));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, PolarBearEntity.class, 8.0F, 1.6D, 1.4D, (p_213493_1_) -> {
            return !this.isDefending();
        }));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.34D, true));
        this.goalSelector.addGoal(5, new FollowTargetGoal());
        this.goalSelector.addGoal(1, new PounceGoal());
        this.goalSelector.addGoal(7, new BiteGoal(1.2F, true));
        this.goalSelector.addGoal(7, new SleepGoal());
        this.goalSelector.addGoal(8, new FollowGoal(this, 1.25D));
        this.goalSelector.addGoal(9, new StrollGoal(32, 200));
        //this.goalSelector.addGoal(10, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(11, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(11, new FindItemsGoal());
        this.goalSelector.addGoal(2, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(12, new WatchGoal(this, PlayerEntity.class, 24.0F));
        this.goalSelector.addGoal(13, new SitAndLookGoal());


    }

    public SoundEvent getEatingSound(ItemStack p_213353_1_) {
        return SoundEvents.FOX_EAT;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        // Added debug statement
        //System.out.println("Raccoon got hurt. Source: " + source + " Amount: " + amount + " Health: " + this.getHealth());
        //  try {
        //     logger.log(Level.INFO, "Raccoon ({0}) Attacker: {1}", new Object[]{this.getUUID(), source.getEntity().getUUID()});
        // } catch (NullPointerException n) {
        //      logger.log(Level.SEVERE, "NullPointerException: No attacker was found.");
        //  }
        return result;
    }

    PrioritizedGoal findWaterGoal = this.goalSelector.getGoal(RaccoonMoveToWaterGoal.class);
    PrioritizedGoal swimGoal = this.goalSelector.getGoal(RaccoonEntity.SwimGoal.class);


    class RaccoonSitGoal extends SitGoal {
        public RaccoonSitGoal(RaccoonEntity raccoon) {
            super(raccoon);
        }

        public boolean canUse() {
            if (!RaccoonEntity.this.isTame()) {
                return false;
            } else if (RaccoonEntity.this.isInWaterOrBubble()) {
                return false;
            } else if (!RaccoonEntity.this.isOnGround()) {
                return false;
            } else {
                LivingEntity livingentity = RaccoonEntity.this.getOwner();
                if (livingentity == null) {
                    return true;
                } else {
                    return RaccoonEntity.this.distanceToSqr(livingentity) < 144.0D && livingentity.getLastHurtByMob() != null ? false : RaccoonEntity.this.isOrderedToSit();
                }
            }
        }

        public void start() {
            RaccoonEntity.this.getNavigation().stop();
            RaccoonEntity.this.setOrderedToSit(true);
            RaccoonEntity.this.setSitting(true);
        }

        public void stop() {
            RaccoonEntity.this.setOrderedToSit(false);
            RaccoonEntity.this.setSitting(false);
        }
    }

    class RaccoonLieOnBedGoal extends MoveToBlockGoal {
        private final RaccoonEntity raccoon;
        private int countdown = RaccoonEntity.this.random.nextInt(140);

        public RaccoonLieOnBedGoal(RaccoonEntity raccoon, double speed, int searchRange) {
            super(raccoon, speed, searchRange, 6);
            this.raccoon = raccoon;
            this.verticalSearchStart = -2;
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        }

        @Override
        public boolean canContinueToUse() {
            if (this.countdown > 0) {
                --this.countdown;
                return false;
            }
            return super.canContinueToUse();
        }

        @Override
        public boolean canUse() {
            if (!canReachPosition(blockPos))
                return false;

            boolean flag = !this.raccoon.isOrderedToSit() && !this.raccoon.isSleeping() && super.canUse();
            // Check if the raccoon is tame
            if (this.raccoon.isTame()) {
                // If the raccoon is tame, return true regardless of player proximity
                return flag;
            } else {
                // If the raccoon is not tame, check for nearby players
                List<CatEntity> catEntities = this.raccoon.level.getEntitiesOfClass(CatEntity.class, this.raccoon.getBoundingBox().inflate(16D, 8D, 16D));
                if (!catEntities.isEmpty()) {
                    this.stop();
                    return false;
                }
                List<ServerPlayerEntity> playerEntities = this.raccoon.level.getEntitiesOfClass(ServerPlayerEntity.class, this.raccoon.getBoundingBox().inflate(7.0D, 5.0D, 7.0D));
                // If there are nearby players, return false
                for (ServerPlayerEntity player : playerEntities) {
                    // Check if the current player is in creative mode or spectator mode
                    if (!player.isCreative() && !player.isSpectator()) {
                        // If any player is found who is not in creative or spectator mode, return false
                        this.stop();
                        return false;
                    }
                }
                // Otherwise, check other conditions

                return flag;
            }
        }

        @Override
        public void start() {
            super.start();
            this.raccoon.setInSittingPose(false);
        }

        @Override
        protected int nextStartTick(Creature creature) {
            return 40;
        }

        private boolean canReachPosition(BlockPos pos) {
            Path path = raccoon.getNavigation().createPath(pos, 0);
            return path != null && path.canReach();
        }

        @Override
        public void stop() {
            super.stop();
            this.countdown = RaccoonEntity.this.random.nextInt(140);
            this.raccoon.wakeUp();
        }

        @Override
        public void tick() {
            super.tick();
            this.raccoon.setInSittingPose(false);
            if (!this.isReachedTarget()) {
                this.raccoon.setSleeping(false);
            } else if (!this.raccoon.isSleeping()) {
                this.raccoon.setSleeping(true);
            }
        }

        @Override
        protected boolean isValidTarget(IWorldReader worldReader, BlockPos pos) {
            return worldReader.isEmptyBlock(pos.above()) && worldReader.getBlockState(pos).getBlock().is(BlockTags.BEDS);
        }
    }


    public void setTame(boolean p_30443_) {
        super.setTame(p_30443_);
        if (p_30443_) {
            if (this.getRaccoonType() == Type.RABID) {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0D);
                this.setHealth(40.0F);
            } else {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(36.0D);
                this.setHealth(36.0F);
            }
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(24.0D);
        }
        if (this.getRaccoonType() == Type.RABID) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(7.0D);
        } else {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6.0D);
        }
        this.navigation.stop();
        this.setOrderedToSit(true);
    }

    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();


        if (this.level.isClientSide) {
            boolean flag = this.isOwnedBy(player) || this.isTame() || (this.isFood(itemstack) && !this.isTame());
            return flag ? ActionResultType.CONSUME : ActionResultType.PASS;
        } else {
            if (this.isTame()) {
                if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    if (!player.abilities.instabuild) {
                        itemstack.shrink(1);
                    }

                    this.heal((float) item.getFoodProperties().getNutrition());
                    return ActionResultType.SUCCESS;
                }

                if (item.equals(Items.POTION)) {
                    this.setThirst(this.getThirst() + 30);
                    itemstack.shrink(1);

                    // Play the drinking sound
                    this.playSound(SoundEvents.GENERIC_DRINK, 1.0F, 1.0F);

                    // Replace with glass bottle
                    if (!player.inventory.add(new ItemStack(Items.GLASS_BOTTLE))) {
                        player.drop(new ItemStack(Items.GLASS_BOTTLE), false);
                    }
                    return ActionResultType.CONSUME;
                }

                if (!(item instanceof DyeItem)) {
                    ActionResultType interactionresult = super.mobInteract(player, hand);
                    if ((!interactionresult.consumesAction() || this.isBaby()) && this.isOwnedBy(player)) {
                        this.setSitting(!this.isOrderedToSit());
                        this.setOrderedToSit(!this.isOrderedToSit());
                        this.jumping = false;
                        this.navigation.stop();

                        if (this.getTarget() != null) {
                            try {
                                this.setTarget(null);
                                // System.out.println("Target set to null successfully");
                            } catch (Exception e) {
                                logger.log(Level.SEVERE, "Error while setting target to null", e);
                            }
                        }
                        return ActionResultType.SUCCESS;
                    }

                    return interactionresult;
                }
            } else if (this.isFood(itemstack)) { // Logic for taming the raccoon
                if (!player.abilities.instabuild) {
                    itemstack.shrink(1);
                }

                if (!this.level.isClientSide) {
                    if (this.random.nextInt(12) == 0) {
                        this.tame(player);
                        this.navigation.stop();
                        this.setTarget(null);
                        this.setOrderedToSit(true);
                        this.level.broadcastEntityEvent(this, (byte) 7);
                    } else {
                        this.level.broadcastEntityEvent(this, (byte) 6);
                    }
                }

                return ActionResultType.SUCCESS;
            }

            return super.mobInteract(player, hand); // Use the parent class method to handle other cases
        }
    }

    class RaccoonBreedGoal extends BreedGoal {
        private static final int BABY_AGE = -24000;
        private static final int PARENT_AGE = 6000;
        private static final int MAX_RANDOM = 10;
        private static final int MEDIUM_RANDOM = 5;

        public RaccoonBreedGoal(double p_28668_) {
            super(RaccoonEntity.this, p_28668_);
        }

        @Override
        public void start() {
            ((RaccoonEntity) this.animal).clearStates();
            ((RaccoonEntity) this.partner).clearStates();
            super.start();
        }

        @Override
        protected void breed() {
            ServerWorld serverWorld = (ServerWorld) this.level;
            RaccoonEntity raccoon = (RaccoonEntity) this.animal.getBreedOffspring(serverWorld, this.partner);
            RaccoonEntity[] babies = createBabies();

            if (areBabiesNotNull(babies)) {
                ServerPlayerEntity player = getPlayer();
                if (player != null) {
                    player.awardStat(Stats.ANIMALS_BRED);
                    CriteriaTriggers.BRED_ANIMALS.trigger(player, this.animal, this.partner, raccoon);
                }

                resetParents();
                positionBabies(babies);

                addBabiesToWorld(serverWorld, babies);

                this.level.broadcastEntityEvent(this.animal, (byte) 18);
                if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                    spawnExperienceOrbs();
                }
            }
        }

        private RaccoonEntity[] createBabies() {
            return new RaccoonEntity[]{
                    EntityType.RACCOON.create(this.level),
                    EntityType.RACCOON.create(this.level),
                    EntityType.RACCOON.create(this.level),
                    EntityType.RACCOON.create(this.level),
                    EntityType.RACCOON.create(this.level)
            };
        }

        private boolean areBabiesNotNull(RaccoonEntity[] babies) {
            for (RaccoonEntity baby : babies) {
                if (baby == null) {
                    return false;
                }
            }
            return true;
        }

        private ServerPlayerEntity getPlayer() {
            ServerPlayerEntity player = this.animal.getLoveCause();
            if (player == null) {
                player = this.partner.getLoveCause();
            }
            return player;
        }

        private void resetParents() {
            this.animal.setAge(PARENT_AGE);
            this.partner.setAge(PARENT_AGE);
            this.animal.resetLove();
            this.partner.resetLove();
        }

        private void positionBabies(RaccoonEntity[] babies) {
            for (RaccoonEntity baby : babies) {
                baby.setAge(BABY_AGE);
                baby.moveTo(this.animal.getX(), this.animal.getY(), this.animal.getZ(), 0.0F, 0.0F);
            }
        }

        private void addBabiesToWorld(ServerWorld serverWorld, RaccoonEntity[] babies) {
            serverWorld.addFreshEntityWithPassengers(babies[0]);
            for (int i = 1; i < babies.length; i++) {
                if (RaccoonEntity.this.random.nextInt(MEDIUM_RANDOM) == 0) {
                    if (RaccoonEntity.this.random.nextInt(MAX_RANDOM) == 0) {
                        babies[i].setRaccoonType(Type.RABID);
                    }
                    serverWorld.addFreshEntityWithPassengers(babies[i]);
                }
            }
        }

        private void spawnExperienceOrbs() {
            this.level.addFreshEntity(
                    new ExperienceOrbEntity(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), this.animal.getRandom().nextInt(7) + 1)
            );
        }
    }

    class RaccoonDropOffItemsGoal extends MoveToBlockGoal {
        private final RaccoonEntity raccoon;
        private LivingEntity owner;
        private final double speed;


        public RaccoonDropOffItemsGoal(RaccoonEntity raccoon, double speed) {
            super(raccoon, speed, 10);
            this.raccoon = raccoon;
            this.speed = speed;
        }

        @Override
        public boolean canUse() {
            if (raccoon.getOwner() != null) {
                this.owner = raccoon.getOwner();
            } else {
                return false;
            }
            if (raccoon.getItemBySlot(EquipmentSlotType.MAINHAND).isEmpty()) {
                return false;
            }

            if (!raccoon.isTame()) {
                return false;
            }
            ItemStack itemStack = raccoon.getItemBySlot(EquipmentSlotType.MAINHAND);
            if (!VALUABLE_ITEMS.contains(itemStack.getItem())) {
                return false;
            }
            return raccoon.getRandom().nextInt(20) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return !raccoon.getItemBySlot(EquipmentSlotType.MAINHAND).isEmpty() && this.owner != null;
        }

        @Override
        public void start() {
            BlockPos ownerPos = owner.blockPosition();
            if (!(ownerPos.getX() == 0) && !(ownerPos.getY() == 0) && !(ownerPos.getZ() == 0)) {
                this.moveTo(ownerPos.getX(), ownerPos.getY(), ownerPos.getZ(), this.speed);
            }

        }

        @Override
        public void tick() {
            super.tick();


            if (this.isReachedTarget()) {
                this.raccoon.spitOutItem(this.raccoon.getItemBySlot(EquipmentSlotType.MAINHAND));
                this.stop();
            }
        }

        @Override
        public void stop() {
            super.stop();
        }

        @Override
        protected boolean isValidTarget(IWorldReader world, BlockPos pos) {
            // Since the target is the owner's position, this will always return true.
            return true;
        }

        private void moveTo(double x, double y, double z, double speed) {
            this.raccoon.getNavigation().moveTo(x, y, z, speed);
        }
    }


    public void aiStep() {
        if (!this.level.isClientSide && this.isAlive() && this.isEffectiveAi()) {
            ++this.ticksSinceEaten;
            ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.MAINHAND);
            if (this.canEat(itemstack)) {
                if (this.ticksSinceEaten > 80) {
                    ItemStack itemstack1 = itemstack.finishUsingItem(this.level, this);
                    int hungerIncrease = MIN_HUNGER_INCREASE + this.random.nextInt(MAX_HUNGER_INCREASE - MIN_HUNGER_INCREASE + 1);
                    this.setHunger(this.hunger + hungerIncrease);

                    try {
                        this.setTarget(null);
                        //System.out.println("Target set to null successfully");
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Error while setting target to null", e);
                    }

                    if (!itemstack1.isEmpty()) {
                        this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack1);
                    }

                    this.ticksSinceEaten = 0;
                } else if (this.ticksSinceEaten > 120 && this.random.nextFloat() < 0.1F) {
                    this.playSound(this.getEatingSound(itemstack), 1.0F, 1.0F);
                    this.level.broadcastEntityEvent(this, (byte) 45);

                }
            }

            LivingEntity livingentity = this.getTarget();
            if (livingentity == null || !livingentity.isAlive()) {
                this.setIsCrouching(false);
                this.setIsInterested(false);
            }
        }

        if (this.isSleeping() || this.isImmobile()) {
            this.jumping = false;
            this.xxa = 0.0F;
            this.zza = 0.0F;
        }

        super.aiStep();
        if (this.isDefending() && this.random.nextFloat() < 0.05F) {
            this.playSound(SoundEvents.FOX_AGGRO, 1.0F, 1.0F);
        }

    }

    protected boolean isImmobile() {
        return this.isDeadOrDying();
    }

    private boolean canEat(ItemStack p_213464_1_) {
        return p_213464_1_.getItem().isEdible() && this.getTarget() == null && this.onGround && !this.isSleeping();
    }

    protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_) {
        if (this.random.nextFloat() < 0.2F) {
            float f = this.random.nextFloat();
            ItemStack itemstack;
            if (f < 0.05F) {
                itemstack = new ItemStack(Items.EMERALD);
            } else if (f < 0.2F) {
                itemstack = new ItemStack(Items.EGG);
            } else if (f < 0.4F) {
                itemstack = this.random.nextBoolean() ? new ItemStack(Items.RABBIT_FOOT) : new ItemStack(Items.RABBIT_HIDE);
            } else if (f < 0.6F) {
                itemstack = new ItemStack(Items.WHEAT);
            } else if (f < 0.8F) {
                itemstack = new ItemStack(Items.LEATHER);
            } else {
                itemstack = new ItemStack(Items.FEATHER);
            }

            this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte p_70103_1_) {
        if (p_70103_1_ == 45) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.MAINHAND);
            if (!itemstack.isEmpty()) {
                for (int i = 0; i < 8; ++i) {
                    Vector3d vector3d = (new Vector3d(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D)).xRot(-this.xRot * ((float) Math.PI / 180F)).yRot(-this.yRot * ((float) Math.PI / 180F));
                    this.level.addParticle(new ItemParticleData(ParticleTypes.ITEM, itemstack), this.getX() + this.getLookAngle().x / 2.0D, this.getY(), this.getZ() + this.getLookAngle().z / 2.0D, vector3d.x, vector3d.y + 0.05D, vector3d.z);
                }
            }
        } else {
            super.handleEntityEvent(p_70103_1_);
        }

    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, (double) 0.3F)
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    public RaccoonEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        RaccoonEntity raccoon = EntityType.RACCOON.create(p_241840_1_);
        raccoon.setRaccoonType(this.random.nextBoolean() ? this.getRaccoonType() : ((RaccoonEntity) p_241840_2_).getRaccoonType());
        return raccoon;
    }

    @Nullable
    public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
        Optional<RegistryKey<Biome>> optional = p_213386_1_.getBiomeName(this.blockPosition());
        Type raccoonType = Type.byBiome(optional);
        boolean flag = false;
        if (p_213386_4_ instanceof RaccoonData) {
            raccoonType = ((RaccoonData) p_213386_4_).type;
            if (((RaccoonData) p_213386_4_).getGroupSize() >= 2) {
                flag = true;
            }
        } else {
            p_213386_4_ = new RaccoonData(raccoonType);
        }

        this.setRaccoonType(raccoonType);
        if (flag) {
            this.setAge(-24000);
        }
        if (this.random.nextInt(25) == 0) {
            this.setRaccoonType(Type.RABID);
        }

        if (p_213386_1_ instanceof ServerWorld) {
            this.setTargetGoals();
        }

        this.populateDefaultEquipmentSlots(p_213386_2_);
        return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
    }

    private void setTargetGoals() {
        if (this.getRaccoonType() == Type.RED) {
            this.targetSelector.addGoal(4, this.landTargetGoal);
            this.targetSelector.addGoal(4, this.turtleEggTargetGoal);
            this.targetSelector.addGoal(6, this.fishTargetGoal);
        } else {
            this.targetSelector.addGoal(4, this.fishTargetGoal);
            this.targetSelector.addGoal(6, this.landTargetGoal);
            this.targetSelector.addGoal(6, this.turtleEggTargetGoal);
        }

    }

    protected void usePlayerItem(PlayerEntity p_175505_1_, ItemStack p_175505_2_) {
        if (this.isFood(p_175505_2_)) {
            this.playSound(this.getEatingSound(p_175505_2_), 1.0F, 1.0F);
        }

        super.usePlayerItem(p_175505_1_, p_175505_2_);
    }

    protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
        return this.isBaby() ? p_213348_2_.height * 0.85F : 0.4F;
    }

    public Type getRaccoonType() {
        return Type.byId(this.entityData.get(DATA_TYPE_ID));
    }

    private void setRaccoonType(Type p_213474_1_) {
        this.entityData.set(DATA_TYPE_ID, p_213474_1_.getId());
    }

    private List<UUID> getTrustedUUIDs() {
        List<UUID> list = Lists.newArrayList();
        list.add(this.entityData.get(DATA_TRUSTED_ID_0).orElse((UUID) null));
        list.add(this.entityData.get(DATA_TRUSTED_ID_1).orElse((UUID) null));
        return list;
    }

    private void addTrustedUUID(@Nullable UUID p_213465_1_) {
        if (this.entityData.get(DATA_TRUSTED_ID_0).isPresent()) {
            this.entityData.set(DATA_TRUSTED_ID_1, Optional.ofNullable(p_213465_1_));
        } else {
            this.entityData.set(DATA_TRUSTED_ID_0, Optional.ofNullable(p_213465_1_));
        }

    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        List<UUID> list = this.getTrustedUUIDs();
        ListNBT listnbt = new ListNBT();

        for (UUID uuid : list) {
            if (uuid != null) {
                listnbt.add(NBTUtil.createUUID(uuid));
            }
        }

        ListNBT listnbt2 = new ListNBT();
        for (UUID uuid : this.homeMembers) {
            if (uuid != null) {
                listnbt2.add(NBTUtil.createUUID(uuid));
            }
        }
        compound.put("HomeMembers", listnbt2);

        // Save the leader's UUID if it exists
        UUID leaderUUID = this.entityData.get(DATA_LEADER_ID).orElse(null);
        if (leaderUUID != null) {
            compound.putUUID("Leader", leaderUUID);
        }

        compound.putInt("Thirst", thirst);
        compound.put("Trusted", listnbt);
        compound.putBoolean("Sleeping", this.isSleeping());
        compound.putString("BoggedType", this.getRaccoonType().getName());
        compound.putBoolean("Sitting", this.isSitting());
        compound.putBoolean("Crouching", this.isCrouching());

        if (HOME_POS != null) {
            compound.put("HomePos", NBTUtil.writeBlockPos(HOME_POS));
        }

        compound.putInt("Hunger", this.hunger);
        compound.putBoolean("HasHome", this.entityData.get(HAS_HOME));
        compound.putBoolean("CanHaveHome", this.entityData.get(CAN_HAVE_HOME));
        compound.putBoolean("CanGoHome", this.entityData.get(CAN_GO_HOME));
        compound.putInt("CooldownBeforeGoingHome", this.entityData.get(COOLDOWN_BEFORE_GOING_HOME));
        compound.putInt("Time_For_breed", TIME_FOR_BREED);
        compound.putInt("TIME_FOR_SITTING", TIME_FOR_SITTING);
        compound.putBoolean("Dirty", isDirty);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        ListNBT listnbt = compound.getList("Trusted", 11);

        for (int i = 0; i < listnbt.size(); ++i) {
            this.addTrustedUUID(NBTUtil.loadUUID(listnbt.get(i)));
        }

        this.setThirst(compound.getInt("Thirst"));

        ListNBT listnbt2 = compound.getList("HomeMembers", 11); // 11 indicates IntArray tag type for UUID
        this.homeMembers.clear();
        for (int i = 0; i < listnbt2.size(); ++i) {
            UUID uuid = NBTUtil.loadUUID(listnbt2.get(i));
            if (uuid != null) {
                this.homeMembers.add(uuid);
            }
        }

        // Load the leader's UUID if it exists
        if (compound.contains("Leader", 11)) { // 11 indicates IntArray
            UUID leaderUUID = NBTUtil.loadUUID(compound.get("Leader"));
            this.entityData.set(DATA_LEADER_ID, Optional.ofNullable(leaderUUID));
        }
        this.setDirty(compound.getBoolean("Dirty"));
        this.setSleeping(compound.getBoolean("Sleeping"));
        this.setRaccoonType(Type.byName(compound.getString("BoggedType")));
        this.setSitting(compound.getBoolean("Sitting"));
        this.setIsCrouching(compound.getBoolean("Crouching"));

        if (compound.contains("HomePos")) {
            BlockPos homePos = NBTUtil.readBlockPos(compound.getCompound("HomePos"));
            this.setHome(homePos);
        }

        this.entityData.set(HAS_HOME, compound.getBoolean("HasHome"));
        this.entityData.set(CAN_HAVE_HOME, compound.getBoolean("CanHaveHome"));
        this.entityData.set(CAN_GO_HOME, compound.getBoolean("CanGoHome"));
        this.entityData.set(COOLDOWN_BEFORE_GOING_HOME, compound.getInt("CooldownBeforeGoingHome"));
        this.setHunger(compound.getInt("Hunger"));

        if (this.level instanceof ServerWorld) {
            this.setTargetGoals();
        }
    }

    public void setLeader(UUID leader) {
        this.entityData.set(DATA_LEADER_ID, Optional.ofNullable(leader));
    }

    public void addHomeMember(UUID member) {
        if (this.homeMembers.size() < MAX_HOME_MEMBERS) {
            this.homeMembers.add(member);
        }
    }

    public void removeHomeMember(UUID member) {
        this.homeMembers.remove(member);
    }

    public UUID getLeader() {
        return this.entityData.get(DATA_LEADER_ID).orElse(null);
    }

    public List<UUID> getHomeMembers() {
        return this.homeMembers;
    }

    public void setCanHome(boolean canGoHome) {
        this.entityData.set(CAN_GO_HOME, canGoHome);
    }

    public boolean CanGoHome() {
        return (this.getHunger() >= 20) && (this.getThirst() >= 20) && (this.getTarget() == null);
    }

    public void setCanHaveHome(boolean canHaveHome) {
        this.entityData.set(CAN_HAVE_HOME, canHaveHome);
    }

    public void setHomeX(int p_28611_) {
        HOME_POS_X = p_28611_;
    }

    public void setHomeZ(int p_28611_) {
        HOME_POS_Z = p_28611_;
    }

    public void setHomeY(int p_28611_) {
        HOME_POS_Y = p_28611_;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public int getHunger() {
        return this.hunger;
    }

    public class FollowLeaderGoal extends Goal {
        private final RaccoonEntity entity;
        private final double speed;
        private final float maxDist;
        private final float minDist;
        @Nullable
        private RaccoonEntity leader;

        public FollowLeaderGoal(RaccoonEntity entity, double speed, float maxDist, float minDist) {
            this.entity = entity;
            this.speed = speed;
            this.maxDist = maxDist;
            this.minDist = minDist;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (isPlayerNearby() || isCropNearby() || entity.isTame() || entity.getTarget() != null || entity.isLeader() || entity.getHunger() <= 40 || entity.getThirst() <= 40) {
                return false;
            }

            UUID leaderUUID = entity.getLeader();
            if (leaderUUID == null) {
                return false;
            }

            Entity leaderEntity = entity.getEntityByUUID(leaderUUID);
            if (!(leaderEntity instanceof RaccoonEntity)) {
                return false;
            }

            this.leader = (RaccoonEntity) leaderEntity;
            return !this.leader.isSleeping();
        }

        @Override
        public boolean canContinueToUse() {
            if (isPlayerNearby() || isCropNearby() || entity.getTarget() != null || entity.isLeader() || this.leader == null || entity.getHunger() <= 40 || entity.getThirst() <= 40 || this.leader.isSleeping()) {
                return false;
            }

            double distanceToLeader = this.entity.distanceTo(this.leader);
            return distanceToLeader >= this.minDist && distanceToLeader <= 8.0D && !this.entity.getNavigation().isDone();
        }

        @Override
        public void start() {
            if (this.leader != null) {
                this.entity.getNavigation().moveTo(this.leader, this.speed);
            }
        }

        @Override
        public void tick() {
            if (this.leader != null) {
                double distanceToLeader = this.entity.distanceTo(this.leader);
                if (this.leader.getTarget() != null) {
                    this.entity.setTarget(this.leader.getTarget());
                }
                if (distanceToLeader > this.maxDist || distanceToLeader < this.minDist) {
                    this.entity.getNavigation().moveTo(this.leader, this.speed);
                } else {
                    Vector3d moveToPos = RandomPositionGenerator.getPosTowards(this.entity, 10, 7, new Vector3d(this.leader.getX(), this.leader.getY(), this.leader.getZ()));
                    if (moveToPos != null) {
                        this.entity.getNavigation().moveTo(moveToPos.x, moveToPos.y, moveToPos.z, this.speed);
                    }
                }
            }
        }

        @Override
        public void stop() {
            this.entity.getNavigation().stop();
        }

        private boolean isPlayerNearby() {
            return this.entity.level.getEntitiesOfClass(PlayerEntity.class, this.entity.getBoundingBox().inflate(12.0D))
                    .stream().anyMatch(player -> !player.isSpectator() && !player.isCreative());
        }

        private boolean isCropNearby() {
            BlockPos entityPos = entity.blockPosition();
            int radius = 7;
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockState blockState = entity.level.getBlockState(entityPos.offset(x, y, z));
                        if (blockState.getBlock() instanceof CropsBlock) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }


    public void setGoHomeCooldown(int ticks) {
        this.goHomeCooldown = ticks;
    }

    public void decrementGoHomeCooldown() {
        if (this.goHomeCooldown > 0) {
            this.goHomeCooldown--;
        }
    }

    public boolean isGoHomeCooldownActive() {
        return this.goHomeCooldown > 0;
    }


    public BlockPos getHomePos() {
        return HOME_POS;
    }

    public void GoToHome(float speed) {
        BlockPos homePos = this.getHomePos();
        if (homePos != null && CanGoHome() && homePos.getX() != 0 && homePos.getY() != 0 && homePos.getZ() != 0) {
            this.getNavigation().moveTo(homePos.getX(), homePos.getY(), homePos.getZ(), speed);
        }
    }


    public void SetThisAsHome(double x, double y, double z) {

        this.setHome(x, y, z);
        this.entityData.set(HAS_HOME, true);
    }

    //BlockPos entityPos1 = new BlockPos(this.getX(), this.getY(), this.getZ());
    public boolean close(Vector3d pos, double distance) {

        BlockPos homePos = HOME_POS;
        if (homePos != null && HOME_POS.getX() != 0 && HOME_POS.getY() != 0 && HOME_POS.getZ() != 0) {
            return homePos.distSqr(pos.x, pos.y, pos.z, true) < distance * distance;
        } else {
            return false;
        }
    }


    public boolean IsAtHome() {
        return HOME_POS.getX() != 0 && HOME_POS.getY() != 0 && HOME_POS.getZ() != 0 && this.close(this.position(), 8D);
    }

    public void PrioritizeHomeOverOthers(float speed) {
        if (HOME_POS != null && CanGoHome()) {
            if (HOME_POS.getX() != 0 && HOME_POS.getY() != 0 && HOME_POS.getZ() != 0) {
                this.getMoveControl().setWantedPosition(HOME_POS.getX(), HOME_POS.getY(), HOME_POS.getZ(), speed);
                LOGGER.info("Raccoon wants to go to" + this.getMoveControl().getWantedX() + " " + this.getMoveControl().getWantedY() + " " + this.getMoveControl().getWantedZ());
            }
        }
    }

    @Nullable
    public Entity getEntityByUUID(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return ((ServerWorld) this.level).getEntity(uuid);
    }

    public boolean isSitting() {
        return this.getFlag(1);
    }

    public void setSitting(boolean p_213466_1_) {
        this.setFlag(1, p_213466_1_);
        if (p_213466_1_) {
            this.getNavigation().stop();
        }
    }

    public boolean isFaceplanted() {
        return this.getFlag(64);
    }

    private void setFaceplanted(boolean p_213492_1_) {
        this.setFlag(64, p_213492_1_);
    }

    private boolean isDefending() {
        return this.getFlag(128);
    }

    private void setDefending(boolean p_213482_1_) {
        this.setFlag(128, p_213482_1_);
    }

    public boolean isSleeping() {
        return this.getFlag(32);
    }

    private void setSleeping(boolean p_213485_1_) {
        this.setFlag(32, p_213485_1_);
    }

    private void setFlag(int p_213505_1_, boolean p_213505_2_) {
        if (p_213505_2_) {
            this.entityData.set(DATA_FLAGS_ID, (byte) (this.entityData.get(DATA_FLAGS_ID) | p_213505_1_));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte) (this.entityData.get(DATA_FLAGS_ID) & ~p_213505_1_));
        }

    }

    private boolean getFlag(int p_213507_1_) {
        return (this.entityData.get(DATA_FLAGS_ID) & p_213507_1_) != 0;
    }

    public boolean canTakeItem(ItemStack p_213365_1_) {
        EquipmentSlotType equipmentslottype = Mob.getEquipmentSlotForItem(p_213365_1_);
        if (!this.getItemBySlot(equipmentslottype).isEmpty()) {
            return false;
        } else {
            return equipmentslottype == EquipmentSlotType.MAINHAND && super.canTakeItem(p_213365_1_);
        }
    }

    public boolean canHoldItem(ItemStack p_175448_1_) {
        Item item = p_175448_1_.getItem();
        ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.MAINHAND);
        return itemstack.isEmpty() && !item.equals(Items.POISONOUS_POTATO) || !item.equals(Items.POISONOUS_POTATO) && this.ticksSinceEaten > 0 && item.isEdible() && !itemstack.getItem().isEdible();
    }

    private void spitOutItem(ItemStack p_213495_1_) {
        if (!p_213495_1_.isEmpty() && !this.level.isClientSide) {
            ItemEntity itementity = new ItemEntity(this.level, this.getX() + this.getLookAngle().x, this.getY() + 1.0D, this.getZ() + this.getLookAngle().z, p_213495_1_);
            itementity.setPickUpDelay(40);
            itementity.setThrower(this.getUUID());
            this.playSound(SoundEvents.FOX_SPIT, 1.0F, 1.0F);
            this.level.addFreshEntity(itementity);
        }
    }

    private void dropItemStack(ItemStack p_213486_1_) {
        ItemEntity itementity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), p_213486_1_);
        this.level.addFreshEntity(itementity);
    }

    protected void pickUpItem(ItemEntity p_175445_1_) {
        ItemStack itemstack = p_175445_1_.getItem();
        if (this.canHoldItem(itemstack)) {
            int i = itemstack.getCount();
            if (i > 1) {
                this.dropItemStack(itemstack.split(i - 1));
            }

            this.spitOutItem(this.getItemBySlot(EquipmentSlotType.MAINHAND));
            this.onItemPickup(p_175445_1_);
            this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack.split(1));
            this.handDropChances[EquipmentSlotType.MAINHAND.getIndex()] = 2.0F;
            this.take(p_175445_1_, itemstack.getCount());
            p_175445_1_.remove();
            this.ticksSinceEaten = 0;
        }

    }

    public boolean ShouldContinueFindingStructure() {
        return !this.isSitting();
    }

    class RaccoonStealGoal extends MoveToBlockGoal {
        private final RaccoonEntity raccoon;
        private int cooldown = 0;

        public RaccoonStealGoal(RaccoonEntity raccoon, double speedIn, int length) {
            super(raccoon, speedIn, length, 6);
            this.raccoon = raccoon;
        }

        @Override
        protected boolean isValidTarget(IWorldReader worldIn, BlockPos pos) {
            BlockState state = worldIn.getBlockState(pos);

            // Check if the block is a ChestBlock
            if (state.getBlock() instanceof ChestBlock) {
                TileEntity tileEntity = worldIn.getBlockEntity(pos);

                // Ensure the TileEntity is an IInventory (which chests are)
                if (tileEntity instanceof IInventory) {
                    IInventory chestInventory = (IInventory) tileEntity;

                    int itemCount = 0; // counter for items

                    // Check each slot to see if it contains an item
                    int numSlots = chestInventory.getContainerSize();
                    for (int i = 0; i < numSlots; i++) {
                        ItemStack stack = chestInventory.getItem(i);
                        if (!stack.isEmpty()) {
                            itemCount++; // Increment counter if item is found
                        }
                    }

                    // Now check if item count is enough
                    if (itemCount >= 2) {
                        // Check for nearby raccoons
                        AxisAlignedBB boundingBox = new AxisAlignedBB(pos).inflate(3.5D, 3.5D, 3.5D);
                        List<RaccoonEntity> nearbyRaccoons = RaccoonEntity.this.level.getEntitiesOfClass(RaccoonEntity.class, boundingBox);

                        // Ensure there are no other raccoons within the radius
                        for (RaccoonEntity raccoon : nearbyRaccoons) {
                            if (!raccoon.equals(this)) {
                                return false;
                            }
                        }

                        return true; // Return true if the chest has enough items and no other raccoons are nearby
                    }
                }
            }

            return false; // Return false if not enough items, if there are other raccoons, or if the block is not a chest
        }


        @Override
        public void tick() {
            super.tick();
            if (this.isReachedTarget()) {
                BlockPos chestPos = this.blockPos;
                TileEntity tileEntity = raccoon.level.getBlockEntity(chestPos);
                if (tileEntity instanceof ChestTileEntity) {
                    IInventory chestInventory = (IInventory) tileEntity;
                    stealFromChest(chestInventory);
                }
            }
        }

        @Override
        public boolean canUse() {
            if (cooldown > 0) {
                cooldown--;
                return false;
            }
            if (RaccoonEntity.this.getItemBySlot(EquipmentSlotType.MAINHAND).isEmpty() || RaccoonEntity.this.getItemBySlot(EquipmentSlotType.OFFHAND).isEmpty()) {
                return super.canUse();
            }
            return false;
        }

        public boolean canContinueToUse() {
            return RaccoonEntity.this.getItemBySlot(EquipmentSlotType.MAINHAND).isEmpty() || RaccoonEntity.this.getItemBySlot(EquipmentSlotType.OFFHAND).isEmpty();
        }

        private void stealFromChest(IInventory chestInventory) {
            if (RaccoonEntity.this.getItemBySlot(EquipmentSlotType.MAINHAND).isEmpty() || RaccoonEntity.this.getItemBySlot(EquipmentSlotType.OFFHAND).isEmpty()) {
                int numSlots = chestInventory.getContainerSize();
                List<Integer> slots = new ArrayList<>();
                for (int i = 0; i < numSlots; i++) {
                    slots.add(i);
                }
                Collections.shuffle(slots);

                for (int i : slots) {
                    ItemStack stack = chestInventory.getItem(i);
                    if (!stack.isEmpty()) {
                        // If main hand is empty, steal an item
                        if (RaccoonEntity.this.getItemBySlot(EquipmentSlotType.MAINHAND).isEmpty()) {
                            performSteal(chestInventory, i, EquipmentSlotType.MAINHAND);
                            if (!RaccoonEntity.this.getItemBySlot(EquipmentSlotType.OFFHAND).isEmpty()) {
                                return;
                            } // If both hands are full, return
                        }

                        // If off hand is empty, steal an item
                        if (RaccoonEntity.this.getItemBySlot(EquipmentSlotType.OFFHAND).isEmpty()) {
                            performSteal(chestInventory, i, EquipmentSlotType.OFFHAND);
                            if (!RaccoonEntity.this.getItemBySlot(EquipmentSlotType.MAINHAND).isEmpty()) {
                                return;
                            } // If both hands are full, return
                        }
                    }
                }
            }
        }

        private void performSteal(IInventory chestInventory, int i, EquipmentSlotType hand) {
            ItemStack stack = chestInventory.getItem(i);
            ItemStack stolenItem = stack.split(1);
            RaccoonEntity.this.playSound(SoundEvents.PIG_STEP, 1.0F, 1.0F);
            RaccoonEntity.this.playSound(SoundEvents.FOX_SNIFF, 1.0F, 1.0F);
            RaccoonEntity.this.setItemSlot(hand, stolenItem);
            chestInventory.setItem(i, stack.isEmpty() ? ItemStack.EMPTY : stack);
        }


        @Override
        public void start() {
            super.start();
            cooldown = 100; // Adjust the cooldown duration as needed
        }
    }

    class RaccoonDefendBabyGoal extends NearestAttackableTargetGoal<PlayerEntity> {
        public RaccoonDefendBabyGoal(RaccoonEntity raccoon) {
            super(raccoon, PlayerEntity.class, 20, true, true, (Predicate<LivingEntity>) null);
        }

        public boolean canUse() {
            if (RaccoonEntity.this.isBaby()) {
                return false;
            } else {
                if (super.canUse()) {
                    for (RaccoonEntity raccoon : RaccoonEntity.this.level.getEntitiesOfClass(RaccoonEntity.class, RaccoonEntity.this.getBoundingBox().inflate(2.0D, 1.8D, 2.0D))) {
                        if (raccoon.isBaby()) {
                            if (!RaccoonEntity.this.isTame()) {
                                List<PlayerEntity> list = RaccoonEntity.this.level.getEntitiesOfClass(PlayerEntity.class, RaccoonEntity.this.getBoundingBox().inflate(1.0D, 1.0D, 1.0D), EntityPredicates.NO_SPECTATORS);
                                if (list.isEmpty()) {
                                    if (!RaccoonEntity.this.isTame() && !RaccoonEntity.this.isSleeping()) {
                                        return true;
                                    } else {
                                        return false;
                                    }

                                } else {
                                    return false;
                                }
                            }
                        }


                    }
                }
            }

            return false;
        }


        protected double getFollowDistance() {
            return 8D;
        }
    }


    public double getHomePosX() {
        return HOME_POS_X;
    }

    public double getHomePosY() {
        return HOME_POS_Y;
    }

    public double getHomePosZ() {
        return HOME_POS_Z;
    }

    public void setHome(double x, double y, double z) {
        BlockPos homePos = new BlockPos(x, y, z);
        HOME_POS = homePos;
    }

    public void setHome(BlockPos blockpos) {
        this.HOME_POS = blockpos;
    }

    public boolean isLeader() {
        return this.getLeader() != null && this.getLeader().equals(this.getUUID());
    }

    class FindHomeGoal extends Goal {
        private final RaccoonEntity raccoon;

        public FindHomeGoal(RaccoonEntity raccoon) {
            this.raccoon = raccoon;
        }

        @Override
        public boolean canUse() {
            return !AlreadyChosenHome && raccoon.getRandom().nextInt(10) == 0;
        }

        @Override
        public void tick() {
            World world = raccoon.level;
            BlockPos pos = getRandomPosition(30, 50, 120);

            if (pos != null && isSuitableHome(pos)) {
                setHome(pos);
            }
        }

        private BlockPos getRandomPosition(int range, int minY, int maxY) {
            World world = raccoon.level;
            BlockPos pos;

            do {
                int x = (int) (raccoon.getX() + raccoon.getRandom().nextInt(range * 2) - range);
                int z = (int) (raccoon.getZ() + raccoon.getRandom().nextInt(range * 2) - range);
                pos = new BlockPos(x, world.getHeight(Heightmap.Type.WORLD_SURFACE, x, z), z);
            } while (!isSuitableHomePosition(pos, world));

            return pos;
        }

        private boolean isSuitableHomePosition(BlockPos pos, World world) {
            BlockPos blockBelow = pos.below();
            BlockState blockStateBelow = world.getBlockState(blockBelow);
            return pos.getY() > 60 && world.isEmptyBlock(pos) && blockStateBelow.isSolidRender(world, blockBelow);
        }

        private boolean isSuitableHome(BlockPos pos) {
            World world = raccoon.level;
            return !world.canSeeSky(pos) && isSuitableHomePosition(pos, world) && canReachPosition(pos);
        }

        private boolean canReachPosition(BlockPos pos) {
            Path path = raccoon.getNavigation().createPath(pos, 0);
            return path != null && path.canReach();
        }

        private void setHome(BlockPos pos) {
            try {
                AlreadyChosenHome = true;
                BlockPos homePos = pos.offset(0, -3 + raccoon.getRandom().nextInt(12), 0);
                raccoon.setHome(homePos.getX(), homePos.getY(), homePos.getZ());

                logger.log(Level.INFO, "Raccoon has found Home! {0} {1} {2}",
                        new Object[]{homePos.getX(), homePos.getY(), homePos.getZ()});

                raccoon.playSound(SoundEvents.FOX_SNIFF, 1.0F, 1.0F);
                raccoon.getNavigation().moveTo(homePos.getX(), homePos.getY(), homePos.getZ(), 1.3);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error in FindHomeGoal tick", e);
            }
        }
    }


    public class RaccoonGoHomeGoal extends Goal {
        private final RaccoonEntity raccoon;
        private final float speed;
        private final double minHunger;
        private final double minThirst;

        public RaccoonGoHomeGoal(RaccoonEntity raccoon, float speed, double minHunger, double minThirst) {
            this.raccoon = raccoon;
            this.speed = speed;
            this.minHunger = minHunger;
            this.minThirst = minThirst;
        }

        @Override
        public boolean canUse() {
            return !raccoon.isGoHomeCooldownActive() && isTimeToGoHome();
        }

        private boolean isTimeToGoHome() {
            return (raccoon.level.isDay() && raccoon.isLeader() && raccoon.getHomePos() != null)
                    || (raccoon.level.isRaining() && raccoon.getHomePos() != null);
        }

        @Override
        public void start() {
            raccoon.GoToHome(this.speed);
        }

        @Override
        public boolean canContinueToUse() {
            return raccoon.getHunger() >= minHunger && raccoon.getThirst() >= minThirst && !raccoon.IsAtHome();
        }

        @Override
        public void tick() {
            if (raccoon.isHome(8D)) {
                stopAndSetCooldown();
            } else if (shouldNavigateHome()) {
                raccoon.getNavigation().stop();
                raccoon.GoToHome(this.speed);
            }

            if (raccoon.level.isRaining()) {
                raccoon.PrioritizeHomeOverOthers(this.speed);
            }
        }

        private boolean shouldNavigateHome() {
            BlockPos homePos = raccoon.getHomePos();
            return !raccoon.getNavigation().isInProgress() || raccoon.getNavigation().getTargetPos() != null && raccoon.getNavigation().getTargetPos().getX() != homePos.getX();
        }

        private void stopAndSetCooldown() {
            stop();
            raccoon.setGoHomeCooldown(1200); // Set cooldown to 1200 ticks
        }

        @Override
        public void stop() {
            raccoon.getNavigation().stop();
        }
    }


    public void Regroup(RaccoonEntity leader) {
        // Get the leader's position
        double leaderPosX = leader.getHomePosX();
        double leaderPosY = leader.getHomePosY();
        double leaderPosZ = leader.getHomePosZ();

        // Iterate through each home member
        for (UUID memberUUID : leader.getHomeMembers()) {
            RaccoonEntity member = (RaccoonEntity) this.getEntityByUUID(memberUUID);
            if (member != null) {
                // Set the member's navigation to the leader's position
                member.getNavigation().moveTo(leaderPosX, leaderPosY, leaderPosZ, 1.0); // Adjust the speed as needed
            }
        }
    }

    protected void sendDebugPackets() {
        super.sendDebugPackets();
        ServerPlayerEntity player = DebugUtils.findLocalPlayer(this.level);
        if (player != null)
            DebugPacketSender.sendRaccoonDebugData(player, this);

        // DebugPacketSender.testCalculateOptimalNodes(player, this, this.level, 20);

    }

    private RaccoonEntity getRaccoonByID(UUID id) {
        return (RaccoonEntity) this.getEntityByUUID(id);
    }

    private void synchronizeHomeMembers(RaccoonEntity raccoon1, RaccoonEntity raccoon2) {
        Set<UUID> combinedMembers = new HashSet<>(raccoon1.getHomeMembers());
        combinedMembers.addAll(raccoon2.getHomeMembers());

        raccoon1.homeMembers = (new ArrayList<>(combinedMembers));
        raccoon2.homeMembers = (new ArrayList<>(combinedMembers));

        // Determine and set a single leader
        UUID leader1 = raccoon1.getLeader();
        UUID leader2 = raccoon2.getLeader();

        RaccoonEntity leaderEntity = (leader1 != null) ? raccoon1 : ((leader2 != null) ? raccoon2 : null);

        if (leader1 != null && leader2 != null && !leader1.equals(leader2)) {
            leaderEntity = raccoon1.random.nextBoolean() ? raccoon1 : raccoon2;
            (leaderEntity == raccoon1 ? raccoon2 : raccoon1).setLeader(null);
        }

        // Update the home position of all members
        if (leaderEntity != null) {
            BlockPos leaderHomePos = leaderEntity.getHomePos();
            leaderEntity.getHomeMembers().stream()
                    .map(this::getRaccoonByID)
                    .filter(Objects::nonNull)
                    .forEach(member -> member.setHome(leaderHomePos));
        }
    }

    @Override
    public void die(DamageSource dmgSource) {
        super.die(dmgSource);

        // Clear this raccoon's home members and remove this raccoon from other members' lists
        List<RaccoonEntity> groupMembers = this.getHomeMembers().stream()
                .map(this::getRaccoonByID)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        groupMembers.forEach(member -> member.getHomeMembers().remove(this.getUUID()));
        this.getHomeMembers().clear();
    }


    public static int Cooldown;


    private void handleHunger() {
        incrementTimeWithoutFindingTarget();
        ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.MAINHAND);


        if (itemstack.isEdible() && !itemstack.isEmpty() && this.getHunger() <= 40) {
            eatFood(itemstack);
        }

        searchForFood();
    }

    private void incrementTimeWithoutFindingTarget() {
        if (TIME_WITHOUT_FINDING_TARGET <= TIME_THRESHOLD) {
            TIME_WITHOUT_FINDING_TARGET++;
        }
    }

    private void searchForFood() {
        List<Animal> nearbyAnimals = this.level.getEntitiesOfClass(Animal.class, this.getBoundingBox().inflate(SEARCH_RADIUS, 12, SEARCH_RADIUS));

        List<Animal> filteredAnimals = nearbyAnimals.stream()
                .filter(animal -> !entitiesToExclude.isPresent() || !entitiesToExclude.get().contains(animal.getClass()))
                .collect(Collectors.toList());

        ++ticksSinceEaten;

        if (!this.getNavigation().isInProgress() && !filteredAnimals.isEmpty() && this.getSensing().canSee(filteredAnimals.get(0))) {
            setNewTarget(filteredAnimals);
        }
    }

    private boolean shouldSearchForTarget(List<Animal> nearbyAnimals) {
        ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.MAINHAND);
        return !itemstack.isEdible() && !nearbyAnimals.isEmpty() || itemstack.isEmpty() && !nearbyAnimals.isEmpty();
    }

    private void setNewTarget(List<Animal> nearbyAnimals) {
        if (this.getTarget() == null) {
            this.setTarget(nearbyAnimals.get(0));
            // this.getNavigation().moveTo(nearbyAnimals.get(0), NAVIGATION_SPEED);
        }
    }

    @Override
    public void tick() {
        super.tick();
        handleDirtyEnvironment();
        handleHungerAndThirst();
        handleHunger();
        //manageNearbyRaccoons();
        handleSittingBehavior();
       // manageLeaderHomePosition();
        manageCooldowns();
        manageEquipment();
        handleSpecialItems();
       // handleRaccoonBehavior();
        updateMovementStates();
    }

    private void handleDirtyEnvironment() {
        BlockPos pos = this.getOnPos();
        Block currentBlock = this.level.getBlockState(pos).getBlock();
        RegistryKey<Biome> currentBiomeKey = this.level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY)
                .getResourceKey(this.level.getBiome(this.blockPosition())).orElse(null);
        boolean isDirtyBiome = DIRTY_BIOMES.contains(currentBiomeKey);
        int dirtyEnvironmentBonus = getDirtyEnvironmentBonus();

        if (this.isOnGround() && DIRTY_BLOCKS.containsKey(currentBlock)) {
            updateDirtyCountdown(DIRTY_BLOCKS.get(currentBlock), dirtyEnvironmentBonus);
        } else if (this.isInWaterOrBubble() && isDirtyBiome) {
            updateDirtyCountdown(1.0f, dirtyEnvironmentBonus);
        }

        if (this.isInWaterRainOrBubble() && this.getRaccoonType() == Type.DIRTY && !isDirtyBiome) {
            cleanRaccoon(this);
        }

        if (this.dirtyCountdown <= 0) {
            setDirty(true);
            playSound(SoundEvents.GRAVEL_STEP, 1.0F, 1.0F);
            spawnSprintParticle();
            resetDirtyCountdown();
        }
    }

    private void updateDirtyCountdown(float dirtiness, int dirtyEnvironmentBonus) {
        if (!this.isDirty() && this.getRaccoonType() == Type.RED) {
            if (this.random.nextFloat() <= dirtiness) {
                this.dirtyCountdown -= dirtyEnvironmentBonus;
            }
        }
    }

    private void handleHungerAndThirst() {
        if (!this.level.isClientSide) {
            if (!this.isTame()) {
                if (this.tickCount % 100 == 0) this.setThirst(this.getThirst() - 1);
                if (this.tickCount % 80 == 0) this.setHunger(this.getHunger() - 1);
            }

            if (this.getHunger() <= 10 && this.tick(60)) this.hurt(DamageSource.STARVE, 1.0F);
            if (this.getHunger() > 20 && this.tickCount % 50 == 0) this.heal(1.0F);
            if (this.getThirst() < 10 && this.tickCount % 80 == 0) this.hurt(DamageSource.DRY_OUT, 1.0F);
        }
    }

    private void manageNearbyRaccoons() {
        if (!this.level.isClientSide && this.random.nextInt(30) == 0) {
            List<RaccoonEntity> nearbyRaccoons = this.level.getEntitiesOfClass(RaccoonEntity.class, this.getBoundingBox().inflate(8.0D), raccoon -> raccoon != this);

            for (RaccoonEntity nearbyRaccoon : nearbyRaccoons) {
                handleRaccoonLeadership(nearbyRaccoon);
                synchronizeHomeMembers(this, nearbyRaccoon);
            }
        }
    }

    private void handleRaccoonLeadership(RaccoonEntity nearbyRaccoon) {
        if (nearbyRaccoon.getLeader() == null && this.getLeader() == null && !this.isLeader()) {
            if (this.random.nextBoolean()) {
                setAsLeader(this);
            } else {
                setAsLeader(nearbyRaccoon);
            }
        } else if (nearbyRaccoon.isLeader() && this.getLeader() == null && !this.isLeader()) {
            if (nearbyRaccoon.getHomeMembers().size() < 5) {
                joinLeaderGroup(nearbyRaccoon);
            }
        }
    }

    private void setAsLeader(RaccoonEntity raccoon) {
        raccoon.setLeader(raccoon.getUUID());
        raccoon.addHomeMember(raccoon.getUUID());
    }

    private void joinLeaderGroup(RaccoonEntity leaderRaccoon) {
        leaderRaccoon.addHomeMember(this.getUUID());
        this.setLeader(leaderRaccoon.getUUID());
        this.addHomeMember(leaderRaccoon.getUUID());
        this.setHome(leaderRaccoon.getHomePosX(), leaderRaccoon.getHomePosY(), leaderRaccoon.getHomePosZ());
    }

    private void handleSittingBehavior() {
        if (this.isSitting()) {
            this.navigation.stop();
        }

        if (this.isSitting() || this.isOrderedToSit()) {
            applySittingEffects();
        }

        if (this.age < -20000) {
            this.setSleeping(true);
            this.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 30, 40, false, false, true));
        }
    }

    private void applySittingEffects() {
        this.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 30, 80, false, false, true));
        if (this.TIME_FOR_SITTING > 0) {
            this.TIME_FOR_SITTING--;
        } else {
            this.setOrderedToSit(false);
            this.setSitting(false);
            this.TIME_FOR_SITTING = 1200;
        }
    }

    private void manageLeaderHomePosition() {
        if (this.isLeader()) {
            BlockPos leaderHomePos = this.getHomePos();
            if (leaderHomePos.getX() != 0 && leaderHomePos.getY() != 0 && leaderHomePos.getZ() != 0) {
                this.getHomeMembers().stream()
                        .map(this::getRaccoonByID)
                        .filter(Objects::nonNull)
                        .forEach(member -> member.setHome(leaderHomePos));
            }
        }
    }

    private void manageCooldowns() {
        if (this.entityData.get(COOLDOWN_BEFORE_GOING_HOME) > 0) {
            this.entityData.set(COOLDOWN_BEFORE_GOING_HOME, this.entityData.get(COOLDOWN_BEFORE_GOING_HOME) - 1);
        }
    }

    private void manageEquipment() {
        ItemStack mainHandItem = this.getItemBySlot(EquipmentSlotType.MAINHAND);
        if (mainHandItem.isEmpty() && !this.getItemBySlot(EquipmentSlotType.OFFHAND).isEmpty()) {
            ItemStack offHandItem = this.getItemBySlot(EquipmentSlotType.OFFHAND);
            // Move the offhand item to the main hand
            this.setItemSlot(EquipmentSlotType.MAINHAND, offHandItem);
            // Clear the offhand slot explicitly
            this.setItemSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
        }
    }

    private void handleSpecialItems() {
        ItemStack mainHandItem = this.getItemBySlot(EquipmentSlotType.MAINHAND);
        if (mainHandItem.getItem() == Items.TNT && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.random.nextInt(60) == 0) {
            deployTNT(mainHandItem);
        } else if (mainHandItem.getItem() == Items.EMERALD_BLOCK && !this.isSitting() && !this.level.isClientSide) {
            moveToVillage();
        }
    }

    private void deployTNT(ItemStack mainHandItem) {
        TNTEntity primedTNT = EntityType.TNT.create(this.level);
        if (primedTNT != null) {
            primedTNT.setFuse(80);
            primedTNT.setPos(this.getX(), this.getY() + 0.3, this.getZ());
            this.level.addFreshEntity(primedTNT);
            mainHandItem.shrink(1);
            this.playSound(SoundEvents.TNT_PRIMED, 1F, 1F);
            this.navigation.moveTo(this.getRandomX(30), this.getY() + 0.4, this.getRandomZ(30), 1.4);
        }
    }

    private void moveToVillage() {
        BlockPos entityPos = new BlockPos(this.getX(), this.getY(), this.getZ());
        ServerWorld serverWorld = (ServerWorld) this.level;
        BlockPos villagePos = serverWorld.findNearestMapFeature(Structure.VILLAGE, entityPos, 100, false);
        if (villagePos != null && this.ShouldContinueFindingStructure()) {
            this.navigation.moveTo(villagePos.getX(), villagePos.getY(), villagePos.getZ(), 1.2);
            playRandomSound(SoundEvents.FOX_SCREECH, 40);
        } else {
            this.navigation.stop();
        }
    }

    private void playRandomSound(SoundEvent soundEvent, int chance) {
        if (this.random.nextInt(chance) == 0) {
            this.playSound(soundEvent, 1F, 1F);
        }
    }

    private void handleRaccoonBehavior() {
        if (this.isEffectiveAi()) {
            boolean isInWater = this.isInWater();
            if (isInWater || this.getTarget() != null || this.level.isThundering()) {
                this.wakeUp();
            }

            if (isInWater || this.isSleeping()) {
                this.setOrderedToSit(false);
                this.setSitting(false);
            }

            if (this.isFaceplanted() && this.level.random.nextFloat() < 0.2F) {
                BlockPos blockPos = this.blockPosition();
                BlockState blockState = this.level.getBlockState(blockPos);
                this.level.levelEvent(2001, blockPos, Block.getId(blockState));
            }
        }
    }

    private void updateMovementStates() {
        this.interestedAngleO = this.interestedAngle;
        this.interestedAngle += (this.isInterested() ? 1.0F : 0.0F) - this.interestedAngle * 0.4F;

        this.crouchAmountO = this.crouchAmount;
        this.crouchAmount += this.isCrouching() ? 0.2F : -this.crouchAmount;
        if (this.crouchAmount > 3.0F) {
            this.crouchAmount = 3.0F;
        }
    }


    public void eatFood(ItemStack itemstack) {
        this.eat(this.level, itemstack);
        this.setItemSlot(EquipmentSlotType.MAINHAND, itemstack.finishUsingItem(this.level, this));

        increaseHunger();
        increaseThirst();

        clearTargetIfNeeded();
    }

    private void increaseHunger() {
        int hungerIncrease = MIN_HUNGER_INCREASE + this.random.nextInt(MAX_HUNGER_INCREASE - MIN_HUNGER_INCREASE + 1);
        this.setHunger(this.hunger + hungerIncrease);
    }

    private void increaseThirst() {
        this.setThirst(this.thirst + this.level.random.nextInt(8));
    }

    private void clearTargetIfNeeded() {
        if (this.getTarget() != null) {
            try {
                this.setTarget(null);
            } catch (NullPointerException e) {
                logger.log(Level.SEVERE, "NullPointerException: No target found.", e);
            }
        }
    }

    public class RaccoonMoveToWaterGoal extends MoveToBlockGoal {
        private final RaccoonEntity raccoon;

        public RaccoonMoveToWaterGoal(RaccoonEntity raccoon, double speed) {
            super(raccoon, speed, 32);
            this.raccoon = raccoon;
        }

        @Override
        public double acceptedDistance() {
            return 2.4D;
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        protected void moveMobToBlock() {
            this.raccoon.getNavigation().moveTo(
                    this.blockPos.getX() + 0.5D,
                    this.blockPos.getY(),
                    this.blockPos.getZ() + 0.5D,
                    this.speedModifier
            );
        }

        @Override
        public boolean canUse() {
            return (this.raccoon.getThirst() < 40 || this.raccoon.isDirty()) && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return (this.raccoon.getThirst() < 40 || this.raccoon.isDirty()) && super.canContinueToUse();
        }

        @Override
        protected boolean isValidTarget(IWorldReader world, BlockPos pos) {
            return isWaterSource(world, pos) && !isInDirtyBiome(pos);
        }

        private boolean isWaterSource(IWorldReader world, BlockPos pos) {
            return world.getBlockState(pos).is(Blocks.WATER);
        }

        private boolean isInDirtyBiome(BlockPos pos) {
            RegistryKey<Biome> currentBiomeKey = this.raccoon.level.registryAccess()
                    .registryOrThrow(Registry.BIOME_REGISTRY)
                    .getResourceKey(this.raccoon.level.getBiome(pos))
                    .orElse(null);
            return DIRTY_BIOMES.contains(currentBiomeKey);
        }

        @Override
        public void tick() {
            super.tick();
            if (this.isReachedTarget() && this.raccoon.getThirst() < 40) {
                drinkWater();
            }
        }

        private void drinkWater() {
            this.raccoon.playSound(SoundEvents.GENERIC_DRINK, 1.0F, 1.0F);
            int thirstIncrease = this.raccoon.getRandom().nextInt(21) + 40; // Random number between 40 and 60
            this.raccoon.setThirst(this.raccoon.getThirst() + thirstIncrease);
        }
    }


    public void cleanRaccoon(RaccoonEntity raccoon) {
        if (raccoon.getRaccoonType() == Type.DIRTY) {
            raccoon.setRaccoonType(Type.RED);
            raccoon.playSound(SoundEvents.WOLF_SHAKE, 1.0F, 1.0F);
        }
    }

    public boolean isFood(ItemStack item) {
        return item.getItem().isEdible();
    }

    protected void onOffspringSpawnedFromEgg(PlayerEntity p_213406_1_, Mob p_213406_2_) {
        ((RaccoonEntity) p_213406_2_).addTrustedUUID(p_213406_1_.getUUID());
    }

    public boolean isPouncing() {
        return this.getFlag(16);
    }

    public void setIsPouncing(boolean p_213461_1_) {
        this.setFlag(16, p_213461_1_);
    }

    public boolean isFullyCrouched() {
        return this.crouchAmount == 3.0F;
    }

    public void setIsCrouching(boolean p_213451_1_) {
        this.setFlag(4, p_213451_1_);
    }

    public boolean isCrouching() {
        return this.getFlag(4);
    }

    public void setIsInterested(boolean p_213502_1_) {
        this.setFlag(8, p_213502_1_);
    }

    public boolean isInterested() {
        return this.getFlag(8);
    }

    @OnlyIn(Dist.CLIENT)
    public float getHeadRollAngle(float p_213475_1_) {
        return MathHelper.lerp(p_213475_1_, this.interestedAngleO, this.interestedAngle) * 0.11F * (float) Math.PI;
    }

    @OnlyIn(Dist.CLIENT)
    public float getCrouchAmount(float p_213503_1_) {
        return MathHelper.lerp(p_213503_1_, this.crouchAmountO, this.crouchAmount);
    }

    public void setTarget(@Nullable LivingEntity p_70624_1_) {
        if (this.isDefending() && p_70624_1_ == null) {
            this.setDefending(false);
        }

        super.setTarget(p_70624_1_);
    }

    protected int calculateFallDamage(float p_225508_1_, float p_225508_2_) {
        return MathHelper.ceil((p_225508_1_ - 5.0F) * p_225508_2_);
    }

    private void wakeUp() {
        this.setSleeping(false);
    }

    private void clearStates() {
        this.setIsInterested(false);
        this.setIsCrouching(false);
        this.setOrderedToSit(false);
        this.setSitting(false);
        this.setSleeping(false);
        this.setDefending(false);
        this.setFaceplanted(false);
    }

    private boolean canMove() {
        return !this.isSleeping() && !this.isSitting() && this.isOrderedToSit() == false && !this.isFaceplanted();
    }

    public void playAmbientSound() {
        SoundEvent soundevent = this.getAmbientSound();
        if (soundevent == SoundEvents.FOX_SCREECH) {
            this.playSound(soundevent, 2.0F, this.getVoicePitch());
        } else {
            super.playAmbientSound();
        }

    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return SoundEvents.FOX_SLEEP;
        } else {
            if (!this.level.isDay() && this.random.nextFloat() < 0.1F) {
                List<PlayerEntity> list = this.level.getEntitiesOfClass(PlayerEntity.class, this.getBoundingBox().inflate(16.0D, 16.0D, 16.0D), EntityPredicates.NO_SPECTATORS);
                if (list.isEmpty()) {
                    return SoundEvents.FOX_SCREECH;
                }
            }

            return SoundEvents.FOX_AMBIENT;
        }
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.FOX_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.FOX_DEATH;
    }

    private boolean trusts(UUID p_213468_1_) {
        return this.getTrustedUUIDs().contains(p_213468_1_);
    }

    protected void dropAllDeathLoot(DamageSource p_213345_1_) {
        ItemStack itemstack = this.getItemBySlot(EquipmentSlotType.MAINHAND);
        ItemStack itemstack2 = this.getItemBySlot(EquipmentSlotType.OFFHAND);
        if (!itemstack.isEmpty()) {
            this.spawnAtLocation(itemstack);
            this.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
        }
        if (!itemstack2.isEmpty()) {
            this.spawnAtLocation(itemstack2);
            this.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
        }

        super.dropAllDeathLoot(p_213345_1_);
    }

    public static boolean isPathClear(RaccoonEntity p_213481_0_, LivingEntity p_213481_1_) {
        double d0 = p_213481_1_.getZ() - p_213481_0_.getZ();
        double d1 = p_213481_1_.getX() - p_213481_0_.getX();
        double d2 = d0 / d1;
        int i = 6;

        for (int j = 0; j < 6; ++j) {
            double d3 = d2 == 0.0D ? 0.0D : d0 * (double) ((float) j / 6.0F);
            double d4 = d2 == 0.0D ? d1 * (double) ((float) j / 6.0F) : d3 / d2;

            for (int k = 1; k < 4; ++k) {
                if (!p_213481_0_.level.getBlockState(new BlockPos(p_213481_0_.getX() + d4, p_213481_0_.getY() + (double) k, p_213481_0_.getZ() + d3)).getMaterial().isReplaceable()) {
                    return false;
                }
            }
        }

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public Vector3d getLeashOffset() {
        return new Vector3d(0.0D, (double) (0.55F * this.getEyeHeight()), (double) (this.getBbWidth() * 0.4F));
    }

    public class AlertablePredicate implements Predicate<LivingEntity> {
        public boolean test(LivingEntity p_test_1_) {
            if (p_test_1_ instanceof RaccoonEntity) {
                return false;
            } else if (!(p_test_1_ instanceof ChickenEntity) && !(p_test_1_ instanceof RabbitEntity) && !(p_test_1_ instanceof Monster)) {
                if (p_test_1_ instanceof TameableEntity) {
                    return !((TameableEntity) p_test_1_).isTame();
                } else if (!(p_test_1_ instanceof PlayerEntity) || !p_test_1_.isSpectator() && !((PlayerEntity) p_test_1_).isCreative()) {
                    if (RaccoonEntity.this.trusts(p_test_1_.getUUID())) {
                        return false;
                    } else {
                        return !p_test_1_.isSleeping() && !p_test_1_.isDiscrete();
                    }
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    abstract class BaseGoal extends Goal {
        private final EntityPredicate alertableTargeting = (new EntityPredicate()).range(12.0D).allowUnseeable().selector(RaccoonEntity.this.new AlertablePredicate());

        private BaseGoal() {
        }

        protected boolean hasShelter() {
            BlockPos blockpos = new BlockPos(RaccoonEntity.this.getX(), RaccoonEntity.this.getBoundingBox().maxY, RaccoonEntity.this.getZ());
            return !RaccoonEntity.this.level.canSeeSky(blockpos) && RaccoonEntity.this.getWalkTargetValue(blockpos) >= 0.0F;
        }

        protected boolean alertable() {
            return !RaccoonEntity.this.level.getNearbyEntities(LivingEntity.class, this.alertableTargeting, RaccoonEntity.this, RaccoonEntity.this.getBoundingBox().inflate(12.0D, 6.0D, 12.0D)).isEmpty();
        }
    }

    class BiteGoal extends MeleeAttackGoal {
        public BiteGoal(double p_i50731_2_, boolean p_i50731_4_) {
            super(RaccoonEntity.this, p_i50731_2_, p_i50731_4_);
        }

        protected void checkAndPerformAttack(LivingEntity p_190102_1_, double p_190102_2_) {
            double d0 = this.getAttackReachSqr(p_190102_1_);
            if (p_190102_2_ <= d0 && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(p_190102_1_);
                RaccoonEntity.this.playSound(SoundEvents.FOX_BITE, 1.0F, 1.0F);
            }

        }

        public void start() {
            RaccoonEntity.this.setIsInterested(false);
            super.start();
        }

        public boolean canUse() {
            return !RaccoonEntity.this.isSitting() && RaccoonEntity.this.isOrderedToSit() == false && !RaccoonEntity.this.isSleeping() && !RaccoonEntity.this.isCrouching() && !RaccoonEntity.this.isFaceplanted() && super.canUse();
        }
    }

    public class RaccoonRaidGardenGoal extends MoveToBlockGoal {
        private final RaccoonEntity raccoon;
        private boolean wantsToRaid;
        private boolean canRaid;

        public RaccoonRaidGardenGoal(RaccoonEntity raccoon) {
            super(raccoon, 1.2F, 32);
            this.raccoon = raccoon;
        }

        @Override
        public double acceptedDistance() {
            return 1.5D;
        }

        @Override
        public boolean canUse() {
            if (this.nextStartTick > 0) return false;

            if (!this.raccoon.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                return false;
            }

            this.canRaid = false;
            this.wantsToRaid = this.raccoon.getHunger() < 60;
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canRaid && this.raccoon.getHunger() < 200 && super.canContinueToUse();
        }

        @Override
        public void tick() {
            super.tick();
            this.raccoon.getLookControl().setLookAt(
                    this.blockPos.getX() + 0.5D,
                    this.blockPos.getY() + 1,
                    this.blockPos.getZ() + 0.5D,
                    10.0F,
                    this.raccoon.getMaxHeadXRot()
            );

            if (this.isReachedTarget()) {
                handleRaid();
            }
        }

        private void handleRaid() {
            World level = this.raccoon.level;
            BlockPos blockPos = this.blockPos.above();
            BlockState blockState = level.getBlockState(blockPos);
            Block block = blockState.getBlock();
            Item dropItem = CROP_DROP_MAP.get(block);

            if (dropItem != null && this.canRaid && blockState.getValue(CropsBlock.AGE) >= 2) {
                harvestCrops(level, blockPos, blockState, dropItem);
                this.canRaid = true;
                this.nextStartTick = 30;
            }
        }

        private void harvestCrops(World level, BlockPos blockPos, BlockState blockState, Item dropItem) {
            int age = blockState.getValue(CropsBlock.AGE);
            blockState = blockState.setValue(CropsBlock.AGE, 1);

            int dropCount = calculateDropCount(level, age);
            handleItemRetrieval(level, blockPos, dropItem, dropCount);

            this.raccoon.playSound(SoundEvents.CROP_BREAK, 1.0F, 1.0F);
            level.setBlock(blockPos, blockState.setValue(CropsBlock.AGE, age - 1), 2);
        }

        private int calculateDropCount(World level, int age) {
            return 1 + level.random.nextInt(2) + (age >= 3 ? 1 : 0);
        }

        private void handleItemRetrieval(World level, BlockPos blockPos, Item dropItem, int dropCount) {
            ItemStack mainHandItem = this.raccoon.getItemBySlot(EquipmentSlotType.MAINHAND);
            ItemStack offHandItem = this.raccoon.getItemBySlot(EquipmentSlotType.OFFHAND);
            ItemStack itemStack = new ItemStack(dropItem);
            itemStack.setCount(level.random.nextInt(6));

            if (mainHandItem.isEmpty() || !mainHandItem.isEdible()) {
                spitOutItem(mainHandItem);
                this.raccoon.setItemSlot(EquipmentSlotType.MAINHAND, itemStack);
                dropCount--;
            } else if (offHandItem.isEmpty() || !offHandItem.isEdible()) {
                spitOutItem(offHandItem);
                this.raccoon.setItemSlot(EquipmentSlotType.OFFHAND, itemStack);
                dropCount--;
            }

            if (dropCount > 0) {
                Block.popResource(level, blockPos, new ItemStack(dropItem, dropCount));
            }
        }

        @Override
        protected boolean isValidTarget(IWorldReader world, BlockPos pos) {
            BlockState blockState = world.getBlockState(pos);
            if (blockState.is(Blocks.FARMLAND) && this.wantsToRaid && !this.canRaid) {
                return checkCropState(world, pos.above());
            }
            return false;
        }

        private boolean checkCropState(IWorldReader world, BlockPos cropPos) {
            BlockState blockState = world.getBlockState(cropPos);
            if (blockState.getBlock() instanceof CropsBlock && !(blockState.getBlock() instanceof BeetrootBlock)) {
                int age = blockState.getValue(CropsBlock.AGE);
                if (age >= 3) {
                    this.canRaid = true;
                    return true;
                }
            }
            return false;
        }
    }


    abstract class AbstractFindItemsGoal extends Goal {
        protected final Predicate<ItemEntity> itemFilter;

        public AbstractFindItemsGoal(Predicate<ItemEntity> itemFilter) {
            this.itemFilter = itemFilter;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!RaccoonEntity.this.getItemBySlot(EquipmentSlotType.MAINHAND).isEmpty()) {
                return false;
            } else if (RaccoonEntity.this.getTarget() == null && RaccoonEntity.this.getLastHurtByMob() == null) {
                if (!RaccoonEntity.this.canMove()) {
                    return false;
                } else if (RaccoonEntity.this.getRandom().nextInt(10) != 0) {
                    return false;
                } else {
                    List<ItemEntity> list = findNearbyItems();
                    return !list.isEmpty() && itemFilter.test(list.get(0));
                }
            } else {
                return false;
            }
        }

        @Override
        public void tick() {
            List<ItemEntity> list = findNearbyItems();
            if (!list.isEmpty() && itemFilter.test(list.get(0))) {
                RaccoonEntity.this.getNavigation().moveTo(list.get(0), getMoveSpeed());
            }
        }

        @Override
        public void start() {
            List<ItemEntity> list = findNearbyItems();
            if (!list.isEmpty() && itemFilter.test(list.get(0))) {
                RaccoonEntity.this.getNavigation().moveTo(list.get(0), getMoveSpeed());
            }
        }

        protected List<ItemEntity> findNearbyItems() {
            return RaccoonEntity.this.level.getEntitiesOfClass(
                    ItemEntity.class,
                    RaccoonEntity.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D),
                    RaccoonEntity.ALLOWED_ITEMS
            );
        }

        protected abstract float getMoveSpeed();
    }

    class FindItemsGoal extends AbstractFindItemsGoal {
        public FindItemsGoal() {
            super(item -> true);
        }

        @Override
        protected float getMoveSpeed() {
            return 1.2F;
        }
    }

    class FindFoodGoal extends AbstractFindItemsGoal {
        public FindFoodGoal() {
            super(item -> item.getItem().isEdible());
        }

        @Override
        protected float getMoveSpeed() {
            return 1.22F;
        }

        @Override
        public boolean canUse() {
            if (RaccoonEntity.this.getItemBySlot(EquipmentSlotType.MAINHAND).isEdible()) {
                return false;
            }
            return super.canUse();
        }
    }

    public boolean emptyHand() {
        return this.getItemBySlot(EquipmentSlotType.MAINHAND).isEmpty();
    }


    class FollowGoal extends FollowParentGoal {
        private final RaccoonEntity fox;

        public FollowGoal(RaccoonEntity p_i50735_2_, double p_i50735_3_) {
            super(p_i50735_2_, p_i50735_3_);
            this.fox = p_i50735_2_;
        }

        public boolean canUse() {
            return !this.fox.isDefending() && super.canUse();
        }

        public boolean canContinueToUse() {
            return !this.fox.isDefending() && super.canContinueToUse();
        }

        public void start() {
            this.fox.clearStates();
            super.start();
        }
    }

    class FollowTargetGoal extends Goal {
        public FollowTargetGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            if (RaccoonEntity.this.isSleeping()) {
                return false;
            } else {
                LivingEntity livingentity = RaccoonEntity.this.getTarget();
                return livingentity != null && livingentity.isAlive() && RaccoonEntity.this.getStalkablePreyPredicate().test(livingentity)
                        && RaccoonEntity.this.distanceToSqr(livingentity) > 36.0D && !RaccoonEntity.this.isCrouching() && !RaccoonEntity.this.isInterested()
                        && !RaccoonEntity.this.jumping;
            }
        }

        public void start() {
            RaccoonEntity.this.setOrderedToSit(false);
            RaccoonEntity.this.setSitting(false);
            RaccoonEntity.this.setFaceplanted(false);
        }

        public void stop() {
            LivingEntity livingentity = RaccoonEntity.this.getTarget();
            if (livingentity != null && RaccoonEntity.isPathClear(RaccoonEntity.this, livingentity)) {
                RaccoonEntity.this.setIsInterested(true);
                RaccoonEntity.this.setIsCrouching(true);
                RaccoonEntity.this.getNavigation().stop();
                RaccoonEntity.this.getLookControl().setLookAt(livingentity, (float) RaccoonEntity.this.getMaxHeadYRot(), (float) RaccoonEntity.this.getMaxHeadXRot());
            } else {
                RaccoonEntity.this.setIsInterested(false);
                RaccoonEntity.this.setIsCrouching(false);
            }

        }

        public void tick() {
            LivingEntity livingentity = RaccoonEntity.this.getTarget();
            RaccoonEntity.this.getLookControl().setLookAt(livingentity, (float) RaccoonEntity.this.getMaxHeadYRot(), (float) RaccoonEntity.this.getMaxHeadXRot());
            if (RaccoonEntity.this.distanceToSqr(livingentity) <= 36.0D) {
                RaccoonEntity.this.setIsInterested(true);
                RaccoonEntity.this.setIsCrouching(true);
                RaccoonEntity.this.getNavigation().stop();
            } else {
                RaccoonEntity.this.getNavigation().moveTo(livingentity, 1.5D);
            }

        }
    }

    public static class RaccoonData extends AgeableData {
        public final Type type;

        public RaccoonData(Type p_i50734_1_) {
            super(false);
            this.type = p_i50734_1_;
        }
    }

    class JumpGoal extends Goal {
        int countdown;

        public JumpGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.JUMP, Flag.MOVE));
        }

        public boolean canUse() {
            return RaccoonEntity.this.isFaceplanted();
        }

        public boolean canContinueToUse() {
            return this.canUse() && this.countdown > 0;
        }

        public void start() {
            this.countdown = 40;
        }

        public void stop() {
            RaccoonEntity.this.setFaceplanted(false);
        }

        public void tick() {
            --this.countdown;
        }
    }

    public class LookHelperController extends LookController {
        public LookHelperController() {
            super(RaccoonEntity.this);
        }

        public void tick() {
            if (!RaccoonEntity.this.isSleeping()) {
                super.tick();
            }

        }

        protected boolean resetXRotOnTick() {
            return !RaccoonEntity.this.isPouncing() && !RaccoonEntity.this.isCrouching() && !RaccoonEntity.this.isInterested() & !RaccoonEntity.this.isFaceplanted();
        }
    }

    class MateGoal extends BreedGoal {
        public MateGoal(double speed) {
            super(RaccoonEntity.this, speed);
        }

        @Override
        public void start() {
            ((RaccoonEntity) this.animal).clearStates();
            ((RaccoonEntity) this.partner).clearStates();
            super.start();
        }

        @Override
        protected void breed() {
            ServerWorld serverWorld = (ServerWorld) this.level;
            int offspringCount = 2 + this.animal.getRandom().nextInt(4); // Random number between 2 and 5

            for (int i = 0; i < offspringCount; i++) {
                RaccoonEntity babyRaccoon = (RaccoonEntity) this.animal.getBreedOffspring(serverWorld, this.partner);
                if (babyRaccoon != null) {
                    handleOffspring(serverWorld, babyRaccoon);
                }
            }

            finalizeBreeding();
        }

        private void handleOffspring(ServerWorld serverWorld, RaccoonEntity babyRaccoon) {
            ServerPlayerEntity loveCauseAnimal = this.animal.getLoveCause();
            ServerPlayerEntity loveCausePartner = this.partner.getLoveCause();
            ServerPlayerEntity loveCause = loveCauseAnimal != null ? loveCauseAnimal : loveCausePartner;

            if (loveCauseAnimal != null && babyRaccoon.random.nextInt(4) == 0) {
                babyRaccoon.addTrustedUUID(loveCauseAnimal.getUUID());
            }
            if (loveCausePartner != null && loveCauseAnimal != loveCausePartner && babyRaccoon.random.nextInt(4) == 0) {
                babyRaccoon.addTrustedUUID(loveCausePartner.getUUID());
            }

            if (loveCause != null) {
                loveCause.awardStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(loveCause, this.animal, this.partner, babyRaccoon);
            }

            babyRaccoon.setAge(-24000);
            babyRaccoon.moveTo(this.animal.getX(), this.animal.getY(), this.animal.getZ(), 0.0F, 0.0F);
            serverWorld.addFreshEntityWithPassengers(babyRaccoon);
        }

        private void finalizeBreeding() {
            this.animal.setAge(6000);
            this.partner.setAge(6000);
            this.animal.resetLove();
            this.partner.resetLove();
            this.level.broadcastEntityEvent(this.animal, (byte) 18);

            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.level.addFreshEntity(new ExperienceOrbEntity(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), this.animal.getRandom().nextInt(7) + 1));
            }
        }
    }

    class MoveHelperController extends MovementController {
        public MoveHelperController() {
            super(RaccoonEntity.this);
        }

        public void tick() {
            if (RaccoonEntity.this.canMove()) {
                super.tick();
            }

        }
    }

    class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
        public PanicGoal(double p_i50729_2_) {
            super(RaccoonEntity.this, p_i50729_2_);
        }

        public boolean canUse() {
            if (RaccoonEntity.this.getHunger() <= 15 || RaccoonEntity.this.getThirst() <= 15 || RaccoonEntity.this.getTarget() != null)
                return false;


            return !RaccoonEntity.this.isDefending() && super.canUse();
        }
    }

    public class PounceGoal extends net.minecraft.entity.ai.goal.JumpGoal {
        public boolean canUse() {


            if (!RaccoonEntity.this.isFullyCrouched()) {
                return false;
            } else {
                LivingEntity livingentity = RaccoonEntity.this.getTarget();
                if (livingentity != null && livingentity.isAlive()) {
                    if (livingentity.getMotionDirection() != livingentity.getDirection()) {
                        return false;
                    } else {
                        boolean flag = RaccoonEntity.isPathClear(RaccoonEntity.this, livingentity);
                        if (!flag) {
                            RaccoonEntity.this.getNavigation().createPath(livingentity, 0);
                            RaccoonEntity.this.setIsCrouching(false);
                            RaccoonEntity.this.setIsInterested(false);
                        }

                        return flag;
                    }
                } else {
                    return false;
                }
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = RaccoonEntity.this.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                double d0 = RaccoonEntity.this.getDeltaMovement().y;
                return (!(d0 * d0 < (double) 0.05F) || !(Math.abs(RaccoonEntity.this.xRot) < 15.0F) || !RaccoonEntity.this.onGround) && !RaccoonEntity.this.isFaceplanted();
            } else {
                return false;
            }
        }

        public boolean isInterruptable() {
            return false;
        }

        public void start() {
            RaccoonEntity.this.setJumping(true);
            RaccoonEntity.this.setIsPouncing(true);
            RaccoonEntity.this.setIsInterested(false);
            LivingEntity livingentity = RaccoonEntity.this.getTarget();
            RaccoonEntity.this.getLookControl().setLookAt(livingentity, 60.0F, 30.0F);
            Vector3d vector3d = (new Vector3d(livingentity.getX() - RaccoonEntity.this.getX(), livingentity.getY() - RaccoonEntity.this.getY(), livingentity.getZ() - RaccoonEntity.this.getZ())).normalize();
            RaccoonEntity.this.setDeltaMovement(RaccoonEntity.this.getDeltaMovement().add(vector3d.x * 0.8D, 0.9D, vector3d.z * 0.8D));
            RaccoonEntity.this.getNavigation().stop();
        }

        public void stop() {
            RaccoonEntity.this.setIsCrouching(false);
            RaccoonEntity.this.crouchAmount = 0.0F;
            RaccoonEntity.this.crouchAmountO = 0.0F;
            RaccoonEntity.this.setIsInterested(false);
            RaccoonEntity.this.setIsPouncing(false);
        }

        public void tick() {
            LivingEntity livingentity = RaccoonEntity.this.getTarget();
            if (livingentity != null) {
                RaccoonEntity.this.getLookControl().setLookAt(livingentity, 60.0F, 30.0F);
            }

            if (!RaccoonEntity.this.isFaceplanted()) {
                Vector3d vector3d = RaccoonEntity.this.getDeltaMovement();
                if (vector3d.y * vector3d.y < (double) 0.03F && RaccoonEntity.this.xRot != 0.0F) {
                    RaccoonEntity.this.xRot = MathHelper.rotlerp(RaccoonEntity.this.xRot, 0.0F, 0.2F);
                } else {
                    double d0 = Math.sqrt(Entity.getHorizontalDistanceSqr(vector3d));
                    double d1 = Math.signum(-vector3d.y) * Math.acos(d0 / vector3d.length()) * (double) (180F / (float) Math.PI);
                    RaccoonEntity.this.xRot = (float) d1;
                }
            }

            if (livingentity != null && RaccoonEntity.this.distanceTo(livingentity) <= 2.0F) {
                RaccoonEntity.this.doHurtTarget(livingentity);
            } else if (RaccoonEntity.this.xRot > 0.0F && RaccoonEntity.this.onGround && (float) RaccoonEntity.this.getDeltaMovement().y != 0.0F && RaccoonEntity.this.level.getBlockState(RaccoonEntity.this.blockPosition()).is(Blocks.SNOW)) {
                RaccoonEntity.this.xRot = 60.0F;
                if (RaccoonEntity.this.getTarget() != null) {
                    try {
                        RaccoonEntity.this.setTarget(null);

                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Error while setting target to null", e);
                    }
                }
                RaccoonEntity.this.setFaceplanted(true);
                if (!(RaccoonEntity.this.getRaccoonType() == Type.SNOW)) {
                    RaccoonEntity.this.setRaccoonType(Type.SNOW);
                }
            }

        }
    }


    public class RaccoonPracticeGoal extends Goal {
        private final RaccoonEntity raccoon;
        private final double speed;
        private ArmorStandEntity target;
        private long lastJumpTime; // Tracks the last time the raccoon jumped
        private static final long COOLDOWN_PERIOD = 3000; // 3 seconds cooldown in milliseconds

        public RaccoonPracticeGoal(RaccoonEntity raccoon, double speed) {
            this.raccoon = raccoon;
            this.speed = speed;
            this.lastJumpTime = 0;
        }

        @Override
        public boolean canUse() {
            List<ArmorStandEntity> list = this.raccoon.level.getEntitiesOfClass(ArmorStandEntity.class, this.raccoon.getBoundingBox().inflate(10.0D, 8.0D, 10.0D));
            if (!list.isEmpty()) {
                for (ArmorStandEntity armorstand : list) {
                    if (raccoon.canSee(armorstand)) {
                        this.target = armorstand;
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && target.isAlive() && raccoon.distanceTo(target) < 10.0D;
        }

        @Override
        public void start() {
            raccoon.setTarget(target);
            raccoon.setIsInterested(true);
            raccoon.setIsCrouching(true);
        }

        @Override
        public void tick() {
            raccoon.getLookControl().setLookAt(target, 60.0F, 30.0F);
            double distance = raccoon.distanceTo(this.target);
            if (distance <= 1.2 && raccoon.onGround && !raccoon.level.isClientSide) {
                target.brokenByAnything(DamageSource.mobAttack(raccoon));
            }
            long currentTime = System.currentTimeMillis();
            if (distance <= 6.0D && currentTime - lastJumpTime >= COOLDOWN_PERIOD) {
                // Ensure cooldown has passed before jumping again
                if (raccoon.isFullyCrouched() && raccoon.onGround) {
                    raccoon.setJumping(true);
                    raccoon.setIsPouncing(true);

                    Vector3d pounceDirection = (new Vector3d(target.getX() - raccoon.getX(), target.getY() - raccoon.getY(), target.getZ() - raccoon.getZ())).normalize();
                    Vector3d newVelocity = raccoon.getDeltaMovement().add(pounceDirection.x * 0.8D, Math.min(0.5D, 0.9D), pounceDirection.z * 0.8D); // Limit vertical velocity
                    raccoon.setDeltaMovement(newVelocity);

                    raccoon.getNavigation().stop();

                    lastJumpTime = currentTime; // Update the last jump time
                }
            } else {
                // Move towards the target if not close enough
                raccoon.getNavigation().moveTo(this.target, this.speed);
            }

            // Adjust the raccoon's rotation during pounce
            Vector3d movement = raccoon.getDeltaMovement();
            if (!raccoon.isFaceplanted()) {
                if (movement.y * movement.y < 0.03F && raccoon.xRot != 0.0F) {
                    raccoon.xRot = MathHelper.rotlerp(raccoon.xRot, 0.0F, 0.2F);
                } else {
                    double horizontalDistance = Math.sqrt(Entity.getHorizontalDistanceSqr(movement));
                    double angle = Math.signum(-movement.y) * Math.acos(horizontalDistance / movement.length()) * (180F / (float) Math.PI);
                    raccoon.xRot = (float) angle;
                }
            }
        }

        @Override
        public void stop() {
            raccoon.setIsInterested(false);
            raccoon.setIsCrouching(false);
            raccoon.setIsPouncing(false);

            try {
                raccoon.setTarget(null);
            } catch (NullPointerException e) {
                System.out.println("No target");
            }
        }
    }



    class RevengeGoal extends NearestAttackableTargetGoal<LivingEntity> {
        @Nullable
        private LivingEntity trustedLastHurtBy;
        private LivingEntity trustedLastHurt;
        private int timestamp;

        public RevengeGoal(Class<LivingEntity> p_i50743_2_, boolean p_i50743_3_, boolean p_i50743_4_, @Nullable Predicate<LivingEntity> p_i50743_5_) {
            super(RaccoonEntity.this, p_i50743_2_, 10, p_i50743_3_, p_i50743_4_, p_i50743_5_);
        }

        public boolean canUse() {
            if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
                return false;
            } else {
                for (UUID uuid : RaccoonEntity.this.getTrustedUUIDs()) {
                    if (uuid != null && RaccoonEntity.this.level instanceof ServerWorld) {
                        Entity entity = ((ServerWorld) RaccoonEntity.this.level).getEntity(uuid);
                        if (entity instanceof LivingEntity) {
                            LivingEntity livingentity = (LivingEntity) entity;
                            this.trustedLastHurt = livingentity;
                            this.trustedLastHurtBy = livingentity.getLastHurtByMob();
                            int i = livingentity.getLastHurtByMobTimestamp();
                            return i != this.timestamp && this.canAttack(this.trustedLastHurtBy, this.targetConditions);
                        }
                    }
                }

                return false;
            }
        }

        public void start() {
            this.setTarget(this.trustedLastHurtBy);
            this.target = this.trustedLastHurtBy;
            if (this.trustedLastHurt != null) {
                this.timestamp = this.trustedLastHurt.getLastHurtByMobTimestamp();
            }

            RaccoonEntity.this.playSound(SoundEvents.FOX_AGGRO, 1.0F, 1.0F);
            RaccoonEntity.this.setDefending(true);
            RaccoonEntity.this.wakeUp();
            super.start();
        }
    }

    class SitAndLookGoal extends BaseGoal {
        private double relX;
        private double relZ;
        private int lookTime;
        private int looksRemaining;

        public SitAndLookGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            if (RaccoonEntity.this.isTame())
                return false;

            return RaccoonEntity.this.getLastHurtByMob() == null && RaccoonEntity.this.getRandom().nextFloat() < 0.02F && !RaccoonEntity.this.isSleeping() && RaccoonEntity.this.getTarget() == null && RaccoonEntity.this.getNavigation().isDone() && !this.alertable() && !RaccoonEntity.this.isPouncing() && !RaccoonEntity.this.isCrouching();
        }

        public boolean canContinueToUse() {
            if (RaccoonEntity.this.isTame())
                return false;

            return this.looksRemaining > 0;
        }

        public void start() {
            this.resetLook();
            this.looksRemaining = 2 + RaccoonEntity.this.getRandom().nextInt(3);
            RaccoonEntity.this.setSitting(true);
            RaccoonEntity.this.setOrderedToSit(true);
            RaccoonEntity.this.getNavigation().stop();
        }

        public void stop() {
            RaccoonEntity.this.setOrderedToSit(false);
            RaccoonEntity.this.setSitting(false);
        }

        public void tick() {
            --this.lookTime;
            if (this.lookTime <= 0) {
                --this.looksRemaining;
                this.resetLook();
            }

            RaccoonEntity.this.getLookControl().setLookAt(RaccoonEntity.this.getX() + this.relX, RaccoonEntity.this.getEyeY(), RaccoonEntity.this.getZ() + this.relZ, (float) RaccoonEntity.this.getMaxHeadYRot(), (float) RaccoonEntity.this.getMaxHeadXRot());
        }

        private void resetLook() {
            double d0 = (Math.PI * 2D) * RaccoonEntity.this.getRandom().nextDouble();
            this.relX = Math.cos(d0);
            this.relZ = Math.sin(d0);
            this.lookTime = 80 + RaccoonEntity.this.getRandom().nextInt(20);
        }
    }

    class SleepGoal extends BaseGoal {
        private int countdown = RaccoonEntity.this.random.nextInt(140);

        public SleepGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        public boolean canUse() {
            if (RaccoonEntity.this.getThirst() <= 40 || RaccoonEntity.this.getHunger() <= 40)
                return false;

            if (RaccoonEntity.this.xxa == 0.0F && RaccoonEntity.this.yya == 0.0F && RaccoonEntity.this.zza == 0.0F) {
                return this.canSleep() || RaccoonEntity.this.isSleeping();
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            if (RaccoonEntity.this.getThirst() <= 40 || RaccoonEntity.this.getHunger() <= 40)
                return false;


            return this.canSleep();
        }

        private boolean canSleep() {
            if (this.countdown > 0) {
                --this.countdown;
                return false;
            } else {
                return RaccoonEntity.this.level.isDay() && this.hasShelter() && !this.alertable();
            }
        }

        public void stop() {
            this.countdown = RaccoonEntity.this.random.nextInt(140);
            RaccoonEntity.this.clearStates();
        }

        public void start() {
            RaccoonEntity.this.setOrderedToSit(false);
            RaccoonEntity.this.setSitting(false);
            RaccoonEntity.this.setIsCrouching(false);
            RaccoonEntity.this.setIsInterested(false);
            RaccoonEntity.this.setJumping(false);
            RaccoonEntity.this.setSleeping(true);
            RaccoonEntity.this.getNavigation().stop();
            RaccoonEntity.this.getMoveControl().setWantedPosition(RaccoonEntity.this.getX(), RaccoonEntity.this.getY(), RaccoonEntity.this.getZ(), 0.0D);
        }
    }

    class StrollGoal extends MoveThroughVillageAtNightGoal {
        public StrollGoal(int p_i50726_2_, int p_i50726_3_) {
            super(RaccoonEntity.this, p_i50726_3_);
        }

        public void start() {
            RaccoonEntity.this.clearStates();
            super.start();
        }

        public boolean canUse() {
            return super.canUse() && this.canRaccoonMove();
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.canRaccoonMove();
        }

        private boolean canRaccoonMove() {
            return !RaccoonEntity.this.isSleeping() && !RaccoonEntity.this.isSitting() && !RaccoonEntity.this.isOrderedToSit() && !RaccoonEntity.this.isDefending() && RaccoonEntity.this.getTarget() == null;
        }
    }

    class SwimGoal extends net.minecraft.entity.ai.goal.SwimGoal {
        public SwimGoal() {
            super(RaccoonEntity.this);
        }

        public void start() {
            super.start();
            RaccoonEntity.this.clearStates();
        }

        public boolean canUse() {
            return RaccoonEntity.this.isInWater() && RaccoonEntity.this.getFluidHeight(FluidTags.WATER) > 0.25D || RaccoonEntity.this.isInLava();
        }
    }

    public static enum Type {
        RED(0, "red"),
        SNOW(1, "snow"),
        RABID(2, "rabid"),
        DIRTY(3, "dirty");


        private static final Type[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(Type::getId)).toArray((p_221084_0_) -> {
            return new Type[p_221084_0_];
        });
        private static final Map<String, Type> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(Type::getName, (p_221081_0_) -> {
            return p_221081_0_;
        }));
        private final int id;
        private final String name;
        private final List<RegistryKey<Biome>> biomes;

        private Type(int p_i241911_3_, String p_i241911_4_, RegistryKey<Biome>... p_i241911_5_) {
            this.id = p_i241911_3_;
            this.name = p_i241911_4_;
            this.biomes = Arrays.asList(p_i241911_5_);
        }

        public String getName() {
            return this.name;
        }

        public int getId() {
            return this.id;
        }

        public static Type byName(String p_221087_0_) {
            return BY_NAME.getOrDefault(p_221087_0_, RED);
        }

        public static Type byId(int p_221080_0_) {
            if (p_221080_0_ < 0 || p_221080_0_ > BY_ID.length) {
                p_221080_0_ = 0;
            }

            return BY_ID[p_221080_0_];
        }

        public static Type byBiome(Optional<RegistryKey<Biome>> p_242325_0_) {
            return p_242325_0_.isPresent() && SNOW.biomes.contains(p_242325_0_.get()) ? SNOW : RED;
        }
    }

    class WatchGoal extends LookAtGoal {
        public WatchGoal(Mob p_i50733_2_, Class<? extends LivingEntity> p_i50733_3_, float p_i50733_4_) {
            super(p_i50733_2_, p_i50733_3_, p_i50733_4_);
        }

        public boolean canUse() {
            return super.canUse() && !RaccoonEntity.this.isFaceplanted() && !RaccoonEntity.this.isInterested();
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && !RaccoonEntity.this.isFaceplanted() && !RaccoonEntity.this.isInterested();
        }
    }
}