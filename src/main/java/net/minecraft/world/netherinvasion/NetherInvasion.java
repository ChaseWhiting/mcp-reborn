package net.minecraft.world.netherinvasion;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.piglin.AbstractPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.netherinvasion.invader.AbstractNetherInvaderEntity;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;

public class NetherInvasion {
    private static final ITextComponent RAID_NAME_COMPONENT = new TranslationTextComponent("event.minecraft.netherinvasion");
    private static final ITextComponent VICTORY = new TranslationTextComponent("event.minecraft.netherinvasion.victory");
    private static final ITextComponent DEFEAT = new TranslationTextComponent("event.minecraft.netherinvasion.defeat");
    private static final ITextComponent RAID_BAR_VICTORY_COMPONENT = RAID_NAME_COMPONENT.copy().append(" - ").append(VICTORY);
    private static final ITextComponent RAID_BAR_DEFEAT_COMPONENT = RAID_NAME_COMPONENT.copy().append(" - ").append(DEFEAT);
    private final Map<Integer, AbstractNetherInvaderEntity> groupToLeaderMap = Maps.newHashMap();
    private final Map<Integer, Set<AbstractNetherInvaderEntity>> groupNetherInvaderMap = Maps.newHashMap();
    private final Set<UUID> heroesOfTheVillage = Sets.newHashSet();
    private long ticksActive;
    private BlockPos center;
    private final ServerWorld level;
    private boolean started;
    private final int id;
    private float totalHealth;
    private int badOmenLevel;
    private boolean active;
    private int groupsSpawned;
    private final ServerBossInfo netherinvasionEvent = new ServerBossInfo(RAID_NAME_COMPONENT, BossInfo.Color.RED, BossInfo.Overlay.NOTCHED_10);
    private int postNetherInvasionTicks;
    private int netherinvasionCooldownTicks;
    private final Random random = new Random();
    private final int numGroups;
    private NetherInvasion.Status status;
    private int celebrationTicks;
    private Optional<BlockPos> waveSpawnPos = Optional.empty();

    public NetherInvasion(int p_i50144_1_, ServerWorld p_i50144_2_, BlockPos p_i50144_3_) {
        this.id = p_i50144_1_;
        this.level = p_i50144_2_;
        this.active = true;
        this.netherinvasionCooldownTicks = 300;
        this.netherinvasionEvent.setPercent(0.0F);
        this.center = p_i50144_3_;
        this.numGroups = this.getNumGroups(p_i50144_2_.getDifficulty());
        this.status = NetherInvasion.Status.ONGOING;
    }

    public NetherInvasion(ServerWorld p_i50145_1_, CompoundNBT p_i50145_2_) {
        this.level = p_i50145_1_;
        this.id = p_i50145_2_.getInt("Id");
        this.started = p_i50145_2_.getBoolean("Started");
        this.active = p_i50145_2_.getBoolean("Active");
        this.ticksActive = p_i50145_2_.getLong("TicksActive");
        this.badOmenLevel = p_i50145_2_.getInt("BadOmenLevel");
        this.groupsSpawned = p_i50145_2_.getInt("GroupsSpawned");
        this.netherinvasionCooldownTicks = p_i50145_2_.getInt("PreNetherInvasionTicks");
        this.postNetherInvasionTicks = p_i50145_2_.getInt("PostNetherInvasionTicks");
        this.totalHealth = p_i50145_2_.getFloat("TotalHealth");
        this.center = new BlockPos(p_i50145_2_.getInt("CX"), p_i50145_2_.getInt("CY"), p_i50145_2_.getInt("CZ"));
        this.numGroups = p_i50145_2_.getInt("NumGroups");
        this.status = NetherInvasion.Status.getByName(p_i50145_2_.getString("Status"));
        this.heroesOfTheVillage.clear();
        if (p_i50145_2_.contains("HeroesOfTheVillage", 9)) {
            ListNBT listnbt = p_i50145_2_.getList("HeroesOfTheVillage", 11);

            for(int i = 0; i < listnbt.size(); ++i) {
                this.heroesOfTheVillage.add(NBTUtil.loadUUID(listnbt.get(i)));
            }
        }

    }

    public boolean isOver() {
        return this.isVictory() || this.isLoss();
    }

    public boolean isBetweenWaves() {
        return this.hasFirstWaveSpawned() && this.getTotalNetherInvadersAlive() == 0 && this.netherinvasionCooldownTicks > 0;
    }

    public boolean hasFirstWaveSpawned() {
        return this.groupsSpawned > 0;
    }

    public boolean isStopped() {
        return this.status == NetherInvasion.Status.STOPPED;
    }

    public boolean isVictory() {
        return this.status == NetherInvasion.Status.VICTORY;
    }

    public boolean isLoss() {
        return this.status == NetherInvasion.Status.LOSS;
    }

    public World getLevel() {
        return this.level;
    }

    public boolean isStarted() {
        return this.started;
    }

    public int getGroupsSpawned() {
        return this.groupsSpawned;
    }

    private Predicate<ServerPlayerEntity> validPlayer() {
        return (p_221302_1_) -> {
            BlockPos blockpos = p_221302_1_.blockPosition();
            return p_221302_1_.isAlive() && this.level.getInvasionAt(blockpos) == this;
        };
    }

    private void updatePlayers() {
        Set<ServerPlayerEntity> set = Sets.newHashSet(this.netherinvasionEvent.getPlayers());
        List<ServerPlayerEntity> list = this.level.getPlayers(this.validPlayer());

        for(ServerPlayerEntity serverplayerentity : list) {
            if (!set.contains(serverplayerentity)) {
                this.netherinvasionEvent.addPlayer(serverplayerentity);
            }
        }

        for(ServerPlayerEntity serverplayerentity1 : set) {
            if (!list.contains(serverplayerentity1)) {
                this.netherinvasionEvent.removePlayer(serverplayerentity1);
            }
        }

    }

    public int getMaxBadOmenLevel() {
        return 5;
    }

    public int getBadOmenLevel() {
        return this.badOmenLevel;
    }

    public void setBadOmenLevel(int level) {
        this.badOmenLevel = level;
    }

    public void absorbBadOmen(PlayerEntity p_221309_1_) {
        if (p_221309_1_.hasEffect(Effects.BAD_OMEN)) {
            this.badOmenLevel += p_221309_1_.getEffect(Effects.BAD_OMEN).getAmplifier() + 1;
            this.badOmenLevel = MathHelper.clamp(this.badOmenLevel, 0, this.getMaxBadOmenLevel());
        }

        p_221309_1_.removeEffect(Effects.BAD_OMEN);
    }

    public void stop() {
        this.active = false;
        this.netherinvasionEvent.removeAllPlayers();
        this.status = NetherInvasion.Status.STOPPED;
    }

    public Collection<BlockPos> getCenterAsCollection() {
        return Collections.singletonList(this.getCenter());
    }

    public void tick() {
        if (!this.isStopped()) {
            if (this.status == NetherInvasion.Status.ONGOING) {
                boolean flag = this.active;
                this.active = this.level.hasChunkAt(this.center);
                if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
                    this.stop();
                    return;
                }

                if (flag != this.active) {
                    this.netherinvasionEvent.setVisible(this.active);
                }

                if (!this.active) {
                    return;
                }

                if (!this.level.isVillage(this.center)) {
                    this.moveNetherInvasionCenterToNearbyVillageSection();
                }

                if (!this.level.isVillage(this.center)) {
                    if (this.groupsSpawned > 0) {
                        this.status = NetherInvasion.Status.LOSS;
                    } else {
                        this.stop();
                    }
                }

                ++this.ticksActive;
                if (this.ticksActive >= 48000L) {
                    this.stop();
                    return;
                }

                int i = this.getTotalNetherInvadersAlive();
                if (i == 0 && this.hasMoreWaves()) {
                    if (this.netherinvasionCooldownTicks <= 0) {
                        if (this.netherinvasionCooldownTicks == 0 && this.groupsSpawned > 0) {
                            this.netherinvasionCooldownTicks = 300;
                            this.netherinvasionEvent.setName(RAID_NAME_COMPONENT);
                            return;
                        }
                    } else {
                        boolean flag1 = this.waveSpawnPos.isPresent();
                        boolean flag2 = !flag1 && this.netherinvasionCooldownTicks % 5 == 0;
                        if (flag1 && !this.level.getChunkSource().isEntityTickingChunk(new ChunkPos(this.waveSpawnPos.get()))) {
                            flag2 = true;
                        }

                        if (flag2) {
                            int j = 0;
                            if (this.netherinvasionCooldownTicks < 100) {
                                j = 1;
                            } else if (this.netherinvasionCooldownTicks < 40) {
                                j = 2;
                            }

                            this.waveSpawnPos = this.getValidSpawnPos(j);
                        }

                        if (this.netherinvasionCooldownTicks == 300 || this.netherinvasionCooldownTicks % 20 == 0) {
                            this.updatePlayers();
                        }

                        --this.netherinvasionCooldownTicks;
                        this.netherinvasionEvent.setPercent(MathHelper.clamp((float)(300 - this.netherinvasionCooldownTicks) / 300.0F, 0.0F, 1.0F));
                    }
                }

                if (this.ticksActive % 20L == 0L) {
                    this.updatePlayers();
                    this.updateNetherInvaders();
                    if (i > 0) {
                        if (i <= 2) {
                            this.netherinvasionEvent.setName(RAID_NAME_COMPONENT.copy().append(" - ").append(new TranslationTextComponent("event.minecraft.netherinvasion.netherinvasioners_remaining", i)));
                        } else {
                            this.netherinvasionEvent.setName(RAID_NAME_COMPONENT);
                        }
                    } else {
                        this.netherinvasionEvent.setName(RAID_NAME_COMPONENT);
                    }
                }

                boolean flag3 = false;
                int k = 0;

                while(this.shouldSpawnGroup()) {
                    BlockPos blockpos = this.waveSpawnPos.isPresent() ? this.waveSpawnPos.get() : this.findRandomSpawnPos(k, 20);
                    if (blockpos != null) {
                        this.started = true;
                        this.spawnGroup(blockpos);
                        if (!flag3) {
                            this.playSound(blockpos);
                            flag3 = true;
                        }
                    } else {
                        ++k;
                    }

                    if (k > 3) {
                        this.stop();
                        break;
                    }
                }

                if (this.isStarted() && !this.hasMoreWaves() && i == 0) {
                    if (this.postNetherInvasionTicks < 40) {
                        ++this.postNetherInvasionTicks;
                    } else {
                        this.status = NetherInvasion.Status.VICTORY;

                        for(UUID uuid : this.heroesOfTheVillage) {
                            Entity entity = this.level.getEntity(uuid);
                            if (entity instanceof LivingEntity && !entity.isSpectator()) {
                                LivingEntity livingentity = (LivingEntity)entity;
                                livingentity.addEffect(new EffectInstance(Effects.HERO_OF_THE_VILLAGE, 48000, this.badOmenLevel - 1, false, false, true));
                                if (livingentity instanceof ServerPlayerEntity) {
                                    ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)livingentity;
                                    serverplayerentity.awardStat(Stats.RAID_WIN);
                                    CriteriaTriggers.RAID_WIN.trigger(serverplayerentity);
                                }
                            }
                        }
                    }
                }

                this.setDirty();
            } else if (this.isOver()) {
                ++this.celebrationTicks;
                if (this.celebrationTicks >= 600) {
                    this.stop();
                    return;
                }

                if (this.celebrationTicks % 20 == 0) {
                    this.updatePlayers();
                    this.netherinvasionEvent.setVisible(true);
                    if (this.isVictory()) {
                        this.netherinvasionEvent.setPercent(0.0F);
                        this.netherinvasionEvent.setName(RAID_BAR_VICTORY_COMPONENT);
                    } else {
                        this.netherinvasionEvent.setName(RAID_BAR_DEFEAT_COMPONENT);
                    }
                }
            }

        }
    }

    private void moveNetherInvasionCenterToNearbyVillageSection() {
        Stream<SectionPos> stream = SectionPos.cube(SectionPos.of(this.center), 2);
        stream.filter(this.level::isVillage).map(SectionPos::center).min(Comparator.comparingDouble((p_223025_1_) -> {
            return p_223025_1_.distSqr(this.center);
        })).ifPresent(this::setCenter);
    }

    private Optional<BlockPos> getValidSpawnPos(int p_221313_1_) {
        for(int i = 0; i < 3; ++i) {
            BlockPos blockpos = this.findRandomSpawnPos(p_221313_1_, 1);
            if (blockpos != null) {
                return Optional.of(blockpos);
            }
        }

        return Optional.empty();
    }

    private boolean hasMoreWaves() {
        if (this.hasBonusWave()) {
            return !this.hasSpawnedBonusWave();
        } else {
            return !this.isFinalWave();
        }
    }

    private boolean isFinalWave() {
        return this.getGroupsSpawned() == this.numGroups;
    }

    private boolean hasBonusWave() {
        return this.badOmenLevel > 1;
    }

    private boolean hasSpawnedBonusWave() {
        return this.getGroupsSpawned() > this.numGroups;
    }

    private boolean shouldSpawnBonusGroup() {
        return this.isFinalWave() && this.getTotalNetherInvadersAlive() == 0 && this.hasBonusWave();
    }

    private void updateNetherInvaders() {
        Iterator<Set<AbstractNetherInvaderEntity>> iterator = this.groupNetherInvaderMap.values().iterator();
        Set<AbstractNetherInvaderEntity> set = Sets.newHashSet();

        while(iterator.hasNext()) {
            Set<AbstractNetherInvaderEntity> set1 = iterator.next();

            for(AbstractNetherInvaderEntity abstractnetherinvasionerentity : set1) {
                BlockPos blockpos = abstractnetherinvasionerentity.blockPosition();
                if (!abstractnetherinvasionerentity.removed && abstractnetherinvasionerentity.level.dimension() == this.level.dimension() && !(this.center.distSqr(blockpos) >= 12544.0D)) {
                    if (abstractnetherinvasionerentity.tickCount > 600) {
                        if (this.level.getEntity(abstractnetherinvasionerentity.getUUID()) == null) {
                            set.add(abstractnetherinvasionerentity);
                        }

                        if (!this.level.isVillage(blockpos) && abstractnetherinvasionerentity.getNoActionTime() > 2400) {
                            abstractnetherinvasionerentity.setTicksOutsideInvasion(abstractnetherinvasionerentity.getTicksOutsideInvasion() + 1);
                        }

                        if (abstractnetherinvasionerentity.getTicksOutsideInvasion() >= 30) {
                            set.add(abstractnetherinvasionerentity);
                        }
                    }
                } else {
                    set.add(abstractnetherinvasionerentity);
                }
            }
        }

        for(AbstractNetherInvaderEntity abstractnetherinvasionerentity1 : set) {
            this.removeFromNetherInvasion(abstractnetherinvasionerentity1, true);
        }

    }

    private void playSound(BlockPos p_221293_1_) {
        float f = 13.0F;
        int i = 64;
        Collection<ServerPlayerEntity> collection = this.netherinvasionEvent.getPlayers();

        for(ServerPlayerEntity serverplayerentity : this.level.players()) {
            Vector3d vector3d = serverplayerentity.position();
            Vector3d vector3d1 = Vector3d.atCenterOf(p_221293_1_);
            float f1 = MathHelper.sqrt((vector3d1.x - vector3d.x) * (vector3d1.x - vector3d.x) + (vector3d1.z - vector3d.z) * (vector3d1.z - vector3d.z));
            double d0 = vector3d.x + (double)(13.0F / f1) * (vector3d1.x - vector3d.x);
            double d1 = vector3d.z + (double)(13.0F / f1) * (vector3d1.z - vector3d.z);
            if (f1 <= 64.0F || collection.contains(serverplayerentity)) {
                serverplayerentity.connection.send(new SPlaySoundEffectPacket(SoundEvents.RAID_HORN, SoundCategory.NEUTRAL, d0, serverplayerentity.getY(), d1, 64.0F, 1.0F));
            }
        }

    }

    private void spawnGroup(BlockPos spawnPosition) {
        boolean hasLeader = false;
        int nextGroupIndex = this.groupsSpawned + 1;
        this.totalHealth = 0.0F;
        DifficultyInstance currentDifficulty = this.level.getCurrentDifficultyAt(spawnPosition);
        boolean shouldSpawnBonus = this.shouldSpawnBonusGroup();

        for(NetherInvasion.WaveMember waveMember : NetherInvasion.WaveMember.VALUES) {
            int numSpawns = this.getDefaultNumSpawns(waveMember, nextGroupIndex, shouldSpawnBonus)
                    + this.getPotentialBonusSpawns(waveMember, this.random, nextGroupIndex, currentDifficulty, shouldSpawnBonus);
            int ravagerSpawnCount = 0;

            for(int spawnIndex = 0; spawnIndex < numSpawns; ++spawnIndex) {
                AbstractNetherInvaderEntity netherinvasioner = waveMember.entityType.create(this.level);
                if (!hasLeader && netherinvasioner.canBeLeader()) {
                    netherinvasioner.setPatrolLeader(true);
                    netherinvasioner.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(netherinvasioner.getAttributeBaseValue(Attributes.FOLLOW_RANGE) * 2);
                    this.setLeader(nextGroupIndex, netherinvasioner);
                    hasLeader = true;
                }

                this.joinNetherInvasion(nextGroupIndex, netherinvasioner, spawnPosition, false);
                if (waveMember.entityType == EntityType.BLAZE) {
                    AbstractNetherInvaderEntity rider = null;
                    if (nextGroupIndex == this.getNumGroups(Difficulty.NORMAL)) {
                        rider = EntityType.PIGLIN.create(this.level);
                    } else if (nextGroupIndex >= this.getNumGroups(Difficulty.HARD)) {
                        if (ravagerSpawnCount == 0) {
                            rider = EntityType.PIGLIN_BRUTE.create(this.level);
                        } else {
                            rider = EntityType.PIGLIN_BRUTE.create(this.level);
                        }
                    }

                    ++ravagerSpawnCount;
                    if (rider != null) {
                        this.joinNetherInvasion(nextGroupIndex, rider, spawnPosition, false);
                        rider.moveTo(spawnPosition, 0.0F, 0.0F);
                        rider.startRiding(netherinvasioner);
                    }
                }
            }
        }

        this.waveSpawnPos = Optional.empty();
        ++this.groupsSpawned;
        this.updateBossbar();
        this.setDirty();
    }

    public void joinNetherInvasion(int p_221317_1_, AbstractNetherInvaderEntity p_221317_2_, @Nullable BlockPos p_221317_3_, boolean p_221317_4_) {
        boolean flag = this.addWaveMob(p_221317_1_, p_221317_2_);
        if (flag) {
            p_221317_2_.setCurrentRaid(this);
            p_221317_2_.setWave(p_221317_1_);
            p_221317_2_.setCanJoinRaid(true);
            p_221317_2_.setTicksOutsideRaid(0);
            if (!p_221317_4_ && p_221317_3_ != null) {
                p_221317_2_.setPos((double)p_221317_3_.getX() + 0.5D, (double)p_221317_3_.getY() + 1.0D, (double)p_221317_3_.getZ() + 0.5D);
                p_221317_2_.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(p_221317_3_), SpawnReason.EVENT, (ILivingEntityData)null, (CompoundNBT)null);
                p_221317_2_.applyRaidBuffs(p_221317_1_, false);
                p_221317_2_.setOnGround(true);
                if (p_221317_2_ instanceof AbstractPiglinEntity) {
                    AbstractPiglinEntity piglin = (AbstractPiglinEntity) p_221317_2_;
                    piglin.setImmuneToZombification(true);
                    this.level.addFreshEntityWithPassengers(piglin);
                } else {
                    this.level.addFreshEntityWithPassengers(p_221317_2_);
                }
            }
        }

    }

    public void updateBossbar() {
        this.netherinvasionEvent.setPercent(MathHelper.clamp(this.getHealthOfLivingNetherInvaders() / this.totalHealth, 0.0F, 1.0F));
    }

    public float getHealthOfLivingNetherInvaders() {
        float f = 0.0F;

        for(Set<AbstractNetherInvaderEntity> set : this.groupNetherInvaderMap.values()) {
            for(AbstractNetherInvaderEntity abstractnetherinvasionerentity : set) {
                f += abstractnetherinvasionerentity.getHealth();
            }
        }

        return f;
    }

    private boolean shouldSpawnGroup() {
        return this.netherinvasionCooldownTicks == 0 && (this.groupsSpawned < this.numGroups || this.shouldSpawnBonusGroup()) && this.getTotalNetherInvadersAlive() == 0;
    }

    public int getTotalNetherInvadersAlive() {
        return this.groupNetherInvaderMap.values().stream().mapToInt(Set::size).sum();
    }

    public Set<AbstractNetherInvaderEntity> getAllNetherInvaders() {
        return this.groupNetherInvaderMap.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
    }

    public void removeFromNetherInvasion(AbstractNetherInvaderEntity p_221322_1_, boolean p_221322_2_) {
        Set<AbstractNetherInvaderEntity> set = this.groupNetherInvaderMap.get(p_221322_1_.getWave());
        if (set != null) {
            boolean flag = set.remove(p_221322_1_);
            if (flag) {
                if (p_221322_2_) {
                    this.totalHealth -= p_221322_1_.getHealth();
                }

                p_221322_1_.setCurrentRaid((NetherInvasion)null);
                this.updateBossbar();
                this.setDirty();
            }
        }

    }

    private void setDirty() {
        this.level.getInvasions().setDirty();
    }

    public static ItemStack getLeaderBannerInstance() {
        ItemStack itemstack = new ItemStack(Items.WHITE_BANNER);
        CompoundNBT compoundnbt = itemstack.getOrCreateTagElement("BlockEntityTag");
        ListNBT listnbt = (new BannerPattern.Builder()).addPattern(BannerPattern.RHOMBUS_MIDDLE, DyeColor.CYAN).addPattern(BannerPattern.STRIPE_BOTTOM, DyeColor.LIGHT_GRAY).addPattern(BannerPattern.STRIPE_CENTER, DyeColor.GRAY).addPattern(BannerPattern.BORDER, DyeColor.LIGHT_GRAY).addPattern(BannerPattern.STRIPE_MIDDLE, DyeColor.BLACK).addPattern(BannerPattern.HALF_HORIZONTAL, DyeColor.LIGHT_GRAY).addPattern(BannerPattern.CIRCLE_MIDDLE, DyeColor.LIGHT_GRAY).addPattern(BannerPattern.BORDER, DyeColor.BLACK).toListTag();
        compoundnbt.put("Patterns", listnbt);
        itemstack.hideTooltipPart(ItemStack.TooltipDisplayFlags.ADDITIONAL);
        itemstack.setHoverName((new TranslationTextComponent("block.minecraft.ominous_banner")).withStyle(TextFormatting.GOLD));
        return itemstack;
    }

    @Nullable
    public AbstractNetherInvaderEntity getLeader(int p_221332_1_) {
        return this.groupToLeaderMap.get(p_221332_1_);
    }

    @Nullable
    private BlockPos findRandomSpawnPos(int p_221298_1_, int p_221298_2_) {
        int i = p_221298_1_ == 0 ? 2 : 2 - p_221298_1_;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for(int i1 = 0; i1 < p_221298_2_; ++i1) {
            float f = this.level.random.nextFloat() * ((float)Math.PI * 2F);
            int j = this.center.getX() + MathHelper.floor(MathHelper.cos(f) * 32.0F * (float)i) + this.level.random.nextInt(5);
            int l = this.center.getZ() + MathHelper.floor(MathHelper.sin(f) * 32.0F * (float)i) + this.level.random.nextInt(5);
            int k = this.level.getHeight(Heightmap.Type.WORLD_SURFACE, j, l);
            blockpos$mutable.set(j, k, l);
            if ((!this.level.isVillage(blockpos$mutable) || p_221298_1_ >= 2) && this.level.hasChunksAt(blockpos$mutable.getX() - 10, blockpos$mutable.getY() - 10, blockpos$mutable.getZ() - 10, blockpos$mutable.getX() + 10, blockpos$mutable.getY() + 10, blockpos$mutable.getZ() + 10) && this.level.getChunkSource().isEntityTickingChunk(new ChunkPos(blockpos$mutable)) && (WorldEntitySpawner.isSpawnPositionOk(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, this.level, blockpos$mutable, EntityType.RAVAGER) || this.level.getBlockState(blockpos$mutable.below()).is(Blocks.SNOW) && this.level.getBlockState(blockpos$mutable).isAir())) {
                return blockpos$mutable;
            }
        }

        return null;
    }

    private boolean addWaveMob(int p_221287_1_, AbstractNetherInvaderEntity p_221287_2_) {
        return this.addWaveMob(p_221287_1_, p_221287_2_, true);
    }

    public boolean addWaveMob(int p_221300_1_, AbstractNetherInvaderEntity p_221300_2_, boolean p_221300_3_) {
        this.groupNetherInvaderMap.computeIfAbsent(p_221300_1_, (p_221323_0_) -> {
            return Sets.newHashSet();
        });
        Set<AbstractNetherInvaderEntity> set = this.groupNetherInvaderMap.get(p_221300_1_);
        AbstractNetherInvaderEntity abstractnetherinvasionerentity = null;

        for(AbstractNetherInvaderEntity abstractnetherinvasionerentity1 : set) {
            if (abstractnetherinvasionerentity1.getUUID().equals(p_221300_2_.getUUID())) {
                abstractnetherinvasionerentity = abstractnetherinvasionerentity1;
                break;
            }
        }

        if (abstractnetherinvasionerentity != null) {
            set.remove(abstractnetherinvasionerentity);
            set.add(p_221300_2_);
        }

        set.add(p_221300_2_);
        if (p_221300_3_) {
            this.totalHealth += p_221300_2_.getHealth();
        }

        this.updateBossbar();
        this.setDirty();
        return true;
    }

    public void setLeader(int p_221324_1_, AbstractNetherInvaderEntity p_221324_2_) {
        this.groupToLeaderMap.put(p_221324_1_, p_221324_2_);
        p_221324_2_.setItemSlot(EquipmentSlotType.HEAD, getLeaderBannerInstance());
        p_221324_2_.setDropChance(EquipmentSlotType.HEAD, 2.0F);
    }

    public void removeLeader(int p_221296_1_) {
        this.groupToLeaderMap.remove(p_221296_1_);
    }

    public BlockPos getCenter() {
        return this.center;
    }

    private void setCenter(BlockPos p_223024_1_) {
        this.center = p_223024_1_;
    }

    public int getId() {
        return this.id;
    }

    private int getDefaultNumSpawns(NetherInvasion.WaveMember p_221330_1_, int p_221330_2_, boolean p_221330_3_) {
        return p_221330_3_ ? p_221330_1_.spawnsPerWaveBeforeBonus[this.numGroups] : p_221330_1_.spawnsPerWaveBeforeBonus[p_221330_2_];
    }

    private int getPotentialBonusSpawns(NetherInvasion.WaveMember waveMember, Random randomGenerator, int waveNumber, DifficultyInstance difficultyInstance, boolean specialCondition) {
        Difficulty difficulty = difficultyInstance.getDifficulty();
        boolean isEasyDifficulty = difficulty == Difficulty.EASY;
        boolean isNormalDifficulty = difficulty == Difficulty.NORMAL;
        int potentialSpawns;
        switch(waveMember) {
            case WITCH:
                if (isEasyDifficulty || waveNumber <= 2 || waveNumber == 4) {
                    return 0;
                }
                potentialSpawns = 1;
                break;
            case PILLAGER:
            case VINDICATOR:
                if (isEasyDifficulty) {
                    potentialSpawns = randomGenerator.nextInt(2);
                } else if (isNormalDifficulty) {
                    potentialSpawns = 1;
                } else {
                    potentialSpawns = 2;
                }
                break;
            case RAVAGER:
                potentialSpawns = !isEasyDifficulty && specialCondition ? 3 : 1;
                break;
            default:
                return 0;
        }

        return potentialSpawns > 0 ? randomGenerator.nextInt(potentialSpawns + 1) : 0;
    }

    public boolean isActive() {
        return this.active;
    }

    public CompoundNBT save(CompoundNBT p_221326_1_) {
        p_221326_1_.putInt("Id", this.id);
        p_221326_1_.putBoolean("Started", this.started);
        p_221326_1_.putBoolean("Active", this.active);
        p_221326_1_.putLong("TicksActive", this.ticksActive);
        p_221326_1_.putInt("BadOmenLevel", this.badOmenLevel);
        p_221326_1_.putInt("GroupsSpawned", this.groupsSpawned);
        p_221326_1_.putInt("PreNetherInvasionTicks", this.netherinvasionCooldownTicks);
        p_221326_1_.putInt("PostNetherInvasionTicks", this.postNetherInvasionTicks);
        p_221326_1_.putFloat("TotalHealth", this.totalHealth);
        p_221326_1_.putInt("NumGroups", this.numGroups);
        p_221326_1_.putString("Status", this.status.getName());
        p_221326_1_.putInt("CX", this.center.getX());
        p_221326_1_.putInt("CY", this.center.getY());
        p_221326_1_.putInt("CZ", this.center.getZ());
        ListNBT listnbt = new ListNBT();

        for(UUID uuid : this.heroesOfTheVillage) {
            listnbt.add(NBTUtil.createUUID(uuid));
        }

        p_221326_1_.put("HeroesOfTheVillage", listnbt);
        return p_221326_1_;
    }

    public int getNumGroups(Difficulty p_221306_1_) {
        switch(p_221306_1_) {
            case EASY:
                return 3;
            case NORMAL:
                return 5;
            case HARD:
                return 7;
            default:
                return 0;
        }
    }

    public float getEnchantOdds() {
        int i = this.getBadOmenLevel();
        if (i == 2) {
            return 0.4F;
        } else if (i == 3) {
            return 0.5F;
        } else if (i == 4) {
            return 0.8F;
        } else {
            return i == 5 ? 1F : 0.0F;
        }
    }

    public void addHeroOfTheVillage(Entity p_221311_1_) {
        this.heroesOfTheVillage.add(p_221311_1_.getUUID());
    }

    static enum Status {
        ONGOING,
        VICTORY,
        LOSS,
        STOPPED;

        private static final NetherInvasion.Status[] VALUES = values();

        private static NetherInvasion.Status getByName(String p_221275_0_) {
            for(NetherInvasion.Status netherinvasion$status : VALUES) {
                if (p_221275_0_.equalsIgnoreCase(netherinvasion$status.name())) {
                    return netherinvasion$status;
                }
            }

            return ONGOING;
        }

        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    static enum WaveMember {
        VINDICATOR(EntityType.PIGLIN_BRUTE, new int[]{0, 0, 2, 0, 1, 4, 2, 5}),
        EVOKER(EntityType.PIGLIN_BRUTE, new int[]{0, 0, 0, 0, 0, 1, 1, 2}),
        PILLAGER(EntityType.PIGLIN, new int[]{0, 4, 3, 3, 4, 4, 4, 2}),
        WITCH(EntityType.BLAZE, new int[]{0, 0, 0, 0, 3, 0, 0, 1}),
        RAVAGER(EntityType.BLAZE, new int[]{0, 0, 0, 1, 0, 1, 0, 2});

        private static final NetherInvasion.WaveMember[] VALUES = values();
        private final EntityType<? extends AbstractNetherInvaderEntity> entityType;
        private final int[] spawnsPerWaveBeforeBonus;

        private WaveMember(EntityType<? extends AbstractNetherInvaderEntity> p_i50602_3_, int[] p_i50602_4_) {
            this.entityType = p_i50602_3_;
            this.spawnsPerWaveBeforeBonus = p_i50602_4_;
        }
    }
}