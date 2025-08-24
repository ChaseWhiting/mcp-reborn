package net.minecraft.client.animation;

import net.minecraft.client.renderer.model.ModelRenderer;

public class Targets {
    public static final AnimationChannel.Target POSITION = (modelPart, vector) -> {
            modelPart.temporaryX = vector.x();
            modelPart.temporaryY = vector.y();
            modelPart.temporaryZ = vector.z();
            //System.out.println("Applying position: " + vector + " on " + modelPart.getName());
    };
    public static final AnimationChannel.Target POSITION_REAL = (modelPart, vector) -> {
        modelPart.temporaryX = vector.x();
        modelPart.temporaryY = vector.y();
        modelPart.temporaryZ = vector.z();
        modelPart.override = true;
        //System.out.println("Applying position: " + vector + " on " + modelPart.getName());
    };
    public static final AnimationChannel.Target POSITION_OFFSET = (modelPart, vector) -> {
        modelPart.x += vector.x();
        modelPart.y += vector.y();
        modelPart.z += vector.z();
        modelPart.override = true;
        //System.out.println("Applying position: " + vector + " on " + modelPart.getName());
    };
    public static final AnimationChannel.Target POSITION_ADD = (modelPart, vector) -> {
        modelPart.temporaryX += vector.z();
        modelPart.temporaryY += vector.y();
        modelPart.temporaryZ += vector.z();
//            System.out.println("Applying position: " + vector + " on " + modelPart.getName());
    };
    public static final AnimationChannel.Target ROTATION = (modelPart, vector) -> {
        modelPart.xRot = vector.x();
        modelPart.yRot = vector.y();
        modelPart.zRot = vector.z();
        //System.out.println("Applying rotation: " + vector + " on " + modelPart.getName());

    };
    public static final AnimationChannel.Target ROTATION_ADD = (modelPart, vector) -> {
        modelPart.xRot += vector.x();
        modelPart.yRot += vector.y();
        modelPart.zRot += vector.z();
    };
    public static final AnimationChannel.Target ROTATION_REMOVE = (modelPart, vector) -> {
        modelPart.xRot -= vector.x();
        modelPart.yRot -= vector.y();
        modelPart.zRot -= vector.z();
    };
    public static final AnimationChannel.Target SCALE = (modelPart, vector) -> {
        if (modelPart != null) {
            ((ModelRenderer) modelPart).applyScale(vector);
        }
    };
}
