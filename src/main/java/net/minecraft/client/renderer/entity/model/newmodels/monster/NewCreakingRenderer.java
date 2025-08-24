package net.minecraft.client.renderer.entity.model.newmodels.monster;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.entity.monster.creaking.CreakingEntity;
import net.minecraft.entity.warden.LivingEntityEmissiveLayer;
import net.minecraft.util.ResourceLocation;

public class NewCreakingRenderer extends MobRenderer<CreakingEntity, NewCreakingModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/creaking.png");
    private static final ResourceLocation EYES = (new ResourceLocation("textures/entity/creaking/creaking_eyes.png"));


    public NewCreakingRenderer(EntityRendererManager manager) {
        super(manager, new NewCreakingModel(manager.bakeLayer(ModelLayers.CREAKING_2)), 0.7F);
        this.addLayer(new LivingEntityEmissiveLayer<CreakingEntity, NewCreakingModel>(this, EYES, (creakingRenderState, f, f1) -> 1.0f, NewCreakingModel::getHeadModelParts, RenderType::eyes, true));

    }

    @Override
    public void render(CreakingEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
        super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);



    }

    public ResourceLocation getTextureLocation(CreakingEntity p_234791_) {


        return TEXTURE;
    }

    public float getFlipDegrees(CreakingEntity entity) {
        return 0;
    }
}
