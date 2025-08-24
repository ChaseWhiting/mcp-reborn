package net.minecraft.entity.terraria.creature;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.Mob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class WormEntity extends Mob {
    private final WormPartEntity[] parts;
    private final WormPartEntity head;
    private final WormPartEntity body;
    private final WormPartEntity tail1;
    private final WormPartEntity tail2;

    public WormEntity(EntityType<? extends WormEntity> type, World world) {
        super(type, world);
        
        // Initialize parts: head, body, and tail segments
        this.head = new WormPartEntity(this, "head", 1.0F, 1.0F);
        this.body = new WormPartEntity(this, "body", 2.0F, 2.0F);
        this.tail1 = new WormPartEntity(this, "tail", 1.5F, 1.5F);
        this.tail2 = new WormPartEntity(this, "tail", 1.5F, 1.5F);

        // Store parts in an array for easy access
        this.parts = new WormPartEntity[]{head, body, tail1, tail2};

        this.setHealth(this.getMaxHealth());
        this.noPhysics = true;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // Update the worm's position and synchronize parts
        updateParts();
    }

    private void updateParts() {
        // Offset each part based on the previous segment's position
        Vector3d previousPosition = this.position();

        for (WormPartEntity part : this.parts) {
            part.movePart(previousPosition);
            previousPosition = part.position();
        }
    }

    public boolean hurt(DamageSource source, WormPartEntity part, float damage) {
        // Handle distributed damage across worm parts
        return this.hurt(source, damage);
    }

    public WormPartEntity[] getParts() {
        return this.parts;
    }
}
