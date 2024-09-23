package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.layers.RaccoonEyesLayer;
import net.minecraft.client.renderer.entity.layers.RaccoonHeldItemLayer;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.client.renderer.entity.model.RaccoonModel;
import net.minecraft.entity.passive.RaccoonEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RaccoonRenderer extends MobRenderer<RaccoonEntity, RaccoonModel<RaccoonEntity>> {
    private static final ResourceLocation RED_FOX_TEXTURE = new ResourceLocation("textures/entity/fox/raccoon.png");
    private static final ResourceLocation RACCOON_DIRTY_TEXTURE = new ResourceLocation("textures/entity/fox/raccoon_dirty.png");
    private static final ResourceLocation RACCOON_DIRTY_SLEEP_TEXTURE = new ResourceLocation("textures/entity/fox/raccoon_dirty_sleep.png");
    private static final ResourceLocation RACCOON_RABID_DIRTY_TEXTURE = new ResourceLocation("textures/entity/fox/raccoon_rabid_dirty.png");
    private static final ResourceLocation RACCOON_RABID_DIRTY_SLEEP_TEXTURE = new ResourceLocation("textures/entity/fox/raccoon_rabid_dirty_sleep.png");
    private static final ResourceLocation RED_FOX_SLEEP_TEXTURE = new ResourceLocation("textures/entity/fox/raccoon_sleep.png");
    private static final ResourceLocation RABID_FOX_TEXTURE = new ResourceLocation("textures/entity/fox/rabid_raccoon.png");
    private static final ResourceLocation RABID_FOX_SLEEP_TEXTURE = new ResourceLocation("textures/entity/fox/rabid_raccoon_sleep.png");
    private static final ResourceLocation SNOW_FOX_TEXTURE = new ResourceLocation("textures/entity/fox/snow_raccoon.png");
    private static final ResourceLocation SNOW_FOX_SLEEP_TEXTURE = new ResourceLocation("textures/entity/fox/snow_raccoon_sleep.png");
    private static final ResourceLocation BABY_RED_FOX_TEXTURE = new ResourceLocation("textures/entity/fox/raccoon.png");
    private static final ResourceLocation BABY_RED_FOX_SLEEP_TEXTURE = new ResourceLocation("textures/entity/fox/raccoon_sleep.png");
    private static final ResourceLocation BABY_RABID_FOX_TEXTURE = new ResourceLocation("textures/entity/fox/rabid_raccoon.png");
    private static final ResourceLocation BABY_RABID_FOX_SLEEP_TEXTURE = new ResourceLocation("textures/entity/fox/rabid_raccoon_sleep.png");
    private static final ResourceLocation BABY_SNOW_FOX_TEXTURE = new ResourceLocation("textures/entity/fox/snow_raccoon.png");
    private static final ResourceLocation BABY_SNOW_FOX_SLEEP_TEXTURE = new ResourceLocation("textures/entity/fox/snow_raccoon_sleep.png");



    public RaccoonRenderer(EntityRendererManager p_i50969_1_) {
        super(p_i50969_1_, new RaccoonModel<>(), 0.4F);
        this.addLayer(new RaccoonHeldItemLayer(this));
        //this.addLayer(new RaccoonEyesLayer<>(this));
    }

    protected void setupRotations(RaccoonEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
        super.setupRotations(p_225621_1_, p_225621_2_, p_225621_3_, p_225621_4_, p_225621_5_);
        if (p_225621_1_.isPouncing() || p_225621_1_.isFaceplanted()) {
            float f = -MathHelper.lerp(p_225621_5_, p_225621_1_.xRotO, p_225621_1_.xRot);
            p_225621_2_.mulPose(Vector3f.XP.rotationDegrees(f));
        }

    }

    public ResourceLocation getTextureLocation(RaccoonEntity raccoon) {
            if (raccoon.getRaccoonType() == RaccoonEntity.Type.DIRTY) {
                return (raccoon.isSleeping() ? RACCOON_DIRTY_SLEEP_TEXTURE : RACCOON_DIRTY_TEXTURE);
            } else if (raccoon.getRaccoonType() == RaccoonEntity.Type.RED) {
                    if (raccoon.isBaby()) {
                        return raccoon.isSleeping() ? BABY_RED_FOX_SLEEP_TEXTURE : BABY_RED_FOX_TEXTURE;
                    } else {
                        return raccoon.isSleeping() ? RED_FOX_SLEEP_TEXTURE : RED_FOX_TEXTURE;
                    }
            } else if (raccoon.getRaccoonType() == RaccoonEntity.Type.RABID) {
                if (raccoon.isBaby()) {
                    return raccoon.isSleeping() ? BABY_RABID_FOX_SLEEP_TEXTURE : BABY_RABID_FOX_TEXTURE;
                } else {
                    return raccoon.isSleeping() ? RABID_FOX_SLEEP_TEXTURE : RABID_FOX_TEXTURE;
                }
            } else {
                if (raccoon.isBaby()) {
                    return raccoon.isSleeping() ? BABY_SNOW_FOX_SLEEP_TEXTURE : BABY_SNOW_FOX_TEXTURE;
                } else {
                    return raccoon.isSleeping() ? SNOW_FOX_SLEEP_TEXTURE : SNOW_FOX_TEXTURE;
                }
            }
    }
}