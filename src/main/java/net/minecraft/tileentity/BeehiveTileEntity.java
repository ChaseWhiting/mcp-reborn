package net.minecraft.tileentity;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IBee;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.QueenBeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.DebugUtils;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class BeehiveTileEntity extends TileEntity implements ITickableTileEntity {
    private final List<BeehiveTileEntity.Bee> stored = Lists.newArrayList();
    @Nullable
    private BlockPos savedFlowerPos = null;

    public BeehiveTileEntity() {
        super(TileEntityType.BEEHIVE);
    }

    public void setChanged() {
        if (this.isFireNearby()) {
            this.emptyAllLivingFromHive((PlayerEntity) null, this.level.getBlockState(this.getBlockPos()), BeehiveTileEntity.State.EMERGENCY);
        }

        super.setChanged();
    }

    public boolean isFireNearby() {
        if (this.level == null) {
            return false;
        } else {
            for (BlockPos blockpos : BlockPos.betweenClosed(this.worldPosition.offset(-1, -1, -1), this.worldPosition.offset(1, 1, 1))) {
                if (this.level.getBlockState(blockpos).getBlock() instanceof FireBlock) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean isEmpty() {
        return this.stored.isEmpty();
    }

    public boolean isFull() {
        return this.stored.size() == 5;
    }

    public void emptyAllLivingFromHive(@Nullable PlayerEntity player, BlockState blockState, BeehiveTileEntity.State hiveState) {
        List<Entity> entities = this.releaseAllOccupants(blockState, hiveState);
        if (player != null) {
            for (Entity entity : entities) {
                if (entity instanceof IBee) {
                    if (entity instanceof BeeEntity) {
                        BeeEntity bee = (BeeEntity) entity;
                        if (player.position().distanceToSqr(entity.position()) <= 16.0D) {
                            if (!this.isSedated()) {
                                bee.setTarget(player);
                            } else {
                                bee.setStayOutOfHiveCountdown(400);
                            }
                        }
                    } else if (entity instanceof QueenBeeEntity) {
                        QueenBeeEntity queenBee = (QueenBeeEntity) entity;
                        if (player.position().distanceToSqr(entity.position()) <= 16.0D) {
                            if (!this.isSedated()) {
                                queenBee.setTarget(player);
                            } else {
                                queenBee.setStayOutOfHiveCountdown(400);
                            }
                        }
                    }
                }
            }
        }
    }

    private List<Entity> releaseAllOccupants(BlockState p_226965_1_, BeehiveTileEntity.State p_226965_2_) {
        List<Entity> list = Lists.newArrayList();
        this.stored.removeIf((p_226966_4_) -> {
            return this.releaseOccupant(p_226965_1_, p_226966_4_, list, p_226965_2_);
        });
        return list;
    }

    public void addOccupant(Entity p_226961_1_, boolean p_226961_2_) {
        this.addOccupantWithPresetTicks(p_226961_1_, p_226961_2_, 0);
    }

    public int getOccupantCount() {
        return this.stored.size();
    }

    public static int getHoneyLevel(BlockState p_226964_0_) {
        return p_226964_0_.getValue(BeehiveBlock.HONEY_LEVEL);
    }

    public boolean isSedated() {
        return CampfireBlock.isSmokeyPos(this.level, this.getBlockPos());
    }

    protected void sendDebugPackets() {
        ServerPlayerEntity player = DebugUtils.findLocalPlayer(this.level);
        DebugPacketSender.sendHiveInfo(this);
        if (player != null)
            DebugPacketSender.sendBeehiveDebugData(player, this.level, this.getBlockPos(), this.getBlockState(), this);
    }

    public void addOccupantWithPresetTicks(Entity entity, boolean flag, int ticks) {
        if (this.stored.size() < 5) {
            entity.stopRiding();
            entity.ejectPassengers();
            CompoundNBT compoundNBT = new CompoundNBT();
            entity.save(compoundNBT);
            this.stored.add(new BeehiveTileEntity.Bee(compoundNBT, ticks, flag ? 2400 : 600));
            if (this.level != null) {
                if (entity instanceof BeeEntity) {
                    BeeEntity beeEntity = (BeeEntity) entity;
                    if (beeEntity.hasSavedFlowerPos() && (!this.hasSavedFlowerPos() || this.level.random.nextBoolean())) {
                        this.savedFlowerPos = beeEntity.getSavedFlowerPos();
                    }
                } else if (entity instanceof QueenBeeEntity) {
                    QueenBeeEntity queenBeeEntity = (QueenBeeEntity) entity;
                    if (queenBeeEntity.hasSavedFlowerPos() && (!this.hasSavedFlowerPos() || this.level.random.nextBoolean())) {
                        this.savedFlowerPos = queenBeeEntity.getSavedFlowerPos();
                    }
                }

                BlockPos blockPos = this.getBlockPos();
                this.level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            entity.remove();
        }
    }

    private boolean releaseOccupant(BlockState blockState, BeehiveTileEntity.Bee beeData, @Nullable List<Entity> entityList, BeehiveTileEntity.State hiveState) {
        if ((this.level.isNight() || this.level.isRaining()) && hiveState != BeehiveTileEntity.State.EMERGENCY) {
            return false;
        } else {
            BlockPos blockPos = this.getBlockPos();
            CompoundNBT compoundNBT = beeData.entityData;
            compoundNBT.remove("Passengers");
            compoundNBT.remove("Leash");
            compoundNBT.remove("UUID");
            Direction direction = blockState.getValue(BeehiveBlock.FACING);
            BlockPos blockPos1 = blockPos.relative(direction);
            boolean hasCollision = !this.level.getBlockState(blockPos1).getCollisionShape(this.level, blockPos1).isEmpty();
            if (hasCollision && hiveState != BeehiveTileEntity.State.EMERGENCY) {
                return false;
            } else {
                Entity entity = EntityType.loadEntityRecursive(compoundNBT, this.level, (loadedEntity) -> loadedEntity);
                if (entity != null) {
                    if (!entity.getType().is(EntityTypeTags.BEEHIVE_INHABITORS)) {
                        return false;
                    } else {
                        if (entity instanceof IBee) {
                            if (entity instanceof BeeEntity) {
                                BeeEntity beeEntity = (BeeEntity) entity;
                                if (this.hasSavedFlowerPos() && !beeEntity.hasSavedFlowerPos() && this.level.random.nextFloat() < 0.9F) {
                                    beeEntity.setSavedFlowerPos(this.savedFlowerPos);
                                }

                                if (hiveState == BeehiveTileEntity.State.HONEY_DELIVERED) {
                                    beeEntity.dropOffNectar();
                                    if (blockState.getBlock().is(BlockTags.BEEHIVES)) {
                                        int honeyLevel = getHoneyLevel(blockState);
                                        if (honeyLevel < 5) {
                                            int increment = this.level.random.nextInt(100) == 0 ? 2 : 1;
                                            if (honeyLevel + increment > 5) {
                                                --increment;
                                            }

                                            this.level.setBlockAndUpdate(this.getBlockPos(), blockState.setValue(BeehiveBlock.HONEY_LEVEL, honeyLevel + increment));
                                        }
                                    }
                                }

                                this.setBeeReleaseData(beeData.ticksInHive, beeEntity);
                                if (entityList != null) {
                                    entityList.add(beeEntity);
                                }
                            } else if (entity instanceof QueenBeeEntity) {
                                QueenBeeEntity queenBeeEntity = (QueenBeeEntity) entity;
                                if (this.hasSavedFlowerPos() && !queenBeeEntity.hasSavedFlowerPos() && this.level.random.nextFloat() < 0.9F) {
                                    queenBeeEntity.setSavedFlowerPos(this.savedFlowerPos);
                                }

                                if (hiveState == BeehiveTileEntity.State.HONEY_DELIVERED) {
                                    queenBeeEntity.dropOffNectar();
                                    if (blockState.getBlock().is(BlockTags.BEEHIVES)) {
                                        int honeyLevel = getHoneyLevel(blockState);
                                        if (honeyLevel < 5) {
                                            int increment = this.level.random.nextInt(100) == 0 ? 2 : 1;
                                            if (honeyLevel + increment > 5) {
                                                --increment;
                                            }

                                            this.level.setBlockAndUpdate(this.getBlockPos(), blockState.setValue(BeehiveBlock.HONEY_LEVEL, honeyLevel + increment));
                                        }
                                    }
                                }

                                this.setBeeReleaseData(beeData.ticksInHive, queenBeeEntity);
                                if (entityList != null) {
                                    entityList.add(queenBeeEntity);
                                }
                            }

                            float width = entity.getBbWidth();
                            double offset = hasCollision ? 0.0D : 0.55D + (double) (width / 2.0F);
                            double x = (double) blockPos.getX() + 0.5D + offset * (double) direction.getStepX();
                            double y = (double) blockPos.getY() + 0.5D - (double) (entity.getBbHeight() / 2.0F);
                            double z = (double) blockPos.getZ() + 0.5D + offset * (double) direction.getStepZ();
                            entity.moveTo(x, y, z, entity.yRot, entity.xRot);
                        }

                        this.level.playSound((PlayerEntity) null, blockPos, SoundEvents.BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        return this.level.addFreshEntity(entity);
                    }
                } else {
                    return false;
                }
            }
        }
    }


    private void setBeeReleaseData(int ticksInHive, Entity entity) {
        if (entity instanceof IBee) {
            if (entity instanceof BeeEntity) {
                BeeEntity beeEntity = (BeeEntity) entity;
                int age = beeEntity.getAge();
                if (age < 0) {
                    beeEntity.setAge(Math.min(0, age + ticksInHive));
                } else if (age > 0) {
                    beeEntity.setAge(Math.max(0, age - ticksInHive));
                }
            } else if (entity instanceof QueenBeeEntity) {
                QueenBeeEntity queenBeeEntity = (QueenBeeEntity) entity;
                int age = queenBeeEntity.getAge();
                if (age < 0) {
                    queenBeeEntity.setAge(Math.min(0, age + ticksInHive));
                } else if (age > 0) {
                    queenBeeEntity.setAge(Math.max(0, age - ticksInHive));
                }
            }
        }
    }


    private boolean hasSavedFlowerPos() {
        return this.savedFlowerPos != null;
    }

    private void tickOccupants() {
        Iterator<BeehiveTileEntity.Bee> iterator = this.stored.iterator();

        BeehiveTileEntity.Bee beehivetileentity$bee;
        for (BlockState blockstate = this.getBlockState(); iterator.hasNext(); beehivetileentity$bee.ticksInHive++) {
            beehivetileentity$bee = iterator.next();
            if (beehivetileentity$bee.ticksInHive > beehivetileentity$bee.minOccupationTicks) {
                BeehiveTileEntity.State beehivetileentity$state = beehivetileentity$bee.entityData.getBoolean("HasNectar") ? BeehiveTileEntity.State.HONEY_DELIVERED : BeehiveTileEntity.State.BEE_RELEASED;
                if (this.releaseOccupant(blockstate, beehivetileentity$bee, (List<Entity>) null, beehivetileentity$state)) {
                    iterator.remove();
                }
            }
        }

    }

    public void tick() {
        if (!this.level.isClientSide) {
            this.tickOccupants();
            BlockPos blockpos = this.getBlockPos();
            if (this.stored.size() > 0 && this.level.getRandom().nextDouble() < 0.005D) {
                double d0 = (double) blockpos.getX() + 0.5D;
                double d1 = (double) blockpos.getY();
                double d2 = (double) blockpos.getZ() + 0.5D;
                this.level.playSound((PlayerEntity) null, d0, d1, d2, SoundEvents.BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            this.sendDebugPackets();

        }
    }

    public void load(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
        super.load(p_230337_1_, p_230337_2_);
        this.stored.clear();
        ListNBT listnbt = p_230337_2_.getList("Bees", 10);

        for (int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            BeehiveTileEntity.Bee beehivetileentity$bee = new BeehiveTileEntity.Bee(compoundnbt.getCompound("EntityData"), compoundnbt.getInt("TicksInHive"), compoundnbt.getInt("MinOccupationTicks"));
            this.stored.add(beehivetileentity$bee);
        }

        this.savedFlowerPos = null;
        if (p_230337_2_.contains("FlowerPos")) {
            this.savedFlowerPos = NBTUtil.readBlockPos(p_230337_2_.getCompound("FlowerPos"));
        }

    }

    public CompoundNBT save(CompoundNBT p_189515_1_) {
        super.save(p_189515_1_);
        p_189515_1_.put("Bees", this.writeBees());
        if (this.hasSavedFlowerPos()) {
            p_189515_1_.put("FlowerPos", NBTUtil.writeBlockPos(this.savedFlowerPos));
        }

        return p_189515_1_;
    }

    public ListNBT writeBees() {
        ListNBT listnbt = new ListNBT();

        for (BeehiveTileEntity.Bee beehivetileentity$bee : this.stored) {
            beehivetileentity$bee.entityData.remove("UUID");
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.put("EntityData", beehivetileentity$bee.entityData);
            compoundnbt.putInt("TicksInHive", beehivetileentity$bee.ticksInHive);
            compoundnbt.putInt("MinOccupationTicks", beehivetileentity$bee.minOccupationTicks);
            listnbt.add(compoundnbt);
        }

        return listnbt;
    }

    static class Bee {
        private final CompoundNBT entityData;
        private int ticksInHive;
        private final int minOccupationTicks;

        private Bee(CompoundNBT p_i225767_1_, int p_i225767_2_, int p_i225767_3_) {
            p_i225767_1_.remove("UUID");
            this.entityData = p_i225767_1_;
            this.ticksInHive = p_i225767_2_;
            this.minOccupationTicks = p_i225767_3_;
        }
    }

    public static enum State {
        HONEY_DELIVERED,
        BEE_RELEASED,
        EMERGENCY;
    }
}