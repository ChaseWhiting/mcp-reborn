package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.awt.*;

public class MeowmereProjectileEntity extends ProjectileItemEntity {

    private int bounces = 0;
    private int hits = 0;
    private long lastBounceTime = 0; // Tracks the last bounce time
    private static final int MAX_BOUNCES = 4;
    private static final int MAX_HITS = 5;
    private static final float GRAVITY_MODIFIER = 0.05f;
    private static final float BASE_DAMAGE = 15f;
    private static final int BOUNCE_COOLDOWN = 10; // Cooldown in ticks
    private static final float Y_OFFSET = 0.2f; // Offset to keep projectile above ground

    public MeowmereProjectileEntity(World world, LivingEntity shooter, Vector3d direction) {
        super(EntityType.CAT_PROJECTILE, shooter.getX(), shooter.getY(), shooter.getZ(), world);
        this.setDeltaMovement(direction);
        this.setNoGravity(false);
    }

    public MeowmereProjectileEntity(EntityType<? extends MeowmereProjectileEntity> p_i50154_1_, World p_i50154_2_) {
        super(p_i50154_1_, p_i50154_2_);
    }

    public MeowmereProjectileEntity(World p_i1781_1_, double p_i1781_2_, double p_i1781_4_, double p_i1781_6_) {
        super(EntityType.CAT_PROJECTILE, p_i1781_2_, p_i1781_4_, p_i1781_6_, p_i1781_1_);
    }

    @Override
    public void tick() {
        super.tick();

        // Add rainbow trail particles as it moves
        createRainbowTrailParticles();

        // Reduced gravity effect
        Vector3d velocity = this.getDeltaMovement();
        this.setDeltaMovement(velocity.x, velocity.y - GRAVITY_MODIFIER, velocity.z);

        // Check for nearby blocks and handle bouncing
        checkForBounce();

        // Apply rainbow lighting
        applyRainbowLighting();
    }

    private void checkForBounce() {
        long currentTime = this.level.getGameTime();

        // Avoid multiple bounces in a short period
        if (currentTime - lastBounceTime < BOUNCE_COOLDOWN) {
            return;
        }

        Vector3d position = this.position();
        Vector3d nextPosition = position.add(this.getDeltaMovement().normalize().scale(0.2)); // Slightly ahead of the current position

        BlockRayTraceResult rayTraceResult = this.level.clip(new RayTraceContext(
                position,
                nextPosition,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                this
        ));

        if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
            // If block detected, bounce off the block
            Vector3d velocity = this.getDeltaMovement();

            switch (rayTraceResult.getDirection().getAxis()) {
                case X -> this.setDeltaMovement(-velocity.x * 0.9f, velocity.y, velocity.z);
                case Y -> {
                    this.setDeltaMovement(velocity.x, -velocity.y * 0.9f, velocity.z);
                    this.setPos(this.getX(), this.getY() + Y_OFFSET, this.getZ()); // Offset upward to avoid ground penetration
                }
                case Z -> this.setDeltaMovement(velocity.x, velocity.y, -velocity.z * 0.9f);
            }

            // Increment bounce count, update bounce time, play sound, and spawn particles
            bounces++;
            lastBounceTime = currentTime;
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.CAT_AMBIENT, SoundCategory.PLAYERS, 1.0f, 1.0f);

            spawnBounceParticles();

            // Remove the projectile if max bounces reached
            if (bounces >= MAX_BOUNCES) {
                this.remove();
            }
        }
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult entityHitResult) {
        super.onHitEntity(entityHitResult);

        Entity hitEntity = entityHitResult.getEntity();
        if (hitEntity instanceof LivingEntity && hits < MAX_HITS) {
            hitEntity.hurt(DamageSource.indirectMagic(this, this.getOwner()), BASE_DAMAGE * 1.25f);
            hits++;
            if (hits >= MAX_HITS) {
                this.remove(); // Remove projectile after max entity hits
            }
        }
    }

    private void createRainbowTrailParticles() {
        float hue = (System.currentTimeMillis() % 1000) / 1000.0f; // Cycle through hues
        Color color = Color.getHSBColor(hue, 1.0f, 0.5f);

        for (int i = 0; i < 2; i++) { // Adjust particle count as needed
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT,
                    this.getX() + random.nextGaussian() * 0.1,
                    this.getY() + random.nextGaussian() * 0.1,
                    this.getZ() + random.nextGaussian() * 0.1,
                    color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0);
        }
    }

    private void spawnBounceParticles() {
        // Generate particles on bounce with growing effect
        float scale = 1.0f + (bounces * 0.5f); // Increase scale on each bounce

        for (int i = 0; i < 10; i++) {
            this.level.addParticle(ParticleTypes.EXPLOSION,
                    this.getX() + random.nextGaussian() * 0.2 * scale,
                    this.getY() + random.nextGaussian() * 0.2 * scale,
                    this.getZ() + random.nextGaussian() * 0.2 * scale,
                    0, 0, 0);
        }
    }

    private void applyRainbowLighting() {
        float hue = (System.currentTimeMillis() % 1000) / 1000.0f;
        Color color = Color.getHSBColor(hue, 1.0f, 0.5f);

        this.level.addParticle(ParticleTypes.ENTITY_EFFECT,
                this.getX() + random.nextGaussian() * 0.1, this.getY() + random.nextGaussian() * 0.1, this.getZ() + random.nextGaussian() * 0.1,
                color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0);
    }

    @Override
    public void remove() {
        super.remove();
        // Generate rainbow particles on removal for visual effect
        for (int i = 0; i < 20; i++) {
            this.level.addParticle(ParticleTypes.EXPLOSION,
                    this.getX() + random.nextGaussian() * 0.5,
                    this.getY() + random.nextGaussian() * 0.5,
                    this.getZ() + random.nextGaussian() * 0.5,
                    0, 0, 0);
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.MEOWMERE_CAT;
    }
}
