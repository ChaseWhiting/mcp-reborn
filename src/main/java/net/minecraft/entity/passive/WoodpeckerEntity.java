package net.minecraft.entity.passive;


import net.minecraft.block.BlockState;
import net.minecraft.client.animation.AnimationState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class WoodpeckerEntity extends Animal implements IFlyingAnimal {
    private static final DataParameter<Boolean> DATA_IS_FLYING = EntityDataManager.defineId(WoodpeckerEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_IS_PECKING = EntityDataManager.defineId(WoodpeckerEntity.class, DataSerializers.BOOLEAN);

    private static final DataParameter<BlockPos> PECK_BLOCKPOS = EntityDataManager.defineId(WoodpeckerEntity.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<Direction> PECKING_DIRECTION = EntityDataManager.defineId(WoodpeckerEntity.class, DataSerializers.DIRECTION);

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_FLYING, false);
        this.entityData.define(PECKING_DIRECTION, Direction.DOWN);
        this.entityData.define(DATA_IS_PECKING, false);

        this.entityData.define(PECK_BLOCKPOS, BlockPos.ZERO);
    }

    public void setPeckPos(BlockPos peckPos) {
        this.entityData.set(PECK_BLOCKPOS, peckPos);
    }

    public BlockPos getPeckPos() {
        return this.entityData.get(PECK_BLOCKPOS);
    }

    public boolean verifyPeckPosition() {
        if (!level.isLoaded(this.getPeckPos())) return false;

        List<WoodpeckerEntity> nearbyWoodpeckers = level.getEntitiesOfClass(WoodpeckerEntity.class, new AxisAlignedBB(this.getPeckPos()).inflate(3.5D), woodpecker -> {
            return woodpecker != this && woodpecker.getPeckPos() == this.getPeckPos();
        });

        return this.level.getBlockState(this.getPeckPos()).is(BlockTags.LOGS_THAT_BURN) && nearbyWoodpeckers.isEmpty() && level.getBlockState(this.getPeckPos().above()).isAir();
    }

    public WoodpeckerEntity(EntityType<? extends Animal> woodPecker, World world) {
        super(woodPecker, world);
        this.moveControl = new FlyingMovementController(this, 40, false);
    }


    private int peckingCooldown;
    private int peckingTime;

    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("IsFlying", this.isFlying());
        nbt.putBoolean("IsPecking", this.isPecking());
        nbt.putInt("PeckingCooldown", peckingCooldown);
        nbt.putInt("PeckingTime", peckingTime);

        nbt.putString("PeckDirection", Direction.UP.getName());

        if (this.getPeckPos() != BlockPos.ZERO) {
            nbt.put("PeckPos", NBTUtil.writeBlockPos(this.getPeckPos()));
        }
    }



    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        this.entityData.set(DATA_IS_FLYING, nbt.getBoolean("IsFlying"));
        this.setPecking(nbt.getBoolean("IsPecking"));
        if (nbt.contains("PeckDirection")) {
            this.entityData.set(PECKING_DIRECTION, Objects.requireNonNull(Direction.byName(nbt.getString("PeckDirection")), "This should exist!"));
        }

        this.peckingCooldown = nbt.getInt("PeckingCooldown");
        this.peckingTime = nbt.getInt("PeckingTime");

        if (nbt.contains("PeckPos")) {
            this.setPeckPos(NBTUtil.readBlockPos(nbt.getCompound("PeckPos")));
        }
    }

    public boolean isFlying() {
        boolean flag = !this.onGround;
        if (this.getBlockPosBelowThatAffectsMyMovement().equals(this.getPeckPos())) return false;

        this.entityData.set(DATA_IS_FLYING, flag);
        return entityData.get(DATA_IS_FLYING);
    }

    public boolean isPecking() {
        return entityData.get(DATA_IS_PECKING);
    }

    public void setPecking(boolean pecking) {
        entityData.set(DATA_IS_PECKING, pecking);
    }

    public AnimationState flyingState = new AnimationState();
    public AnimationState peckingState = new AnimationState();


    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 12.0D).add(Attributes.FLYING_SPEED, (double)0.7F).add(Attributes.MOVEMENT_SPEED, (double)0.28F);
    }

    protected PathNavigator createNavigation(World p_175447_1_) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, p_175447_1_) {
            public boolean isStableDestination(BlockPos blockPos) {
                return !this.level.getBlockState(blockPos.below()).isAir();
            }


            public void tick() {
                if (!WoodpeckerEntity.this.isPecking()) {
                    super.tick();
                }
            }


        };
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(false);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    protected PathNavigator createGroundNavigation() {
        return new GroundPathNavigator(this, level);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Monster.class, 5f, 0.8, 0.8));
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(0, new PeckGoal(this));
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new FollowParentGoal(this, 1D));
        this.goalSelector.addGoal(2, new LookAtGoal(this, PlayerEntity.class, 8.0F) {
            @Override
            public boolean canUse() {
                return super.canUse() && !WoodpeckerEntity.this.isPecking();
            }
        });
        this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 0.8D, 0.6f) {
            @Override
            public boolean canUse() {
                return super.canUse() && !WoodpeckerEntity.this.getBlockPosBelowThatAffectsMyMovement().equals(WoodpeckerEntity.this.getPeckPos());
            }

            public void start() {
                WoodpeckerEntity.this.navigation = WoodpeckerEntity.this.createGroundNavigation();
                WoodpeckerEntity.this.moveControl = new MovementController(WoodpeckerEntity.this);
                super.start();
            }

            public void stop() {
                super.stop();
                WoodpeckerEntity.this.navigation = WoodpeckerEntity.this.createNavigation(WoodpeckerEntity.this.level);
                WoodpeckerEntity.this.moveControl = new FlyingMovementController(WoodpeckerEntity.this, 40, false);
            }
        });
        this.goalSelector.addGoal(3, new WaterAvoidingRandomFlyingGoal(this, 1.0D, 0.85f) {
            @Override
            public boolean canUse() {
                return super.canUse() && !WoodpeckerEntity.this.getBlockPosBelowThatAffectsMyMovement().equals(WoodpeckerEntity.this.getPeckPos()) && WoodpeckerEntity.this.random.nextFloat() < 0.3;
            }

            @Override
            public void start() {
                WoodpeckerEntity.this.navigation = WoodpeckerEntity.this.createNavigation(WoodpeckerEntity.this.level);
                WoodpeckerEntity.this.moveControl = new FlyingMovementController(WoodpeckerEntity.this, 40, false);
                super.start();
            }


        });
        this.goalSelector.addGoal(3, new FollowMobGoal(this, 1.0D, 3.0F, 7.0F));
    }

    public boolean isFood(ItemStack food) {
        return food.getItem() == Items.SWEET_BERRIES;
    }

    //TODO Not finished yet, I need to add support for pecking on north, south, east, and west.
    private static class PeckGoal extends Goal {
        private final WoodpeckerEntity mob;

        private PeckGoal(WoodpeckerEntity woodpeckerEntity) {
            this.mob = woodpeckerEntity;
        }

        @Override
        public boolean canUse() {
            if (mob.peckingCooldown-- > 0) return false;
            if (mob.verifyPeckPosition() && mob.getPeckPos() != BlockPos.ZERO) return true;

            for (BlockPos pos : BlockPos.betweenClosed(
                    mob.blockPosition().offset(-10, -10, -10),
                    mob.blockPosition().offset(10, 10, 10)))
            {
                if (mob.random.nextFloat() > 0.35 && new Random(mob.level.getDayTime()).nextGaussian() * 3.5 < mob.random.nextGaussian() * 2.5) continue;
                mob.setPeckPos(pos);
                if (mob.verifyPeckPosition()) {
                    break;
                }
                mob.setPeckPos(BlockPos.ZERO);
            }

            return mob.getPeckPos() != BlockPos.ZERO;
        }

        public void start() {
            mob.peckingTime = TickRangeConverter.rangeOfSeconds(16, 32).randomValue(mob.random);
            mob.getMoveControl().setWantedPosition(mob.getPeckPos().getX() + 0.5, mob.getPeckPos().getY() + 0.5, mob.getPeckPos().getZ() + 0.5, 0.8D);
        }

        public boolean isInterruptable() {
            return false;
        }

        @Override
        public void tick() {
            if (mob.verifyPeckPosition()) {
                if (mob.getOnPos().equals(mob.getPeckPos())) {
                    this.mob.setPecking(true);
                    this.mob.lookControl.setLookAt(mob.getPeckPos());


                } else {
                    mob.getMoveControl().setWantedPosition(
                            mob.getPeckPos().getX() + 0.5,
                            mob.getPeckPos().above().getY(),
                            mob.getPeckPos().getZ() + 0.5,
                            0.8D
                    );
                }
            } else {
                stop();
            }
        }



        public void stop() {
            mob.peckingCooldown = TickRangeConverter.rangeOfSeconds(45, 80).randomValue(mob.random);
            mob.peckingTime = 0;
            mob.setPeckPos(BlockPos.ZERO);
            mob.setPecking(false);
        }

        @Override
        public boolean canContinueToUse() {
            return mob.verifyPeckPosition() && mob.peckingTime > 0;
        }

    }

    public boolean isImmobile() {
        return this.isPecking() && (this.getBlockPosBelowThatAffectsMyMovement().equals(this.getPeckPos())) ;
    }

    public void tick() {
        super.tick();
        if (this.isPecking()) {
            if (tick(5) && level.isServerSide && this.getOnPos().equals(this.getPeckPos())) {
                level.playSound(null, this, SoundEvents.WOOD_BREAK, SoundCategory.BLOCKS, 0.1f, 1.2f);
            }
            if (level.isClientSide) {
                peckingState.animateWhen(true, this.tickCount);
            }
            if (!(this.getOnPos().equals(this.getPeckPos()))) {
                this.setPecking(false);
            } else {
                this.peckingTime--;
                this.setPecking(true);
            }
            if (peckingTime < 0) {
                peckingTime = 0;
                this.setPecking(false);
                this.setPeckPos(BlockPos.ZERO);
            }
        }



        if (!this.level.isClientSide) return;

        flyingState.animateWhen(this.isFlying(), this.tickCount);

    }

    @Override
    public @Nullable AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return EntityType.WOODPECKER.create(p_241840_1_);
    }

    public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
        return false;
    }

    protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
    }

    public boolean doHurtTarget(Entity target) {
        return target.hurt(DamageSource.mobAttack(this), 3.0F);
    }
}
