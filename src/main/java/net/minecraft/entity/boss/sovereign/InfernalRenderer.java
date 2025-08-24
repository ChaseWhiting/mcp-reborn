package net.minecraft.entity.boss.sovereign;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.InfernalLavaGlowLayer;
import net.minecraft.entity.monster.creaking.CreakingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class InfernalRenderer extends MobRenderer<InfernalSovereignEntity, InfernalSovereignModel<InfernalSovereignEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/infernal_sovereign.png");
    private final InfernalLavaGlowLayer<InfernalSovereignEntity, InfernalSovereignModel<InfernalSovereignEntity>> layer = new InfernalLavaGlowLayer<>(this);

    public InfernalRenderer(EntityRendererManager manager) {
        super(manager, new InfernalSovereignModel<>(), 0.7F);
        this.addLayer(layer);
    }

    public ResourceLocation getTextureLocation(InfernalSovereignEntity p_234791_) {

        return TEXTURE;
    }

    public int getBlockLightLevel(InfernalSovereignEntity entity, BlockPos pos) {
        return 15;
    }

    public float getFlipDegrees(CreakingEntity entity) {
        return 0;
    }
}
