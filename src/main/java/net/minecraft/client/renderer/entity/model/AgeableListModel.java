package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public abstract class AgeableListModel<E extends Entity>
extends EntityModel<E> {
    private final boolean scaleHead;
    private final float babyYHeadOffset;
    private final float babyZHeadOffset;
    private final float babyHeadScale;
    private final float babyBodyScale;
    private final float bodyYOffset;

    protected AgeableListModel(boolean bl, float f, float f2) {
        this(bl, f, f2, 2.0f, 2.0f, 24.0f);
    }

    protected AgeableListModel(boolean bl, float f, float f2, float f3, float f4, float f5) {
        this(RenderType::entityCutoutNoCull, bl, f, f2, f3, f4, f5);
    }

    protected AgeableListModel(Function<ResourceLocation, RenderType> function, boolean bl, float f, float f2, float f3, float f4, float f5) {
        super(function);
        this.scaleHead = bl;
        this.babyYHeadOffset = f;
        this.babyZHeadOffset = f2;
        this.babyHeadScale = f3;
        this.babyBodyScale = f4;
        this.bodyYOffset = f5;
    }

    protected AgeableListModel() {
        this(false, 5.0f, 2.0f);
    }

    @Override
    public void renderToBuffer(MatrixStack poseStack, IVertexBuilder vertexConsumer, int n, int n2, float f, float f2, float f3, float f4) {
        if (this.young) {
            float f5;
            poseStack.pushPose();
            if (this.scaleHead) {
                f5 = 1.5f / this.babyHeadScale;
                poseStack.scale(f5, f5, f5);
            }
            poseStack.translate(0.0f, this.babyYHeadOffset / 16.0f, this.babyZHeadOffset / 16.0f);
            this.headParts().forEach(modelPart -> modelPart.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4));
            poseStack.popPose();
            poseStack.pushPose();
            f5 = 1.0f / this.babyBodyScale;
            poseStack.scale(f5, f5, f5);
            poseStack.translate(0.0f, this.bodyYOffset / 16.0f, 0.0f);
            this.bodyParts().forEach(modelPart -> modelPart.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4));
            poseStack.popPose();
        } else {
            this.headParts().forEach(modelPart -> modelPart.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4));
            this.bodyParts().forEach(modelPart -> modelPart.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4));
        }
    }

    protected abstract Iterable<ModelPart> headParts();

    protected abstract Iterable<ModelPart> bodyParts();
}
