package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.axolotl.AxolotlEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Map;

public class AxolotlModel<T extends AxolotlEntity> extends AgeableModel<T> {
    public static final float SWIMMING_LEG_XROT = 1.8849558f;

    private final ModelRenderer tail;
    private final ModelRenderer leftHindLeg;
    private final ModelRenderer rightHindLeg;
    private final ModelRenderer leftFrontLeg;
    private final ModelRenderer rightFrontLeg;
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer topGills;
    private final ModelRenderer leftGills;
    private final ModelRenderer rightGills;

    public AxolotlModel() {
        super(true, 8.0F, 3.35F);

        this.texWidth = 64;
        this.texHeight = 64;

        // Body
        this.body = new ModelRenderer(this, 0, 11);
        this.body.addBox(-4.0F, -2.0F, -9.0F, 8.0F, 4.0F, 10.0F);
        this.body.setPos(0.0F, 20.0F, 5.0F);
        this.body.texOffs(2, 17).addBox(0.0F, -3.0F, -8.0F, 0.0F, 5.0F, 9.0F);

        // Head
        this.head = new ModelRenderer(this, 0, 1);
        this.head.addBox(-4.0F, -3.0F, -5.0F, 8.0F, 5.0F, 5.0F);
        this.head.setPos(0.0F, 0.0F, -9.0F);
        this.body.addChild(this.head);

        // Gills
        this.topGills = new ModelRenderer(this, 3, 37);
        this.topGills.addBox(-4.0F, -3.0F, 0.0F, 8.0F, 3.0F, 0.0F);
        this.topGills.setPos(0.0F, -3.0F, -1.0F);
        this.head.addChild(this.topGills);

        this.leftGills = new ModelRenderer(this, 0, 40);
        this.leftGills.addBox(-3.0F, -5.0F, 0.0F, 3.0F, 7.0F, 0.0F);
        this.leftGills.setPos(-4.0F, 0.0F, -1.0F);
        this.head.addChild(this.leftGills);

        this.rightGills = new ModelRenderer(this, 11, 40);
        this.rightGills.addBox(0.0F, -5.0F, 0.0F, 3.0F, 7.0F, 0.0F);
        this.rightGills.setPos(4.0F, 0.0F, -1.0F);
        this.head.addChild(this.rightGills);

        // Legs
        this.rightHindLeg = new ModelRenderer(this, 2, 13);
        this.rightHindLeg.addBox(-2.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0005F);
        this.rightHindLeg.setPos(-3.5F, 1.0F, -1.0F);
        this.body.addChild(this.rightHindLeg);

        this.leftHindLeg = new ModelRenderer(this, 2, 13);
        this.leftHindLeg.addBox(-1.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0005F);
        this.leftHindLeg.setPos(3.5F, 1.0F, -1.0F);
        this.body.addChild(this.leftHindLeg);

        this.rightFrontLeg = new ModelRenderer(this, 2, 13);
        this.rightFrontLeg.addBox(-2.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0005F);
        this.rightFrontLeg.setPos(-3.5F, 1.0F, -8.0F);
        this.body.addChild(this.rightFrontLeg);

        this.leftFrontLeg = new ModelRenderer(this, 2, 13);
        this.leftFrontLeg.addBox(-1.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0005F);
        this.leftFrontLeg.setPos(3.5F, 1.0F, -8.0F);
        this.body.addChild(this.leftFrontLeg);

        // Tail
        this.tail = new ModelRenderer(this, 2, 19);
        this.tail.addBox(0.0F, -3.0F, 0.0F, 0.0F, 5.0F, 12.0F);
        this.tail.setPos(0.0F, 0.0F, 1.0F);
        this.body.addChild(this.tail);
    }

    @Override
    protected Iterable<ModelRenderer> headParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelRenderer> bodyParts() {
        return ImmutableList.of(this.body);
    }

    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        boolean bl;
        this.setupInitialAnimationValues(t, f4, f5);
        if (((AxolotlEntity)t).isPlayingDead()) {
            this.setupPlayDeadAnimation(f4);
            this.saveAnimationValues(t);
            return;
        }
        boolean bl2 = bl = ((Entity)t).getDeltaMovement().horizontalDistanceSqr() > 1.0E-7 || ((Entity)t).xRot != ((AxolotlEntity)t).xRotO || ((Entity)t).yRot != ((AxolotlEntity)t).yRotO || ((AxolotlEntity)t).xOld != ((Entity)t).getX() || ((AxolotlEntity)t).zOld != ((Entity)t).getZ();
        if (((Entity)t).isInWaterOrBubble()) {
            if (bl) {
                this.setupSwimmingAnimation(f3, f5);
            } else {
                this.setupWaterHoveringAnimation(f3);
            }
            this.saveAnimationValues(t);
            return;
        }
        if (((Entity)t).isOnGround()) {
            if (bl) {
                this.setupGroundCrawlingAnimation(f3, f4);
            } else {
                this.setupLayStillOnGroundAnimation(f3, f4);
            }
        }
        this.saveAnimationValues(t);
    }

    private void saveAnimationValues(T t) {
        Map<String, Vector3f> map = ((AxolotlEntity)t).getModelRotationValues();
        map.put("body", this.getRotationVector(this.body));
        map.put("head", this.getRotationVector(this.head));
        map.put("right_hind_leg", this.getRotationVector(this.rightHindLeg));
        map.put("left_hind_leg", this.getRotationVector(this.leftHindLeg));
        map.put("right_front_leg", this.getRotationVector(this.rightFrontLeg));
        map.put("left_front_leg", this.getRotationVector(this.leftFrontLeg));
        map.put("tail", this.getRotationVector(this.tail));
        map.put("top_gills", this.getRotationVector(this.topGills));
        map.put("left_gills", this.getRotationVector(this.leftGills));
        map.put("right_gills", this.getRotationVector(this.rightGills));
    }

    private void setupInitialAnimationValues(T t, float f, float f2) {
        this.body.x = 0.0f;
        this.head.y = 0.0f;
        this.body.y = 20.0f;
        Map<String, Vector3f> map = ((AxolotlEntity)t).getModelRotationValues();
        if (map.isEmpty()) {
            this.body.setRotationAngle(f2 * ((float)Math.PI / 180), f * ((float)Math.PI / 180), 0.0f);
            this.head.setRotationAngle(0.0f, 0.0f, 0.0f);
            this.leftHindLeg.setRotationAngle(0.0f, 0.0f, 0.0f);
            this.rightHindLeg.setRotationAngle(0.0f, 0.0f, 0.0f);
            this.leftFrontLeg.setRotationAngle(0.0f, 0.0f, 0.0f);
            this.rightFrontLeg.setRotationAngle(0.0f, 0.0f, 0.0f);
            this.leftGills.setRotationAngle(0.0f, 0.0f, 0.0f);
            this.rightGills.setRotationAngle(0.0f, 0.0f, 0.0f);
            this.topGills.setRotationAngle(0.0f, 0.0f, 0.0f);
            this.tail.setRotationAngle(0.0f, 0.0f, 0.0f);
        } else {
            this.setRotationFromVector(this.body, map.get("body"));
            this.setRotationFromVector(this.head, map.get("head"));
            this.setRotationFromVector(this.leftHindLeg, map.get("left_hind_leg"));
            this.setRotationFromVector(this.rightHindLeg, map.get("right_hind_leg"));
            this.setRotationFromVector(this.leftFrontLeg, map.get("left_front_leg"));
            this.setRotationFromVector(this.rightFrontLeg, map.get("right_front_leg"));
            this.setRotationFromVector(this.leftGills, map.get("left_gills"));
            this.setRotationFromVector(this.rightGills, map.get("right_gills"));
            this.setRotationFromVector(this.topGills, map.get("top_gills"));
            this.setRotationFromVector(this.tail, map.get("tail"));
        }
    }

    private Vector3f getRotationVector(ModelRenderer modelPart) {
        return new Vector3f(modelPart.xRot, modelPart.yRot, modelPart.zRot);
    }

    private void setRotationFromVector(ModelRenderer modelPart, Vector3f vector3f) {
        modelPart.setRotationAngle(vector3f.x(), vector3f.y(), vector3f.z());
    }

    private float lerpTo(float f, float f2) {
        return this.lerpTo(0.05f, f, f2);
    }

    private float lerpTo(float f, float f2, float f3) {
        return MathHelper.rotLerp(f, f2, f3);
    }

    private void lerpPart(ModelRenderer modelPart, float f, float f2, float f3) {
        modelPart.setRotationAngle(this.lerpTo(modelPart.xRot, f), this.lerpTo(modelPart.yRot, f2), this.lerpTo(modelPart.zRot, f3));
    }

    private void setupLayStillOnGroundAnimation(float f, float f2) {
        float f3 = f * 0.09f;
        float f4 = MathHelper.sin(f3);
        float f5 = MathHelper.cos(f3);
        float f6 = f4 * f4 - 2.0f * f4;
        float f7 = f5 * f5 - 3.0f * f4;
        this.head.xRot = this.lerpTo(this.head.xRot, -0.09f * f6);
        this.head.yRot = this.lerpTo(this.head.yRot, 0.0f);
        this.head.zRot = this.lerpTo(this.head.zRot, -0.2f);
        this.tail.yRot = this.lerpTo(this.tail.yRot, -0.1f + 0.1f * f6);
        this.topGills.xRot = this.lerpTo(this.topGills.xRot, 0.6f + 0.05f * f7);
        this.leftGills.yRot = this.lerpTo(this.leftGills.yRot, -this.topGills.xRot);
        this.rightGills.yRot = this.lerpTo(this.rightGills.yRot, -this.leftGills.yRot);
        this.lerpPart(this.leftHindLeg, 1.1f, 1.0f, 0.0f);
        this.lerpPart(this.leftFrontLeg, 0.8f, 2.3f, -0.5f);
        this.applyMirrorLegRotations();
        this.body.xRot = this.lerpTo(0.2f, this.body.xRot, 0.0f);
        this.body.yRot = this.lerpTo(this.body.yRot, f2 * ((float)Math.PI / 180));
        this.body.zRot = this.lerpTo(this.body.zRot, 0.0f);
    }

    private void setupGroundCrawlingAnimation(float f, float f2) {
        float f3 = f * 0.11f;
        float f4 = MathHelper.cos(f3);
        float f5 = (f4 * f4 - 2.0f * f4) / 5.0f;
        float f6 = 0.7f * f4;
        this.head.xRot = this.lerpTo(this.head.xRot, 0.0f);
        this.head.yRot = this.lerpTo(this.head.yRot, 0.09f * f4);
        this.head.zRot = this.lerpTo(this.head.zRot, 0.0f);
        this.tail.yRot = this.lerpTo(this.tail.yRot, this.head.yRot);
        this.topGills.xRot = this.lerpTo(this.topGills.xRot, 0.6f - 0.08f * (f4 * f4 + 2.0f * MathHelper.sin(f3)));
        this.leftGills.yRot = this.lerpTo(this.leftGills.yRot, -this.topGills.xRot);
        this.rightGills.yRot = this.lerpTo(this.rightGills.yRot, -this.leftGills.yRot);
        this.lerpPart(this.leftHindLeg, 0.9424779f, 1.5f - f5, -0.1f);
        this.lerpPart(this.leftFrontLeg, 1.0995574f, 1.5707964f - f6, 0.0f);
        this.lerpPart(this.rightHindLeg, this.leftHindLeg.xRot, -1.0f - f5, 0.0f);
        this.lerpPart(this.rightFrontLeg, this.leftFrontLeg.xRot, -1.5707964f - f6, 0.0f);
        this.body.xRot = this.lerpTo(0.2f, this.body.xRot, 0.0f);
        this.body.yRot = this.lerpTo(this.body.yRot, f2 * ((float)Math.PI / 180));
        this.body.zRot = this.lerpTo(this.body.zRot, 0.0f);
    }

    private void applyMirrorLegRotations() {
        this.lerpPart(this.rightHindLeg, this.leftHindLeg.xRot, -this.leftHindLeg.yRot, -this.leftHindLeg.zRot);
        this.lerpPart(this.rightFrontLeg, this.leftFrontLeg.xRot, -this.leftFrontLeg.yRot, -this.leftFrontLeg.zRot);
    }

    private void setupWaterHoveringAnimation(float f) {
        float f2 = f * 0.075f;
        float f3 = MathHelper.cos(f2);
        float f4 = MathHelper.sin(f2) * 0.15f;
        this.body.xRot = this.lerpTo(this.body.xRot, -0.15f + 0.075f * f3);
        this.body.y -= f4;
        this.head.xRot = this.lerpTo(this.head.xRot, -this.body.xRot);
        this.topGills.xRot = this.lerpTo(this.topGills.xRot, 0.2f * f3);
        this.leftGills.yRot = this.lerpTo(this.leftGills.yRot, -0.3f * f3 - 0.19f);
        this.rightGills.yRot = this.lerpTo(this.rightGills.yRot, -this.leftGills.yRot);
        this.lerpPart(this.leftHindLeg, 2.3561945f - f3 * 0.11f, 0.47123894f, 1.7278761f);
        this.lerpPart(this.leftFrontLeg, 0.7853982f - f3 * 0.2f, 2.042035f, 0.0f);
        this.applyMirrorLegRotations();
        this.tail.yRot = this.lerpTo(this.tail.yRot, 0.5f * f3);
        this.head.yRot = this.lerpTo(this.head.yRot, 0.0f);
        this.head.zRot = this.lerpTo(this.head.zRot, 0.0f);
    }

    private void setupSwimmingAnimation(float f, float f2) {
        float f3 = f * 0.33f;
        float f4 = MathHelper.sin(f3);
        float f5 = MathHelper.cos(f3);
        float f6 = 0.13f * f4;
        this.body.xRot = this.lerpTo(0.1f, this.body.xRot, f2 * ((float)Math.PI / 180) + f6);
        this.head.xRot = -f6 * 1.8f;
        this.body.y -= 0.45f * f5;
        this.topGills.xRot = this.lerpTo(this.topGills.xRot, -0.5f * f4 - 0.8f);
        this.leftGills.yRot = this.lerpTo(this.leftGills.yRot, 0.3f * f4 + 0.9f);
        this.rightGills.yRot = this.lerpTo(this.rightGills.yRot, -this.leftGills.yRot);
        this.tail.yRot = this.lerpTo(this.tail.yRot, 0.3f * MathHelper.cos(f3 * 0.9f));
        this.lerpPart(this.leftHindLeg, 1.8849558f, -0.4f * f4, 1.5707964f);
        this.lerpPart(this.leftFrontLeg, 1.8849558f, -0.2f * f5 - 0.1f, 1.5707964f);
        this.applyMirrorLegRotations();
        this.head.yRot = this.lerpTo(this.head.yRot, 0.0f);
        this.head.zRot = this.lerpTo(this.head.zRot, 0.0f);
        this.body.zRot = this.lerpTo(this.body.zRot, 0.0f);
    }

    private void setupPlayDeadAnimation(float f) {
        this.lerpPart(this.leftHindLeg, 1.4137167f, 1.0995574f, 0.7853982f);
        this.lerpPart(this.leftFrontLeg, 0.7853982f, 2.042035f, 0.0f);
        this.body.xRot = this.lerpTo(this.body.xRot, -0.15f);
        this.body.zRot = this.lerpTo(this.body.zRot, 0.35f);
        this.applyMirrorLegRotations();
        this.body.yRot = this.lerpTo(this.body.yRot, f * ((float)Math.PI / 180));
        this.head.xRot = this.lerpTo(this.head.xRot, 0.0f);
        this.head.yRot = this.lerpTo(this.head.yRot, 0.0f);
        this.head.zRot = this.lerpTo(this.head.zRot, 0.0f);
        this.tail.yRot = this.lerpTo(this.tail.yRot, 0.0f);
        this.lerpPart(this.topGills, 0.0f, 0.0f, 0.0f);
        this.lerpPart(this.leftGills, 0.0f, 0.0f, 0.0f);
        this.lerpPart(this.rightGills, 0.0f, 0.0f, 0.0f);
    }

}
