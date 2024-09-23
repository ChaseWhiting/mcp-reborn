package net.minecraft.entity.monster.bogged;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ICrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.WeightedItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BoggedUtils {

    public static void followEntity(BoggedEntity follower, LivingEntity target) {
        if (target != null && follower != null && target.isAlive()) {
            double distance = follower.distanceTo(target);
            if (distance > 2.0D && follower.getTarget() == null) {
                Vector3d direction = new Vector3d(target.getX() - follower.getX(), target.getY() - follower.getY(), target.getZ() - follower.getZ()).normalize().scale(Math.max(distance - 2.0D, 0.0D));
                follower.getNavigation().moveTo(follower.getX() + direction.x, follower.getY() + direction.y, follower.getZ() + direction.z, distance > 15 ? 1.36D : 1.25D);
            }

            List<Mob> mobs = follower.level.getEntitiesOfClass(Mob.class, target.getBoundingBox().inflate(20, 5, 20), entity -> entity != follower && entity != target);
            if (!mobs.isEmpty() && follower.getTarget() == null) {
                int index = follower.getRandom().nextInt(mobs.size());
                if (mobs.get(0).getTarget() == target && target.distanceTo(mobs.get(0)) < 30 || mobs.get(0).getLastHurtByMob() == target) {
                    if (mobs.get(0).isAlive()) {
                        follower.setTarget(mobs.get(0));
                    }
                } else {
                    if (mobs.get(index).getTarget() == target && target.distanceTo(mobs.get(index)) < 30 || mobs.get(index).getLastHurtByMob() == target) {
                        if (mobs.get(index).isAlive()) {
                            follower.setTarget(mobs.get(index));
                        }
                    }
                }

            }
        }
    }

    public static void addEffectPerType(ArrowEntity arrow, BoggedEntity entity) {
        switch (entity.getBoggedType()) {
            case BOGGED, BLOSSOMED -> arrow.addEffect(new EffectInstance(Effects.POISON, 100, entity.veryHardmode() ? 1 : 0));
            case FROSTED, PARCHED, FESTERED, FESTERED_BROWN -> arrow.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 130, 0));
            case WITHERED -> arrow.addEffect(new EffectInstance(Effects.WITHER, 100, entity.veryHardmode() ? 1 : 0));
        }

    }


    public static class ItemUtil {
        public static List<WeightedItemStack> createMushroomList(Item item1, Item item2, int weight, int min, int max) {
            return List.of(
                    new WeightedItemStack.Builder()
                            .addWeightedItem(new ItemStack(item1), weight, min, max) // Use weight and rolls for each item
                            .setBonusRolls(new WeightedItemStack.RandomValueRange(0, 0)) // Set bonus rolls if needed
                            .build(),

                    new WeightedItemStack.Builder()
                            .addWeightedItem(new ItemStack(item2), weight, min, max) // Use weight and rolls for each item
                            .setBonusRolls(new WeightedItemStack.RandomValueRange(0, 0)) // Set bonus rolls if needed
                            .build()
            );
        }

        public static List<WeightedItemStack> createSingleItemStack(Item item, int weight, int min, int max) {
            return List.of(
                    new WeightedItemStack.Builder()
                            .addWeightedItem(new ItemStack(item), weight, min, max) // Use weight and rolls for the item
                            .setBonusRolls(new WeightedItemStack.RandomValueRange(0, 0)) // Set bonus rolls if needed
                            .build()
            );
        }

        public static List<WeightedItemStack> createRandomFlowerList(int min, int max) {
            // Collect small flowers, excluding WITHER_ROSE
            List<Item> flowers = ItemTags.SMALL_FLOWERS.getValues().stream()
                    .filter(item -> item != Items.WITHER_ROSE)
                    .collect(Collectors.toList());

            // If there are fewer than two flowers, return an empty list
            if (flowers.size() < 2) {
                return List.of();
            }

            // Shuffle and pick two random flowers
            Collections.shuffle(flowers);
            return List.of(
                    new WeightedItemStack.Builder()
                            .addWeightedItem(new ItemStack(flowers.get(0)), 1, min, max) // Use a weight of 1, adjust if needed
                            .setBonusRolls(new WeightedItemStack.RandomValueRange(0, 0)) // Set bonus rolls if needed
                            .build(),

                    new WeightedItemStack.Builder()
                            .addWeightedItem(new ItemStack(flowers.get(1)), 1, min, max) // Use a weight of 1, adjust if needed
                            .setBonusRolls(new WeightedItemStack.RandomValueRange(0, 0)) // Set bonus rolls if needed
                            .build()
            );
        }
    }

    public static ActionResultType itemUsedOnBogged(ItemStack itemInHand, PlayerEntity player, Hand hand, BoggedEntity bogged) {
        if (itemInHand.getItem() == Items.BONE_MEAL && bogged.getBoggedType() == BoggedType.BLOSSOMED && bogged.getTrustedPlayer().contains(player.getUUID()) && bogged.getHealth() < bogged.getMaxHealth()) {
            itemInHand.shrink(1);
            player.swing(hand);
            if (bogged.getRandom().nextBoolean() && !bogged.isSheared()) {
                bogged.regrowMushrooms();
            }
            bogged.heal(5F);
            if (bogged.level.isClientSide) {
                assert bogged.getAmbientSound() != null;
                bogged.level.playSound(player, bogged, bogged.getAmbientSound(), SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
            return ActionResultType.CONSUME;
        }
        if (itemInHand.getItem() == (Items.SHEARS) && bogged.readyForShearing()) {
            player.swing(hand);
            if (bogged.getRandom().nextInt(4) == 0 && bogged.getBoggedType() == BoggedType.BLOSSOMED && bogged.getTrustedPlayer().get(0) == null && bogged.level.isServerSide) {
                bogged.setTrustedPlayer(player.getUUID());
                bogged.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30D);
                bogged.setTarget(null);
                bogged.setCanPickUpLoot(true);
                bogged.setPersistenceRequired();
            }
            bogged.shear(SoundCategory.PLAYERS);
            //this.level.gameEvent(GameEvent.SHEAR, player);
            if (!bogged.level.isClientSide) {
                itemInHand.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            }
            return ActionResultType.sidedSuccess(bogged.level.isClientSide);

        } else {
            return bogged.mobInteract(hand, player);
        }
    }

    public static class Entity {
        public static void setAttributes(BoggedEntity entity) {
            switch (entity.getBoggedType()) {
                case PARCHED -> parchedSpeed(entity);
                case BLOSSOMED -> blossomedSpeed(entity);
                case WITHERED -> witheredSpeed(entity);
                case BOGGED -> boggedSpeed(entity);
                case FROSTED -> frostedSpeed(entity);
                case FESTERED, FESTERED_BROWN -> festeredSpeed(entity);
                default -> throw new IllegalArgumentException("Unexpected BoggedType: " + entity.getBoggedType());
            }
        }

        public static void parchedSpeed(BoggedEntity entity) {
            entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.20D);
        }

        public static void frostedSpeed(BoggedEntity entity) {
            entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.23D);
        }

        public static void festeredSpeed(BoggedEntity entity) {
            entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.19D);
        }

        public static void boggedSpeed(BoggedEntity entity) {
            entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.23D);
        }

        public static void witheredSpeed(BoggedEntity entity) {
            entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.22D);
        }

        public static void blossomedSpeed(BoggedEntity entity) {
            entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        }

        public static boolean convertsInWater(BoggedEntity entity) {
            BoggedType type = entity.getBoggedType();

            return switch (type) {
                case BOGGED -> canConvertTo(BoggedType.PARCHED, entity);  // BOGGED converts to PARCHED in water
                case PARCHED -> canConvertTo(BoggedType.BLOSSOMED, entity);  // PARCHED to BLOSSOMED
                case BLOSSOMED -> canConvertTo(BoggedType.BOGGED, entity);  // BLOSSOMED back to BOGGED
                case WITHERED -> true;
                case FESTERED ->
                        canConvertTo(BoggedType.FESTERED_BROWN, entity);  // FESTERED leads to FESTERED_BROWN in water
                case FESTERED_BROWN -> canConvertTo(BoggedType.BLOSSOMED, entity);  // FESTERED_BROWN to BLOSSOMED
                case FROSTED -> canConvertTo(BoggedType.PARCHED, entity);  // FROSTED leads to PARCHED
                default -> false;
            };
        }

        public static boolean canConvertTo(BoggedType type, BoggedEntity entity) {
            return BoggedType.canConvertToType(type, entity);
        }
    }

    public static void reassessWeapon(BoggedEntity entity) {
        if (entity.level != null && !entity.level.isClientSide) {
            entity.getGoalSelector().removeGoal(entity.meleeGoal);
            entity.getGoalSelector().removeGoal(entity.bowGoal);
            entity.getGoalSelector().removeGoal(entity.bowGoalAdvanced);
            entity.getGoalSelector().removeGoal(entity.rangedCrossbowAttackGoal);
            ItemStack itemstack = entity.getItemInHand(ProjectileHelper.getWeaponHoldingHand(entity, Items.BOW));
            ItemStack itemStack1 = entity.getItemInHand(ProjectileHelper.getWeaponHoldingCrossbow(entity));
            if (itemStack1.getItem() instanceof ICrossbowItem) {
                entity.getGoalSelector().addGoal(3, entity.rangedCrossbowAttackGoal);
                return;
            }

            if (itemstack.getItem() == Items.BOW) {
                int i = entity.getAttackInterval(); // Base interval from BoggedType
                if (entity.level.getDifficulty() == Difficulty.HARD) {
                    i = (int) (i * 0.7); // Reduce by 30% on Hard Mode
                }

                if (entity.veryHardmode()) {
                    i = (int) (i * 0.7); // Further reduce by 30% on Very Hardmode
                }

                entity.bowGoal.setMinAttackInterval(i);
                entity.bowGoalAdvanced.setMinAttackInterval(i);
                entity.getGoalSelector().addGoal(4, entity.veryHardmode() ? entity.bowGoalAdvanced : entity.bowGoal);
            } else {
                entity.getGoalSelector().addGoal(4, entity.meleeGoal);
            }
        }
    }
}
