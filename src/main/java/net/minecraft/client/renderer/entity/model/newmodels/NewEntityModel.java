package net.minecraft.client.renderer.entity.model.newmodels;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public abstract class NewEntityModel<T extends EntityRenderState>
        extends NewModel {
    public static final float MODEL_Y_OFFSET = -1.501f;

    protected NewEntityModel(ModelPart modelPart) {
        this(modelPart, RenderType::entityCutoutNoCull);
    }

    protected NewEntityModel(ModelPart modelPart, Function<ResourceLocation, RenderType> function) {
        super(modelPart, function);
    }

    public void setupAnim(T t) {
        this.resetPose();
    }
}
