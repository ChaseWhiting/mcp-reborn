package net.minecraft.entity.ai.goal;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Mob;
import net.minecraft.item.*;
import net.minecraft.item.tool.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.GameEvent;

import java.util.function.Predicate;

public class AdvancedBreakBlockGoal extends CustomBlockInteractGoal {
   private final Predicate<Difficulty> validDifficulties;
   protected int breakTime;
   protected int lastBreakProgress = -1;
   protected Mob mob;
   protected int tryTicks = 0;
   public static final Predicate<BlockState> FENCE = block -> block.getBlock() instanceof FenceBlock;

   public AdvancedBreakBlockGoal(Mob mob, Predicate<BlockState> block, Predicate<Difficulty> validDifficulties) {
      super(mob, block.and(state -> !state.isAir() && !(state.getBlock() instanceof AirBlock && !(state.getBlock() instanceof TallGrassBlock) && state.getFluidState().isEmpty() && state.getMaterial() != Material.AIR && state.getDestroySpeed() > 1 && state.getBlock() != Blocks.DEAD_BUSH && state.getBlock() != Blocks.GRASS && state.getBlock() != Blocks.TALL_GRASS)));
      this.mob = mob;
      this.validDifficulties = validDifficulties;
   }

   public AdvancedBreakBlockGoal(Mob mob, Predicate<Difficulty> validDifficulties) {
      super(mob, FENCE.and(state -> !state.isAir() && !(state.getBlock() instanceof AirBlock && !(state.getBlock() instanceof TallGrassBlock))));
      this.mob = mob;
      this.validDifficulties = validDifficulties;
   }

   public Predicate<BlockState> getBlockState() {
      return statePredicate;
   }

   public boolean breaksWood() {
      return statePredicate.test(Blocks.OAK_PLANKS.defaultBlockState());
   }

   public boolean breaksStone() {
      return statePredicate.test(Blocks.STONE.defaultBlockState());
   }

   public boolean breaks(Block block) {
      return statePredicate.test(block.defaultBlockState());
   }

   public boolean breaks(BlockState block) {
      return statePredicate.test(block);
   }




   protected int getBlockBreakTime() {
      BlockState blockState = this.mob.level.getBlockState(this.blockPos);
      float destroySpeed = blockState.getDestroySpeed(this.mob.level, this.blockPos);
      ItemStack handItem = this.mob.getMainHandItem();

      // Define tool compatibility based on block material
      boolean flag = (blockState.getMaterial() == Material.STONE || blockState.getMaterial() == Material.METAL || blockState.getMaterial() == Material.HEAVY_METAL || blockState.getMaterial() == Material.PISTON) && handItem.getItem() instanceof PickaxeItem ||
              (blockState.getMaterial() == Material.DIRT || blockState.getMaterial() == Material.GRASS || blockState.getMaterial() == Material.SAND || blockState.getMaterial() == Material.CLAY || blockState.getMaterial() == Material.SNOW || blockState.getMaterial() == Material.TOP_SNOW) && handItem.getItem() instanceof ShovelItem ||
              (blockState.getMaterial() == Material.WOOD || blockState.getMaterial() == Material.NETHER_WOOD || blockState.getMaterial() == Material.BAMBOO || blockState.getMaterial() == Material.BAMBOO_SAPLING) && handItem.getItem() instanceof AxeItem ||
              (blockState.getMaterial() == Material.CLOTH_DECORATION || blockState.getMaterial() == Material.WOOL || blockState.getMaterial() == Material.LEAVES || blockState.getMaterial() == Material.WEB) && handItem.getItem() instanceof ShearsItem ||
              (blockState.getMaterial() == Material.PLANT || blockState.getMaterial() == Material.REPLACEABLE_PLANT || blockState.getMaterial() == Material.REPLACEABLE_FIREPROOF_PLANT || blockState.getMaterial() == Material.REPLACEABLE_WATER_PLANT || blockState.getMaterial() == Material.VEGETABLE || blockState.getMaterial() == Material.CACTUS || blockState.getMaterial() == Material.CORAL || blockState.getMaterial() == Material.EGG) && handItem.getItem() instanceof HoeItem;

      // Calculate the raw block break time
      float calculatedTime = destroySpeed * 90;

      // Apply tool modifier after calculatedTime
      if (handItem.isCorrectToolForDrops(blockState) || flag) {
         float modifier = 0;
         if (handItem.getItem() instanceof ToolItem) {
            ToolItem toolItem = (ToolItem) handItem.getItem();
            modifier = switch (toolItem.tier()) {
               case WOOD -> 0.15F;
               case STONE -> 0.4F;
               case IRON -> 0.8F;
               case GOLD -> 1.67F;
               case DIAMOND -> 1.2F;
               case NETHERITE -> 1.4F;
               default -> 0.7f;
            };
         }
         calculatedTime /= (2 + modifier); // Apply the modifier after the raw time calculation
      }

      // Get the level of Efficiency enchantment and reduce block break time
      int efficiencyLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, handItem);
      if (efficiencyLevel > 0 && flag) {
         // Reduce block break time by 10% per Efficiency level
         float efficiencyMultiplier = 1 - (efficiencyLevel * 0.1F);
         calculatedTime *= efficiencyMultiplier; // Apply the efficiency modifier after time is calculated
      }

      return (int) calculatedTime;
   }

   public boolean canUse() {
      if (!super.canUse()) {
         return false;
      } else if (!this.mob.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         return false;
      } else {
         if (this.mob.getTarget() != null) {
            return (this.mob.getTarget().distanceTo(this.mob) > 13 || !this.mob.getSensing().canSee(this.mob.getTarget())) && this.isValidDifficulty(this.mob.level.getDifficulty());
         }


         return this.isValidDifficulty(this.mob.level.getDifficulty());
      }
   }

   public void start() {
      super.start();


      this.breakTime = 0;
   }

   public boolean canContinueToUse() {
      boolean flag = this.breakTime <= this.getBlockBreakTime()
              && this.blockPos.closerThan(this.mob.position(), 4.3D)
              && this.isValidDifficulty(this.mob.level.getDifficulty());

      if (this.mob.getTarget() != null) {
         flag = flag && this.mob.getTarget().distanceTo(this.mob) > 13 || !this.mob.getSensing().canSee(this.mob.getTarget());
      }

      return flag;
   }

   public void stop() {
      super.stop();
      this.mob.level.destroyBlockProgress(this.mob.getId(), this.blockPos, -1);
   }

   public void tick() {
      super.tick();
      if (++tryTicks > 300 && this.getBlockBreakTime() <= 300) {
         this.tryTicks = 0;
         this.stop();
      }

      if (this.mob.getRandom().nextInt(4) == 0) { // Increased interaction frequency
         this.mob.level.gameEvent(GameEvent.BLOCK_BREAKING, this.blockPos, 0);
         if (!this.mob.swinging) {
            this.mob.swing(this.mob.getUsedItemHand());
         }
      }

      ++this.breakTime;
      int i = (int)((float)this.breakTime / (float)this.getBlockBreakTime() * 10.0F);
      if (i != this.lastBreakProgress) {
         this.mob.level.destroyBlockProgress(this.mob.getId(), this.blockPos, i);
         this.lastBreakProgress = i;
      }

      this.mob.getNavigation().moveTo(blockPos.asVector(), 1.14D);
      this.mob.getLookControl().setLookAt(blockPos.asVector());


      if (this.breakTime == this.getBlockBreakTime() && this.isValidDifficulty(this.mob.level.getDifficulty())) {
         this.mob.level.destroyBlock(this.blockPos, true);
         //this.mob.level.levelEvent(1021, this.blockPos, 0);
         this.mob.level.gameEvent(GameEvent.BLOCK_DESTROY, this.blockPos, Block.getId(this.mob.level.getBlockState(this.blockPos)));
         this.stop();
         this.mob.swinging = false;
      }
   }

   private boolean isValidDifficulty(Difficulty difficulty) {
      return this.validDifficulties.test(difficulty);
   }
}