package net.minecraft.client.animation;

import net.minecraft.client.model.geom.ModelPart;

public class Targets2 {

    public static final AnimationChannel.Target2 POSITION = ModelPart::offsetPos;
    public static final AnimationChannel.Target2 ROTATION = ModelPart::offsetRotation;
    public static final AnimationChannel.Target2 SCALE = ModelPart::offsetScale;
}
