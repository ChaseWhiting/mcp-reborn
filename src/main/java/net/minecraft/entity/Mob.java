package net.minecraft.entity;

import com.clearspring.analytics.util.Lists;
import com.google.common.collect.Maps;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.EntityBoundSoundInstance;
import net.minecraft.client.renderer.debug.EntityAIDebugRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.leashable.Leashable;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.monster.crimson_mosquito.EntityBoundSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.dagger.DesolateDaggerEntity;
import net.minecraft.item.equipment.trim.*;
import net.minecraft.item.tool.AxeItem;
import net.minecraft.item.tool.BowItem;
import net.minecraft.item.tool.SwordItem;
import net.minecraft.item.tool.ToolItem;
import net.minecraft.loot.LootContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.Effects;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Mob extends LivingEntity implements Leashable {
   private static final DataParameter<Byte> DATA_MOB_FLAGS_ID = EntityDataManager.defineId(Mob.class, DataSerializers.BYTE);
   public int ambientSoundTime;
   public int xpReward;
   protected LookController lookControl;
   protected MovementController moveControl;
   protected JumpController jumpControl;
   protected BodyController bodyRotationControl;
   protected PathNavigator navigation;
   protected final GoalSelector goalSelector;
   public final GoalSelector targetSelector;
   private UUID blindnessModifierUUID = UUID.fromString("c79c47ee-9d5b-4fb3-a887-ce5683051cc4");
   private AttributeModifier blindnessModifier = new AttributeModifier(
           blindnessModifierUUID,
           "blindness modifier",
           -4.0D, // -2.0 means reducing the value to 1/3 (since MULTIPLY_TOTAL adds to 1)
           AttributeModifier.Operation.MULTIPLY_TOTAL
   );
   private LivingEntity target;
   private final EntitySenses sensing;
   private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
   protected final float[] handDropChances = new float[2];
   private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
   protected final float[] armorDropChances = new float[4];
   private boolean canPickUpLoot;
   protected boolean persistenceRequired;
   private final Map<PathNodeType, Float> pathfindingMalus = Maps.newEnumMap(PathNodeType.class);
   private ResourceLocation lootTable;
   private long lootTableSeed;





   public void stopInPlace() {
      this.getNavigation().stop();
      this.setXxa(0.0f);
      this.setYya(0.0f);
      this.setSpeed(0.0f);
      this.setDeltaMovement(0.0, 0.0, 0.0);
      this.resetAngularLeashMomentum();
   }

   private LeashData leashData;


   @Override
   @Nullable
   public LeashData getLeashData() {
      return this.leashData;
   }

   private void resetAngularLeashMomentum() {
      if (this.leashData != null) {
         this.leashData.angularMomentum = 0.0;
      }
   }

   @Override
   public void setLeashData(@Nullable LeashData leashData) {
      this.leashData = leashData;
   }

   @Override
   public void onLeashRemoved() {
      if (this.getLeashData() == null) {
         this.restrictRadius = -1.0F;
      }
   }

   @Override
   public void leashTooFarBehaviour() {
      Leashable.super.leashTooFarBehaviour();
      this.goalSelector.disableControlFlag(Goal.Flag.MOVE);
   }

   @Override
   public boolean canBeLeashed() {
      return this.getType().is(EntityTypeTags.LEASHABLE);
   }




   private BlockPos restrictCenter = BlockPos.ZERO;
   private float restrictRadius = -1.0F;

   public void setHomeTo(BlockPos blockPos, int n) {
      this.restrictCenter = blockPos;
      this.restrictRadius = n;
   }

   public boolean isWithinHome(Vector3d vec3) {
      if (this.restrictRadius == -1) {
         return true;
      }
      return this.restrictCenter.distToCenterSqr(vec3) < (double)(this.restrictRadius * this.restrictRadius);
   }

   public void clearHome() {
      this.restrictRadius = -1.0F;
   }

   public boolean hasHome() {
      return this.restrictRadius != -1F;
   }

   public float getHomeRadius() {
      return this.restrictRadius;
   }

   public BlockPos getHomePosition() {
      return this.restrictCenter;
   }

   protected Mob(EntityType<? extends Mob> entity, World world) {
      super(entity, world);
      this.goalSelector = new GoalSelector(world.getProfilerSupplier());
      this.targetSelector = new GoalSelector(world.getProfilerSupplier());
      this.lookControl = new LookController(this);
      this.moveControl = new MovementController(this);
      this.jumpControl = new JumpController(this);
      this.bodyRotationControl = this.createBodyControl();
      this.navigation = this.createNavigation(world);
      this.sensing = new EntitySenses(this);
      Arrays.fill(this.armorDropChances, 0.085F);
      Arrays.fill(this.handDropChances, 0.085F);
      if (world != null && !world.isClientSide) {
         this.registerGoals();
      }

   }



   protected boolean canShearEquipment(PlayerEntity player) {
      return !this.isVehicle();
   }

   public GoalSelector getGoalSelector() {
      return goalSelector;
   }

   protected void registerGoals() {
   }

   public void removeAllGoals(Predicate<Goal> predicate) {
      this.goalSelector.removeAllGoals(predicate);
   }

   public void onPathfindingStart() {
   }

   public void onPathfindingDone() {
   }



   public static AttributeModifierMap.MutableAttribute createMobAttributes() {
      return LivingEntity.createLivingAttributes().add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.ATTACK_KNOCKBACK);
   }

   protected PathNavigator createNavigation(World p_175447_1_) {
      return new GroundPathNavigator(this, p_175447_1_);
   }

   protected boolean shouldPassengersInheritMalus() {
      return false;
   }

   public float getPathfindingMalus(PathNodeType p_184643_1_) {
      Mob mobentity;
      if (this.getVehicle() instanceof Mob && ((Mob)this.getVehicle()).shouldPassengersInheritMalus()) {
         mobentity = (Mob)this.getVehicle();
      } else {
         mobentity = this;
      }

      Float f = mobentity.pathfindingMalus.get(p_184643_1_);
      return f == null ? p_184643_1_.getMalus() : f;
   }

   public void setPathfindingMalus(PathNodeType nodeType, float malusValue) {
      this.pathfindingMalus.put(nodeType, malusValue);
   }

   public boolean canCutCorner(PathNodeType p_233660_1_) {
      return p_233660_1_ != PathNodeType.DANGER_FIRE && p_233660_1_ != PathNodeType.DANGER_CACTUS && p_233660_1_ != PathNodeType.DANGER_OTHER && p_233660_1_ != PathNodeType.WALKABLE_DOOR;
   }

   protected BodyController createBodyControl() {
      return new BodyController(this);
   }

   public BodyController getBodyRotationControl() {
      return bodyRotationControl;
   }

   public LookController getLookControl() {
      return this.lookControl;
   }

   public MovementController getMoveControl() {
      if (this.isPassenger() && this.getVehicle() instanceof Mob) {
         Mob mobentity = (Mob)this.getVehicle();
         return mobentity.getMoveControl();
      } else {
         return this.moveControl;
      }
   }

   public void strafe(float a, float b) {
      getMoveControl().strafe(a,b);
   }

   public JumpController getJumpControl() {
      return this.jumpControl;
   }

   public PathNavigator getNavigation() {
      if (this.isPassenger() && this.getVehicle() instanceof Mob) {
         Mob mobentity = (Mob)this.getVehicle();
         return mobentity.getNavigation();
      } else {
         return this.navigation;
      }
   }

   public EntitySenses getSensing() {
      return this.sensing;
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.target;
   }

   public void setTarget(@Nullable LivingEntity p_70624_1_) {
      this.target = p_70624_1_;
   }

   public boolean canAttackType(EntityType<?> p_213358_1_) {
      return p_213358_1_ != EntityType.GHAST;
   }

   public boolean canFireProjectileWeapon(ShootableItem p_230280_1_) {
      return false;
   }

   public void ate() {
      gameEvent(GameEvent.EAT);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_MOB_FLAGS_ID, (byte)0);
   }

   public int getAmbientSoundInterval() {
      return 80;
   }

   public void playAmbientSound() {
      SoundEvent soundevent = this.getAmbientSound();
      if (soundevent != null) {
         if (this instanceof EntityBoundSounds) {
            if (this.level.isClientSide) {
               EntityBoundSoundInstance si = new EntityBoundSoundInstance(soundevent, this.getSoundSource(), this.getSoundVolume(), this.getVoicePitch(), this);

               Minecraft.getInstance().getSoundManager().play(si);
            }

         }

          if (!(this instanceof EntityBoundSounds)) {
              this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
          }
      }

   }



   public void baseTick() {
      super.baseTick();
      this.level.getProfiler().push("mobBaseTick");
      if (this.isAlive() && this.random.nextInt(1000) < this.ambientSoundTime++) {
         this.resetAmbientSoundTime();
         this.playAmbientSound();
      }

      this.level.getProfiler().pop();
   }

   protected void playHurtSound(DamageSource p_184581_1_) {
      this.resetAmbientSoundTime();
      super.playHurtSound(p_184581_1_);
   }

   protected void resetAmbientSoundTime() {
      this.ambientSoundTime = -this.getAmbientSoundInterval();
   }

   protected int getExperienceReward(PlayerEntity p_70693_1_) {
      if (this.xpReward > 0) {
         int i = this.xpReward;

         for(int j = 0; j < this.armorItems.size(); ++j) {
            if (!this.armorItems.get(j).isEmpty() && this.armorDropChances[j] <= 1.0F) {
               i += 1 + this.random.nextInt(3);
            }
         }

         for(int k = 0; k < this.handItems.size(); ++k) {
            if (!this.handItems.get(k).isEmpty() && this.handDropChances[k] <= 1.0F) {
               i += 1 + this.random.nextInt(3);
            }
         }

         return i;
      } else {
         return this.xpReward;
      }
   }

   public void spawnAnim() {
      if (this.level.isClientSide) {
         for(int i = 0; i < 20; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            double d3 = 10.0D;
            this.level.addParticle(ParticleTypes.POOF, this.getX(1.0D) - d0 * 10.0D, this.getRandomY() - d1 * 10.0D, this.getRandomZ(1.0D) - d2 * 10.0D, d0, d1, d2);
         }
      } else {
         this.level.broadcastEntityEvent(this, (byte)20);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 20) {
         this.spawnAnim();
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide) {
         if (this.tickCount % 5 == 0) {
            this.updateControlFlags();
         }

         // Only send the packet if it's a server world
         if (this.level instanceof ServerWorld) {
            ServerPlayerEntity player = DebugUtils.findLocalPlayer(this.level);
            if (player != null && this.isAlive()) {
               sendGoalSelectorDebugPacket();
            }
         }
      }
   }

   private void sendGoalSelectorDebugPacket() {
      List<EntityAIDebugRenderer.Entry> goals = new ArrayList<>();
      // Collect all goals
      for (PrioritizedGoal goal : this.goalSelector.getAvailableGoals().collect(Collectors.toList())) {
         Goal actualGoal = goal.getGoal();
         int priority = goal.getPriority();
         boolean isRunning = goal.isRunning();
         String name = actualGoal.getClass().getSimpleName(); // Get the goal class name as the description
         goals.add(new EntityAIDebugRenderer.Entry(this.blockPosition(), priority, name, isRunning));
      }
      // Find the player and send the packet
      ServerPlayerEntity player = DebugUtils.findLocalPlayer(this.level);
      DebugPacketSender.sendGoalSelectorDebugPacket(this.level, this, goals, player);
   }

   protected void updateControlFlags() {
      boolean flag = !(this.getControllingPassenger() instanceof Mob);
      boolean flag1 = !(this.getVehicle() instanceof BoatEntity);
      this.goalSelector.setControlFlag(Goal.Flag.MOVE, flag);
      this.goalSelector.setControlFlag(Goal.Flag.JUMP, flag && flag1);
      this.goalSelector.setControlFlag(Goal.Flag.LOOK, flag);
   }

   protected float tickHeadTurn(float p_110146_1_, float p_110146_2_) {
      this.bodyRotationControl.clientTick();
      return p_110146_2_;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return null;
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putBoolean("CanPickUpLoot", this.canPickUpLoot());
      p_213281_1_.putBoolean("PersistenceRequired", this.persistenceRequired);
      ListNBT listnbt = new ListNBT();

      for(ItemStack itemstack : this.armorItems) {
         CompoundNBT compoundnbt = new CompoundNBT();
         if (!itemstack.isEmpty()) {
            itemstack.save(compoundnbt);
         }

         listnbt.add(compoundnbt);
      }

      p_213281_1_.put("ArmorItems", listnbt);
      ListNBT listnbt1 = new ListNBT();

      for(ItemStack itemstack1 : this.handItems) {
         CompoundNBT compoundnbt1 = new CompoundNBT();
         if (!itemstack1.isEmpty()) {
            itemstack1.save(compoundnbt1);
         }

         listnbt1.add(compoundnbt1);
      }

      p_213281_1_.put("HandItems", listnbt1);
      ListNBT listnbt2 = new ListNBT();

      for(float f : this.armorDropChances) {
         listnbt2.add(FloatNBT.valueOf(f));
      }

      p_213281_1_.put("ArmorDropChances", listnbt2);
      ListNBT listnbt3 = new ListNBT();

      for(float f1 : this.handDropChances) {
         listnbt3.add(FloatNBT.valueOf(f1));
      }

      p_213281_1_.put("HandDropChances", listnbt3);

      p_213281_1_.putBoolean("LeftHanded", this.isLeftHanded());
      if (this.lootTable != null) {
         p_213281_1_.putString("DeathLootTable", this.lootTable.toString());
         if (this.lootTableSeed != 0L) {
            p_213281_1_.putLong("DeathLootTableSeed", this.lootTableSeed);
         }
      }

      if (this.isNoAi()) {
         p_213281_1_.putBoolean("NoAI", this.isNoAi());
      }

      this.writeLeashData(p_213281_1_, this.leashData);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      if (p_70037_1_.contains("CanPickUpLoot", 1)) {
         this.setCanPickUpLoot(p_70037_1_.getBoolean("CanPickUpLoot"));
      }

      this.persistenceRequired = p_70037_1_.getBoolean("PersistenceRequired");
      if (p_70037_1_.contains("ArmorItems", 9)) {
         ListNBT listnbt = p_70037_1_.getList("ArmorItems", 10);

         for(int i = 0; i < this.armorItems.size(); ++i) {
            this.armorItems.set(i, ItemStack.of(listnbt.getCompound(i)));
         }
      }

      if (p_70037_1_.contains("HandItems", 9)) {
         ListNBT listnbt1 = p_70037_1_.getList("HandItems", 10);

         for(int j = 0; j < this.handItems.size(); ++j) {
            this.handItems.set(j, ItemStack.of(listnbt1.getCompound(j)));
         }
      }

      if (p_70037_1_.contains("ArmorDropChances", 9)) {
         ListNBT listnbt2 = p_70037_1_.getList("ArmorDropChances", 5);

         for(int k = 0; k < listnbt2.size(); ++k) {
            this.armorDropChances[k] = listnbt2.getFloat(k);
         }
      }

      if (p_70037_1_.contains("HandDropChances", 9)) {
         ListNBT listnbt3 = p_70037_1_.getList("HandDropChances", 5);

         for(int l = 0; l < listnbt3.size(); ++l) {
            this.handDropChances[l] = listnbt3.getFloat(l);
         }
      }

      this.setLeftHanded(p_70037_1_.getBoolean("LeftHanded"));
      if (p_70037_1_.contains("DeathLootTable", 8)) {
         this.lootTable = new ResourceLocation(p_70037_1_.getString("DeathLootTable"));
         this.lootTableSeed = p_70037_1_.getLong("DeathLootTableSeed");
      }

      this.setNoAi(p_70037_1_.getBoolean("NoAI"));

      this.readLeashData(p_70037_1_);
   }

   protected void dropFromLootTable(DamageSource p_213354_1_, boolean p_213354_2_) {
      super.dropFromLootTable(p_213354_1_, p_213354_2_);
      this.lootTable = null;
   }

   protected LootContext.Builder createLootContext(boolean p_213363_1_, DamageSource p_213363_2_) {
      return super.createLootContext(p_213363_1_, p_213363_2_).withOptionalRandomSeed(this.lootTableSeed, this.random);
   }

   public final ResourceLocation getLootTable() {
      return this.lootTable == null ? this.getDefaultLootTable() : this.lootTable;
   }

   protected ResourceLocation getDefaultLootTable() {
      return super.getLootTable();
   }

   public void setZza(float p_191989_1_) {
      this.zza = p_191989_1_;
   }

   public void setYya(float p_70657_1_) {
      this.yya = p_70657_1_;
   }

   public void setXxa(float p_184646_1_) {
      this.xxa = p_184646_1_;
   }

   public void setSpeed(float p_70659_1_) {
      super.setSpeed(p_70659_1_);
      this.setZza(p_70659_1_);
   }

   public void aiStep() {
      super.aiStep();
      this.level.getProfiler().push("looting");
      if (!this.level.isClientSide && this.canPickUpLoot() && this.isAlive() && !this.dead && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         for(ItemEntity itementity :
                 this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox()
                         .inflate(this.getPickupReach().getX(), this.getPickupReach().getY(), this.getPickupReach().getZ()))) {
            if (!itementity.removed && !itementity.getItem().isEmpty() && !itementity.hasPickUpDelay() && this.wantsToPickUp(itementity.getItem())) {
               this.pickUpItem(itementity);
            }
         }
      }

      this.level.getProfiler().pop();
   }

   public Vector3i getPickupReach() {
      return new Vector3i(1, 0, 1);
   }

   protected void pickUpItem(ItemEntity p_175445_1_) {
      ItemStack itemstack = p_175445_1_.getItem();
      if (this.equipItemIfPossible(itemstack)) {
         this.onItemPickup(p_175445_1_);
         this.take(p_175445_1_, itemstack.getCount());
         p_175445_1_.remove();
      }

   }

   public boolean equipItemIfPossible(ItemStack p_233665_1_) {
      EquipmentSlotType equipmentslottype = getEquipmentSlotForItem(p_233665_1_);
      ItemStack itemstack = this.getItemBySlot(equipmentslottype);
      boolean flag = this.canReplaceCurrentItem(p_233665_1_, itemstack);
      if (flag && this.canHoldItem(p_233665_1_)) {
         double d0 = (double)this.getEquipmentDropChance(equipmentslottype);
         if (!itemstack.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1F, 0.0F) < d0) {
            this.spawnAtLocation(itemstack);
         }

         this.setItemSlotAndDropWhenKilled(equipmentslottype, p_233665_1_);
         this.playEquipSound(p_233665_1_);

         if (doesEmitEquipEvent(equipmentslottype)) {
            gameEvent(GameEvent.EQUIP);
         }
         return true;
      } else {
         return false;
      }
   }

   protected boolean doesEmitEquipEvent(EquipmentSlotType equipmentSlot) {
      return true;
   }

   protected void setItemSlotAndDropWhenKilled(EquipmentSlotType p_233657_1_, ItemStack p_233657_2_) {
      this.setItemSlot(p_233657_1_, p_233657_2_);
      this.setGuaranteedDrop(p_233657_1_);
      this.persistenceRequired = true;
   }

   public void setGuaranteedDrop(EquipmentSlotType p_233663_1_) {
      switch(p_233663_1_.getType()) {
      case HAND:
         this.handDropChances[p_233663_1_.getIndex()] = 2.0F;
         break;
      case ARMOR:
         this.armorDropChances[p_233663_1_.getIndex()] = 2.0F;
      }

   }

   protected boolean canReplaceCurrentItem(ItemStack p_208003_1_, ItemStack p_208003_2_) {
      if (p_208003_2_.isEmpty()) {
         return true;
      } else if (p_208003_1_.getItem() instanceof SwordItem) {
         if (!(p_208003_2_.getItem() instanceof SwordItem)) {
            return true;
         } else {
            SwordItem sworditem = (SwordItem)p_208003_1_.getItem();
            SwordItem sworditem1 = (SwordItem)p_208003_2_.getItem();
            if (sworditem.getDamage() != sworditem1.getDamage()) {
               return sworditem.getDamage() > sworditem1.getDamage();
            } else {
               return this.canReplaceEqualItem(p_208003_1_, p_208003_2_);
            }
         }
      } else if (p_208003_1_.getItem() instanceof BowItem && p_208003_2_.getItem() instanceof BowItem) {
         return this.canReplaceEqualItem(p_208003_1_, p_208003_2_);
      } else if (p_208003_1_.getItem() instanceof CrossbowItem && p_208003_2_.getItem() instanceof CrossbowItem || p_208003_1_.getItem() instanceof GildedCrossbowItem && p_208003_2_.getItem() instanceof GildedCrossbowItem) {
         return this.canReplaceEqualItem(p_208003_1_, p_208003_2_);
      } else if (p_208003_1_.getItem() instanceof ArmorItem) {
         if (EnchantmentHelper.hasBindingCurse(p_208003_2_)) {
            return false;
         } else if (!(p_208003_2_.getItem() instanceof ArmorItem)) {
            return true;
         } else {
            ArmorItem armoritem = (ArmorItem)p_208003_1_.getItem();
            ArmorItem armoritem1 = (ArmorItem)p_208003_2_.getItem();
            if (armoritem.getDefense() != armoritem1.getDefense()) {
               return armoritem.getDefense() > armoritem1.getDefense();
            } else if (armoritem.getToughness() != armoritem1.getToughness()) {
               return armoritem.getToughness() > armoritem1.getToughness();
            } else {
               return this.canReplaceEqualItem(p_208003_1_, p_208003_2_);
            }
         }
      } else {
         if (p_208003_1_.getItem() instanceof ToolItem) {
            if (p_208003_2_.getItem() instanceof BlockItem) {
               return true;
            }

            if (p_208003_2_.getItem() instanceof ToolItem) {
               ToolItem toolitem = (ToolItem)p_208003_1_.getItem();
               ToolItem toolitem1 = (ToolItem)p_208003_2_.getItem();
               if (toolitem.getAttackDamage() != toolitem1.getAttackDamage()) {
                  return toolitem.getAttackDamage() > toolitem1.getAttackDamage();
               }

               return this.canReplaceEqualItem(p_208003_1_, p_208003_2_);
            }
         }

         return false;
      }
   }

   public boolean canReplaceEqualItem(ItemStack p_233659_1_, ItemStack p_233659_2_) {
      if (p_233659_1_.getDamageValue() >= p_233659_2_.getDamageValue() && (!p_233659_1_.hasTag() || p_233659_2_.hasTag())) {
         if (p_233659_1_.hasTag() && p_233659_2_.hasTag()) {
            return p_233659_1_.getTag().getAllKeys().stream().anyMatch((p_233664_0_) -> {
               return !p_233664_0_.equals("Damage");
            }) && !p_233659_2_.getTag().getAllKeys().stream().anyMatch((p_233662_0_) -> {
               return !p_233662_0_.equals("Damage");
            });
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public boolean canHoldItem(ItemStack p_175448_1_) {
      return true;
   }

   public boolean wantsToPickUp(ItemStack p_230293_1_) {
      return this.canHoldItem(p_230293_1_);
   }

   public boolean removeWhenFarAway(double p_213397_1_) {
      return true;
   }

   public boolean requiresCustomPersistence() {
      return this.isPassenger();
   }

   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   public void checkDespawn() {
      if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
         this.remove();
      } else if (!this.isPersistenceRequired() && !this.requiresCustomPersistence()) {
         Entity entity = this.level.getNearestPlayer(this, -1.0D);
         if (entity != null) {
            double d0 = entity.distanceToSqr(this);
            int i = this.getType().getCategory().getDespawnDistance();
            int j = i * i;
            if (d0 > (double)j && this.removeWhenFarAway(d0)) {
               this.remove();
            }

            int k = this.getType().getCategory().getNoDespawnDistance();
            int l = k * k;
            if (this.noActionTime > 600 && this.random.nextInt(800) == 0 && d0 > (double)l && this.removeWhenFarAway(d0)) {
               this.remove();
            } else if (d0 < (double)l) {
               this.noActionTime = 0;
            }
         }

      } else {
         this.noActionTime = 0;
      }
   }

   protected final void serverAiStep() {
      ++this.noActionTime;
      if (!(this instanceof EndermanEntity) && !(this instanceof WitherEntity)) {
         this.level.getProfiler().push("blindness");
         if (this.hasEffect(Effects.BLINDNESS)) {
            if (!this.getAttribute(Attributes.FOLLOW_RANGE).hasModifier(blindnessModifier)) {
               this.getAttribute(Attributes.FOLLOW_RANGE).addTransientModifier(blindnessModifier);
            }
         } else {
            this.getAttribute(Attributes.FOLLOW_RANGE).removeModifier(blindnessModifierUUID);
         }
      }
      this.level.getProfiler().pop();
      this.level.getProfiler().push("sensing");
      this.sensing.tick();
      this.level.getProfiler().pop();
      this.level.getProfiler().push("targetSelector");
      this.targetSelector.tick();
      this.level.getProfiler().pop();
      this.level.getProfiler().push("goalSelector");
      this.goalSelector.tick();
      this.level.getProfiler().pop();
      this.level.getProfiler().push("navigation");
      this.navigation.tick();
      this.level.getProfiler().pop();
      this.level.getProfiler().push("mob tick");
      this.customServerAiStep();
      this.level.getProfiler().pop();
      this.level.getProfiler().push("controls");
      this.level.getProfiler().push("move");
      this.moveControl.tick();
      this.level.getProfiler().popPush("look");
      this.lookControl.tick();
      this.level.getProfiler().popPush("jump");
      if (jumpControl != null) {
         this.jumpControl.tick();
      }
      this.level.getProfiler().pop();
      this.level.getProfiler().pop();
      this.sendDebugPackets();
   }

   protected void sendDebugPackets() {
      DebugPacketSender.sendGoalSelector(this.level, this, this.goalSelector);
   }

   protected void customServerAiStep() {
   }

   public int getMaxHeadXRot() {
      return 40;
   }

   public int getMaxHeadYRot() {
      return 75;
   }

   public int getHeadRotSpeed() {
      return 10;
   }

   public void lookAt(Entity p_70625_1_, float p_70625_2_, float p_70625_3_) {
      double d0 = p_70625_1_.getX() - this.getX();
      double d2 = p_70625_1_.getZ() - this.getZ();
      double d1;
      if (p_70625_1_ instanceof LivingEntity) {
         LivingEntity livingentity = (LivingEntity)p_70625_1_;
         d1 = livingentity.getEyeY() - this.getEyeY();
      } else {
         d1 = (p_70625_1_.getBoundingBox().minY + p_70625_1_.getBoundingBox().maxY) / 2.0D - this.getEyeY();
      }

      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
      float f1 = (float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
      this.xRot = this.rotlerp(this.xRot, f1, p_70625_3_);
      this.yRot = this.rotlerp(this.yRot, f, p_70625_2_);
   }

   private float rotlerp(float p_70663_1_, float p_70663_2_, float p_70663_3_) {
      float f = MathHelper.wrapDegrees(p_70663_2_ - p_70663_1_);
      if (f > p_70663_3_) {
         f = p_70663_3_;
      }

      if (f < -p_70663_3_) {
         f = -p_70663_3_;
      }

      return p_70663_1_ + f;
   }

   public static boolean checkMobSpawnRules(EntityType<? extends Mob> p_223315_0_, IWorld p_223315_1_, SpawnReason p_223315_2_, BlockPos p_223315_3_, Random p_223315_4_) {
      BlockPos blockpos = p_223315_3_.below();
      return p_223315_2_ == SpawnReason.SPAWNER || p_223315_1_.getBlockState(blockpos).isValidSpawn(p_223315_1_, blockpos, p_223315_0_);
   }

   public boolean checkSpawnRules(IWorld p_213380_1_, SpawnReason p_213380_2_) {
      return true;
   }

   public boolean checkSpawnObstruction(IWorldReader p_205019_1_) {
      return !p_205019_1_.containsAnyLiquid(this.getBoundingBox()) && p_205019_1_.isUnobstructed(this);
   }

   public int getMaxSpawnClusterSize() {
      return 4;
   }

   public boolean isMaxGroupSizeReached(int p_204209_1_) {
      return false;
   }

   public int getMaxFallDistance() {
      if (this.getTarget() == null) {
         return 3;
      } else {
         int i = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
         i = i - (3 - this.level.getDifficulty().getId()) * 4;
         if (i < 0) {
            i = 0;
         }

         return i + 3;
      }
   }

   public Iterable<ItemStack> getHandSlots() {
      return this.handItems;
   }

   public Iterable<ItemStack> getArmorSlots() {
      return this.armorItems;
   }

   public Iterable<ItemStack> getArmorAndHandSlots() {
      List<ItemStack> l = Lists.newArrayList();
      l.addAll(this.armorItems);
      l.add(this.getMainHandItem());
      l.add(this.getOffhandItem());
      return l;
   }

   public ItemStack getItemBySlot(EquipmentSlotType p_184582_1_) {
      switch(p_184582_1_.getType()) {
      case HAND:
         return this.handItems.get(p_184582_1_.getIndex());
      case ARMOR:
         return this.armorItems.get(p_184582_1_.getIndex());
      default:
         return ItemStack.EMPTY;
      }
   }

   public void setItemSlot(EquipmentSlotType slotType, ItemStack item) {
      switch(slotType.getType()) {
      case HAND:
         this.handItems.set(slotType.getIndex(), item);
         break;
      case ARMOR:
         this.armorItems.set(slotType.getIndex(), item);
      }

   }

   public void setItemSlot(EquipmentSlotType slotType, Item item1) {
      ItemStack item = new ItemStack(item1);
      switch(slotType.getType()) {
         case HAND:
            this.handItems.set(slotType.getIndex(), item);
            break;
         case ARMOR:
            this.armorItems.set(slotType.getIndex(), item);
      }

   }

   protected void dropCustomDeathLoot(DamageSource p_213333_1_, int p_213333_2_, boolean p_213333_3_) {
      super.dropCustomDeathLoot(p_213333_1_, p_213333_2_, p_213333_3_);

      for(EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
         ItemStack itemstack = this.getItemBySlot(equipmentslottype);
         float f = this.getEquipmentDropChance(equipmentslottype);
         boolean flag = f > 1.0F;
         if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack) && (p_213333_3_ || flag) && Math.max(this.random.nextFloat() - (float)p_213333_2_ * 0.01F, 0.0F) < f) {
            if (!flag && itemstack.isDamageableItem()) {
               itemstack.setDamageValue(itemstack.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(itemstack.getMaxDamage() - 3, 1))));
            }

            this.spawnAtLocation(itemstack);
            this.setItemSlot(equipmentslottype, ItemStack.EMPTY);
         }
      }

   }

   protected float getEquipmentDropChance(EquipmentSlotType p_205712_1_) {
      float f;
      switch(p_205712_1_.getType()) {
      case HAND:
         f = this.handDropChances[p_205712_1_.getIndex()];
         break;
      case ARMOR:
         f = this.armorDropChances[p_205712_1_.getIndex()];
         break;
      default:
         f = 0.0F;
      }

      return f;
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance p_180481_1_) {
      if (this.random.nextFloat() < 0.15F * p_180481_1_.getSpecialMultiplier()) {
         int i = this.random.nextInt(2);
         float f = this.level.getDifficulty() == Difficulty.HARD ? veryHardmode() ? 0.05F : 0.1F : 0.25F;
         float chance = veryHardmode() ? 0.25f : 0.095f;
         if (this.random.nextFloat() < chance) {
            ++i;
         }

         if (this.random.nextFloat() < chance) {
            ++i;
         }

         if (this.random.nextFloat() < chance) {
            ++i;
         }

         boolean flag = true;

         for(EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
            if (equipmentslottype.getType() == EquipmentSlotType.Group.ARMOR) {
               ItemStack itemstack = this.getItemBySlot(equipmentslottype);
               if (!flag && this.random.nextFloat() < f) {
                  break;
               }

               flag = false;
               if (itemstack.isEmpty()) {
                  Item item = getEquipmentForSlot(equipmentslottype, i);
                  if (item != null) {

                     ItemStack stack = new ItemStack(item);

                     this.applyTrimsRandomly(equipmentslottype, stack);

                     this.setItemSlot(equipmentslottype, stack);
                  }
               }
            }
         }
      }

   }

   public boolean canApplyTrims() {
      return true;
   }

   public float getTrimApplyChance() {
      return 0.255F;
   }

   private void applyTrimsRandomly(EquipmentSlotType equipmentslottype, ItemStack stack) {
      if (this.canApplyTrims()) {
         if (!(this.random.nextFloat() < this.getTrimApplyChance())) return;

         int a = switch(equipmentslottype) {
            case FEET -> 1;
            case LEGS -> 3;
            case CHEST -> 4;
            case HEAD -> 2;
            default -> 0;
         };
         if (this.random.nextFloat() < (0.35F - (0.05F * (a)))) {
            List<TrimPattern> trimPatterns =
                    Util.shuffledCopy(Registry.TRIM_PATTERN.stream()
                            .filter(pattern -> pattern != TrimPatterns.DUMMY_TRIM_PATTERN).toList(), random);
            TrimPattern trimPattern = trimPatterns.get(this.random.nextInt(trimPatterns.size()));
            List<TrimMaterial> trimMaterials =
                    Util.shuffledCopy(Registry.TRIM_MATERIAL.stream()
                            .filter(material -> material != TrimMaterials.DUMMY_TRIM_MATERIAL).toList(), random);

            TrimMaterial trimMaterial = trimMaterials.get(this.random.nextInt(trimMaterials.size()));

            ArmorTrim trim = new ArmorTrim(trimMaterial, trimPattern, random.nextFloat() < (0.12F - (0.02F * a)));

            ArmorTrim.setTrim(null, stack, trim);

            this.setDropChance(equipmentslottype, Math.max(0.0F, this.getEquipmentDropChance(equipmentslottype) - 0.01F * a));
         }
      }
   }

   public static EquipmentSlotType getEquipmentSlotForItem(ItemStack p_184640_0_) {
      Item item = p_184640_0_.getItem();

      if (item != Blocks.CARVED_PUMPKIN.asItem() && item != Blocks.WHITE_CARVED_PUMPKIN.asItem() && (!(item instanceof BlockItem) || !(((BlockItem)item).getBlock() instanceof AbstractSkullBlock))) {
         if (item instanceof ArmorItem) {
            return ((ArmorItem)item).getSlot();
         } else if (item == Items.ELYTRA || item instanceof AbstractCapeItem) {
            return EquipmentSlotType.CHEST;
         }  else {
            return item == Items.SHIELD || item == Items.NETHERITE_SHIELD || item == Items.SHIELD_OF_CTHULHU ? EquipmentSlotType.OFFHAND : EquipmentSlotType.MAINHAND;

         }
      } else {
         return EquipmentSlotType.HEAD;
      }
   }

   @Nullable
   public static Item getEquipmentForSlot(EquipmentSlotType p_184636_0_, int p_184636_1_) {
      switch(p_184636_0_) {
      case HEAD:
         if (p_184636_1_ == 0) {
            return Items.LEATHER_HELMET;
         } else if (p_184636_1_ == 1) {
            return Items.GOLDEN_HELMET;
         } else if (p_184636_1_ == 2) {
            return Items.CHAINMAIL_HELMET;
         } else if (p_184636_1_ == 3) {
            return Items.IRON_HELMET;
         } else if (p_184636_1_ == 4) {
            return Items.DIAMOND_HELMET;
         }
      case CHEST:
         if (p_184636_1_ == 0) {
            return Items.LEATHER_CHESTPLATE;
         } else if (p_184636_1_ == 1) {
            return Items.GOLDEN_CHESTPLATE;
         } else if (p_184636_1_ == 2) {
            return Items.CHAINMAIL_CHESTPLATE;
         } else if (p_184636_1_ == 3) {
            return Items.IRON_CHESTPLATE;
         } else if (p_184636_1_ == 4) {
            return Items.DIAMOND_CHESTPLATE;
         }
      case LEGS:
         if (p_184636_1_ == 0) {
            return Items.LEATHER_LEGGINGS;
         } else if (p_184636_1_ == 1) {
            return Items.GOLDEN_LEGGINGS;
         } else if (p_184636_1_ == 2) {
            return Items.CHAINMAIL_LEGGINGS;
         } else if (p_184636_1_ == 3) {
            return Items.IRON_LEGGINGS;
         } else if (p_184636_1_ == 4) {
            return Items.DIAMOND_LEGGINGS;
         }
      case FEET:
         if (p_184636_1_ == 0) {
            return Items.LEATHER_BOOTS;
         } else if (p_184636_1_ == 1) {
            return Items.GOLDEN_BOOTS;
         } else if (p_184636_1_ == 2) {
            return Items.CHAINMAIL_BOOTS;
         } else if (p_184636_1_ == 3) {
            return Items.IRON_BOOTS;
         } else if (p_184636_1_ == 4) {
            return Items.DIAMOND_BOOTS;
         }
      default:
         return null;
      }
   }

   protected void populateDefaultEquipmentEnchantments(DifficultyInstance p_180483_1_) {
      float f = p_180483_1_.getSpecialMultiplier();
      if (veryHardmode()) f *= 4;
      this.enchantSpawnedWeapon(f);

      for(EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
         if (equipmentslottype.getType() == EquipmentSlotType.Group.ARMOR) {
            this.enchantSpawnedArmor(f, equipmentslottype);
         }
      }

   }

   protected void enchantSpawnedWeapon(float p_241844_1_) {
      if (!this.getMainHandItem().isEmpty() && this.random.nextFloat() < 0.25F * p_241844_1_) {
         this.setItemSlot(EquipmentSlotType.MAINHAND, EnchantmentHelper.enchantItem(this.random, this.getMainHandItem(), (int)(5.0F + p_241844_1_ * (float)this.random.nextInt(18)), false));
      }

   }

   protected void enchantSpawnedArmor(float p_242289_1_, EquipmentSlotType p_242289_2_) {
      ItemStack itemstack = this.getItemBySlot(p_242289_2_);
      if (!itemstack.isEmpty() && this.random.nextFloat() < 0.5F * p_242289_1_) {
         this.setItemSlot(p_242289_2_, EnchantmentHelper.enchantItem(this.random, itemstack, (int)(5.0F + p_242289_1_ * (float)this.random.nextInt(veryHardmode() ? 30 : 18)), veryHardmode()));
      }

   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05D, AttributeModifier.Operation.MULTIPLY_BASE));
      finalizeHardData(p_213386_3_, p_213386_2_);

      if (this.random.nextFloat() < 0.05F) {
         this.setLeftHanded(true);
      } else {
         this.setLeftHanded(false);
      }

      if (this instanceof WarmColdVariantHolder) {
         WarmColdVariantHolder warmColdVariantHolder = (WarmColdVariantHolder) this;
         warmColdVariantHolder.setVariantOnSpawn(p_213386_1_, this.blockPosition());
      }

      return p_213386_4_;
   }

   public boolean extraHealth() {
      return true;
   }

   public void finalizeHardData(SpawnReason reason, DifficultyInstance instance) {
      if (!this.extraHealth()) return;
      double additionalHealth = this.random.nextDouble() * 7.0D;
      double maxAdditionalHealth = 7.0D; // Define the maximum additional health that can be added

// Cap the additionalHealth to ensure it doesn't exceed the maximum allowed
      additionalHealth = Math.min(additionalHealth, maxAdditionalHealth);

      List<SpawnReason> validReasons = new ArrayList<>(Arrays.asList(SpawnReason.values()));
      validReasons.remove(SpawnReason.COMMAND);
      validReasons.remove(SpawnReason.MOB_SUMMONED);
      if (new Random().nextFloat() < 0.6 && instance.isVeryDifficult() && validReasons.contains(reason)) {
         AttributeModifier modifier = new AttributeModifier("Random spawn bonus 1", additionalHealth, AttributeModifier.Operation.ADDITION);
         this.getAttribute(Attributes.MAX_HEALTH).removeModifier(modifier.getId()); // Ensure no duplicate modifiers
         this.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(modifier);

         this.setHealth(this.getMaxHealth());
      }
   }

   public boolean canBeControlledByRider() {
      return false;
   }

   public void setPersistenceRequired() {
      this.persistenceRequired = true;
   }

   public void setDropChance(EquipmentSlotType p_184642_1_, float p_184642_2_) {
      switch(p_184642_1_.getType()) {
      case HAND:
         this.handDropChances[p_184642_1_.getIndex()] = p_184642_2_;
         break;
      case ARMOR:
         this.armorDropChances[p_184642_1_.getIndex()] = p_184642_2_;
      }

   }

   public boolean canPickUpLoot() {
      return this.canPickUpLoot;
   }

   public void setCanPickUpLoot(boolean p_98053_1_) {
      this.canPickUpLoot = p_98053_1_;
   }

   public boolean canTakeItem(ItemStack p_213365_1_) {
      EquipmentSlotType equipmentslottype = getEquipmentSlotForItem(p_213365_1_);
      return this.getItemBySlot(equipmentslottype).isEmpty() && this.canPickUpLoot();
   }

   public boolean isPersistenceRequired() {
      return this.persistenceRequired;
   }

//   public final ActionResultType interact(PlayerEntity p_184230_1_, Hand p_184230_2_) {
//      if (!this.isAlive()) {
//         return ActionResultType.PASS;
//      } else if (this.getLeashHolder() == p_184230_1_) {
//         this.dropLeash(true, !p_184230_1_.abilities.instabuild);
//         return ActionResultType.sidedSuccess(this.level.isClientSide);
//      } else {
//         ActionResultType actionresulttype = this.checkAndHandleImportantInteractions(p_184230_1_, p_184230_2_);
//         if (actionresulttype.consumesAction()) {
//            this.gameEvent(GameEvent.ENTITY_INTERACT);
//            return actionresulttype;
//         } else {
//            actionresulttype = this.mobInteract(p_184230_1_, p_184230_2_);
//            return actionresulttype.consumesAction() ? actionresulttype : super.interact(p_184230_1_, p_184230_2_);
//         }
//      }
//   }

   public final ActionResultType interact(PlayerEntity player, Hand hand) {

      if (!this.isAlive()) {
         return ActionResultType.PASS;
      }

      ActionResultType actionResultType = this.checkAndHandleImportantInteractions(player, hand);
      if (actionResultType.consumesAction()) {
         this.gameEvent(GameEvent.ENTITY_INTERACT);
         return actionResultType;
      }
      ActionResultType actionResultType2 = super.interact(player, hand);
      if (actionResultType2 != ActionResultType.PASS) {
         return actionResultType2;
      }

      actionResultType = this.mobInteract(player, hand);
      if (actionResultType.consumesAction()) {
         this.gameEvent(GameEvent.ENTITY_INTERACT);
         return actionResultType;
      }

      return ActionResultType.PASS;
   }

   private ActionResultType checkAndHandleImportantInteractions(PlayerEntity p_233661_1_, Hand p_233661_2_) {
      ItemStack itemstack = p_233661_1_.getItemInHand(p_233661_2_);
      {
         if (itemstack.getItem() == Items.NAME_TAG) {
            ActionResultType actionresulttype = itemstack.interactLivingEntity(p_233661_1_, this, p_233661_2_);
            if (actionresulttype.consumesAction()) {
               return actionresulttype;
            }
         }

         if (itemstack.getItem() instanceof SpawnEggItem) {
            if (this.level instanceof ServerWorld) {
               SpawnEggItem spawneggitem = (SpawnEggItem)itemstack.getItem();
               Optional<Mob> optional = spawneggitem.spawnOffspringFromSpawnEgg(p_233661_1_, this, (EntityType)this.getType(), (ServerWorld)this.level, this.position(), itemstack);
               optional.ifPresent((p_233658_2_) -> {
                  this.onOffspringSpawnedFromEgg(p_233661_1_, p_233658_2_);
               });
               return optional.isPresent() ? ActionResultType.SUCCESS : ActionResultType.PASS;
            } else {
               return ActionResultType.CONSUME;
            }
         } else {
            return ActionResultType.PASS;
         }
      }
   }

   protected void onOffspringSpawnedFromEgg(PlayerEntity p_213406_1_, Mob p_213406_2_) {
   }

   protected ActionResultType mobInteract(PlayerEntity p_230254_1_, Hand p_230254_2_) {
      return ActionResultType.PASS;
   }

   public boolean isWithinRestriction() {
      return this.isWithinRestriction(this.blockPosition());
   }

   public boolean isWithinRestriction(BlockPos p_213389_1_) {
      if (this.restrictRadius == -1.0F) {
         return true;
      } else {
         return this.restrictCenter.distSqr(p_213389_1_) < (double)(this.restrictRadius * this.restrictRadius);
      }
   }

   public void restrictTo(BlockPos p_213390_1_, int p_213390_2_) {
      this.restrictCenter = p_213390_1_;
      this.restrictRadius = (float)p_213390_2_;
   }

   public BlockPos getRestrictCenter() {
      return this.restrictCenter;
   }

   public float getRestrictRadius() {
      return this.restrictRadius;
   }

   public boolean hasRestriction() {
      return this.restrictRadius != -1.0F;
   }

   @Nullable
   public <T extends Mob> T convertTo(EntityType<T> targetType, boolean keepEquipment) {
      if (this.removed) {
         return null;
      } else {
         T newEntity = targetType.create(this.level);
         newEntity.copyPosition(this);
         newEntity.setBaby(this.isBaby());
         newEntity.setNoAi(this.isNoAi());
         if (this.hasCustomName()) {
            newEntity.setCustomName(this.getCustomName());
            newEntity.setCustomNameVisible(this.isCustomNameVisible());
         }

         if (this.isPersistenceRequired()) {
            newEntity.setPersistenceRequired();
         }

         newEntity.setInvulnerable(this.isInvulnerable());
         if (keepEquipment) {
            newEntity.setCanPickUpLoot(this.canPickUpLoot());

            for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
               ItemStack itemStack = this.getItemBySlot(slotType);
               if (!itemStack.isEmpty()) {
                  newEntity.setItemSlot(slotType, itemStack.copy());
                  newEntity.setDropChance(slotType, this.getEquipmentDropChance(slotType));
                  itemStack.setCount(0);
               }
            }
         }
         newEntity.setRot(this.getYHeadRot(), this.xRot);

         this.level.addFreshEntity(newEntity);
         if (this.isPassenger()) {
            Entity vehicle = this.getVehicle();
            this.stopRiding();
            newEntity.startRiding(vehicle, true);
         }

         this.remove();
         return newEntity;
      }
   }

   public boolean startRiding(Entity p_184205_1_, boolean p_184205_2_) {
      boolean flag = super.startRiding(p_184205_1_, p_184205_2_);
      if (flag && this.isLeashed()) {
         dropLeash();
      }

      return flag;
   }

   public boolean setSlot(int p_174820_1_, ItemStack p_174820_2_) {
      EquipmentSlotType equipmentslottype;
      if (p_174820_1_ == 98) {
         equipmentslottype = EquipmentSlotType.MAINHAND;
      } else if (p_174820_1_ == 99) {
         equipmentslottype = EquipmentSlotType.OFFHAND;
      } else if (p_174820_1_ == 100 + EquipmentSlotType.HEAD.getIndex()) {
         equipmentslottype = EquipmentSlotType.HEAD;
      } else if (p_174820_1_ == 100 + EquipmentSlotType.CHEST.getIndex()) {
         equipmentslottype = EquipmentSlotType.CHEST;
      } else if (p_174820_1_ == 100 + EquipmentSlotType.LEGS.getIndex()) {
         equipmentslottype = EquipmentSlotType.LEGS;
      } else {
         if (p_174820_1_ != 100 + EquipmentSlotType.FEET.getIndex()) {
            return false;
         }

         equipmentslottype = EquipmentSlotType.FEET;
      }

      if (!p_174820_2_.isEmpty() && !isValidSlotForItem(equipmentslottype, p_174820_2_) && equipmentslottype != EquipmentSlotType.HEAD) {
         return false;
      } else {
         this.setItemSlot(equipmentslottype, p_174820_2_);
         return true;
      }
   }

   public boolean isControlledByLocalInstance() {
      return this.canBeControlledByRider() && super.isControlledByLocalInstance();
   }

   public static boolean isValidSlotForItem(EquipmentSlotType p_184648_0_, ItemStack p_184648_1_) {
      EquipmentSlotType equipmentslottype = getEquipmentSlotForItem(p_184648_1_);
      return equipmentslottype == p_184648_0_ || equipmentslottype == EquipmentSlotType.MAINHAND && p_184648_0_ == EquipmentSlotType.OFFHAND || equipmentslottype == EquipmentSlotType.OFFHAND && p_184648_0_ == EquipmentSlotType.MAINHAND;
   }

   public boolean isEffectiveAi() {
      return super.isEffectiveAi() && !this.isNoAi();
   }

   public void setNoAi(boolean p_94061_1_) {
      byte b0 = this.entityData.get(DATA_MOB_FLAGS_ID);
      this.entityData.set(DATA_MOB_FLAGS_ID, p_94061_1_ ? (byte)(b0 | 1) : (byte)(b0 & -2));
   }

   public void setLeftHanded(boolean p_184641_1_) {
      byte b0 = this.entityData.get(DATA_MOB_FLAGS_ID);
      this.entityData.set(DATA_MOB_FLAGS_ID, p_184641_1_ ? (byte)(b0 | 2) : (byte)(b0 & -3));
   }

   public void setAggressive(boolean p_213395_1_) {
      byte b0 = this.entityData.get(DATA_MOB_FLAGS_ID);
      this.entityData.set(DATA_MOB_FLAGS_ID, p_213395_1_ ? (byte)(b0 | 4) : (byte)(b0 & -5));
   }

   public boolean isNoAi() {
      return (this.entityData.get(DATA_MOB_FLAGS_ID) & 1) != 0;
   }

   public boolean isLeftHanded() {
      return (this.entityData.get(DATA_MOB_FLAGS_ID) & 2) != 0;
   }

   public boolean isAggressive() {
      return (this.entityData.get(DATA_MOB_FLAGS_ID) & 4) != 0;
   }

   public void setBaby(boolean p_82227_1_) {
   }

   public HandSide getMainArm() {
      return this.isLeftHanded() ? HandSide.LEFT : HandSide.RIGHT;
   }

   public boolean canAttack(LivingEntity p_213336_1_) {
      return p_213336_1_.getType() == EntityType.PLAYER && ((PlayerEntity)p_213336_1_).abilities.invulnerable ? false : super.canAttack(p_213336_1_);
   }

   public boolean doHurtTarget(Entity target) {
      float attackDamage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
      float attackKnockback = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
      if (target instanceof LivingEntity) {
         attackDamage += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)target).getMobType());
         attackKnockback += (float)EnchantmentHelper.getKnockbackBonus(this);
      }

      int fireAspectLevel = EnchantmentHelper.getFireAspect(this);
      if (fireAspectLevel > 0) {
         target.setSecondsOnFire(fireAspectLevel * 4);
      }

      boolean didHurt = target.hurt(DamageSource.mobAttack(this), attackDamage);
      if (didHurt) {
         if (attackKnockback > 0.0F && target instanceof LivingEntity) {
            ((LivingEntity)target).knockback(attackKnockback * 0.5F, (double)MathHelper.sin(this.yRot * ((float)Math.PI / 180F)), (double)(-MathHelper.cos(this.yRot * ((float)Math.PI / 180F))));
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
         }

         if (target instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)target;
            this.maybeDisableShield(playerentity, this.getMainHandItem(), playerentity.isUsingItem() ? playerentity.getUseItem() : ItemStack.EMPTY);
         }

         this.doEnchantDamageEffects(this, target);
         this.setLastHurtMob(target);

         if (this.getMainHandItem().getItem() == Items.DESOLATE_DAGGER) {
            ItemStack stack = this.getMainHandItem();

            int delayedLevel = stack.getEnchantmentLevel(Enchantments.IMPENDING_STAB);
            int doubleStab = stack.getEnchantmentLevel(Enchantments.DOUBLE_STAB);

            for(int i = 0; i < 1 + doubleStab; i++){
               DesolateDaggerEntity daggerEntity = EntityType.DESOLATE_DAGGER.create(this.level());
               daggerEntity.setTargetId(target.getId());
               daggerEntity.copyPosition(this);
               daggerEntity.setItemStack(stack);
               daggerEntity.entity = this;
               daggerEntity.orbitFor = (delayedLevel > 0 ? 40 : 20) + this.getRandom().nextInt(10) + (doubleStab != 0  && i != 0 ? 8 + 8 * (i == 1 ? 0 : i) : 0);
               this.level().addFreshEntity(daggerEntity);
            }
         }
      }

      return didHurt;
   }

   private void maybeDisableShield(PlayerEntity player, ItemStack axeItemStack, ItemStack shieldItemStack) {
      boolean b = this.canDisableShield();
      if (((!axeItemStack.isEmpty() && axeItemStack.getItem() instanceof AxeItem) || b) && !shieldItemStack.isEmpty()) {
         if (shieldItemStack.getItem() == Items.SHIELD) {
            float chance = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(player) * 0.05F;
            if (this.random.nextFloat() < chance || b) {
               player.getCooldowns().addCooldown(Items.SHIELD, 100);
               this.level.broadcastEntityEvent(player, (byte)30);
            }
         } else if (shieldItemStack.getItem() == Items.NETHERITE_SHIELD) { // Replace MyModItems with your actual mod item registry class
            float chance = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(player) * 0.05F;
            if (this.random.nextFloat() < chance || b) {
               player.getCooldowns().addCooldown(Items.NETHERITE_SHIELD, 20); // Cooldown for 30 ticks for netherite shield
               this.level.broadcastEntityEvent(player, (byte)30);
            }
         } else if (shieldItemStack.getItem() == Items.SHIELD_OF_CTHULHU) { // Replace MyModItems with your actual mod item registry class
            float chance = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(player) * 0.05F;
            if (this.random.nextFloat() < chance || b) {
               player.getCooldowns().addCooldown(Items.SHIELD_OF_CTHULHU, 20); // Cooldown for 30 ticks for netherite shield
               this.level.broadcastEntityEvent(player, (byte)30);
            }
         }
      }
   }

   protected boolean isSunBurnTick() {

      if (this.level.isDay() && !this.level.isClientSide) {
         float f = this.getBrightness();
         BlockPos blockpos = this.getVehicle() instanceof BoatEntity ? (new BlockPos(this.getX(), (double)Math.round(this.getY()), this.getZ())).above() : new BlockPos(this.getX(), (double)Math.round(this.getY()), this.getZ());
         if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.level.canSeeSky(blockpos)) {
            return true;
         }
      }

      return false;
   }

   protected void jumpInLiquid(ITag<Fluid> p_180466_1_) {
      if (this.getNavigation().canFloat()) {
         super.jumpInLiquid(p_180466_1_);
      } else {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.3D, 0.0D));
      }

   }

   protected void removeAfterChangingDimensions() {
      super.removeAfterChangingDimensions();
      this.dropLeash();
   }

   public Orientation getOrientation() {
      // The entity's normal (up) vector, typically pointing upwards (0, 1, 0)
      Vector3d normal = new Vector3d(0.0D, 1.0D, 0.0D);

      // The local forward vector (localZ) aligned with the entity's current look direction
      float yaw = this.yRot; // Get entity's yaw
      float pitch = this.xRot; // Get entity's pitch

      // Calculate the direction vectors based on the entity's yaw and pitch
      float yawRad = yaw * 0.017453292F; // Convert to radians
      float pitchRad = pitch * 0.017453292F; // Convert to radians

      // Forward (localZ) is based on yaw and pitch
      Vector3d localZ = new Vector3d(-MathHelper.sin(yawRad) * MathHelper.cos(pitchRad),
              -MathHelper.sin(pitchRad),
              MathHelper.cos(yawRad) * MathHelper.cos(pitchRad));

      // Right (localX) is perpendicular to forward
      Vector3d localX = new Vector3d(MathHelper.cos(yawRad), 0.0D, MathHelper.sin(yawRad));

      // Up (localY) is typically just the world up vector (0, 1, 0)
      Vector3d localY = normal;

      // Components along local axes, typically 1 when entity is standing normally
      float componentZ = 1.0F;
      float componentY = 1.0F;
      float componentX = 1.0F;

      // Return the Orientation object
      return new Orientation(normal, localZ, localY, localX, componentZ, componentY, componentX, yaw, pitch);
   }

}
