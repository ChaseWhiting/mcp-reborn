package net.minecraft.entity.axolotl;

import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.world.World;


public interface Bucketable {
    public boolean fromBucket();

    public void setFromBucket(boolean var1);

    public void saveToBucketTag(ItemStack var1);

    public void loadFromBucketTag(CompoundNBT var1);

    public ItemStack getBucketItemStack();

    public SoundEvent getPickupSound();

    @Deprecated
    public static void saveDefaultDataToBucketTag(Mob mob, ItemStack itemStack) {
        CompoundNBT compoundTag = itemStack.getOrCreateTag();
        if (mob.hasCustomName()) {
            itemStack.setHoverName(mob.getCustomName());
        }
        if (mob.isNoAi()) {
            compoundTag.putBoolean("NoAI", mob.isNoAi());
        }
        if (mob.isSilent()) {
            compoundTag.putBoolean("Silent", mob.isSilent());
        }
        if (mob.isNoGravity()) {
            compoundTag.putBoolean("NoGravity", mob.isNoGravity());
        }
        if (mob.isGlowing()) {
            compoundTag.putBoolean("Glowing", mob.isGlowing());
        }
        if (mob.isInvulnerable()) {
            compoundTag.putBoolean("Invulnerable", mob.isInvulnerable());
        }
        compoundTag.putFloat("Health", mob.getHealth());
    }

    @Deprecated
    public static void loadDefaultDataFromBucketTag(Mob mob, CompoundNBT compoundTag) {
        if (compoundTag.contains("NoAI")) {
            mob.setNoAi(compoundTag.getBoolean("NoAI"));
        }
        if (compoundTag.contains("Silent")) {
            mob.setSilent(compoundTag.getBoolean("Silent"));
        }
        if (compoundTag.contains("NoGravity")) {
            mob.setNoGravity(compoundTag.getBoolean("NoGravity"));
        }
        if (compoundTag.contains("Glowing")) {
            mob.setGlowing(compoundTag.getBoolean("Glowing"));
        }
        if (compoundTag.contains("Invulnerable")) {
            mob.setInvulnerable(compoundTag.getBoolean("Invulnerable"));
        }
        if (compoundTag.contains("Health", 99)) {
            mob.setHealth(compoundTag.getFloat("Health"));
        }
    }

    public static <T extends LivingEntity> Optional<ActionResultType> bucketMobPickup(PlayerEntity player, Hand interactionHand, T t) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (itemStack.getItem() == Items.WATER_BUCKET && t.isAlive()) {
            t.playSound(((Bucketable)((Object)t)).getPickupSound(), 1.0f, 1.0f);
            ItemStack itemStack2 = ((Bucketable)((Object)t)).getBucketItemStack();
            ((Bucketable)((Object)t)).saveToBucketTag(itemStack2);
            ItemStack itemStack3 = DrinkHelper.createFilledResult(itemStack, player, itemStack2, false);
            player.setItemInHand(interactionHand, itemStack3);
            World level = t.level;
            if (!level.isClientSide) {
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity) player, itemStack2);
            }
            t.discard();
            return Optional.of(ActionResultType.sidedSuccess(level.isClientSide));
        }
        return Optional.empty();
    }
}

