package net.minecraft.entity.monster.creaking;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CreakingEyesLayer;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.util.ResourceLocation;

public class CreakingRenderer extends MobRenderer<CreakingEntity, CreakingModel<CreakingEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/creaking.png");
    private final CreakingEyesLayer<CreakingEntity, CreakingModel<CreakingEntity>> layer = new CreakingEyesLayer<>(this);

    public CreakingRenderer(EntityRendererManager manager) {
        super(manager, new CreakingModel<>(), 0.7F);
        this.addLayer(layer);
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
