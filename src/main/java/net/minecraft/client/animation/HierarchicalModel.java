package net.minecraft.client.animation;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public abstract class HierarchicalModel<E extends Entity> extends EntityModel<E> {
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    public HierarchicalModel() {
        this(RenderType::entityCutoutNoCull);
    }

    public HierarchicalModel(Function<ResourceLocation, RenderType> renderType) {
        super(renderType);
    }

    public void renderToBuffer(@NotNull MatrixStack matrixStack, @NotNull IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root().render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root().render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }


    public void setupAnim(@NotNull E entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getAllParts().forEach(ModelRenderer::resetPose);
    }

    public void headTurn(ModelRenderer head, float netHeadYaw, float headPitch) {
        head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        head.xRot = headPitch * ((float) Math.PI / 180F);
    }

    public abstract ImmutableSet<ModelRenderer> getAllParts();


    public abstract ModelRenderer root();

    public Optional<ModelRenderer> getAnyDescendantWithName(String name) {
        return name.equals("root") ? Optional.of(this.root()) : getChildModels(this.root()).stream()
                .filter(child -> name.equals(child.getName()))
                .findFirst();
    }

    private List<ModelRenderer> getChildModels(ModelRenderer modelRenderer) {
        List<ModelRenderer> children = new ArrayList<>();
        children.add(modelRenderer);

        List<ModelRenderer> childModels = modelRenderer.children;
        for (ModelRenderer child : childModels) {
            children.addAll(getChildModels(child));
        }

        return children;
    }

    protected void animate(AnimationState animationState, AnimationDefinition animationDefinition, float partialTicks) {
        this.animate(animationState, animationDefinition, partialTicks, 1.0F);
    }

    protected void animateWalk(AnimationDefinition animationDefinition,
                               float limbSwing,
                               float limbSwingAmount,
                               float baseSpeed,
                               float intensityClamp) {
        long animationTime = (long) (limbSwing * 50.0F * baseSpeed);
        float animationIntensity = Math.min(limbSwingAmount * intensityClamp, 1.0F);
        KeyframeAnimations.animate(this, animationDefinition, animationTime, animationIntensity, new Vector3f());
    }

    protected void animate(AnimationState animationState, AnimationDefinition animationDefinition, float partialTicks, float factor) {
        animationState.updateTime(partialTicks, factor);
        animationState.ifStarted((state) -> KeyframeAnimations.animate(this, animationDefinition, state.getAccumulatedTime(), 1.0F, new Vector3f()));
    }

}
