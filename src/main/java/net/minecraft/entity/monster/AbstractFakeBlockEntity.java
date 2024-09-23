package net.minecraft.entity.monster;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractFakeBlockEntity extends Monster {
	protected AbstractFakeBlockEntity(EntityType<? extends Monster> type, World world) {
		super(type, world);
		this.xpReward = 0;
		this.setNoGravity(true);  // Ensure it doesn't fall like a regular entity.
		this.jumpControl = null;
		this.blocksBuilding = true;
	}

	public static AttributeModifierMap createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MOVEMENT_SPEED, 0.3)
				.add(Attributes.MAX_HEALTH, 13.0)
				.add(Attributes.ARMOR, 0.0)
				.add(Attributes.ATTACK_DAMAGE, 4.0)
				.add(Attributes.KNOCKBACK_RESISTANCE, 1)
				.add(Attributes.FOLLOW_RANGE, 16.0).build();
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
			@Override
			protected double getAttackReachSqr(LivingEntity entity) {
				return 1;
			}
		});
		this.goalSelector.addGoal(2, new HurtByTargetGoal(this));
		//this.goalSelector.addGoal(3, new RandomWalkingGoal(this, 0.8));
		this.goalSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return SoundEvents.STONE_HIT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.STONE_BREAK;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.isProjectile() || source == DamageSource.FALL || source == DamageSource.DROWN
				|| source == DamageSource.LIGHTNING_BOLT || source == DamageSource.ANVIL
				|| source == DamageSource.DRAGON_BREATH || source == DamageSource.WITHER) {
			return false;
		}
		return super.hurt(source, amount);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.level.isClientSide) return;
		// Prevent rotation and keep the entity aligned like a block.

		LivingEntity target = this.getTarget();
		if (target != null) {
			if (jumpControl == null) {
				jumpControl = new JumpController(this);
			}
			this.getNavigation().moveTo(target != null ? target : null, 1.0);
		} else {
			this.jumpControl = null;
			this.yRot = 0.0F;
			this.xRot = 0.0F;
			this.yRotO = 0.0F;
			this.xRotO = 0.0F;
			this.getNavigation().stop();
		}
	}

	@Override
	public CreatureAttribute getMobType() {
		return CreatureAttribute.UNDEFINED;
	}

//	@Override
//	public boolean isImmobile() {
//		// Mimic a real block by being immobile unless a player is close
//		return this.getTarget() == null;
//	}

	@Override
	public void baseTick() {
		super.baseTick();
		if (this.level.isClientSide) return;


		LivingEntity target = this.getTarget();
		if (target == null) {
			this.yRot = 0.0F;
			this.xRot = 0.0F;
			this.yRotO = 0.0F;
			this.xRotO = 0.0F;
			this.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 40, 30, true, false));
			// Ensure the entity's position is "snapped" to the block grid
			BlockPos blockPos = this.blockPosition();
			this.setPos(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
		} else {
			this.removeEffect(Effects.MOVEMENT_SLOWDOWN);
		}
	}


}
