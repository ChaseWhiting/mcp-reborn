package net.minecraft.entity.monster;

import net.minecraft.bundle.BundleItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.bogged.BoggedEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

public class GreatHungerEntity extends Monster {
	private static final int MAXIMUM_STORED_EXPERIENCE_POINTS = 1695;
	private static final DataParameter<Boolean> ABOUT_TO_SWALLOW_ITEM = EntityDataManager.defineId(GreatHungerEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> DIGGING = EntityDataManager.defineId(GreatHungerEntity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> SWALLOWED_ITEM_COUNT = EntityDataManager.defineId(GreatHungerEntity.class, DataSerializers.INT);
	private static final DataParameter<Integer> SWALLOWED_ENTITY_COUNT = EntityDataManager.defineId(GreatHungerEntity.class, DataSerializers.INT);
	private static final DataParameter<Integer> SAVED_XP = EntityDataManager.defineId(GreatHungerEntity.class, DataSerializers.INT);
	private static final DataParameter<Integer> DATA_STATE = EntityDataManager.defineId(GreatHungerEntity.class, DataSerializers.INT);
	private float clientSideSwallowTicks = 0.0F;
	private float clientSideGrowProgress = 0.0F;
	@OnlyIn(Dist.CLIENT)
	public float mouthOpenTicks = 0;

	// Entity swallowing data
	public final List<CompoundNBT> swallowedEntitiesNBT = new ArrayList<>();

	@OnlyIn(Dist.CLIENT)
	public float getClientSideSwallowTicks() {
		return this.clientSideSwallowTicks;
	}

	public static boolean checkSpawnRules(EntityType<GreatHungerEntity> type, IServerWorld world, SpawnReason reason, BlockPos pos, Random random) {
		return checkMonsterSpawnRules(type, world, reason, pos, random) && pos.getY() >= 55 || world.getLevel().isDay() && pos.getY() >= 55;
	}

	@OnlyIn(Dist.CLIENT)
	public void setClientSideSwallowTicks(float progress) {
		this.clientSideSwallowTicks = progress;
	}

	@OnlyIn(Dist.CLIENT)
	public float getClientSideGrowProgress() {
		return clientSideGrowProgress;
	}

	@OnlyIn(Dist.CLIENT)
	public void setClientSideGrowProgress(float clientSideGrowProgress) {
		this.clientSideGrowProgress = clientSideGrowProgress;
	}

	public GreatHungerEntity(EntityType<? extends Monster> type, World world) {
		super(type, world);
		this.xpReward = 0;
		this.maxUpStep = 1.2F;
	}

	public void setDigging(boolean digging) {
		this.entityData.set(DIGGING, digging);
	}

	public boolean isDigging() {
		return this.entityData.get(DIGGING);
	}

	public final Set<ItemEntity> swallowedItems = new HashSet<>();

	public Set<ItemEntity> getSwallowedItems() {
		return this.swallowedItems;
	}

	public int getSwallowedItemCount() {
		return Math.min(getExperienceAccumulation() > 0 ? 300 : 200, Math.max(0, this.entityData.get(SWALLOWED_ITEM_COUNT) + this.getSwallowedEntityCount() + this.getExperienceAccumulation()));
	}


	public boolean canActivelyFightPlayer() {
		return getSwallowedItemCount() >= 30;
	}

	public void updateExperience(int xp) {
		setStoredExperiencePoints(this.getStoredExperiencePoints() + xp / 5);
	}

	public int getSwallowedEntityCount() {
		return Math.min(64, Math.max(0, this.entityData.get(SWALLOWED_ENTITY_COUNT)));
	}

	public boolean canSwallow(Entity entity) {
		// Ensure it is a living entity and not too large
		if (entity instanceof PlayerEntity) return false;

		if (entity instanceof ItemEntity) {
			if (entity.as(ItemEntity.class).isEdible() == false) return false;
		}

		float width = this.getBbWidth();
		float height = this.getBbHeight();

		// Check the entity's size relative to this entity
		return entity.getBbHeight() <= height - 0.2 && entity.getBbWidth() < width - 0.2 || entity instanceof RabbitEntity || entity instanceof ChickenEntity || entity instanceof SilverfishEntity;
	}

	public State getCurrentState() {
		return State.byId(this.entityData.get(DATA_STATE));
	}

	public void setState(State state) {
		this.entityData.set(DATA_STATE, state.getId());
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ABOUT_TO_SWALLOW_ITEM, false);
		this.entityData.define(DATA_STATE, State.IDLE.getId());
		this.entityData.define(DIGGING, false);
		this.entityData.define(SWALLOWED_ITEM_COUNT, 0);  // Initialize with 0
		this.entityData.define(SWALLOWED_ENTITY_COUNT, 0);
		this.entityData.define(SAVED_XP, 0);
	}

	public int getStoredExperiencePoints() {
		return entityData.get(SAVED_XP);
	}

	public int getExperienceAccumulation() {
		return 2 * (this.getStoredExperiencePoints() / 4);
	}

	public void setStoredExperiencePoints(int xp) {
		// If XP is increasing and exceeds the maximum, clamp it
		if (xp > this.getStoredExperiencePoints() && xp >= MAXIMUM_STORED_EXPERIENCE_POINTS) {
			xp = MAXIMUM_STORED_EXPERIENCE_POINTS;
		}
		this.entityData.set(SAVED_XP, xp);
	}

	public boolean isAboutToSwallowItem() {
		return this.entityData.get(ABOUT_TO_SWALLOW_ITEM) || this.entityData.get(DATA_STATE) == State.SWALLOW_ITEM.getId();
	}

	public void updateSwallowedItemCount() {
		this.entityData.set(SWALLOWED_ITEM_COUNT, this.swallowedItems.size());
	}


	public void updateSwallowedEntityCount(int add) {
		this.entityData.set(SWALLOWED_ENTITY_COUNT, this.entityData.get(SWALLOWED_ENTITY_COUNT) + add);
	}

	public void setAboutToSwallowItem(boolean val) {
		this.entityData.set(ABOUT_TO_SWALLOW_ITEM, val);
		if (val) {
			this.entityData.set(DATA_STATE, State.SWALLOW_ITEM.getId());
		} else {
			this.entityData.set(DATA_STATE, State.IDLE.getId());
		}
	}

	@Override
	public boolean requiresCustomPersistence() {
		// If the entity has swallowed items or entities, it should not despawn
		return super.requiresCustomPersistence() || !this.swallowedItems.isEmpty() || !this.swallowedEntitiesNBT.isEmpty();
	}

	@Override
	public boolean removeWhenFarAway(double distanceToPlayer) {
		// If the entity has swallowed any items or entities, do not remove it
		return !this.requiresCustomPersistence() && super.removeWhenFarAway(distanceToPlayer);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.xpReward = this.getExperienceAccumulation();
		if (this.getSwallowedItemCount() > 0 || this.getStoredExperiencePoints() > 0) {
			// Set the attack damage, but limit it to a maximum of 35
			double attackDamage = 2 + 0.03 * this.getSwallowedItemCount();
			this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(Math.min(attackDamage, 35));

			// Set the movement speed, but limit it to a maximum of 1
			double movementSpeed = 0.15 + 0.001 * this.getSwallowedItemCount();
			this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(Math.min(movementSpeed, 1));

			// Separate base health and bonus health based on swallowed items
			double bonusArmour = 3D + 0.002 * this.getSwallowedItemCount();
			this.getAttribute(Attributes.ARMOR).setBaseValue(Math.min(bonusArmour, 30));
			this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(Math.min((bonusArmour - 3 + 6) / 4, 20));
		}
	}

	private void spitOutItem(ItemEntity itementity) {
		this.setAboutToSwallowItem(true);
		if (!itementity.getItem().isEmpty() && !this.level.isClientSide) {
			this.damage(itementity, 0.20, 0.25);

			itementity.setPos(this.getX(), this.getY() + 0.8D, this.getZ());
			itementity.setPickUpDelay(TickRangeConverter.rangeOfTicks(10, 40).randomValue(random));
			itementity.setThrower(this.getUUID());
			itementity.setEdible(false);
			itementity.removed = false;
			this.heal(itementity.getItem().isDamageableItem() ? 3F : 1F);
			this.playSound(SoundEvents.FOX_SPIT, 1.0F, this.getVoicePitch());
			this.level.addFreshEntity(itementity);
			if (this.swallowedItems.contains(itementity)) {
				swallowedItems.remove(itementity);
			}
			this.updateSwallowedItemCount();
		}
		this.setAboutToSwallowItem(false);
	}

	public void add(ItemEntity entity) {
		this.spawnItem(entity.getItem(), 0, 0.1F + 0.003 * this.getSwallowedItemCount());
	}


	@Nullable
	public ItemEntity spawnItem(ItemStack item, float raise, double deltaMod) {
		if (item.isEmpty()) {
			return null;
		} else if (this.level.isClientSide) {
			return null;
		} else {
			ItemEntity itementity = new ItemEntity(this.level, this.getX(), this.getY() + (double) raise, this.getZ(), item, deltaMod);
			itementity.setDefaultPickUpDelay();
			this.level.addFreshEntity(itementity);
			return itementity;
		}
	}

	public EntitySize getDimensions(Pose pose) {
		float minScaleWidth = 0.65F;  // Minimum width scale for the entity
		float minScaleHeight = 0.62F; // Minimum height scale for the entity

		if (this.isDigging()) {
			// If digging, we shrink to a very small size
			return super.getDimensions(pose).scale(0.0005F);
		} else {
			int swallowedItemCount = this.getSwallowedItemCount();
			if (swallowedItemCount > 0) {
				// Ensure the size does not drop below the minimum size, while scaling with swallowed items
				float scaleFactor = 1.0F + (0.014F * swallowedItemCount);  // Increase size based on swallowed items
				float scaledWidth = Math.max(minScaleWidth, minScaleWidth * scaleFactor);   // Apply minimum width
				float scaledHeight = Math.max(minScaleHeight, minScaleHeight * scaleFactor); // Apply minimum height

				// Apply the scaled width and height
				return EntitySize.scalable(scaledWidth, scaledHeight);
			}
		}

		// Default case, return the base size
		return super.getDimensions(pose);
	}

	public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
		if (DIGGING.equals(p_184206_1_) || SWALLOWED_ITEM_COUNT.equals(p_184206_1_) || SAVED_XP.equals(p_184206_1_)) {
			this.refreshDimensions();
		}

		super.onSyncedDataUpdated(p_184206_1_);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 1D));
		this.goalSelector.addGoal(1, new SwimGoal(this));
		this.targetSelector.addGoal(1, new AttackGoal(this, 1.4F, true));
		this.goalSelector.addGoal(1, new BetterLeapAtTargetGoal<>(this, 0.36F));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, false, false, player -> {
			return this.canActivelyFightPlayer();
		}));
		this.goalSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers(GreatHungerEntity.class));
		this.goalSelector.addGoal(1, new FindItemToSwallowGoal(this));
		this.goalSelector.addGoal(2, new FindEntityToSwallowGoal(this));  // Entity swallowing goal added
	}

	public static class AttackGoal extends MeleeAttackGoal {
		private final GreatHungerEntity mob;
		private final float speed;

		public AttackGoal(GreatHungerEntity entity, float speed, boolean followTargetEvenIfNotSeen) {
			super(entity, speed, followTargetEvenIfNotSeen);
			this.mob = entity;
			this.speed = speed;
		}

		public void start() {
			super.start();
			if (this.mob.getTarget() != null) {
				this.mob.getMoveControl().setWantedPosition(this.mob.getTarget().blockPosition(), speed);
			}
		}

		public boolean canContinueToUse() {
			return super.canContinueToUse();
		}

		protected double getAttackReachSqr(LivingEntity p_179512_1_) {
			return (double)(this.mob.getBbWidth() * 1.6F * this.mob.getBbWidth() * 1.6F + p_179512_1_.getBbWidth());
		}

		protected void checkAndPerformAttack(LivingEntity p_190102_1_, double p_190102_2_) {
			super.checkAndPerformAttack(p_190102_1_, p_190102_2_);
		}

		public void stop() {
			super.stop();
			this.mob.setState(State.IDLE);
		}

		public void tick() {
			super.tick();
			if (this.mob.getTarget() != null) {
				this.mob.getLookControl().setLookAt(this.mob.getTarget().blockPosition().offset(0, 1.2, 0).asVector());
				// Ensure the mob's state is set to FIGHT when it has a target
				this.mob.setState(GreatHungerEntity.State.FIGHT);
			}
		}
	}

	public boolean swallowItem(ItemEntity entity) {
		if (!this.canSwallow(entity)) {
			return false;
		}
		if (this.swallowedItems.contains(entity)) {
			return false;
		}

		if (entity.getItem().getItem() instanceof BundleItem) {
			BundleItem.dropAllContents(entity, BundleItem.getContents(entity.getItem()));
		}

		if (this.swallowedItems.add(entity)) {
			if (entity.getItem().isEnchanted()) {
				updateExperience(this.getExperienceFromItem(entity.getItem()));
			}
			entity.setItem(this.removeEnchantments(entity.getItem()));
			entity.remove();
			this.updateSwallowedItemCount();
			return true;
		}

		return false;
	}

	// Swallow entity method
	public boolean swallowEntity(LivingEntity entity) {
		if (!this.canSwallow(entity)) {
			return false;
		}

		// Store the entity's NBT data
		CompoundNBT entityNBT = new CompoundNBT();
		entity.save(entityNBT);

		// Prevent duplicate swallowing
		if (this.swallowedEntitiesNBT.contains(entityNBT)) {
			return false;
		}

		this.swallowedEntitiesNBT.add(entityNBT);
		if (entity instanceof Mob && entity.as(Mob.class).xpReward > 0) {
			this.setStoredExperiencePoints(this.getStoredExperiencePoints() + entity.as(Mob.class).xpReward);
			entity.as(Mob.class).xpReward = 0;
		}
		entity.remove();
		this.updateSwallowedItemCount();
		this.updateSwallowedEntityCount(5);
		return true;
	}

	private ItemStack removeEnchantments(ItemStack stack) {
		ItemStack itemstack = stack.copy();
		itemstack.removeTagKey("Enchantments");
		itemstack.removeTagKey("StoredEnchantments");
		return itemstack;
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT nbt) {
		super.addAdditionalSaveData(nbt);
		ListNBT swallowedItemsList = new ListNBT();

		for (ItemEntity itemEntity : this.swallowedItems) {
			CompoundNBT entityNBT = new CompoundNBT();
			entityNBT.put("ItemStack", itemEntity.getItem().save(new CompoundNBT()));
			swallowedItemsList.add(entityNBT);
		}
		nbt.put("SwallowedItems", swallowedItemsList);
		nbt.putInt("State", this.getCurrentState().getId());
		nbt.putBoolean("AboutToSwallowItem", this.isAboutToSwallowItem());
		nbt.putBoolean("Digging", this.isDigging());
		nbt.putInt("SwallowedItemCount", this.getSwallowedItemCount());
		nbt.putInt("SwallowedEntityCount", this.getSwallowedEntityCount());

		nbt.putInt("SavedXPPoints", this.getStoredExperiencePoints());

		// Save swallowed entities
		ListNBT swallowedEntitiesList = new ListNBT();
		for (CompoundNBT entityNBT : this.swallowedEntitiesNBT) {
			swallowedEntitiesList.add(entityNBT);
		}
		nbt.put("SwallowedEntities", swallowedEntitiesList);
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT nbt) {
		super.readAdditionalSaveData(nbt);
		this.swallowedItems.clear();
		ListNBT swallowedItemsList = nbt.getList("SwallowedItems", 10);
		for (int i = 0; i < swallowedItemsList.size(); i++) {
			CompoundNBT entityNBT = swallowedItemsList.getCompound(i);
			ItemStack itemStack = ItemStack.of(entityNBT.getCompound("ItemStack"));
			ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), itemStack);

			this.swallowedItems.add(itemEntity);
		}
		this.setState(State.byId(nbt.getInt("State")));
		if (nbt.contains("Digging")) {
			this.setDigging(nbt.getBoolean("Digging"));
		}
		if (nbt.contains("AboutToSwallowItem")) {
			this.setAboutToSwallowItem(nbt.getBoolean("AboutToSwallowItem"));
		}

		if (nbt.contains("SwallowedItemCount")) {
			this.entityData.set(SWALLOWED_ITEM_COUNT, nbt.getInt("SwallowedItemCount"));
		}
		if (nbt.contains("SwallowedEntityCount")) {
			this.entityData.set(SWALLOWED_ENTITY_COUNT, nbt.getInt("SwallowedEntityCount"));
		}
		this.setStoredExperiencePoints(nbt.getInt("SavedXPPoints"));

		// Load swallowed entities
		this.swallowedEntitiesNBT.clear();
		ListNBT swallowedEntitiesList = nbt.getList("SwallowedEntities", 10);
		for (int i = 0; i < swallowedEntitiesList.size(); i++) {
			CompoundNBT entityNBT = swallowedEntitiesList.getCompound(i);
			this.swallowedEntitiesNBT.add(entityNBT);
		}
	}

	private int getExperienceFromItem(ItemStack stack) {
		int l = 0;
		Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);

		for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
			Enchantment enchantment = entry.getKey();
			Integer integer = entry.getValue();
			if (!enchantment.isCurse()) {
				l += enchantment.getMinCost(integer);
			}
		}

		return l;
	}

	public boolean extraHealth() {
		return false;
	}

	public static AttributeModifierMap.MutableAttribute createMonsterAttributes() {
		return Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE, 2).add(Attributes.ARMOR_TOUGHNESS, 3D).add(Attributes.ARMOR, 6D).add(Attributes.MAX_HEALTH, 16).add(Attributes.FOLLOW_RANGE, 20).add(Attributes.MOVEMENT_SPEED, 0.15D).add(Attributes.KNOCKBACK_RESISTANCE, 0.12D);
	}

	@Override
	public boolean isInWall() {
		return this.isDigging();
	}

	public boolean isInvulnerableTo(DamageSource source) {
		if (source == DamageSource.IN_WALL) return true;

		return super.isInvulnerableTo(source);
	}

	public void tick() {
		super.tick();
		if (this.tick(this.getCurrentState() == State.FIGHT ? 30 : 80) && (this.getHealth() < this.getMaxHealth() || this.getCurrentState() == State.FIGHT && this.random.nextFloat() < 0.25F)) {
			List<ItemEntity> itemList = new ArrayList<>(swallowedItems);
			if (!itemList.isEmpty()) {
				ItemEntity randomItem = itemList.get(random.nextInt(itemList.size()));
				this.spitOutItem(randomItem);
			}
		}
	}

	public void damage(ItemEntity itementity, double min, double max) {
		ItemStack itemStack = itementity.getItem();
		// Check if the item is damageable
		if (itemStack.isDamageableItem()) {
			int currentDamage = itemStack.getDamageValue();
			int maxDamage = itemStack.getMaxDamage();

			// Calculate the current remaining durability
			int remainingDurability = maxDamage - currentDamage;

			// Calculate a random reduction between 20% and 45% of the remaining durability
			double reductionPercentage = min + (random.nextDouble() * max);
			int durabilityReduction = (int) (remainingDurability * reductionPercentage);

			// Calculate the new remaining durability
			int newRemainingDurability = remainingDurability - durabilityReduction;

			// Set the new damage value (maxDamage - newRemainingDurability)
			int newDamage = maxDamage - newRemainingDurability;

			// Ensure the damage value is not negative (0 is fully damaged)
			itemStack.setDamageValue(Math.min(newDamage, maxDamage));
			itementity.setItem(itemStack);
		}
	}

	public boolean doHurtTarget(Entity entity) {
		if (super.doHurtTarget(entity)) {
			if (entity instanceof PlayerEntity) {
				PlayerEntity player = entity.asPlayer();
				ItemStack item = player.inventory.takeRandomItemAndRemove(random);
				if (item != null) {
					 this.swallowItem(new ItemEntity(level, this.getX(),this.getY(),this.getZ(), item));
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public void die(DamageSource source) {
		super.die(source);
		swallowedItems.forEach(entity -> {
			if (this.level.isServerSide) {
				entity.removed = false;
				this.add(entity);
			}
		});
		swallowedItems.clear();
		this.updateSwallowedItemCount();

		for (CompoundNBT entityNBT : this.swallowedEntitiesNBT) {
			Entity entity = EntityType.loadEntityRecursive(entityNBT, this.level, (entityIn) -> {
				if (entityIn instanceof Mob) {
					entityIn.as(Mob.class).xpReward = 0;
				}
				if (entityIn instanceof IAngerable) {
					((IAngerable)entityIn).startPersistentAngerTimer();
				}

				// Randomize the position slightly around the death position to avoid cramming
				double offsetX = (random.nextDouble() - 0.5) * 2.0; // Random value between -1 and 1
				double offsetY = 0.3; // Keep entities slightly above the ground
				double offsetZ = (random.nextDouble() - 0.5) * 2.0;

				entityIn.moveTo(this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ, this.yRot, this.xRot);
				return entityIn;
			});

			if (entity != null) {
				entity.removed = false;
				this.level.addFreshEntity(entity);
			}
		}
		this.swallowedEntitiesNBT.clear();
		this.updateSwallowedEntityCount(0);
	}

	public void playSwallowItemSound() {
		this.playSound(SoundEvents.FOX_BITE, 1.0F, 1.2F);
	}

	protected void playHurtSound(DamageSource p_184581_1_) {
		this.resetAmbientSoundTime();
		SoundEvent soundevent = this.getHurtSound(p_184581_1_);
		if (soundevent != null) {
			this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch() - 0.37F);
		}
	}

	public SoundEvent getDeathSound() {
		return SoundEvents.FOX_AGGRO;
	}

	public SoundEvent getAmbientSound() {
		return SoundEvents.FOX_AGGRO;
	}

	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.FOX_BITE;
	}

	public float getVoicePitch() {
		return super.getVoicePitch() + 0.6F;
	}

	class FindItemToSwallowGoal extends Goal {
		private final GreatHungerEntity mob;
		private final World level;
		private final float speedModifier = 0.85F;
		private ItemEntity itemToSwallow = null;

		public FindItemToSwallowGoal(GreatHungerEntity hungerEntity) {
			this.mob = hungerEntity;
			this.level = hungerEntity.level;
			this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET, Flag.JUMP));
		}

		@Override
		public boolean canUse() {
			if (this.mob.getTarget() != null) return false;
			List<ItemEntity> nearbyItems = level.getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(12 + 0.03 * this.mob.getSwallowedItemCount(), 3.5 + 0.3 * this.mob.getSwallowedItemCount(), 12 + 0.03 * this.mob.getSwallowedItemCount()), itemEntity -> itemEntity.getItem() != null && itemEntity.getPickupDelay() < 80 && itemEntity.isAlive() && this.mob.canSee(itemEntity) && itemEntity.isEdible());
			if (nearbyItems.isEmpty()) {
				return false;
			} else {
				itemToSwallow = nearbyItems.get(mob.random.nextInt(nearbyItems.size()));
				return true;
			}
		}

		@Override
		public boolean canContinueToUse() {
			return itemToSwallow != null && itemToSwallow.isAlive() && this.mob.getTarget() == null;
		}

		@Override
		public void tick() {
			checkDistance();
			if (itemToSwallow != null && itemToSwallow.isAlive()) {
				this.mob.getMoveControl().setWantedPosition(itemToSwallow.getX(), itemToSwallow.getY(), itemToSwallow.getZ(), speedModifier);
				this.mob.getLookControl().setLookAt(itemToSwallow.blockPosition().offset(0, 0.5, 0).asVector());
			}
		}

		public void checkDistance() {
			if (itemToSwallow == null || !itemToSwallow.isAlive()) {
				stop();
				return;
			}

			double distance = this.mob.distanceTo(itemToSwallow);

			if (distance < 1 + 0.07 * mob.getSwallowedItemCount()) {
				if (!this.mob.isAboutToSwallowItem()) {
					this.mob.setAboutToSwallowItem(true);
				}
			} else {
				if (this.mob.isAboutToSwallowItem()) {
					this.mob.setAboutToSwallowItem(false);
				}
			}

			if (distance < 0.3F + 0.01F * mob.getSwallowedItemCount() && level.isServerSide) {
				if (this.mob.swallowItem(itemToSwallow)) {
					this.mob.playSwallowItemSound();
					this.mob.setAboutToSwallowItem(false);
					itemToSwallow = null;
					stop();
				}
			}
		}

		@Override
		public void start() {
			super.start();
			if (itemToSwallow != null && itemToSwallow.isAlive()) {
				this.mob.getMoveControl().setWantedPosition(itemToSwallow.getX(), itemToSwallow.getY(), itemToSwallow.getZ(), speedModifier);
			}
		}

		@Override
		public void stop() {
			super.stop();
			itemToSwallow = null;
			this.mob.setAboutToSwallowItem(false);
		}
	}

	class FindEntityToSwallowGoal extends Goal {
		private final GreatHungerEntity mob;
		private final World level;
		private final float speedModifier = 0.85F;
		private LivingEntity entityToSwallow = null;

		public FindEntityToSwallowGoal(GreatHungerEntity hungerEntity) {
			this.mob = hungerEntity;
			this.level = hungerEntity.level;
			this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET, Flag.JUMP));
		}

		@Override
		public boolean canUse() {
			if (this.mob.getTarget() != null) return false;
			List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, this.mob.getBoundingBox().inflate(12 + 0.03 * this.mob.getSwallowedItemCount(), 3.5 + 0.3 * this.mob.getSwallowedItemCount(), 12 + 0.03 * this.mob.getSwallowedItemCount()), entity -> this.mob.canSwallow(entity) && this.mob.canSee(entity));
			if (nearbyEntities.isEmpty()) {
				return false;
			} else {
				entityToSwallow = nearbyEntities.get(mob.random.nextInt(nearbyEntities.size()));
				return true;
			}
		}

		@Override
		public boolean canContinueToUse() {
			return entityToSwallow != null && entityToSwallow.isAlive() && this.mob.getTarget() == null;
		}

		@Override
		public void tick() {
			checkDistance();
			if (entityToSwallow != null && entityToSwallow.isAlive()) {
				this.mob.getMoveControl().setWantedPosition(entityToSwallow.getX(), entityToSwallow.getY(), entityToSwallow.getZ(), speedModifier);
				this.mob.getLookControl().setLookAt(entityToSwallow.blockPosition().offset(0, 0.5, 0).asVector());
			}
		}

		public void checkDistance() {
			if (entityToSwallow == null || !entityToSwallow.isAlive()) {
				stop();
				return;
			}

			double distance = this.mob.distanceTo(entityToSwallow);

			if (distance < 2 + 0.07 * mob.getSwallowedItemCount()) {
				if (!this.mob.isAboutToSwallowItem()) {
					this.mob.setAboutToSwallowItem(true);
				}
			} else {
				if (this.mob.isAboutToSwallowItem()) {
					this.mob.setAboutToSwallowItem(false);
				}
			}

			if (distance < 0.6F + 0.01F * mob.getSwallowedItemCount()) {
				if (this.mob.swallowEntity(entityToSwallow)) {
					this.mob.playSwallowItemSound();
					this.mob.setAboutToSwallowItem(false);
					entityToSwallow = null;
					stop();
				}
			}
		}

		@Override
		public void start() {
			super.start();
			if (entityToSwallow != null && entityToSwallow.isAlive()) {
				this.mob.getMoveControl().setWantedPosition(entityToSwallow.getX(), entityToSwallow.getY(), entityToSwallow.getZ(), speedModifier);
			}
		}

		@Override
		public void stop() {
			super.stop();
			entityToSwallow = null;
			this.mob.setAboutToSwallowItem(false);
		}
	}

	public enum State {
		TRAP(0),
		SWALLOW_ITEM(1),
		IDLE(2),
		SPIT_ITEM(4),
		FIGHT(3);

		private final int id;

		private State(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public static State byId(int id) {
			return switch (id) {
				case 0 -> TRAP;
				case 1 -> SWALLOW_ITEM;
				case 2 -> IDLE;
				case 3 -> FIGHT;
				case 4 -> SPIT_ITEM;
				default -> IDLE;
			};
		}
	}






	private boolean tryToInteractWithEnhancedItem(ItemStack itemStack) {
		int storedExperiencePoints = this.getStoredExperiencePoints();

		if (storedExperiencePoints >= MAXIMUM_STORED_EXPERIENCE_POINTS) {
			return false;
		}

		int experiencePoints = this.getExperienceFromItem(itemStack);
		int recalculatedExperiencePoints = storedExperiencePoints + experiencePoints;

		if (recalculatedExperiencePoints > MAXIMUM_STORED_EXPERIENCE_POINTS) {
			recalculatedExperiencePoints = MAXIMUM_STORED_EXPERIENCE_POINTS;
		}

		this.setStoredExperiencePoints(recalculatedExperiencePoints);

		itemStack.removeTagKey("Enchantments");
		itemStack.removeTagKey("StoredEnchantments");

		this.playSound(SoundEvents.FOX_BITE, 0.5F, this.getVoicePitch() - 0.32F);
		this.spawnParticles(ParticleTypes.ENCHANT, 7);

		return true;
	}

	public void spawnParticles(IParticleData particleEffect, int amount) {
		World world = this.getWorld();

		if (world.isClientSide) {
			return;
		}

		for (int i = 0; i < amount; i++) {

				world.addParticle(
						particleEffect,               // IParticleData
						this.getX(1.0D),              // X coordinate
						this.getY() + 0.5D,           // Y coordinate
						this.getZ(1.0D),              // Z coordinate
						this.getRandom().nextGaussian() * 0.02D,  // X velocity
						this.getRandom().nextGaussian() * 0.02D,  // Y velocity
						this.getRandom().nextGaussian() * 0.02D // Z velocity

				);

		}
	}


	private boolean tryToInteractWithGlassBottle(
			PlayerEntity player,
			ItemStack itemStack
	) {

		int storedExperiencePoints = this.getStoredExperiencePoints();

		if (storedExperiencePoints < 7) {
			return false;
		}

		int glassBottlesCount = itemStack.getCount();
		int experienceBottleCount = storedExperiencePoints / 7;

		if (experienceBottleCount > glassBottlesCount) {
			experienceBottleCount = glassBottlesCount;
		}

		itemStack.shrink(experienceBottleCount);
		ItemStack experienceBottleItemStack = new ItemStack(Items.EXPERIENCE_BOTTLE, experienceBottleCount);
		if (!player.addItem(experienceBottleItemStack)) {
			player.drop(experienceBottleItemStack, true);
		}

		this.setStoredExperiencePoints(storedExperiencePoints - experienceBottleCount * 7);
		this.playSound(SoundEvents.BOTTLE_FILL_DRAGONBREATH, 1.0F, 1.0F);

		return true;
	}

	public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
		if (this.removed || this.isDeadOrDying()) {
			return ActionResultType.FAIL;
		}
		ItemStack itemstack = player.getItemInHand(hand);
		if (itemstack.getItem() == Items.GLASS_BOTTLE) {
			if (this.tryToInteractWithGlassBottle(player, itemstack)) {
				player.swing(hand);
				return ActionResultType.CONSUME;
			}
		} else if (itemstack.isEnchanted()) {
			if (this.tryToInteractWithEnhancedItem(itemstack)) {
				player.swing(hand);
				return ActionResultType.sidedSuccess(this.level.isClientSide);
			}
		}

		return super.mobInteract(player, hand);
	}
}
