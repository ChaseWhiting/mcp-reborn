package net.minecraft.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

public class QueenBeeEntity extends BeeEntity implements IAngerable, IFlyingAnimal, IBee {

    public QueenBeeEntity(EntityType<? extends QueenBeeEntity> queenBee, World world) {
        super(queenBee, world);
        this.moveControl = new FlyingMovementController(this, 20, true);
        this.lookControl = new BeeEntity.BeeLookController(this);
        this.setPathfindingMalus(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathNodeType.WATER, -1.0F);
        this.setPathfindingMalus(PathNodeType.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(PathNodeType.COCOA, -1.0F);
        this.setPathfindingMalus(PathNodeType.FENCE, -1.0F);
    }

//    private Random random = new Random();
//
//    private BeeEntity.FindFlowerGoal goToKnownFlowerGoal;
//   // private PollinateGoal beePollinateGoal = new PollinateGoal();
//    private BeeEntity.FindBeehiveGoal goToHiveGoal;
//    private int remainingCooldownBeforeLocatingNewHive = 0;
//    private int remainingCooldownBeforeLocatingNewFlower = 0;
//    private BlockPos savedFlowerPos = null;

//    protected void registerGoals() {
//        this.goalSelector.addGoal(0, new BeeEntity.StingGoal(this, (double)1.4F, true));
//        this.goalSelector.addGoal(1, new BeeEntity.EnterBeehiveGoal());
//       // this.goalSelector.addGoal(1, beePollinateGoal);
//     //   this.goalSelector.addGoal(4, this.beePollinateGoal);
//        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
//        this.goalSelector.addGoal(5, new BeeEntity.UpdateBeehiveGoal());
//        this.goToHiveGoal = new BeeEntity.FindBeehiveGoal();
//        this.goalSelector.addGoal(5, this.goToHiveGoal);
//        this.goToKnownFlowerGoal = new BeeEntity.FindFlowerGoal();
//        this.goalSelector.addGoal(6, this.goToKnownFlowerGoal);
//        this.goalSelector.addGoal(7, new BeeEntity.FindPollinationTargetGoal());
//        this.goalSelector.addGoal(8, new BeeEntity.WanderGoal());
//        this.goalSelector.addGoal(9, new SwimGoal(this));
//        this.targetSelector.addGoal(1, (new AngerGoal(this)).setAlertOthers(new Class[0]));
//        this.targetSelector.addGoal(2, new BeeEntity.AttackPlayerGoal(this));
//        this.targetSelector.addGoal(3, new ResetAngerGoal<>(this, true));
//    }

    public boolean isBee(LivingEntity entity) {
        return entity instanceof IBee;
    }

//    class AngerGoal extends HurtByTargetGoal {
//        AngerGoal(QueenBeeEntity p_i225726_2_) {
//            super(p_i225726_2_);
//        }
//
//        public boolean canContinueToUse() {
//            return QueenBeeEntity.this.isAngry() && super.canContinueToUse();
//        }
//
//        protected void alertOther(MobEntity bee, LivingEntity target) {
//            if (isBee(bee) && this.mob.canSee(target)) {
//                bee.setTarget(target);
//            }
//
//        }
//    }


}
