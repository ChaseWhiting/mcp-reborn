package net.minecraft.entity.terraria.boss;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class BossEntity extends Monster {

    protected final Set<UUID> playersWhoDealtDamage = new HashSet<>();

    protected BossEntity(EntityType<? extends Monster> entityType, World world) {
        super(entityType, world);
    }

    public abstract Item getTreasureBag();


    public boolean hasSpecialDropConditions() {
        return false;
    }

    public boolean fulfillsConditions(DamageSource source, BossEntity boss) {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (source.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) source.getEntity();
            playersWhoDealtDamage.add(player.getUUID());
        }

        return super.hurt(source, damage);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);

        if (hasSpecialDropConditions()) {
            if (!fulfillsConditions(source, this)) return;
        }

        for (UUID uuid : playersWhoDealtDamage) {
            PlayerEntity player = level.getPlayerByUUID(uuid);
            ItemStack treasureBag = new ItemStack(this.getTreasureBag());

            if (player != null) {
                if (!player.addItem(treasureBag, true)) {player.drop(treasureBag, true);}
            } else {
                ItemEntity item = this.spawnAtLocation(treasureBag);
                item.setExtendedLifetime();
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);

        ListNBT uuidList = new ListNBT();
        for (UUID uuid : playersWhoDealtDamage) {
            CompoundNBT uuidNBT = new CompoundNBT();
            uuidNBT.putUUID("UUID", uuid);
            uuidList.add(uuidNBT);
        }
        nbt.put("PlayersWhoDealtDamage", uuidList);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);

        playersWhoDealtDamage.clear();
        if (nbt.contains("PlayersWhoDealtDamage")) {
            ListNBT uuidList = nbt.getList("PlayersWhoDealtDamage", 10);
            for (int i = 0; i < uuidList.size(); i++) {
                CompoundNBT uuidNBT = uuidList.getCompound(i);
                UUID uuid = uuidNBT.getUUID("UUID");
                playersWhoDealtDamage.add(uuid);
            }
        }
    }

    public void tick() {
        super.tick();

    }
}
