package net.minecraft.client.animation;

import net.minecraft.client.renderer.model.ModelRenderer;

public class Targets {
    public static final AnimationChannel.Target POSITION = (modelPart, vector) -> {
            modelPart.temporaryX = vector.z();
            modelPart.temporaryY = vector.y();
            modelPart.temporaryZ = vector.z();
//            System.out.println("Applying position: " + vector + " on " + modelPart.getName());
    };
    public static final AnimationChannel.Target ROTATION = (modelPart, vector) -> {
        modelPart.xRot = vector.x();
        modelPart.yRot = vector.y();
        modelPart.zRot = vector.z();
        //System.out.println("Applying rotation: " + vector + " on " + modelPart.getName());

    };
    public static final AnimationChannel.Target SCALE = (modelPart, vector) -> {
        if (modelPart != null) {
            ((ModelRenderer) modelPart).applyScale(vector);
        }
    };
}
