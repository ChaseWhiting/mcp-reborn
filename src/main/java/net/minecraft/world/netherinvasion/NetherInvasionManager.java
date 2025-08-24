package net.minecraft.world.netherinvasion;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.DebugUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameRules;
import net.minecraft.world.netherinvasion.invader.AbstractNetherInvaderEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class NetherInvasionManager extends WorldSavedData {
    private final Map<Integer, NetherInvasion> invasionMap = Maps.newHashMap();
    private final ServerWorld level;
    private int nextAvailableID;
    private int tick;

    public NetherInvasionManager(ServerWorld p_i50142_1_) {
        super(getFileId(p_i50142_1_.dimensionType()));
        this.level = p_i50142_1_;
        this.nextAvailableID = 1;
        this.setDirty();
    }

    public NetherInvasion get(int p_215167_1_) {
        return this.invasionMap.get(p_215167_1_);
    }

    public NetherInvasion getInvasionAt(BlockPos pos) {
        for (NetherInvasion netherInvasion : this.invasionMap.values()) {
            BlockPos center = netherInvasion.getCenter();
            double distance = Math.sqrt(center.distSqr(pos));
            if (distance <= 60) {
                return netherInvasion;
            }
        }
        return null;
    }


    public void tick() {
        ++this.tick;
        Iterator<NetherInvasion> iterator = this.invasionMap.values().iterator();

        while(iterator.hasNext()) {
            NetherInvasion raid = iterator.next();
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                raid.stop();
            }

            if (raid.isStopped()) {
                iterator.remove();
                this.setDirty();
            } else {
                raid.tick();
            }
        }

        if (this.tick % 200 == 0) {
            this.setDirty();
        }

        //DebugPacketSender.sendRaids(this.level, this.invasionMap.values());
        ServerPlayerEntity player = DebugUtils.findLocalPlayer(this.level);
        if (player != null) {
            for (NetherInvasion raid : this.invasionMap.values()) {
                DebugPacketSender.sendRaidDebugPacket(player, raid.getCenterAsCollection());
            }
        }
    }

    public static boolean canJoinRaid(AbstractNetherInvaderEntity p_215165_0_, NetherInvasion p_215165_1_) {
        if (p_215165_0_ != null && p_215165_1_ != null && p_215165_1_.getLevel() != null) {
            return p_215165_0_.isAlive() && p_215165_0_.canJoinRaid() && p_215165_0_.getNoActionTime() <= 2400 && p_215165_0_.level.dimensionType() == p_215165_1_.getLevel().dimensionType();
        } else {
            return false;
        }
    }

    @Nullable
    public NetherInvasion createOrExtendRaid(ServerPlayerEntity p_215170_1_) {
        if (p_215170_1_.isSpectator()) {
            return null;
        } else if (this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
            return null;
        } else {
            DimensionType dimensiontype = p_215170_1_.level.dimensionType();
            if (!dimensiontype.hasRaids()) {
                return null;
            } else {
                BlockPos blockpos = p_215170_1_.blockPosition();
                List<PointOfInterest> list = this.level.getPoiManager().getInRange(PointOfInterestType.ALL, blockpos, 64, PointOfInterestManager.Status.IS_OCCUPIED).collect(Collectors.toList());
                int i = 0;
                Vector3d vector3d = Vector3d.ZERO;

                for(PointOfInterest pointofinterest : list) {
                    BlockPos blockpos2 = pointofinterest.getPos();
                    vector3d = vector3d.add((double)blockpos2.getX(), (double)blockpos2.getY(), (double)blockpos2.getZ());
                    ++i;
                }

                BlockPos blockpos1;
                if (i > 0) {
                    vector3d = vector3d.scale(1.0D / (double)i);
                    blockpos1 = new BlockPos(vector3d);
                } else {
                    blockpos1 = blockpos;
                }

                NetherInvasion raid = this.getOrCreateRaid(p_215170_1_.getLevel(), blockpos1);
                boolean flag = false;
                if (!raid.isStarted()) {
                    if (!this.invasionMap.containsKey(raid.getId())) {
                        this.invasionMap.put(raid.getId(), raid);
                    }

                    flag = true;
                } else if (raid.getBadOmenLevel() < raid.getMaxBadOmenLevel()) {
                    flag = true;
                } else {
                    p_215170_1_.removeEffect(Effects.BAD_OMEN);
                    p_215170_1_.connection.send(new SEntityStatusPacket(p_215170_1_, (byte)43));
                }

                if (flag) {
                    raid.absorbBadOmen(p_215170_1_);
                    p_215170_1_.connection.send(new SEntityStatusPacket(p_215170_1_, (byte)43));
                    if (!raid.hasFirstWaveSpawned()) {
                        p_215170_1_.awardStat(Stats.RAID_TRIGGER);
                        CriteriaTriggers.BAD_OMEN.trigger(p_215170_1_);
                    }
                }

                this.setDirty();
                return raid;
            }
        }
    }

    private NetherInvasion getOrCreateRaid(ServerWorld serverWorld, BlockPos pos) {
        NetherInvasion raid = serverWorld.getInvasionAt(pos);
        return raid != null ? raid : new NetherInvasion(this.getUniqueId(), serverWorld, pos);
    }

    public void load(CompoundNBT compoundNBT) {
        this.nextAvailableID = compoundNBT.getInt("NextAvailableID");
        this.tick = compoundNBT.getInt("Tick");
        ListNBT listnbt = compoundNBT.getList("Invasions", 10);

        for(int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            NetherInvasion raid = new NetherInvasion(this.level, compoundnbt);
            this.invasionMap.put(raid.getId(), raid);
        }

    }

    public CompoundNBT save(CompoundNBT compoundNBT) {
        compoundNBT.putInt("NextAvailableID", this.nextAvailableID);
        compoundNBT.putInt("Tick", this.tick);
        ListNBT listnbt = new ListNBT();

        for(NetherInvasion raid : this.invasionMap.values()) {
            CompoundNBT compoundnbt = new CompoundNBT();
            raid.save(compoundnbt);
            listnbt.add(compoundnbt);
        }

        compoundNBT.put("Invasions", listnbt);
        return compoundNBT;
    }

    public static String getFileId(DimensionType p_234620_0_) {
        return "netherinvasions" + p_234620_0_.getFileSuffix();
    }

    private int getUniqueId() {
        return ++this.nextAvailableID;
    }

    @Nullable
    public NetherInvasion getNearbyInvasions(BlockPos pos, int i) {
        NetherInvasion raid = null;
        double d0 = (double)i;

        for(NetherInvasion raid1 : this.invasionMap.values()) {
            double d1 = raid1.getCenter().distSqr(pos);
            if (raid1.isActive() && d1 < d0) {
                raid = raid1;
                d0 = d1;
            }
        }

        return raid;
    }
}