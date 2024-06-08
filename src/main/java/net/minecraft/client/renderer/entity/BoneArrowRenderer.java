package net.minecraft.client.renderer.entity;

import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.BoneArrowEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BoneArrowRenderer extends ArrowRenderer<BoneArrowEntity> {
    public static final ResourceLocation BONE_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/bone_arrow.png");

    public BoneArrowRenderer(EntityRendererManager manager) {
        super(manager);
    }

    public ResourceLocation getTextureLocation(BoneArrowEntity p_110775_1_) {
        return BONE_ARROW_LOCATION;
    }
}