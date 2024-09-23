package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.*;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

public class NuclearExplosion {
   private static final ExplosionContext EXPLOSION_DAMAGE_CALCULATOR = new ExplosionContext();
   private final boolean fire;
   private final NuclearExplosion.Mode blockInteraction;
   private final Random random = new Random();
   private final World level;
   private final double x;
   private final double y;
   private final double z;
   @Nullable
   private final Entity source;
   private final float radius;
   private final DamageSource damageSource;
   private final ExplosionContext damageCalculator;
   private final List<BlockPos> toBlow = Lists.newArrayList();
   private final List<BlockPos> toBlowStage1 = Lists.newArrayList();
   private final List<BlockPos> toBlowStage2 = Lists.newArrayList();
   private final List<BlockPos> toBlowStage3 = Lists.newArrayList();

   private final Map<PlayerEntity, Vector3d> hitPlayers = Maps.newHashMap();

   @OnlyIn(Dist.CLIENT)
   public NuclearExplosion(World world, @Nullable Entity entity, double x, double y, double z, float strength, List<BlockPos> affectedBlocks) {
      this(world, entity, x, y, z, strength, false, NuclearExplosion.Mode.DESTROY, affectedBlocks);
   }

   @OnlyIn(Dist.CLIENT)
   public NuclearExplosion(World world, @Nullable Entity entity, double x, double y, double z, float strength, boolean causesFire, NuclearExplosion.Mode mode, List<BlockPos> affectedBlocks) {
      this(world, entity, x, y, z, strength, causesFire, mode);
      this.toBlow.addAll(affectedBlocks);
   }

   @OnlyIn(Dist.CLIENT)
   public NuclearExplosion(World world, @Nullable Entity entity, double x, double y, double z, float strength, boolean causesFire, NuclearExplosion.Mode mode) {
      this(world, entity, (DamageSource) null, (ExplosionContext) null, x, y, z, strength, causesFire, mode);
   }

   public NuclearExplosion(World world, @Nullable Entity mob, @Nullable DamageSource damageSource, @Nullable ExplosionContext context, double x, double y, double z, float radius, boolean fire, NuclearExplosion.Mode mode) {
      this.level = world;
      this.source = mob;
      this.radius = radius;
      this.x = x;
      this.y = y;
      this.z = z;
      this.fire = fire;
      this.blockInteraction = mode;
      this.damageSource = damageSource == null ? DamageSource.nuclearExplosion(this) : damageSource;
      this.damageCalculator = context == null ? this.makeDamageCalculator(mob) : context;
   }

   private ExplosionContext makeDamageCalculator(@Nullable Entity p_234894_1_) {
      return (ExplosionContext)(p_234894_1_ == null ? EXPLOSION_DAMAGE_CALCULATOR : new EntityExplosionContext(p_234894_1_));
   }

   public static float getSeenPercent(Vector3d vector3d1, Entity entity) {
      AxisAlignedBB axisalignedbb = entity.getBoundingBox();
      double densityFactor = 3.0; // Increase this factor to increase visibility
      double d0 = 1.0D / ((axisalignedbb.maxX - axisalignedbb.minX) * densityFactor + 1.0D);
      double d1 = 1.0D / ((axisalignedbb.maxY - axisalignedbb.minY) * densityFactor + 1.0D);
      double d2 = 1.0D / ((axisalignedbb.maxZ - axisalignedbb.minZ) * densityFactor + 1.0D);
      double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
      double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;
      if (!(d0 < 0.0D) && !(d1 < 0.0D) && !(d2 < 0.0D)) {
         int i = 0;
         int j = 0;

         for (float f = 0.0F; f <= 1.0F; f = (float) ((double) f + d0)) {
            for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float) ((double) f1 + d1)) {
               for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float) ((double) f2 + d2)) {
                  double d5 = MathHelper.lerp((double) f, axisalignedbb.minX, axisalignedbb.maxX);
                  double d6 = MathHelper.lerp((double) f1, axisalignedbb.minY, axisalignedbb.maxY);
                  double d7 = MathHelper.lerp((double) f2, axisalignedbb.minZ, axisalignedbb.maxZ);
                  Vector3d vector3d = new Vector3d(d5 + d3, d6, d7 + d4);
                  if (entity.level.clip(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity)).getType() == RayTraceResult.Type.MISS) {
                     ++i;
                  }

                  ++j;
               }
            }
         }

         return (float) i / (float) j;
      } else {
         return 0.0F;
      }
   }


   public void explode() {
      Set<BlockPos> affectedBlocks = Sets.newHashSet();
      int radius = (int) this.radius;
      int depth = radius / 2;
      int craterDepth = radius / 2;
      double craterShallownessFactor = 0.7; // Factor to control how shallow the crater becomes

      for (int x = -radius; x <= radius; x++) {
         for (int y = -depth; y <= radius; y++) {
            for (int z = -radius; z <= radius; z++) {
               double distance = Math.sqrt(x * x + y * y + z * z);
               if (distance <= radius) {
                  BlockPos pos = new BlockPos(this.x + x, this.y + y, this.z + z);
                  double distanceFactor = 1.0 - distance / radius;
                  if (distanceFactor > 0.0) {
                     float radiusFactor = (float) (this.radius * distanceFactor);
                     BlockState blockState = this.level.getBlockState(pos);
                     FluidState fluidState = this.level.getFluidState(pos);
                     Optional<Float> blockResistance = this.damageCalculator.getBlockExplosionResistance(this, this.level, pos, blockState, fluidState);
                     if (blockResistance.isPresent()) {
                        radiusFactor -= (blockResistance.get() + 0.3F) * 0.3F;
                     }

                     if (radiusFactor > 0.0F) {
                        affectedBlocks.add(pos);
                     }

                     // Handle water vaporization
                     if (fluidState.getType() == FluidTags.WATER && distance <= radius * 1.5) {
                        affectedBlocks.add(pos);
                     }
                  }
               }
            }
         }
      }

      int maxCraterDepth = craterDepth; // Maximum depth at the center
      for (int x = -radius; x <= radius; x++) {
         for (int z = -radius; z <= radius; z++) {
            double distance = Math.sqrt(x * x + z * z);
            if (distance <= radius) {
               int adjustedDepth = (int) (maxCraterDepth * (1 - distance / radius));
               for (int y = 0; y < adjustedDepth; y++) {
                  BlockPos pos = new BlockPos(this.x + x, this.y - y, this.z + z);
                  if (this.level.getBlockState(pos).getBlock() != Blocks.BEDROCK) {
                     affectedBlocks.add(pos);
                  }
               }
            }
         }
      }

      this.toBlow.addAll(affectedBlocks);

      float explosionDiameter = this.radius * 4.0F;
      int minX = MathHelper.floor(this.x - explosionDiameter - 1.0D);
      int maxX = MathHelper.floor(this.x + explosionDiameter + 1.0D);
      int minY = MathHelper.floor(this.y - explosionDiameter - 1.0D);
      int maxY = MathHelper.floor(this.y + explosionDiameter + 1.0D);
      int minZ = MathHelper.floor(this.z - explosionDiameter - 1.0D);
      int maxZ = MathHelper.floor(this.z + explosionDiameter + 1.0D);

      List<Entity> nearbyEntities = this.level.getEntities(this.source, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
      Vector3d explosionCenter = new Vector3d(this.x, this.y, this.z);

//      if (this.level.isClientSide) {
         createMushroomCloud(level, this.x, this.y, this.z, radius);
//      }

      for (Entity entity : nearbyEntities) {
         if (!entity.ignoreExplosion()) {
            double distanceToEntity = MathHelper.sqrt(entity.distanceToSqr(explosionCenter)) / explosionDiameter;
            if (distanceToEntity <= 1.0D) {
               double deltaX = entity.getX() - this.x;
               double deltaY = (entity instanceof TNTEntity ? entity.getY() : entity.getEyeY()) - this.y;
               double deltaZ = entity.getZ() - this.z;
               double distanceFactor = MathHelper.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
               if (distanceFactor != 0.0D) {
                  deltaX = deltaX / distanceFactor;
                  deltaY = deltaY / distanceFactor;
                  deltaZ = deltaZ / distanceFactor;
                  double visibility = (double) getSeenPercent(explosionCenter, entity);
                  double impact = (1.0D - distanceToEntity) * visibility;

                  entity.hurt(this.getDamageSource(), (float) ((int) ((impact * impact + impact) / 2.0D * 7.0D * (double) explosionDiameter + 1.0D)));
                  double knockbackFactor = impact;
                  if (entity instanceof LivingEntity) {
                     int radAmount = (int) (2000 - 80 * distanceToEntity);
                     do {
                        radAmount+=100;
                     } while (radAmount < 400);
                     ((LivingEntity)entity).setRads(((LivingEntity) entity).radiationManager.getRads() + radAmount);
                     knockbackFactor = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity) entity, impact);
                  }

                  entity.setDeltaMovement(entity.getDeltaMovement().add(deltaX * knockbackFactor, deltaY * knockbackFactor, deltaZ * knockbackFactor));
                  if (entity instanceof PlayerEntity) {
                     PlayerEntity player = (PlayerEntity) entity;
                     if (!player.isSpectator() && (!player.isCreative() || !player.abilities.flying)) {
                        this.hitPlayers.put(player, new Vector3d(deltaX * impact, deltaY * impact, deltaZ * impact));
                     }
                  }
               }
            }
         }
      }
   }

   public void finalizeExplosion(boolean particles) {
      if (this.level.isClientSide) {
         this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 3.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
      }

      boolean flag = this.blockInteraction != NuclearExplosion.Mode.NONE;
      if (particles) {
         if (!(this.radius < 2.0F) && flag) {
            this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
         } else {
            this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
         }
      }

      if (flag) {
         ObjectArrayList<Pair<ItemStack, BlockPos>> objectarraylist = new ObjectArrayList<>();
         Collections.shuffle(this.toBlow, this.level.random);

         for (BlockPos blockpos : this.toBlow) {
            BlockState blockstate = this.level.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (!blockstate.isAir()) {
               BlockPos blockpos1 = blockpos.immutable();
               this.level.getProfiler().push("explosion_blocks");
               if (block.dropFromExplosion(this) && this.level instanceof ServerWorld) {
                  TileEntity tileentity = block.isEntityBlock() ? this.level.getBlockEntity(blockpos) : null;
                  LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld) this.level)).withRandom(this.level.random).withParameter(LootParameters.ORIGIN, Vector3d.atCenterOf(blockpos)).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withOptionalParameter(LootParameters.BLOCK_ENTITY, tileentity).withOptionalParameter(LootParameters.THIS_ENTITY, this.source);
                  if (this.blockInteraction == NuclearExplosion.Mode.DESTROY) {
                     lootcontext$builder.withParameter(LootParameters.EXPLOSION_RADIUS, this.radius);
                  }

                  blockstate.getDrops(lootcontext$builder).forEach((p_229977_2_) -> {
                     addBlockDrops(objectarraylist, p_229977_2_, blockpos1);
                  });
               }

               this.level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 3);
               block.wasExploded(this.level, blockpos, this);
               this.level.getProfiler().pop();
            }
         }

         for (Pair<ItemStack, BlockPos> pair : objectarraylist) {
            Block.popResource(this.level, pair.getSecond(), pair.getFirst());
         }
      }

      if (this.fire) {
         for (BlockPos blockpos2 : this.toBlow) {
            if (this.random.nextInt(3) == 0 && this.level.getBlockState(blockpos2).isAir() && this.level.getBlockState(blockpos2.below()).isSolidRender(this.level, blockpos2.below())) {
               this.level.setBlockAndUpdate(blockpos2.offset(0,-1,0), Blocks.NETHERRACK.defaultBlockState());
               this.level.setBlockAndUpdate(blockpos2, AbstractFireBlock.getState(this.level, blockpos2));
            }
         }
      }
   }

   public void createMushroomCloud(World world, double explosionX, double explosionY, double explosionZ, double radius) {
      int baseRadius = (int) (3 * radius);
      int cloudHeight = 30;

      for (int x = -baseRadius; x <= baseRadius; x++) {
         for (int y = 0; y < cloudHeight; y++) {
            for (int z = -baseRadius; z <= baseRadius; z++) {
               double distance = Math.sqrt(x * x + z * z);

               if (y < 5 && distance < 3 * radius) {
                  world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, explosionX + x, explosionY + y, explosionZ + z, 0, 0.1, 0);
               } else if (y >= 5 && y < 15 && distance < 1 * radius) {
                  world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, explosionX + x, explosionY + y, explosionZ + z, 0, 0.1, 0);
               } else if (y >= 15 && y < 20 && distance < 2 * radius) {
                  world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, explosionX + x, explosionY + y, explosionZ + z, 0, 0.1, 0);
               } else if (y >= 20 && distance < 3 * radius) {
                  world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, explosionX + x, explosionY + y, explosionZ + z, 0, 0.1, 0);
               }
            }
         }
      }
   }



   private static void addBlockDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> p_229976_0_, ItemStack p_229976_1_, BlockPos p_229976_2_) {
      int i = p_229976_0_.size();

      for(int j = 0; j < i; ++j) {
         Pair<ItemStack, BlockPos> pair = p_229976_0_.get(j);
         ItemStack itemstack = pair.getFirst();
         if (ItemEntity.areMergable(itemstack, p_229976_1_)) {
            ItemStack itemstack1 = ItemEntity.merge(itemstack, p_229976_1_, 16);
            p_229976_0_.set(j, Pair.of(itemstack1, pair.getSecond()));
            if (p_229976_1_.isEmpty()) {
               return;
            }
         }
      }

      p_229976_0_.add(Pair.of(p_229976_1_, p_229976_2_));
   }

   public DamageSource getDamageSource() {
      return this.damageSource;
   }

   public Map<PlayerEntity, Vector3d> getHitPlayers() {
      return this.hitPlayers;
   }

   @Nullable
   public LivingEntity getSourceMob() {
      if (this.source == null) {
         return null;
      } else if (this.source instanceof TNTEntity) {
         return ((TNTEntity)this.source).getOwner();
      } else if (this.source instanceof LivingEntity) {
         return (LivingEntity)this.source;
      } else {
         if (this.source instanceof ProjectileEntity) {
            Entity entity = ((ProjectileEntity)this.source).getOwner();
            if (entity instanceof LivingEntity) {
               return (LivingEntity)entity;
            }
         }

         return null;
      }
   }

   public void clearToBlow() {
      this.toBlow.clear();
   }

   public List<BlockPos> getToBlow() {
      return this.toBlow;
   }

   public static enum Mode {
      NONE,
      BREAK,
      DESTROY;
   }
}