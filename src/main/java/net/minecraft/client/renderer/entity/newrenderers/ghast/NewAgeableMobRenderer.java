package net.minecraft.client.renderer.entity.newrenderers.ghast;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Mob;

public abstract class NewAgeableMobRenderer<T extends Mob, M extends EntityModel<T>>
extends MobRenderer<T, M> {
    private final M adultModel;
    private final M babyModel;

    public NewAgeableMobRenderer(EntityRendererManager context, M m, M m2, float f) {
        super(context, m, f);
        this.adultModel = m;
        this.babyModel = m2;
    }

    @Override
    public void render(T p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
        this.model = p_225623_1_.isBaby() ? this.babyModel : this.adultModel;
        super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
    }
}
