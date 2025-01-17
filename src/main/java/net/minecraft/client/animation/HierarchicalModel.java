package net.minecraft.client.animation;

import java.util.Optional;
import java.util.function.Function;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.monster.creaking.CreakingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.entity.Entity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;
import net.minecraft.client.renderer.entity.model.EntityModel;

@OnlyIn(Dist.CLIENT)
public abstract class HierarchicalModel<E extends Entity> extends EntityModel<E> {
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();
    private static final Field childModelsField;

    static {
        try {
            childModelsField = ModelRenderer.class.getDeclaredField("children");
            childModelsField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to access childModels field", e);
        }
    }

    public HierarchicalModel() {
        this(RenderType::entityCutoutNoCull);
    }

    public HierarchicalModel(Function<ResourceLocation, RenderType> renderType) {
        super(renderType);
    }

    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root().render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);


        this.getAllParts().forEach(modelRenderer -> {
            modelRenderer.applyScale(new Vector3f(0, 0, 0));
            modelRenderer.temporaryX = 0f;
            modelRenderer.temporaryY = 0f;
            modelRenderer.temporaryZ = 0f;
        });
    }

    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root().render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }


    public void setupAnim(E entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getAllParts().forEach(ModelRenderer::reset);
    }

    public void headTurn(ModelRenderer head, float netHeadYaw, float headPitch) {
        head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        head.xRot = headPitch * ((float)Math.PI / 180F);
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
        try {
            List<ModelRenderer> childModels = (List<ModelRenderer>) childModelsField.get(modelRenderer);
            for (ModelRenderer child : childModels) {
                if (child instanceof ModelRenderer) {
                    children.addAll(getChildModels(child));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access child models", e);
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
        KeyframeAnimations.animate(this, animationDefinition, animationTime, animationIntensity, ANIMATION_VECTOR_CACHE);
    }

    protected void animate(AnimationState animationState, AnimationDefinition animationDefinition, float partialTicks, float factor) {
        animationState.updateTime(partialTicks, factor);
        animationState.ifStarted((state) -> {
            KeyframeAnimations.animate(this, animationDefinition, state.getAccumulatedTime(), 1.0F, ANIMATION_VECTOR_CACHE);
        });
    }

    protected void applyStatic(AnimationDefinition animationDefinition) {
        KeyframeAnimations.animate(this, animationDefinition, 0L, 1.0F, ANIMATION_VECTOR_CACHE);
    }
}
