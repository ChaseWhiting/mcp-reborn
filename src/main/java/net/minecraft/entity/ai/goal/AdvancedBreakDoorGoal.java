package net.minecraft.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.entity.Mob;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TieredItem;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.GameEvent;

import java.util.function.Predicate;

public class AdvancedBreakDoorGoal extends CustomInteractDoorGoal {
   private final Predicate<Difficulty> validDifficulties;
   protected int breakTime;
   protected int lastBreakProgress = -1;
   protected int doorBreakTime = -1;
   protected Mob mob;

   public AdvancedBreakDoorGoal(Mob p_i50332_1_, Predicate<Difficulty> p_i50332_2_) {
      super(p_i50332_1_);
      this.mob = p_i50332_1_;
      this.validDifficulties = p_i50332_2_;
   }

   public AdvancedBreakDoorGoal(Mob p_i50333_1_, int p_i50333_2_, Predicate<Difficulty> p_i50333_3_) {
      this(p_i50333_1_, p_i50333_3_);
      this.mob = p_i50333_1_;
      this.doorBreakTime = p_i50333_2_;
   }

   protected int getDoorBreakTime() {
      // Reducing the break time by 30% or depending on the difficulty.
      if (mob.getMainHandItem().getItem() instanceof TieredItem || mob.getMainHandItem().getItem().getWeight(new ItemStack(Items.BUNDLE)) > 10) {
         return 35;
      }
      return 85;

   }

   public boolean canUse() {
      if (!super.canUse()) {
         return false;
      } else if (!this.mob.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         return false;
      } else {
         return this.isValidDifficulty(this.mob.level.getDifficulty());
      }
   }

   public void start() {
      super.start();
      this.breakTime = 0;
   }

   public boolean canContinueToUse() {
      return this.breakTime <= this.getDoorBreakTime()
              && this.doorPos.closerThan(this.mob.position(), 3.3D) // Increased proximity check
              && this.isValidDifficulty(this.mob.level.getDifficulty());
   }

   public void stop() {
      super.stop();
      this.mob.level.destroyBlockProgress(this.mob.getId(), this.doorPos, -1);
   }

   public void tick() {
      super.tick();
      if (this.mob.getRandom().nextInt(4) == 0) { // Increased interaction frequency by reducing the interval
         this.mob.level.levelEvent(1019, this.doorPos, 0);
         if (!this.mob.swinging) {
            this.mob.swing(this.mob.getUsedItemHand());
         }
      }

      ++this.breakTime;
      int i = (int)((float)this.breakTime / (float)this.getDoorBreakTime() * 10.0F);
      if (i != this.lastBreakProgress) {
         this.mob.level.destroyBlockProgress(this.mob.getId(), this.doorPos, i);
         this.lastBreakProgress = i;
      }

      if (this.breakTime == this.getDoorBreakTime() && this.isValidDifficulty(this.mob.level.getDifficulty())) {
         this.mob.level.removeBlock(this.doorPos, false);
         this.mob.level.levelEvent(1021, this.doorPos, 0);
         this.mob.level.gameEvent(GameEvent.BLOCK_DESTROY, this.doorPos, Block.getId(this.mob.level.getBlockState(this.doorPos)));
      }

   }

   private boolean isValidDifficulty(Difficulty p_220696_1_) {
      return this.validDifficulties.test(p_220696_1_);
   }
}