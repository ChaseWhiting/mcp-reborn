package net.minecraft.client.renderer.entity.state.hitbox;

import com.google.common.collect.ImmutableList;

public record HitboxesRenderState(double viewX, double viewY, double viewZ, ImmutableList<HitboxRenderState> hitboxes) {
}