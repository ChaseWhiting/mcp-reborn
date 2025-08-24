package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;

public abstract class SkullModelBase extends Model implements IHeadModel {

    public SkullModelBase() {
        super(RenderType::entityTranslucent);
    }

    public abstract void setupAnim(float var1, float var2, float var3);
}
