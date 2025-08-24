package net.minecraft.client.renderer.entity.state.hitbox;

import javax.annotation.Nullable;

public record ServerHitboxesRenderState(boolean missing, double serverEntityX, double serverEntityY, double serverEntityZ, double deltaMovementX, double deltaMovementY, double deltaMovementZ, float eyeHeight, @Nullable HitboxesRenderState hitboxes) {
    public ServerHitboxesRenderState(boolean bl) {
        this(bl, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0f, null);
    }
}

