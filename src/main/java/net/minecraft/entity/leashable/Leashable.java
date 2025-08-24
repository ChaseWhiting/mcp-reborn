package net.minecraft.entity.leashable;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.camel.CamelEntity;
import net.minecraft.entity.goat.GoatEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.UUIDCodec;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public interface Leashable {
    public static final Logger LEASH_LOGGER = LogManager.getLogger();
    public static final String LEASH_TAG = "leash";
    public static final double MAXIMUM_ALLOWED_LEASHED_DIST = 16.0;
    public static final float SPRING_DAMPENING = 0.7f;
    public static final List<Vector3d> ENTITY_ATTACHMENT_POINT = ImmutableList.of(
            new Vector3d(0.0, 0.5, 0.5));
    public static final List<Vector3d> LEASHER_ATTACHMENT_POINT = ImmutableList.of(
            new Vector3d(0.0, 0.5, 0.0));
    public static final List<Vector3d> SHARED_QUAD_ATTACHMENT_POINTS = ImmutableList.of(
            new Vector3d(-0.5, 0.5, 0.5),
            new Vector3d(-0.5, 0.5, -0.5),
            new Vector3d(0.5, 0.5, -0.5),
            new Vector3d(0.5, 0.5, 0.5));

    @Nullable
    public LeashData getLeashData();

    public void setLeashData(@Nullable LeashData var1);

    default public boolean isLeashed() {
        return this.getLeashData() != null && this.getLeashData().leashHolder != null;
    }

    default public boolean mayBeLeashed() {
        return this.getLeashData() != null;
    }

    default public boolean canHaveALeashAttachedTo(Entity entity) {
        if (this == entity) {
            return false;
        }
        if (this.leashDistanceTo(entity) > this.leashSnapDistance()) {
            return false;
        }
        return this.canBeLeashed();
    }

    default public double leashDistanceTo(Entity entity) {
        return entity.getBoundingBox().getCenter().distanceTo(((Entity)((Object)this)).getBoundingBox().getCenter());
    }

    default public boolean canBeLeashed() {
        return true;
    }

    default public void setDelayedLeashHolderId(int n) {
        this.setLeashData(new LeashData(n));
        Leashable.dropLeash((Entity)((Object)this), false, false);
    }

    default public void readLeashData(CompoundNBT compound) {
        if (compound.contains(LEASH_TAG, 10)) {
            DataResult<LeashData> leashDataResult = LeashData.CODEC.parse(new Dynamic<>(NBTDynamicOps.INSTANCE, compound.get(LEASH_TAG)));
            leashDataResult.resultOrPartial(LEASH_LOGGER::error).ifPresentOrElse(this::setLeashData, this::removeLeash);

//            Optional<UUID> optional = Optional.of(compound.getUUID("uuid"));
//            Optional<BlockPos> optionalPos = Optional.of(new BlockPos(compound.getInt("X"), compound.getInt("Y"), compound.getInt("Z")));
//
//
//
//            LeashData leashData = null;
//            Either<UUID, BlockPos> either = null;
//
//            if (optional.isPresent()) {
//                either = Either.left(optional.get());
//            } else if (optionalPos.isPresent() && optionalPos.get().getY() != 0 && optionalPos.get().getZ() != 0 && optionalPos.get().getX() != 0) {
//                either = Either.right(optionalPos.get());
//            }
//
//            if (either != null) {
//                leashData = new LeashData(either);
//            }
//
//            if (leashData == null) {
//                this.removeLeash();
//            } else {
//                this.setLeashData(leashData);
//            }
        }/* else {
            this.removeLeash();
        }*/
    }

    default public void writeLeashData(CompoundNBT compound, @Nullable LeashData leashData) {
        if (leashData != null) {
            LeashData.CODEC.encodeStart(NBTDynamicOps.INSTANCE, leashData).resultOrPartial(LEASH_LOGGER::error).ifPresent(nbt -> {
                compound.put(LEASH_TAG, nbt);
            });

//            Either<UUID, BlockPos> leashEither = leashData.delayedLeashInfo;
//            int delayedLeashHolderID = leashData.delayedLeashHolderId;
//            Entity leashHolder = leashData.leashHolder;
//
//            CompoundNBT leashNBT = new CompoundNBT();
//
//            if (leashEither != null) {
//
//                leashEither.ifLeft(uuid -> {
//                    leashNBT.putUUID("uuid", uuid);
//                }).ifRight(blockpos -> {
//                    leashNBT.putInt("X", blockpos.getX());
//                    leashNBT.putInt("Y", blockpos.getY());
//                    leashNBT.putInt("Z", blockpos.getZ());
//                });
//            } else {
//                if (leashHolder != null) {
//                    leashNBT.putUUID("uuid", leashHolder.getUUID());
//                }
//
//                //leashNBT.putInt("delayedID", delayedLeashHolderID);
//            }
//
//            compound.put(LEASH_TAG, leashNBT);
        }




    }

    private static <E extends Entity> void restoreLeashFromSave(E e, LeashData leashData) {
        Object object;
        if (leashData.delayedLeashInfo != null && (object = e.level()) instanceof ServerWorld) {
            ServerWorld serverLevel = (ServerWorld)object;
            object = leashData.delayedLeashInfo.left();
            Optional optional = leashData.delayedLeashInfo.right();
            if (((Optional)object).isPresent()) {
                Entity entity = serverLevel.getEntity((UUID)((Optional)object).get());
                if (entity != null) {
                    Leashable.setLeashedTo(e, entity, true);
                    return;
                }
            } else if (optional.isPresent()) {
                Leashable.setLeashedTo(e, LeashKnotEntity.getOrCreateKnot(serverLevel, (BlockPos)optional.get()), true);
                return;
            }
            if (e.tickCount > 100) {
                e.spawnAtLocation(serverLevel, Items.LEAD);
                ((Leashable)((Object)e)).setLeashData(null);
            }
        }
    }

    default public void dropLeash() {
        Leashable.dropLeash((Entity)((Object)this), true, true);
    }

    default public void removeLeash() {
        Leashable.dropLeash((Entity)((Object)this), true, false);
    }

    default public void onLeashRemoved() {
    }

    private static <E extends Entity> void dropLeash(E e, boolean bl, boolean bl2) {
        LeashData leashData = ((Leashable)((Object)e)).getLeashData();
        if (leashData != null && leashData.leashHolder != null) {
            ((Leashable)((Object)e)).setLeashData(null);
            ((Leashable)((Object)e)).onLeashRemoved();
            World level = e.level();
            if (level instanceof ServerWorld serverLevel) {
                if (bl2) {
                    e.spawnAtLocation(serverLevel, Items.LEAD);
                }
                if (bl) {
                    serverLevel.getChunkSource().broadcast(e, new SMountEntityPacket(e, null));
                }
                leashData.leashHolder.notifyLeasheeRemoved((Leashable)((Object)e));
            }
        }
    }

    public static <E extends Entity> void tickLeash(ServerWorld serverLevel, E e) {
        Entity entity;
        LeashData leashData = ((Leashable)((Object)e)).getLeashData();
        if (leashData != null && leashData.delayedLeashInfo != null) {
            Leashable.restoreLeashFromSave(e, leashData);
        }
        if (leashData == null || leashData.leashHolder == null) {
            return;
        }
        if (!e.isAlive() || !leashData.leashHolder.isAlive()) {
            if (serverLevel.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                ((Leashable)((Object)e)).dropLeash();
            } else {
                ((Leashable)((Object)e)).removeLeash();
            }
        }
        if ((entity = ((Leashable)((Object)e)).getLeashHolder()) != null && entity.level() == e.level()) {
            double d = ((Leashable)((Object)e)).leashDistanceTo(entity);
            ((Leashable)((Object)e)).whenLeashedTo(entity);
            if (d > ((Leashable)((Object)e)).leashSnapDistance()) {
                serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.LEAD_BREAK, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                ((Leashable)((Object)e)).leashTooFarBehaviour();
            } else if (d > ((Leashable)((Object)e)).leashElasticDistance() - (double)entity.getBbWidth() - (double)e.getBbWidth() && ((Leashable)((Object)e)).checkElasticInteractions(entity, leashData)) {
                ((Leashable)((Object)e)).onElasticLeashPull();
            } else {
                ((Leashable)((Object)e)).closeRangeLeashBehaviour(entity);
            }
            e.setYRot((float)((double)e.getYRot() - leashData.angularMomentum));
            leashData.angularMomentum *= (double)Leashable.angularFriction(e);
        }
    }

    default public void onElasticLeashPull() {
        Entity entity = (Entity)((Object)this);
        entity.checkFallDistanceAccumulation();
    }

    default public double leashSnapDistance() {
        return this.getLeashHandling().tooFarDistance();
    }

    default public double leashElasticDistance() {
        return this.getLeashHandling().elasticDistance();
    }

    public static <E extends Entity> float angularFriction(E e) {
        if (e.isOnGround()) {
            return e.level().getBlockState(e.directlyBelow()).getBlock().getFriction() * 0.91f;
        }
        if (e.isInLiquid()) {
            return 0.8f;
        }
        return 0.91f;
    }

    default public void whenLeashedTo(Entity entity) {
        entity.notifyLeashHolder(this);
    }

    default public void leashTooFarBehaviour() {
        this.dropLeash();
    }

    default public void closeRangeLeashBehaviour(Entity entity) {
    }


    default public boolean checkElasticInteractions(Entity entity, LeashData leashData) {
        boolean bl = entity.supportQuadLeashAsHolder() && this.supportQuadLeash();
        List<Wrench> list = Leashable.computeElasticInteraction(((Entity)this), entity, bl ? SHARED_QUAD_ATTACHMENT_POINTS : ENTITY_ATTACHMENT_POINT, bl ? SHARED_QUAD_ATTACHMENT_POINTS : LEASHER_ATTACHMENT_POINT);
        if (list.isEmpty()) {
            return false;
        }
        Wrench wrench = Wrench.accumulate(list).scale(bl ? 0.25 : 1.0);
        leashData.angularMomentum += this.getLeashHandling().torsionalElasticity() * wrench.torque();
        Vector3d vec3 = Leashable.getHolderMovement(entity).subtract(((Entity)(this)).getKnownMovement());
        ((Entity)(this)).addDeltaMovement(wrench.force().multiply(this.getLeashHandling().elasticityPerAxis()).add(vec3.scale(this.getLeashHandling().stiffness())));
        return true;
    }

    private static Vector3d getHolderMovement(Entity entity) {
        Mob mob;
        if (entity instanceof Mob && (mob = (Mob)entity).isNoAi()) {
            return Vector3d.ZERO;
        }
        return entity.getKnownMovement();
    }

    private static <E extends Entity> List<Wrench> computeElasticInteraction(E e, Entity entity, List<Vector3d> list, List<Vector3d> list2) {
        double d = ((Leashable)((Object)e)).leashElasticDistance();
        Vector3d vec3 = Leashable.getHolderMovement(e);
        float f = e.getYRot() * ((float)Math.PI / 180);
        Vector3d vec32 = new Vector3d(e.getBbWidth(), e.getBbHeight(), e.getBbWidth());
        float f2 = entity.getYRot() * ((float)Math.PI / 180);
        Vector3d vec33 = new Vector3d(entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth());
        ArrayList<Wrench> arrayList = new ArrayList<Wrench>();
        for (int i = 0; i < list.size(); ++i) {
            Vector3d vec34 = list.get(i).multiply(vec32).yRot(-f);
            Vector3d vec35 = e.position().add(vec34);
            Vector3d vec36 = list2.get(i).multiply(vec33).yRot(-f2);
            Vector3d vec37 = entity.position().add(vec36);
            Leashable.computeDampenedSpringInteraction(vec37, vec35, d, vec3, vec34).ifPresent(arrayList::add);
        }
        return arrayList;
    }

    private static Optional<Wrench> computeDampenedSpringInteraction(Vector3d vec3, Vector3d vec32, double d, Vector3d vec33, Vector3d vec34) {
        boolean bl;
        double d2 = vec32.distanceTo(vec3);
        if (d2 < d) {
            return Optional.empty();
        }
        Vector3d vec35 = vec3.subtract(vec32).normalize().scale(d2 - d);
        double d3 = Wrench.torqueFromForce(vec34, vec35);
        boolean bl2 = bl = vec33.dot(vec35) >= 0.0;
        if (bl) {
            vec35 = vec35.scale(0.3f);
        }
        return Optional.of(new Wrench(vec35, d3));
    }

    default public boolean supportQuadLeash() {
        return false;
    }

    default public Vector3d[] getQuadLeashOffsets() {
        return Leashable.createQuadLeashOffsets((Entity)(this), 0.0, 0.5, 0.5, 0.5);
    }

    public static Vector3d[] createQuadLeashOffsets(Entity entity, double d, double d2, double d3, double d4) {
        float f = entity.getBbWidth();
        double d5 = d * (double)f;
        double d6 = d2 * (double)f;
        double d7 = d3 * (double)f;
        double d8 = d4 * (double)entity.getBbHeight();
        return new Vector3d[]{new Vector3d(-d7, d8, d6 + d5), new Vector3d(-d7, d8, -d6 + d5), new Vector3d(d7, d8, -d6 + d5), new Vector3d(d7, d8, d6 + d5)};
    }

    default public Vector3d getLeashOffset(float f) {
        return this.getLeashOffset();
    }

    default public Vector3d getLeashOffset() {
        Entity entity = (Entity)((Object)this);
        return new Vector3d(0.0, entity.getEyeHeight(), entity.getBbWidth() * 0.4f);
    }

    default public void setLeashedTo(Entity entity, boolean bl) {
        if (this == entity) {
            return;
        }
        Leashable.setLeashedTo((Entity)((Object)this), entity, bl);
    }

    private static <E extends Entity> void setLeashedTo(E e, Entity entity, boolean bl) {
        World level;
        Object object;
        LeashData leashData = ((Leashable)((Object)e)).getLeashData();
        if (leashData == null) {
            leashData = new LeashData(entity);
            ((Leashable)((Object)e)).setLeashData(leashData);
        } else {
            object = leashData.leashHolder;
            leashData.setLeashHolder(entity);
            if (object != null && object != entity) {
                ((Entity)object).notifyLeasheeRemoved((Leashable)((Object)e));
            }
        }
        if (bl && (level = e.level()) instanceof ServerWorld) {
            object = (ServerWorld)level;
            ((ServerWorld)object).getChunkSource().broadcast(e, new SMountEntityPacket(e, entity));
        }
        if (e.isPassenger()) {
            e.stopRiding();
        }
    }

    @Nullable
    default public Entity getLeashHolder() {
        return Leashable.getLeashHolder((Entity)((Object)this));
    }

    @Nullable
    private static <E extends Entity> Entity getLeashHolder(E e) {
        Entity entity;
        LeashData leashData = ((Leashable)((Object)e)).getLeashData();
        if (leashData == null) {
            return null;
        }
        if (leashData.delayedLeashHolderId != 0 && e.level().isClientSide && (entity = e.level().getEntity(leashData.delayedLeashHolderId)) instanceof Entity) {
            Entity entity2 = entity;
            leashData.setLeashHolder(entity2);
        }
        return leashData.leashHolder;
    }

    public static List<Leashable> leashableLeashedTo(Entity entity) {
        return Leashable.leashableInArea(entity, leashable -> leashable.getLeashHolder() == entity);
    }

    public static List<Leashable> leashableInArea(Entity entity, Predicate<Leashable> predicate) {
        return Leashable.leashableInArea(entity.level(), entity.getBoundingBox().getCenter(), predicate);
    }

    public static List<Leashable> leashableInArea(World level, Vector3d vec3, Predicate<Leashable> predicate) {
        double d = 32.0;
        AxisAlignedBB aABB = AxisAlignedBB.ofSize(vec3, 32.0, 32.0, 32.0);
        return level.getEntitiesOfClass(Entity.class, aABB, entity -> {
            Leashable leashable;
            return entity instanceof Leashable && predicate.test(leashable = (Leashable)((Object)entity));
        }).stream().map(Leashable.class::cast).toList();
    }

    public static final class LeashData {
        public static final Codec<LeashData> CODEC = ExtraCodecs.xor(UUIDCodec.CODEC.fieldOf("UUID").codec(), BlockPos.VANILLA_COMPOUND_CODEC).xmap(LeashData::new, leashData -> {
            Entity entity = leashData.leashHolder;
            if (entity instanceof LeashKnotEntity leashFenceKnotEntity) {
                return Either.right(leashFenceKnotEntity.getPos());
            }
            if (leashData.leashHolder != null) {
                return Either.left(leashData.leashHolder.getUUID());
            }
            return Objects.requireNonNull(leashData.delayedLeashInfo, "Invalid LeashData had no attachment");
        });
        int delayedLeashHolderId;
        @Nullable
        public Entity leashHolder;
        @Nullable
        public Either<UUID, BlockPos> delayedLeashInfo;
        public double angularMomentum;

        private LeashData(Either<UUID, BlockPos> either) {
            this.delayedLeashInfo = either;
        }

        LeashData(Entity entity) {
            this.leashHolder = entity;
        }

        LeashData(int n) {
            this.delayedLeashHolderId = n;
        }

        public void setLeashHolder(Entity entity) {
            this.leashHolder = entity;
            this.delayedLeashInfo = null;
            this.delayedLeashHolderId = 0;
        }
    }

    public record Wrench(Vector3d force, double torque) {
        static Wrench ZERO = new Wrench(Vector3d.ZERO, 0.0);

        static double torqueFromForce(Vector3d vec3, Vector3d vec32) {
            return vec3.z * vec32.x - vec3.x * vec32.z;
        }

        static Wrench accumulate(List<Wrench> list) {
            if (list.isEmpty()) {
                return ZERO;
            }
            double d = 0.0;
            double d2 = 0.0;
            double d3 = 0.0;
            double d4 = 0.0;
            for (Wrench wrench : list) {
                Vector3d vec3 = wrench.force;
                d += vec3.x;
                d2 += vec3.y;
                d3 += vec3.z;
                d4 += wrench.torque;
            }
            return new Wrench(new Vector3d(d, d2, d3), d4);
        }

        public Wrench scale(double d) {
            return new Wrench(this.force.scale(d), this.torque * d);
        }
    }

    default public LeashHandling getLeashHandling() {
        if (this instanceof SlimeEntity) {
            return LeashHandling.SLIME;
        }

        if (this instanceof IAngerable angerable && angerable.isAngry() && this.getLeashHolder() instanceof LivingEntity living && !(angerable instanceof IronGolemEntity)) {


            if (!angerable.isAngryAt(living)) {
                return LeashHandling.ANGERABLE_ANGRY;
            } else {
                return LeashHandling.ANGERABLE_ANGRY_AT_HOLDER;
            }
        }

        if (this instanceof Entity entity) {
            if (FLYING_MOBS.test(entity)) {
                return LeashHandling.FLYING_MOB;
            }
        }

        if (this instanceof LivingEntity livingEntity && livingEntity.isBaby()) {
            return LeashHandling.BABY_MOB;
        }

        if (this instanceof RavagerEntity) {
            return LeashHandling.RAVAGER;
        }

        if (this instanceof WolfEntity wolfEntity) {
            if (!wolfEntity.isTame()) {
                return LeashHandling.WOLF;
            } else {
                return LeashHandling.WOLF_TAMED;
            }
        }

        if (this instanceof GoatEntity) return LeashHandling.GOAT;

        if (this instanceof CowEntity || this instanceof MooshroomEntity) return LeashHandling.COW;

        if (this instanceof Entity entity) {
            if (LIGHT_MOBS.test(entity)) {
                return LeashHandling.LIGHT_MOB;
            }

            if (MEDIUM_MOBS.test(entity)) {
                if (entity instanceof CamelEntity camel && camel.getCarpetColor().isPresent()) {
                    if (getLeashHolder(entity) instanceof CamelEntity camel1 && camel1.getCarpetColor().isPresent()) {
                        return camel.getCarpetColor().get() == camel1.getCarpetColor().get() ? LeashHandling.CAMEL_CARPETED_TOGETHER_SAME_COLOUR : LeashHandling.CAMEL_CARPETED_TOGETHER;
                    }

                    return LeashHandling.CAMEL_CARPETED;
                }

                return LeashHandling.MEDIUM_MOB;
            }
        }

        return LeashHandling.NORMAL;
    }

    public static final Predicate<Entity> LIGHT_MOBS = entity -> {
        return entity.getBbHeight() <= 0.7F && entity.getBbWidth() <= 0.6F;
    };

    public static final Predicate<Entity> MEDIUM_MOBS = entity -> {
        return entity.getBbHeight() >= 0.85F && entity.getBbWidth() >= 0.9F;
    };

    public static final Predicate<Entity> FLYING_MOBS = entity -> {
        return entity instanceof FlyingEntity || entity instanceof IBee || entity instanceof Creature creature && (creature.getMoveControl() instanceof FlyingMovementController || creature.getNavigation() instanceof FlyingPathNavigator);
    };


    public record LeashHandling(double tooFarDistance, double elasticDistance,
                                Vector3d elasticityPerAxis, double torsionalElasticity,
                                double stiffness) {

        //tooFarDistance - The leash will instantly snap upon getting this far away from the holder
        //elasticityPerAxis - If lower, mobs will be harder to pull along
        //elasticDistance - Upon reaching this distance away from the holder, will start getting pulled towards the holder based on the elasticityPerAxis

        static LeashHandling NORMAL = new LeashHandling(12.0, 6.0,
                new Vector3d(0.8D, 0.2D, 0.8D), 10.0, 0.11);
        //For mobs with no specified leash handling yet, and don't fit the other types.

        static LeashHandling RAVAGER = new LeashHandling(10.0, 5.0,
                new Vector3d(0.4D, 0.1D, 0.4D), 6.0, 0.05
        );

        static LeashHandling WOLF = new LeashHandling(6.5, 2.5,
                new Vector3d(0.05D, 0.15D, 0.05D), 4.5, 0.008);

        static LeashHandling SLIME = new LeashHandling(24.5, 10.5,
                new Vector3d(0.45D, 1.25D, 0.45D), 6.5, 0.475); //Slimes and Magma Cubes

        static LeashHandling ANGERABLE_ANGRY = new LeashHandling(1.5, 0.5,
                new Vector3d(0.07D, 0.15D, 0.07D), 20, 0.008);
        // IAngerable mobs (Bees, Polar Bears, Wolves, which are angry at something, excluding the Iron Golems)

        static LeashHandling ANGERABLE_ANGRY_AT_HOLDER = new LeashHandling(0.5, 0.2,
                new Vector3d(0.07D, 0.15D, 0.07D), 30, 0.008);
        // IAngerable mobs (Bees, Polar Bears, Wolves, which are angry at their leash holder, excluding the Iron Golems)

        static LeashHandling WOLF_TAMED = new LeashHandling(14, 6.5,
                new Vector3d(0.6D, 0.2D, 0.6D), 9.0, 0.15);

        static LeashHandling GOAT = new LeashHandling(9.5, 5.0,
                new Vector3d(0.1D, 0.15D, 0.1D), 8.0, 0.05);

        static LeashHandling LIGHT_MOB = new LeashHandling(12.0, 4.56,
                new Vector3d(0.9D, 0.212D, 0.9D), 16.0, 0.24); //Mobs of size 0.7x0.6 and smaller

        static LeashHandling MEDIUM_MOB = new LeashHandling(10.0, 6,
                new Vector3d(0.656D, 0.15D, 0.656D), 10.0, 0.085); //Mobs of size 0.85x0.9 and bigger

        static LeashHandling CAMEL_CARPETED = new LeashHandling(9.5, 4.5,
                new Vector3d(0.856D, 0.15D, 0.856D), 5, 0.2); //Camels that are carpeted

        static LeashHandling CAMEL_CARPETED_TOGETHER = new LeashHandling(6.5, 3,
                new Vector3d(1D, 0.4D, 1D), 2, 0.4);//Camels which are carpeted and leashed to each-other

        static LeashHandling CAMEL_CARPETED_TOGETHER_SAME_COLOUR = new LeashHandling(6, 3,
                new Vector3d(1.1D, 0.4D, 1.1D), 0, 0.6);//Camels which are leashed together and have the same carpet colour

        static LeashHandling BABY_MOB = new LeashHandling(16.0, 2.0,
                new Vector3d(0.95D, 0.25D, 0.95D), 19.0, 0.3);
        //All baby mob, overriding other baby versions of already-specified ones

        static LeashHandling FLYING_MOB = new LeashHandling(16.0, 8.5,
                new Vector3d(0.85D, 0.75D, 0.85D), 1.0, 0.2885);

        static LeashHandling COW = new LeashHandling(9.375, 5.5,
                new Vector3d(0.556D, 0.135D, 0.556D), 8.5, 0.08);


    }
}

