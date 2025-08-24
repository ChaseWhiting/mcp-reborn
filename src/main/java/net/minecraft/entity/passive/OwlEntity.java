package net.minecraft.entity.passive;

import com.google.common.base.Optional;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.entity.pathfinding.owl.OwlFlyingMovementController;
import net.minecraft.entity.pathfinding.owl.OwlFlyingPathNavigator;
import net.minecraft.entity.pathfinding.owl.OwlWaterAvoidingRandomFlyingGoal;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;

public class OwlEntity extends Animal implements IFlyingAnimal {

    private static final DataParameter<Integer> DATA_TYPE_ID = EntityDataManager.defineId(OwlEntity.class, DataSerializers.INT);
    private static final DataParameter<Byte> DATA_FLAGS_ID = EntityDataManager.defineId(OwlEntity.class, DataSerializers.BYTE);

    private static final int FLAG_PERCHING = 1;
    private static final int FLAG_HUNTING = 2;

    public OwlEntity(EntityType<? extends OwlEntity> owl, World world) {
        super(owl, world);
        this.moveControl = new OwlFlyingMovementController(this, 32, false);
        this.setPathfindingMalus(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathNodeType.WATER, -1.0F);
        this.setPathfindingMalus(PathNodeType.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(PathNodeType.COCOA, -1.0F);
        this.setPathfindingMalus(PathNodeType.FENCE, -1.0F);
    }

    @Override
    protected PathNavigator createNavigation(World world) {
        OwlFlyingPathNavigator navigator = new OwlFlyingPathNavigator(this, world);
        navigator.setCanOpenDoors(false);
        navigator.setCanFloat(false);
        navigator.setCanPassDoors(true);
        return navigator;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE_ID, 0);
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 14.0D)
                .add(Attributes.FLYING_SPEED, 1F)
                .add(Attributes.MOVEMENT_SPEED, 0.4F)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);

    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new OwlWaterAvoidingRandomFlyingGoal(this, 1.3D));
        this.goalSelector.addGoal(1, new PerchGoal(this));
        this.goalSelector.addGoal(2, new HuntFishGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        // Additional tick behaviors

        if (this.fallDistance < 3F) {
            this.navigation.moveTo(this.getX(), this.getY() + 1, this.getZ(), 1.0F);
        }
    }

    public boolean isPerching() {
        return this.getFlag(FLAG_PERCHING);
    }

    public void setPerching(boolean perching) {
        this.setFlag(FLAG_PERCHING, perching);
    }

    public boolean isHunting() {
        return this.getFlag(FLAG_HUNTING);
    }

    public void setHunting(boolean hunting) {
        this.setFlag(FLAG_HUNTING, hunting);
    }

    private boolean getFlag(int flag) {
        return (this.entityData.get(DATA_FLAGS_ID) & flag) != 0;
    }

    private void setFlag(int flag, boolean set) {
        byte currentFlags = this.entityData.get(DATA_FLAGS_ID);
        if (set) {
            this.entityData.set(DATA_FLAGS_ID, (byte) (currentFlags | flag));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte) (currentFlags & ~flag));
        }
    }

    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(ServerWorld world, AgeableEntity mate) {
        return null;
    }

    public boolean causeFallDamage(float f, float f1) {
        return false;
    }

    public OwlEntity.Type getOwlType() {
        return OwlEntity.Type.byId(this.entityData.get(DATA_TYPE_ID));
    }

    private void setOwlType(OwlEntity.Type type) {
        this.entityData.set(DATA_TYPE_ID, type.getId());
    }

    public static enum Type {
        GREAT_HORNED(0, "great_horned", Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.TAIGA_MOUNTAINS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.GIANT_SPRUCE_TAIGA_HILLS, Biomes.FOREST, Biomes.BIRCH_FOREST, Biomes.FLOWER_FOREST),
        SNOWY(1, "snowy", Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS);

        private static final OwlEntity.Type[] BY_ID = Arrays.stream(values())
                .sorted(Comparator.comparingInt(OwlEntity.Type::getId))
                .toArray(OwlEntity.Type[]::new);
        private static final Map<String, Type> BY_NAME = Arrays.stream(values())
                .collect(Collectors.toMap(OwlEntity.Type::getName, type -> type));
        private final int id;
        private final String name;
        private final List<RegistryKey<Biome>> biomes;

        private Type(int id, String name, RegistryKey<Biome>... biomes) {
            this.id = id;
            this.name = name;
            this.biomes = Arrays.asList(biomes);
        }

        public String getName() {
            return this.name;
        }

        public int getId() {
            return this.id;
        }

        public static OwlEntity.Type byName(String name) {
            return BY_NAME.getOrDefault(name, GREAT_HORNED);
        }

        public static OwlEntity.Type byId(int id) {
            if (id < 0 || id >= BY_ID.length) {
                id = 0;
            }
            return BY_ID[id];
        }

        public static OwlEntity.Type byBiome(Optional<RegistryKey<Biome>> biomeKey) {
            return biomeKey.isPresent() && SNOWY.biomes.contains(biomeKey.get()) ? SNOWY : GREAT_HORNED;
        }
    }

    class PerchGoal extends Goal {
        private final OwlEntity owl;

        public PerchGoal(OwlEntity owl) {
            this.owl = owl;
        }

        @Override
        public boolean canUse() {
            return !owl.isPerching() && owl.getRandom().nextInt(10) == 0 && !owl.isHunting();
        }

        @Override
        public boolean canContinueToUse() {
            return owl.isPerching();
        }

        @Override
        public void start() {
            owl.setPerching(true);
        }

        @Override
        public void stop() {
            owl.setPerching(false);
        }

        @Override
        public void tick() {
            if (owl.isOnGround()) {
                BlockPos perchPos = owl.blockPosition().above();
                owl.getNavigation().moveTo(perchPos.getX(), perchPos.getY(), perchPos.getZ(), 1.0D);
            } else {
                owl.setPerching(false);
            }
        }
    }

    class HuntFishGoal extends TargetGoal {
        private final OwlEntity owl;
        private AbstractFishEntity fish;
        public HuntFishGoal(OwlEntity owl) {
            super(owl, false, true);
            this.owl = owl;
        }

        @Override
        public boolean canUse() {
            return owl.isInWater() && !owl.isHunting();
        }

        @Override
        public boolean canContinueToUse() {
            return owl.isHunting();
        }

        @Override
        public void start() {
            owl.setHunting(true);
        }

        @Override
        public void stop() {
            owl.setHunting(false);
            this.fish = null;
        }

        @Override
        public void tick() {
            List<AbstractFishEntity> fishEntities = owl.level.getEntitiesOfClass(AbstractFishEntity.class, owl.getBoundingBox().inflate(15.0D, 15.0D, 15.0D));
            if (!fishEntities.isEmpty() && fish == null) {
                this.fish = fishEntities.get(0);

            } else if (fish != null) {
                owl.getNavigation().moveTo(fish, 1.5D);
                if (owl.getBoundingBox().intersects(fish.getBoundingBox())) {
                    owl.doHurtTarget(fish);
                }
            }
        }
    }
}
