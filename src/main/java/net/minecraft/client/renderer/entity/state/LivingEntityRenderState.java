package net.minecraft.client.renderer.entity.state;

import net.minecraft.block.SkullBlock;
import net.minecraft.entity.Pose;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class LivingEntityRenderState
extends EntityRenderState {
    public float bodyRot;
    public float yRot;
    public float xRot;
    public float deathTime;
    public float walkAnimationPos;
    public float walkAnimationSpeed;
    public float scale = 1.0f;
    public float ageScale = 1.0f;
    public boolean isUpsideDown;
    public boolean isFullyFrozen;
    public boolean isBaby;
    public boolean isInWater;
    public boolean isAutoSpinAttack;
    public boolean hasRedOverlay;
    public boolean isInvisibleToPlayer;
    public boolean appearsGlowing;
    @Nullable
    public Direction bedOrientation;
    @Nullable
    public ITextComponent customName;
    public Pose pose = Pose.STANDING;
    public float wornHeadAnimationPos;
    @Nullable
    public SkullBlock.Types wornHeadType;

    public boolean hasPose(Pose pose) {
        return this.pose == pose;
    }
}

