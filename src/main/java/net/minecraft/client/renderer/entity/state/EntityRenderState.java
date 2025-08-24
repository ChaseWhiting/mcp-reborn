package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.hitbox.HitboxesRenderState;
import net.minecraft.client.renderer.entity.state.hitbox.ServerHitboxesRenderState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class EntityRenderState {
    public EntityType<?> entityType;
    public double x;
    public double y;
    public double z;
    public float ageInTicks;
    public float boundingBoxWidth;
    public float boundingBoxHeight;
    public float eyeHeight;
    public double distanceToCameraSq;
    public boolean isInvisible;
    public boolean isDiscrete;
    public boolean displayFireAnimation;
    @Nullable
    public Vector3d passengerOffset;
    @Nullable
    public ITextComponent nameTag;
    @Nullable
    public Vector3d nameTagAttachment;
    @Nullable
    public List<LeashState> leashStates;
    @Nullable
    public HitboxesRenderState hitboxesRenderState;
    @Nullable
    public ServerHitboxesRenderState serverHitboxesRenderState;

    public void fillCrashReportCategory(CrashReportCategory crashReportCategory) {
        crashReportCategory.setDetail("EntityRenderState", this.getClass().getCanonicalName());
        crashReportCategory.setDetail("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.x, this.y, this.z));
    }

    public static class LeashState {
        public Vector3d offset = Vector3d.ZERO;
        public Vector3d start = Vector3d.ZERO;
        public Vector3d end = Vector3d.ZERO;
        public int startBlockLight = 0;
        public int endBlockLight = 0;
        public int startSkyLight = 15;
        public int endSkyLight = 15;
        public boolean slack = true;
    }
}

