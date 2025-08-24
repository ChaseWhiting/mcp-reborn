package com.mojang.blaze3d.vertex;

import com.google.common.collect.Queues;
import java.util.Deque;

import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class PoseStack {
    public final Deque<Pose> poseStack = Util.make(Queues.newArrayDeque(), arrayDeque -> {
        Matrix4f matrix4f = new Matrix4f();
        Matrix3f matrix3f = new Matrix3f();
        arrayDeque.add(new Pose(matrix4f, matrix3f));
    });

    public void translate(double d, double d2, double d3) {
        this.translate((float)d, (float)d2, (float)d3);
    }

    public void translate(float f, float f2, float f3) {
        Pose pose = this.poseStack.getLast();
        pose.pose.translate(f, f2, f3);
    }

    public void rotateAround(Quaternionf quaternionf, float f, float f2, float f3) {
        Pose pose = this.poseStack.getLast();
        pose.pose.rotateAround((Quaternionfc)quaternionf, f, f2, f3);
        pose.normal.rotate((Quaternionfc)quaternionf);
    }



    public void scale(float f, float f2, float f3) {
        Pose pose = this.poseStack.getLast();
        pose.pose.scale(f, f2, f3);
        if (f == f2 && f2 == f3) {
            if (f > 0.0f) {
                return;
            }
            pose.normal.scale(-1.0f);
        }
        float f4 = 1.0f / f;
        float f5 = 1.0f / f2;
        float f6 = 1.0f / f3;
        float f7 = MathHelper.fastInvCubeRoot(f4 * f5 * f6);
        pose.normal.scale(f7 * f4, f7 * f5, f7 * f6);
    }

    public void mulPose(Quaternionf quaternionf) {
        Pose pose = this.poseStack.getLast();
        pose.pose.rotate((Quaternionfc)quaternionf);
        pose.normal.rotate((Quaternionfc)quaternionf);
    }

    public void pushPose() {
        Pose pose = this.poseStack.getLast();
        this.poseStack.addLast(new Pose(new Matrix4f((Matrix4fc)pose.pose), new Matrix3f((Matrix3fc)pose.normal)));
    }

    public void popPose() {
        this.poseStack.removeLast();
    }

    public Pose last() {
        return this.poseStack.getLast();
    }

    public boolean clear() {
        return this.poseStack.size() == 1;
    }

    public void setIdentity() {
        Pose pose = this.poseStack.getLast();
        pose.pose.identity();
        pose.normal.identity();
    }

    public void mulPoseMatrix(Matrix4f matrix4f) {
        this.poseStack.getLast().pose.mul((Matrix4fc)matrix4f);
    }

    public static final class Pose {
        final Matrix4f pose;
        final Matrix3f normal;

        public Pose(Matrix4f matrix4f, Matrix3f matrix3f) {
            this.pose = matrix4f;
            this.normal = matrix3f;
        }

        public Matrix4f pose() {
            return this.pose;
        }

        public Matrix3f normal() {
            return this.normal;
        }
    }



}