package net.minecraft.client.model.geom;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.AnimationState;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Optional;
import java.util.function.Function;

public abstract class NewHierarchicalModel<E extends Entity>
extends EntityModel<E> {
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    public NewHierarchicalModel() {
        this(RenderType::entityCutoutNoCull);
    }

    public NewHierarchicalModel(Function<ResourceLocation, RenderType> function) {
        super(function);
    }

    @Override
    public void renderToBuffer(MatrixStack poseStack, IVertexBuilder vertexConsumer, int n, int n2, float f, float f2, float f3, float f4) {
        this.root().render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4);
    }

    public void resetPose() {
        this.root().getAllParts().forEach(ModelPart::resetPose);
    }

    public abstract ModelPart root();

    public Optional<ModelPart> getAnyDescendantWithName(String string) {
        if (string.equals("root")) {
            return Optional.of(this.root());
        }
        return this.root().getAllParts().filter(modelPart -> modelPart.hasChild(string)).findFirst().map(modelPart -> modelPart.getChild(string));
    }

    protected void animate(AnimationState animationState, AnimationDefinition animationDefinition, float f) {
        this.animate(animationState, animationDefinition, f, 1.0f);
    }

    public void headTurn(ModelPart head, float netHeadYaw, float headPitch) {
        head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        head.xRot = headPitch * ((float) Math.PI / 180F);
    }

    protected void animate(AnimationState animationState2, AnimationDefinition animationDefinition, float f, float f2) {
        animationState2.updateTime(f, f2);
        animationState2.ifStarted(animationState -> KeyframeAnimations.animateNew(this, animationDefinition, animationState.getAccumulatedTime(), 1.0f, ANIMATION_VECTOR_CACHE));
    }

    protected void animateWalk(AnimationDefinition animationDefinition, float f, float f2, float f3, float f4) {
        long l = (long)(f * 50.0f * f3);
        float f5 = Math.min(f2 * f4, 1.0f);
        KeyframeAnimations.animateA(this, animationDefinition, l, f5, ANIMATION_VECTOR_CACHE);
    }
}