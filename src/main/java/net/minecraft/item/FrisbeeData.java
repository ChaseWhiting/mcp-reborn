package net.minecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FrisbeeEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class FrisbeeData {

    public static FrisbeeData DEFAULT = new FrisbeeData.FrisbeeDataBuilder().build("default");

    private final double baseDamage;
    private final int speed;
    public int distanceToComeBack;
    private final double reducWhenHittingMob;
    private final int distanceHalved;
    final int cooldown;
    private final boolean fireResistant;
    private final boolean windResistant;
    private double[] frisbeeHomingRange;
    private final boolean bypassArmour;
    private final boolean phase;
    private final RegistryKey<World>[] dimensionTypes;

    // Behavior interfaces
    private final FrisbeeBehaviours.OnHitEntityBehavior onHitEntityBehavior;
    private final FrisbeeBehaviours.OnHitBlockBehavior onHitBlockBehavior;
    private final FrisbeeBehaviours.OnThrowBehavior onThrowBehavior;
    private final FrisbeeBehaviours.OnReturnBehavior onReturnBehavior;
    private final FrisbeeBehaviours.WhileFlyingBehavior whileFlyingBehavior;


    public String getSpeedReductionPercentage() {
        // Calculate the percentage reduction
        double reductionPercentage = (1.0 - reducWhenHittingMob) * 100;

        // Format the result as a percentage string
        return String.format("%.0f%%", reductionPercentage);
    }

    public double convertPercentageToReductionValue() {
        // Remove the percentage sign and parse the number

        // Convert the percentage back to the decimal reduction value
        return Double.parseDouble(getSpeedReductionPercentage().replace("%", ""));
    }

    // Private constructor to enforce the use of the builder
    private FrisbeeData(FrisbeeDataBuilder builder) {
        this.baseDamage = builder.baseDamage;
        this.speed = builder.speed;
        this.distanceToComeBack = builder.distanceToComeBack;
        this.reducWhenHittingMob = builder.reducWhenHittingMob;
        this.distanceHalved = builder.distanceHalved;
        this.fireResistant = builder.fireResistant;
        this.onHitEntityBehavior = builder.onHitEntityBehavior;
        this.onHitBlockBehavior = builder.onHitBlockBehavior;
        this.onThrowBehavior = builder.onThrowBehavior;
        this.onReturnBehavior = builder.onReturnBehavior;
        this.whileFlyingBehavior = builder.whileFlyingBehavior;
        this.bypassArmour = builder.bypassArmour;
        this.cooldown = builder.cooldown;
        this.phase = builder.phase;
        this.windResistant = builder.windResistant;
        this.dimensionTypes = builder.dimensionTypes;
        this.frisbeeHomingRange = builder.frisbeeHomingRange;
    }

    // Getters for the properties
    public double getBaseDamage() {
        return baseDamage;
    }

    public double[] getRange() {
        return frisbeeHomingRange;
    }

    public RegistryKey<World>[] getDimensions() {
        return dimensionTypes;
    }

    public boolean phase() {
        return phase;
    }

    public int getSpeed() {
        return speed;
    }

    public int getDistanceToComeBack() {
        return distanceToComeBack;
    }

    public double getReducWhenHittingMob() {
        return reducWhenHittingMob;
    }

    public int getDistanceHalved() {
        return distanceHalved;
    }

    public boolean isFireResistant() {
        return fireResistant;
    }

    public boolean isWindResistant() {
        return windResistant;
    }

    // Methods to trigger behaviors
    public void triggerOnHitEntity(FrisbeeEntity frisbee, EntityRayTraceResult result) {
        if (this.onHitEntityBehavior != null) {
            this.onHitEntityBehavior.onHitEntity(frisbee, result);
        }
    }

    public void triggerOnHitBlock(FrisbeeEntity frisbee, BlockRayTraceResult result) {
        if (this.onHitBlockBehavior != null) {
            this.onHitBlockBehavior.onHitBlock(frisbee, result);
        }
    }

    public void triggerOnThrow(FrisbeeEntity frisbee, PlayerEntity player) {
        if (this.onThrowBehavior != null) {
            this.onThrowBehavior.onThrow(frisbee, player);
        }
    }

    public void triggerOnReturn(FrisbeeEntity frisbee, PlayerEntity player) {
        if (this.onReturnBehavior != null) {
            this.onReturnBehavior.onReturn(frisbee, player);
        }
    }

    public void triggerFlyingBehavior(FrisbeeEntity frisbee) {
        if (this.whileFlyingBehavior != null) {
            this.whileFlyingBehavior.onFlyingTick(frisbee);
        }
    }

    public boolean isBypassArmour() {
        return bypassArmour;
    }

    // FrisbeeDataBuilder inner class
    public static class FrisbeeDataBuilder {

        // Default values
        private double baseDamage = 1.0; // example default
        private int speed = 4; // example default
        private int distanceToComeBack = 100; // example default, gets squared in constructor
        private double reducWhenHittingMob = 0.5; // example default
        private boolean fireResistant = false; // example default
        private int distanceHalved;
        private boolean bypassArmour = false;
        private int cooldown = 5;
        private double[] frisbeeHomingRange = {12D,6D};
        private boolean phase = false;
        private boolean windResistant = false;
        private RegistryKey<World>[] dimensionTypes = new RegistryKey[]{World.OVERWORLD,World.NETHER,World.END};

        // Behavior interfaces
        private FrisbeeBehaviours.OnHitEntityBehavior onHitEntityBehavior;
        private FrisbeeBehaviours.OnHitBlockBehavior onHitBlockBehavior;
        private FrisbeeBehaviours.OnThrowBehavior onThrowBehavior;
        private FrisbeeBehaviours.OnReturnBehavior onReturnBehavior;
        private FrisbeeBehaviours.WhileFlyingBehavior whileFlyingBehavior;


        // Setters for the builder
        public FrisbeeDataBuilder setBaseDamage(double baseDamage) {
            this.baseDamage = baseDamage;
            return this;
        }

        public FrisbeeDataBuilder setHomingRange(double[] val) {
            if (val.length != 2) {
                throw new IllegalArgumentException("Homing range array must contain exactly 2 elements.");
            }
            this.frisbeeHomingRange = val;
            return this;
        }

        public FrisbeeDataBuilder setAvailableDimensions(List<RegistryKey<World>> dimensionTypes) {
            this.dimensionTypes = dimensionTypes.toArray(new RegistryKey[0]);
            return this;
        }

        public FrisbeeDataBuilder phase(boolean value) {
            this.phase = value;
            return this;
        }

        public FrisbeeDataBuilder setCooldown(int cooldown) {
            this.cooldown = cooldown;
            return this;
        }

        public FrisbeeDataBuilder bypassArmour() {
            this.bypassArmour = true;
            return this;
        }

        public FrisbeeDataBuilder setSpeed(int speed) {
            this.speed = speed;
            return this;
        }

        public FrisbeeDataBuilder setDistanceToComeBack(int distance) {
            this.distanceToComeBack = distance * distance;
            this.distanceHalved = (distance / 2) * (distance / 2);
            return this;
        }

        public FrisbeeDataBuilder setReducWhenHittingMob(double reducWhenHittingMob) {
            this.reducWhenHittingMob = reducWhenHittingMob;
            return this;
        }

        public FrisbeeDataBuilder setFireResistant(boolean fireResistant) {
            this.fireResistant = fireResistant;
            return this;
        }

        public FrisbeeDataBuilder fireResistant() {
            this.fireResistant = true;
            return this;
        }

        public FrisbeeDataBuilder windResistant() {
            this.windResistant = true;
            return this;
        }

        // Setters for behavior interfaces // use frisbee.level to get the world
        public FrisbeeDataBuilder setOnHitEntityBehavior(FrisbeeBehaviours.OnHitEntityBehavior onHitEntityBehavior) {
            this.onHitEntityBehavior = onHitEntityBehavior; // (FrisbeeEntity, EntityRayTraceResult)
            return this;
        }

        public FrisbeeDataBuilder setOnHitBlockBehavior(FrisbeeBehaviours.OnHitBlockBehavior onHitBlockBehavior) {
            this.onHitBlockBehavior = onHitBlockBehavior; // (FrisbeeEntity, BlockRayTraceResult)
            return this;
        }

        public FrisbeeDataBuilder setOnThrowBehavior(FrisbeeBehaviours.OnThrowBehavior onThrowBehavior) {
            this.onThrowBehavior = onThrowBehavior; // (FrisbeeEntity, PlayerEntity)
            return this;
        }

        public FrisbeeDataBuilder setOnReturnBehavior(FrisbeeBehaviours.OnReturnBehavior onReturnBehavior) {
            this.onReturnBehavior = onReturnBehavior; // (FrisbeeEntity, PlayerEntity)
            return this;
        }

        public FrisbeeDataBuilder setFlyingBehavior(FrisbeeBehaviours.WhileFlyingBehavior onReturnBehavior) {
            this.whileFlyingBehavior = onReturnBehavior; // (FrisbeeEntity, PlayerEntity)
            return this;
        }

        // Build method
        public FrisbeeData build(String location) {
            return register(this, new ResourceLocation(location));
        }

        public FrisbeeData DEFAULT() {
            return FrisbeeData.DEFAULT;
        }
        private FrisbeeData asFrisbee() {
            return new FrisbeeData(this);
        }

        public FrisbeeData register(FrisbeeDataBuilder builder, ResourceLocation location) {
            return Registry.register(Registry.FRISBEE_DATA, location, builder.asFrisbee());
        }
    }
}
