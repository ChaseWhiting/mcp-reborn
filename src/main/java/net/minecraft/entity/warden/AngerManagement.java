package net.minecraft.entity.warden;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.UUIDCodec;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.VisibleForTesting;
import org.nd4j.shade.guava.collect.Streams;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AngerManagement {
    @VisibleForTesting
    protected static final int CONVERSION_DELAY = 2;
    @VisibleForTesting
    protected static final int MAX_ANGER = 150;
    private static final int DEFAULT_ANGER_DECREASE = 1;
    private int conversionDelay = MathHelper.randomBetweenInclusive(new Random(), 0, 2);
    int highestAnger;
    private static final Codec<Pair<UUID, Integer>> SUSPECT_ANGER_PAIR = RecordCodecBuilder.create(instance -> instance.group(UUIDCodec.CODEC.fieldOf("uuid").forGetter(Pair::getFirst), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("anger").forGetter(Pair::getSecond)).apply(instance, Pair::of));
    private final Predicate<Entity> filter;
    @VisibleForTesting
    protected final ArrayList<Entity> suspects;
    private final Sorter suspectSorter;
    @VisibleForTesting
    protected final Object2IntMap<Entity> angerBySuspect;
    @VisibleForTesting
    protected final Object2IntMap<UUID> angerByUuid;



    public static Codec<AngerManagement> codec(Predicate<Entity> predicate) {
        return RecordCodecBuilder.create(instance -> instance.group(SUSPECT_ANGER_PAIR.listOf().fieldOf("suspects").orElse(Collections.emptyList()).forGetter(AngerManagement::createUuidAngerPairs)).apply(instance, list -> new AngerManagement(predicate, (List<Pair<UUID, Integer>>)list)));
    }

    public AngerManagement(Predicate<Entity> predicate, List<Pair<UUID, Integer>> list) {
        this.filter = predicate;
        this.suspects = new ArrayList<>();
        this.suspectSorter = new Sorter(this);
        this.angerBySuspect = new Object2IntOpenHashMap<>();
        this.angerByUuid = new Object2IntOpenHashMap<>(list.size());
        list.forEach(pair -> this.angerByUuid.put(pair.getFirst(), pair.getSecond()));
    }

    private List<Pair<UUID, Integer>> createUuidAngerPairs() {
        return Streams.<Pair<UUID, Integer>>concat(
                this.suspects.stream()
                        .map(entity -> Pair.of(entity.getUUID(), this.angerBySuspect.getInt(entity))),
                this.angerByUuid.object2IntEntrySet().stream()
                        .map(entry -> Pair.of(entry.getKey(), entry.getIntValue()))
        ).collect(Collectors.toList());
    }

    public void tick(ServerWorld serverLevel, Predicate<Entity> predicate) {
        Object2IntMap.Entry<UUID> entry;

        --this.conversionDelay;
        if (this.conversionDelay <= 0) {
            this.convertFromUuids(serverLevel);
            this.conversionDelay = 2;
        }
        ObjectIterator<Object2IntMap.Entry<UUID>> objectIterator = this.angerByUuid.object2IntEntrySet().iterator();
        while (objectIterator.hasNext()) {
            entry = objectIterator.next();
            int n = entry.getIntValue();
            if (n <= 1) {
                objectIterator.remove();
            } else {
                entry.setValue(n - 1);
            }
        }

        ObjectIterator<Object2IntMap.Entry<Entity>> entityIterator = this.angerBySuspect.object2IntEntrySet().iterator();
        while (entityIterator.hasNext()) {
            Object2IntMap.Entry<Entity> entry2 = entityIterator.next();
            int n = entry2.getIntValue();
            Entity entity = entry2.getKey();

            if (n <= 1 || !predicate.test(entity)) {
                this.suspects.remove(entity);
                entityIterator.remove();
            } else {
                entry2.setValue(n - 1);
            }
        }

        this.sortAndUpdateHighestAnger();
    }

    private void sortAndUpdateHighestAnger() {
        this.highestAnger = 0;
        this.suspects.sort(this.suspectSorter);
        if (this.suspects.size() == 1) {
            this.highestAnger = this.angerBySuspect.getInt((Object)this.suspects.get(0));
        }
    }

    public int increaseAnger(Entity entity2, int n) {
        boolean bl = !this.angerBySuspect.containsKey(entity2);
        int n3 = this.angerBySuspect.computeInt(entity2, (entity, n2) -> Math.min(150, (n2 == null ? 0 : n2) + n));
        if (bl) {
            int n4 = this.angerByUuid.removeInt(entity2.getUUID());
            this.angerBySuspect.put(entity2, n3 += n4);
            this.suspects.add(entity2);
        }
        this.sortAndUpdateHighestAnger();
        return n3;
    }

    private void convertFromUuids(ServerWorld serverLevel) {
        ObjectIterator<Object2IntMap.Entry<UUID>> objectIterator = this.angerByUuid.object2IntEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Object2IntMap.Entry<UUID> entry = objectIterator.next();
            int n = entry.getIntValue();
            Entity entity = serverLevel.getEntity((UUID)entry.getKey());
            if (entity == null) continue;
            this.angerBySuspect.put(entity, n);
            this.suspects.add(entity);
            objectIterator.remove();
        }
    }

    public void clearAnger(Entity entity) {
        this.angerBySuspect.removeInt((Object)entity);
        this.suspects.remove(entity);
        this.sortAndUpdateHighestAnger();
    }

    @Nullable
    private Entity getTopSuspect() {
        return this.suspects.stream().filter(this.filter).findFirst().orElse(null);
    }

    public int getActiveAnger(@Nullable Entity entity) {
        return entity == null ? this.highestAnger : this.angerBySuspect.getInt((Object)entity);
    }

    public Optional<LivingEntity> getActiveEntity() {
        return Optional.ofNullable(this.getTopSuspect()).filter(entity -> entity instanceof LivingEntity).map(entity -> (LivingEntity)entity);
    }

    protected static class Sorter implements Comparator<Entity> {
        AngerManagement angerManagement;

        public Sorter(AngerManagement angerManagement) {
            this.angerManagement = angerManagement;
        }

        @Override
        public int compare(Entity entity, Entity entity2) {
            boolean bl;
            if (entity.equals(entity2)) {
                return 0;
            }

            int n = this.angerManagement.angerBySuspect.getOrDefault(entity, 0);
            int n2 = this.angerManagement.angerBySuspect.getOrDefault(entity2, 0);

            this.angerManagement.highestAnger = Math.max(this.angerManagement.highestAnger, Math.max(n, n2));
            boolean bl2 = AngerLevel.byAnger(n).isAngry();
            if (bl2 != (bl = AngerLevel.byAnger(n2).isAngry())) {
                return bl2 ? -1 : 1;
            }
            boolean bl3 = entity instanceof PlayerEntity;
            boolean bl4 = entity2 instanceof PlayerEntity;
            if (bl3 != bl4) {
                return bl3 ? -1 : 1;
            }

            return Integer.compare(n2, n);
        }
    }

}
