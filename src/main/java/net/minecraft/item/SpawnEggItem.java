package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Mob;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SpawnEggItem extends Item {
   private static final Map<EntityType<?>, SpawnEggItem> BY_ID = Maps.newIdentityHashMap();
   private final int color1;
   private final int color2;
   private final EntityType<?> defaultType;
   @Nullable
   private final MobSpawnHandler spawnHandler; // New handler

   // Existing constructor
   public SpawnEggItem(EntityType<?> entityType, int color1, int color2, Item.Properties properties) {
      this(entityType, color1, color2, properties, null);
   }

   // New constructor with the functional interface
   public SpawnEggItem(EntityType<?> entityType, int color1, int color2, Item.Properties properties, @Nullable MobSpawnHandler spawnHandler) {
      super(properties);
      this.defaultType = entityType;
      this.color1 = color1;
      this.color2 = color2;
      this.spawnHandler = spawnHandler;
      BY_ID.put(entityType, this);
   }

   // Modify the existing spawn logic to call the handler
   public ActionResultType useOn(ItemUseContext context) {
      World world = context.getLevel();
      if (!(world instanceof ServerWorld)) {
         return ActionResultType.SUCCESS;
      } else {
         ItemStack itemstack = context.getItemInHand();
         BlockPos blockpos = context.getClickedPos();
         Direction direction = context.getClickedFace();
         BlockState blockstate = world.getBlockState(blockpos);

         // Spawner logic
         if (blockstate.is(Blocks.SPAWNER)) {
            TileEntity tileentity = world.getBlockEntity(blockpos);
            if (tileentity instanceof MobSpawnerTileEntity) {
               AbstractSpawner abstractspawner = ((MobSpawnerTileEntity)tileentity).getSpawner();
               EntityType<?> entitytype1 = this.getType(itemstack.getTag());
               abstractspawner.setEntityId(entitytype1);
               tileentity.setChanged();
               world.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
               itemstack.shrink(1);
               return ActionResultType.CONSUME;
            }
         }

         BlockPos blockpos1;
         if (blockstate.getCollisionShape(world, blockpos).isEmpty()) {
            blockpos1 = blockpos;
         } else {
            blockpos1 = blockpos.relative(direction);
         }

         EntityType<?> entitytype = this.getType(itemstack.getTag());
         Mob mob = (Mob) entitytype.spawn((ServerWorld)world, itemstack, context.getPlayer(), blockpos1, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP);

         // If mob is successfully spawned, call the custom spawn handler if present
         if (mob != null) {
            itemstack.shrink(1);
            if (this.spawnHandler != null) {
               this.spawnHandler.onMobSpawn(mob, (ServerWorld) world, blockpos1, context.getPlayer());
            }
         }

         return ActionResultType.CONSUME;
      }
   }

   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      RayTraceResult raytraceresult = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
      if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
         return ActionResult.pass(itemstack);
      } else if (!(world instanceof ServerWorld)) {
         return ActionResult.success(itemstack);
      } else {
         BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)raytraceresult;
         BlockPos blockpos = blockraytraceresult.getBlockPos();
         if (!(world.getBlockState(blockpos).getBlock() instanceof FlowingFluidBlock)) {
            return ActionResult.pass(itemstack);
         } else if (world.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos, blockraytraceresult.getDirection(), itemstack)) {
            EntityType<?> entitytype = this.getType(itemstack.getTag());
            Mob mob = (Mob) entitytype.spawn((ServerWorld)world, itemstack, player, blockpos, SpawnReason.SPAWN_EGG, false, false);

            // Call the custom spawn handler if mob is successfully spawned
            if (mob != null) {
               if (!player.abilities.instabuild) {
                  itemstack.shrink(1);
               }
               player.awardStat(Stats.ITEM_USED.get(this));
               if (this.spawnHandler != null) {
                  this.spawnHandler.onMobSpawn(mob, (ServerWorld) world, blockpos, player);
               }
               return ActionResult.consume(itemstack);
            } else {
               return ActionResult.pass(itemstack);
            }
         } else {
            return ActionResult.fail(itemstack);
         }
      }
   }


   public boolean spawnsEntity(@Nullable CompoundNBT p_208077_1_, EntityType<?> p_208077_2_) {
      return Objects.equals(this.getType(p_208077_1_), p_208077_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public int getColor(int p_195983_1_) {
      return p_195983_1_ == 0 ? this.color1 : this.color2;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static SpawnEggItem byId(@Nullable EntityType<?> p_200889_0_) {
      return BY_ID.get(p_200889_0_);
   }

   public static Iterable<SpawnEggItem> eggs() {
      return Iterables.unmodifiableIterable(BY_ID.values());
   }

   public EntityType<?> getType(@Nullable CompoundNBT p_208076_1_) {
      if (p_208076_1_ != null && p_208076_1_.contains("EntityTag", 10)) {
         CompoundNBT compoundnbt = p_208076_1_.getCompound("EntityTag");
         if (compoundnbt.contains("id", 8)) {
            return EntityType.byString(compoundnbt.getString("id")).orElse(this.defaultType);
         }
      }

      return this.defaultType;
   }

   public Optional<Mob> spawnOffspringFromSpawnEgg(PlayerEntity p_234809_1_, Mob p_234809_2_, EntityType<? extends Mob> p_234809_3_, ServerWorld p_234809_4_, Vector3d p_234809_5_, ItemStack p_234809_6_) {
      if (!this.spawnsEntity(p_234809_6_.getTag(), p_234809_3_)) {
         return Optional.empty();
      } else {
         Mob mobentity;
         if (p_234809_2_ instanceof AgeableEntity) {
            mobentity = ((AgeableEntity)p_234809_2_).getBreedOffspring(p_234809_4_, (AgeableEntity)p_234809_2_);
         } else {
            mobentity = p_234809_3_.create(p_234809_4_);
         }

         if (mobentity == null) {
            return Optional.empty();
         } else {
            mobentity.setBaby(true);
            if (!mobentity.isBaby()) {
               return Optional.empty();
            } else {
               mobentity.moveTo(p_234809_5_.x(), p_234809_5_.y(), p_234809_5_.z(), 0.0F, 0.0F);
               p_234809_4_.addFreshEntityWithPassengers(mobentity);
               if (p_234809_6_.hasCustomHoverName()) {
                  mobentity.setCustomName(p_234809_6_.getHoverName());
               }

               if (!p_234809_1_.abilities.instabuild) {
                  p_234809_6_.shrink(1);
               }

               return Optional.of(mobentity);
            }
         }
      }
   }
}