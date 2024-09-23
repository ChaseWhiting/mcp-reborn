package net.minecraft.entity.monster;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FakeEndstoneBlockEntity extends AbstractFakeBlockEntity {
	public FakeEndstoneBlockEntity(EntityType<? extends Monster> type, World world) {
		super(type, world);
		this.xpReward = 0;
		this.setNoGravity(true);  // Ensure it doesn't fall like a regular entity.
	}

	private Set<LivingEntity> entitySet = new HashSet<>();




	public void tick() {
		super.tick();
		List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox(), entity -> {
			return !(entity instanceof PlayerEntity) && entity != this && !entitySet.contains(entity);
		});

		if (!list.isEmpty() && this.isAlive()) {
			for (LivingEntity entity : list) {
				entitySet.add(entity);
				entity.remove();
			}
		}
	}

	public void die(DamageSource source) {
		super.die(source);

		if (!entitySet.isEmpty()) {
			for (LivingEntity entity : entitySet) {
				entity.removed = false;
				//entity.setUUID(UUID.randomUUID());
				entity.setPos(this.blockPosition().offset(0,1,0));

				this.level.addFreshEntity(entity);
			}

			entitySet.clear();
		}
	}

}
