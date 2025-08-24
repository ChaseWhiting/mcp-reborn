package net.minecraft.entity.monster;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class FakeEndstoneBlockEntity extends AbstractFakeBlockEntity {
	public FakeEndstoneBlockEntity(EntityType<? extends Monster> type, World world) {
		super(type, world);
		this.xpReward = 0;
	}
}
