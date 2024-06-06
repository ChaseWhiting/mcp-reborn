package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.OwlModel;
import net.minecraft.entity.passive.OwlEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OwlRenderer extends MobRenderer<OwlEntity, OwlModel<OwlEntity>> {
    private static final ResourceLocation GREAT_HORNED = new ResourceLocation("textures/entity/owl/great_horned.png");
    private static final ResourceLocation SNOWY = new ResourceLocation("textures/entity/owl/snowy.png");



    public OwlRenderer(EntityRendererManager p_i50969_1_) {
        super(p_i50969_1_, new OwlModel<>(), 0.4F);
        //this.addLayer(new RaccoonHeldItemLayer(this));
    }

    protected void setupRotations(OwlEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
        super.setupRotations(p_225621_1_, p_225621_2_, p_225621_3_, p_225621_4_, p_225621_5_);
     /*   if (p_225621_1_.isPouncing() || p_225621_1_.isFaceplanted()) {
            float f = -MathHelper.lerp(p_225621_5_, p_225621_1_.xRotO, p_225621_1_.xRot);
            p_225621_2_.mulPose(Vector3f.XP.rotationDegrees(f));
        } */

    }

    public ResourceLocation getTextureLocation(OwlEntity owl) {
            if (owl.getOwlType() == OwlEntity.Type.GREAT_HORNED) {
                return GREAT_HORNED;
            } else {
                return null;
            }


    }
}