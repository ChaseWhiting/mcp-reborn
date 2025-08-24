/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.particle;


import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.warden.event.position.PositionSource;
import net.minecraft.particles.VibrationParticleOption;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.function.Consumer;

public class VibrationParticleSignal
extends SpriteTexturedParticle {
    private final PositionSource target;
    private float rot;
    private float rotO;
    private float pitch;
    private float pitchO;

    VibrationParticleSignal(ClientWorld clientLevel, double d, double d2, double d3, PositionSource source, int n) {
        super(clientLevel, d, d2, d3, 0, 0, 0);
        this.quadSize = 0.3f;
        this.target = source;
        this.lifetime = n;
        Optional<Vector3d> optional = source.getPosition(clientLevel);
        if (optional.isPresent()) {
            Vector3d vector3d = optional.get();
            double d4 = d - vector3d.x;
            double d5 = d2 - vector3d.y;
            double d6 = d3 - vector3d.z;
            this.rotO = this.rot = (float)MathHelper.atan2(d4, d6);
            this.pitchO = this.pitch = (float) MathHelper.atan2(d5, Math.sqrt(d4 * d4 + d6 * d6));
        }
    }

    @Override
    public void render(IVertexBuilder consumer, ActiveRenderInfo info, float f) {
        float f2 = MathHelper.sin(((float)this.age + f - (float)Math.PI * 2) * 0.05f) * 2.0f;
        float f3 = MathHelper.lerp(f, this.rotO, this.rot);
        float f4 = MathHelper.lerp(f, this.pitchO, this.pitch) + 1.5707964f;
        this.renderSignal(consumer, info, f, quanternionf -> quanternionf.rotateY(f3).rotateX(-f4));
        this.renderSignal(consumer, info, f, quanternionf -> quanternionf.rotateY((float)(-Math.PI) + f3).rotateX(f4).rotateY(f2));

    }

    private void renderSignal(IVertexBuilder vertexConsumer, ActiveRenderInfo camera, float f, Consumer<Quaternionf> consumer) {
        Vector3d vector3D = camera.getPosition();
        float f2 = (float)(MathHelper.lerp((double)f, this.xo, this.x) - vector3D.x());
        float f3 = (float)(MathHelper.lerp((double)f, this.yo, this.y) - vector3D.y());
        float f4 = (float)(MathHelper.lerp((double)f, this.zo, this.z) - vector3D.z());
        Vector3f vector3f = new Vector3f(0.5f, 0.5f, 0.5f).normalize();
        Quaternionf quaternionf = new Quaternionf().setAngleAxis(0.0f, vector3f.x(), vector3f.y(), vector3f.z());
        consumer.accept(quaternionf);
        Vector3f[] vector3fArray = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};
        float f5 = this.getQuadSize(f);
        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f2 = vector3fArray[i];
            vector3f2.rotate((Quaternionfc)quaternionf);
            vector3f2.mul(f5);
            vector3f2.add(f2, f3, f4);
        }
        float f6 = this.getU0();
        float f7 = this.getU1();
        float f8 = this.getV0();
        float f9 = this.getV1();
        int n = this.getLightColor(f);
        vertexConsumer.vertex(vector3fArray[0].x(), vector3fArray[0].y(), vector3fArray[0].z()).uv(f7, f9).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(n).endVertex();
        vertexConsumer.vertex(vector3fArray[1].x(), vector3fArray[1].y(), vector3fArray[1].z()).uv(f7, f8).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(n).endVertex();
        vertexConsumer.vertex(vector3fArray[2].x(), vector3fArray[2].y(), vector3fArray[2].z()).uv(f6, f8).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(n).endVertex();
        vertexConsumer.vertex(vector3fArray[3].x(), vector3fArray[3].y(), vector3fArray[3].z()).uv(f6, f9).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(n).endVertex();
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        Optional<Vector3d> optional = this.target.getPosition(this.level);
        if (optional.isEmpty()) {
            this.remove();
            return;
        }
        int n = this.lifetime - this.age;
        double d = 1.0 / (double)n;
        Vector3d vector3D = optional.get();
        this.x = MathHelper.lerp(d, this.x, vector3D.x());
        this.y = MathHelper.lerp(d, this.y, vector3D.y());
        this.z = MathHelper.lerp(d, this.z, vector3D.z());
        double d2 = this.x - vector3D.x();
        double d3 = this.y - vector3D.y();
        double d4 = this.z - vector3D.z();
        this.rotO = this.rot;
        this.rot = (float)MathHelper.atan2(d2, d4);
        this.pitchO = this.pitch;
        this.pitch = (float)MathHelper.atan2(d3, Math.sqrt(d2 * d2 + d4 * d4));
    }

    @Override
    public int getLightColor(float f) {
        return 240;
    }

    public static class Provider
    implements IParticleFactory<VibrationParticleOption> {
        private final IAnimatedSprite sprite;

        public Provider(IAnimatedSprite spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(VibrationParticleOption vibrationParticleOption, ClientWorld clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            VibrationParticleSignal vibrationSignalParticle = new VibrationParticleSignal(clientLevel, d, d2, d3, vibrationParticleOption.getDestination(), vibrationParticleOption.getArrivalInTicks());
            vibrationSignalParticle.pickSprite(this.sprite);
            vibrationSignalParticle.setAlpha(1.0f);
            return vibrationSignalParticle;
        }

    }
}

