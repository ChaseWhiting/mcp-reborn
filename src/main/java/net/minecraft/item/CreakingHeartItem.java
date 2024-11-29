package net.minecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import java.util.UUID;

public class CreakingHeartItem extends Item{


    public CreakingHeartItem(Properties properties) {
        super(properties.stacksTo(1));
    }


    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isServerSide) {
            ItemStack stack = player.getItemInHand(hand);

            CompoundNBT nbt = stack.getOrCreateTag();

            ListNBT listNBT = nbt.getList("CapturedEntities", 10);

            if (listNBT.isEmpty()) {
                return (ActionResult.fail(stack));
            }
            for (int i = 0; i < listNBT.size(); i++) {
                CompoundNBT nbt1 = listNBT.getCompound(i);
                UUID uuid = UUID.randomUUID();
                Entity entity = EntityType.loadEntityRecursive(nbt1, world, ent -> {
                    if (ent instanceof LivingEntity) {

                        ent.as(LivingEntity.class).heal(ent.getMaxHealth());
                        ent.moveTo(player.position().add(0, 0.76,0));
                    }
                    ent.setUUID(uuid);
                    return ent;
                });

                if (entity != null) {
                    entity.removed = false;

                    world.addFreshEntity(entity);
                }
            }
            world.playSound(player, player.blockPosition(), SoundEvents.CREAKING_DEACTIVATE, SoundCategory.PLAYERS, 1.0f, 1.0f);
            stack.shrink(1);



            return ActionResult.consume(stack);
        }

        return super.use(world, player, hand);
    }

    public static void addEntityToCreakingHeart(ItemStack stack, Entity... entities) {
        CompoundNBT nbt = stack.getOrCreateTag();

        if (nbt.contains("CapturedEntities", 10)) {
            ListNBT listNBT = nbt.getList("CapturedEntities", 10);
            for (Entity entity : entities) {
                CompoundNBT entityNBT = new CompoundNBT();
                entity.save(entityNBT);

                // Reset properties to avoid death state
                if (entity instanceof LivingEntity) {
                    entityNBT.putFloat("Health", ((LivingEntity) entity).getMaxHealth());
                    entityNBT.remove("DeathTime");  // Clears death animation
                    entityNBT.remove("HurtTime");   // Clears any hurt animations
                }

                listNBT.add(entityNBT);
            }
        } else {
            ListNBT listNBT = new ListNBT();
            for (Entity entity : entities) {
                CompoundNBT entityNBT = new CompoundNBT();
                entity.save(entityNBT);

                // Reset properties to avoid death state
                if (entity instanceof LivingEntity) {
                    entityNBT.putFloat("Health", ((LivingEntity) entity).getMaxHealth());
                    entityNBT.remove("DeathTime");
                    entityNBT.remove("HurtTime");
                }

                listNBT.add(entityNBT);
            }

            nbt.put("CapturedEntities", listNBT);
        }
    }

    public static boolean hasRoom(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();

        if (!nbt.contains("CapturedEntities", 10)) return true;

        ListNBT listNBT = nbt.getList("CapturedEntities", 10);

        if (listNBT.isEmpty()) return true;

        return false;
    }

}
