package net.minecraft.client.renderer.entity.newrenderers.mob;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.hitbox.HitboxRenderState;
import net.minecraft.client.renderer.entity.state.hitbox.HitboxesRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.leashable.Leashable;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.LightType;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;

public abstract class NewEntityRenderer<T extends Entity, S extends EntityRenderState> {
    protected static final float NAMETAG_SCALE = 0.025f;
    public static final int LEASH_RENDER_STEPS = 24;
    public static final float LEASH_WIDTH = 0.05f;
    protected final EntityRendererManager entityRenderDispatcher;
    private final FontRenderer fontRenderer;
    protected float shadowRadius;
    protected float shadowStrength = 1.0f;
    private final S reusedState = this.createRenderState();

    protected NewEntityRenderer(EntityRendererManager context) {
        this.entityRenderDispatcher = context;
        this.fontRenderer = context.getFont();
    }

    public final int getPackedLightCoords(T entity, float f) {
        BlockPos blockPos = BlockPos.containing(entity.getLightProbePosition(f));
        return LightTexture.pack(this.getBlockLightLevel(entity, blockPos), this.getSkyLightLevel(entity, blockPos));
    }

    protected int getSkyLightLevel(T entity, BlockPos blockPos) {
        return (entity).level().getBrightness(LightType.SKY, blockPos);
    }

    protected int getBlockLightLevel(T entity, BlockPos blockPos) {
        if (entity.isOnFire()) {
            return 15;
        }
        return (entity.level().getBrightness(LightType.SKY, blockPos));
    }

    public boolean shouldRender(T entity, ClippingHelper frustum, double x, double y, double z) {
        if (!entity.shouldRender(x, y, z)) {
            return false;
        } else if (entity.noCulling) {
            return true;
        } else {
            AxisAlignedBB axisalignedbb = entity.getBoundingBoxForCulling().inflate(0.5D);
            if (axisalignedbb.hasNaN() || axisalignedbb.getSize() == 0.0D) {
                axisalignedbb = new AxisAlignedBB(entity.getX() - 2.0D, entity.getY() - 2.0D, entity.getZ() - 2.0D, entity.getX() + 2.0D, entity.getY() + 2.0D, entity.getZ() + 2.0D);
            }

            return frustum.isVisible(axisalignedbb);
        }
    }

    protected AxisAlignedBB getBoundingBoxForCulling(T entity) {
        return entity.getBoundingBox();
    }

    protected boolean affectedByCulling(T entity) {
        return true;
    }

    public Vector3d getRenderOffset(S state) {
        if (state.passengerOffset != null) {
            return state.passengerOffset;
        }
        return Vector3d.ZERO;
    }

    public void render(S state, MatrixStack matrix, IRenderTypeBuffer multiBufferSource, int n) {
        if (state.leashStates != null) {
            for (EntityRenderState.LeashState leashState : state.leashStates) {
                renderLeash(matrix, multiBufferSource, leashState);
            }
        }
        if (state.nameTag != null) {
            this.renderNameTag(state, state.nameTag, matrix, multiBufferSource, n);
        }

    }

    private static void renderLeash(MatrixStack matrixStack, IRenderTypeBuffer bufferSource, EntityRenderState.LeashState leashState) {
        float dx = (float)(leashState.end.x - leashState.start.x);
        float dy = (float)(leashState.end.y - leashState.start.y);
        float dz = (float)(leashState.end.z - leashState.start.z);

        // Normalize horizontal direction and scale it down
        float horizontalScale = MathHelper.invSqrt(dx * dx + dz * dz) * 0.05f / 2.0f;
        float offsetX = dz * horizontalScale;
        float offsetZ = dx * horizontalScale;

        matrixStack.pushPose();
        matrixStack.translate(leashState.offset);

        IVertexBuilder vertexBuilder = bufferSource.getBuffer(RenderType.leash());
        Matrix4f matrix = matrixStack.last().pose();

        // Draw first half of the leash
        for (int segment = 0; segment <= 24; ++segment) {
            addVertexPair(vertexBuilder, matrix, 15728880, dx, dy, dz, 0.05f, 0.05f, offsetX, offsetZ, 24, segment, false);
        }

        // Draw second half (reverse)
        for (int segment = 24; segment >= 0; --segment) {
            addVertexPair(vertexBuilder, matrix, 15728880, dx, dy, dz, 0.05f, 0.0f, offsetX, offsetZ, 24, segment, true);
        }

        matrixStack.popPose();
    }


    public static void addVertexPair(IVertexBuilder vertexBuilder, Matrix4f matrix, int lightmap, float dx, float dy, float dz, float offsetA, float offsetB, float offsetX, float offsetZ, int totalSegments, int segmentIndex, boolean reversed) {
        float r = 0.5F;
        float g = 0.4F;
        float b = 0.3F;
        if (segmentIndex % 2 == 0) {
            r *= 0.7F;
            g *= 0.7F;
            b *= 0.7F;
        }
        float progress = (float)segmentIndex / (float)totalSegments;
        float x = dx * progress;
        float y = dy > 0.0F ? dy * progress * progress : dy - dy * (1.0F - progress) * (1.0F - progress);
        float z = dz * progress;
        if (!reversed) {
            vertexBuilder.vertex(matrix, x + offsetX, y + offsetB - offsetA, z - offsetZ).color(r, g, b, 1.0F).uv2(lightmap).endVertex();
        }
        vertexBuilder.vertex(matrix, x - offsetX, y + offsetA, z + offsetZ).color(r, g, b, 1.0F).uv2(lightmap).endVertex();
        if (reversed) {
            vertexBuilder.vertex(matrix, x + offsetX, y + offsetB - offsetA, z - offsetZ).color(r, g, b, 1.0F).uv2(lightmap).endVertex();
        }
    }

    protected boolean shouldShowName(T entity, double d) {
        return entity.shouldShowName() || entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity;
    }

    public FontRenderer getFont() {
        return this.fontRenderer;
    }

    protected void renderNameTag(S state, ITextComponent component, MatrixStack matrix, IRenderTypeBuffer multiBufferSource, int n) {
        Vector3d vec3 = ((EntityRenderState)state).nameTagAttachment;
        if (vec3 == null) {
            return;
        }
        boolean bl = !state.isDiscrete;
        int n2 = "deadmau5".equals(component.getString()) ? -10 : 0;
        matrix.pushPose();
        matrix.translate(vec3.x, vec3.y + 0.5, vec3.z);
        matrix.mulPose(this.entityRenderDispatcher.cameraOrientation());
        matrix.scale(0.025f, -0.025f, 0.025f);
        Matrix4f matrix4f = matrix.last().pose();
        FontRenderer font = this.getFont();
        float f = (float)(-font.width(component)) / 2.0f;
        int n3 = (int)(Minecraft.getInstance().options.getBackgroundOpacity(0.25f) * 255.0f) << 24;
        font.drawInBatch(component, f, (float)n2, -2130706433, false, matrix4f, multiBufferSource, bl, n3, n);
        if (bl) {
            font.drawInBatch(component, f, (float)n2, -1, false, matrix4f, multiBufferSource, false, 0, LightTexture.lightCoordsWithEmission(n, 2));
        }
        matrix.popPose();
    }

    @Nullable
    protected ITextComponent getNameTag(T entity) {
        return entity.getDisplayName();
    }

    protected float getShadowRadius(S s) {
        return this.shadowRadius;
    }

    protected float getShadowStrength(S s) {
        return this.shadowStrength;
    }

    public abstract S createRenderState();

    public final S createRenderState(T t, float f) {
        S s = this.reusedState;
        this.extractRenderState(t, s, f);
        return s;
    }

    public void extractRenderState(T t, S s, float f) {
        Entity entity = (Entity) t;
        EntityRenderState state = (EntityRenderState) s;

        state.entityType = entity.getType();
        state.x = MathHelper.lerp(f, entity.xOld, entity.getX());
        state.y = MathHelper.lerp(f, entity.yOld, entity.getY());
        state.z = MathHelper.lerp(f, entity.zOld, entity.getZ());
        state.isInvisible = entity.isInvisible();
        state.ageInTicks = entity.tickCount + f;
        state.boundingBoxWidth = entity.getBbWidth();
        state.boundingBoxHeight = entity.getBbHeight();
        state.eyeHeight = entity.getEyeHeight();

        if (entity.isPassenger()) {
            Entity vehicle = entity.getVehicle();
            if (vehicle instanceof AbstractMinecartEntity minecart) {{
                    state.passengerOffset = null;
                }
            } else {
                state.passengerOffset = null;
            }
        } else {
            state.passengerOffset = null;
        }

        state.distanceToCameraSq = this.entityRenderDispatcher.distanceToSqr(entity);
        boolean showName = state.distanceToCameraSq < 4096.0 && this.shouldShowName(t, state.distanceToCameraSq);
        state.nameTag = showName ? this.getNameTag(t) : null;
        state.nameTagAttachment = showName ? new Vector3d(0.0D, entity.getNameTagOffsetY(), 0) : null;

        state.isDiscrete = entity.isDiscrete();

        if (t instanceof Leashable leashable) {
            Entity holder = leashable.getLeashHolder();
            if (true) {
                float bodyRot = entity.getPreciseBodyRotation(f) * ((float) Math.PI / 180);
                Vector3d leashOffset = leashable.getLeashOffset(f);
                BlockPos pos = BlockPos.containing(entity.getEyePosition(f));
                BlockPos holderPos = BlockPos.containing(holder.getEyePosition(f));
                int blockLight = this.getBlockLightLevel(t, pos);
                //int holderBlockLight = this.entityRenderDispatcher.getRenderer(holder).getBlockLightLevel(holder, holderPos);
                int holderBlockLight = this.getBlockLightLevel((T) holder, holderPos);
                int skyLight = entity.level().getBrightness(LightType.SKY, pos);
                int holderSkyLight = entity.level().getBrightness(LightType.SKY, holderPos);

                boolean quad = holder.supportQuadLeashAsHolder() && leashable.supportQuadLeash();
                int count = quad ? 4 : 1;

                if (state.leashStates == null || state.leashStates.size() != count) {
                    state.leashStates = new ArrayList<>(count);
                    for (int i = 0; i < count; ++i) {
                        state.leashStates.add(new EntityRenderState.LeashState());
                    }
                }

                if (quad) {
                    float holderRot = holder.getPreciseBodyRotation(f) * ((float) Math.PI / 180);
                    Vector3d holderPosVec = holder.getPosition(f);
                    Vector3d[] leashOffsets = leashable.getQuadLeashOffsets();
                    Vector3d[] holderOffsets = holder.getQuadLeashHolderOffsets();

                    for (int i = 0; i < count; ++i) {
                        var leashState = state.leashStates.get(i);
                        leashState.offset = leashOffsets[i].yRot(-bodyRot);
                        leashState.start = entity.getPosition(f).add(leashState.offset);
                        leashState.end = holderPosVec.add(holderOffsets[i].yRot(-holderRot));
                        leashState.startBlockLight = blockLight;
                        leashState.endBlockLight = holderBlockLight;
                        leashState.startSkyLight = skyLight;
                        leashState.endSkyLight = holderSkyLight;
                        leashState.slack = false;
                    }
                } else {
                    Vector3d offset = leashOffset.yRot(-bodyRot);
                    EntityRenderState.LeashState leashState = state.leashStates.get(0);
                    leashState.offset = offset;
                    leashState.start = entity.getPosition(f).add(offset);
                    leashState.end = holder.getRopeHoldPosition(f);
                    leashState.startBlockLight = blockLight;
                    leashState.endBlockLight = holderBlockLight;
                    leashState.startSkyLight = skyLight;
                    leashState.endSkyLight = holderSkyLight;
                }
            } else {
                state.leashStates = null;
            }
        } else {
            state.leashStates = null;
        }

        state.displayFireAnimation = entity.displayFireAnimation();

        Minecraft mc = Minecraft.getInstance();
        if (mc.getEntityRenderDispatcher().shouldRenderHitBoxes() && !state.isInvisible && !mc.showOnlyReducedInfo()) {
            this.extractHitboxes(t, s, f);
        } else {
            state.hitboxesRenderState = null;
            state.serverHitboxesRenderState = null;
        }
    }

    private void extractHitboxes(T t, S s, float f) {
        ((EntityRenderState)s).hitboxesRenderState = this.extractHitboxes(t, f, false);
        ((EntityRenderState)s).serverHitboxesRenderState = null;
    }

    private HitboxesRenderState extractHitboxes(T t, float f, boolean bl) {
        ImmutableList.Builder builder = new ImmutableList.Builder();
        AxisAlignedBB aABB = ((Entity)t).getBoundingBox();
        HitboxRenderState hitboxRenderState = bl ? new HitboxRenderState(aABB.minX - ((Entity)t).getX(), aABB.minY - ((Entity)t).getY(), aABB.minZ - ((Entity)t).getZ(), aABB.maxX - ((Entity)t).getX(), aABB.maxY - ((Entity)t).getY(), aABB.maxZ - ((Entity)t).getZ(), 0.0f, 1.0f, 0.0f) : new HitboxRenderState(aABB.minX - ((Entity)t).getX(), aABB.minY - ((Entity)t).getY(), aABB.minZ - ((Entity)t).getZ(), aABB.maxX - ((Entity)t).getX(), aABB.maxY - ((Entity)t).getY(), aABB.maxZ - ((Entity)t).getZ(), 1.0f, 1.0f, 1.0f);
        builder.add((Object)hitboxRenderState);
        Entity entity = ((Entity)t).getVehicle();
        if (entity != null) {
            float f2 = Math.min(entity.getBbWidth(), ((Entity)t).getBbWidth()) / 2.0f;
            float f3 = 0.0625f;
//            Vector3d vec3 = entity.getPassengerRidingPosition((Entity)t).subtract(((Entity)t).position());
//            HitboxRenderState hitboxRenderState2 = new HitboxRenderState(vec3.x - (double)f2, vec3.y, vec3.z - (double)f2, vec3.x + (double)f2, vec3.y + 0.0625, vec3.z + (double)f2, 1.0f, 1.0f, 0.0f);
//            builder.add((Object)hitboxRenderState2);
        }
        this.extractAdditionalHitboxes(t, (ImmutableList.Builder<HitboxRenderState>)builder, f);
        Vector3d vec3 = ((Entity)t).getViewVector(f);
        return new HitboxesRenderState(vec3.x, vec3.y, vec3.z, (ImmutableList<HitboxRenderState>)builder.build());
    }

    protected void extractAdditionalHitboxes(T t, ImmutableList.Builder<HitboxRenderState> builder, float f) {
    }

    @Nullable
    private static Entity getServerSideEntity(Entity entity) {
        ServerWorld serverLevel;
        IntegratedServer integratedServer = Minecraft.getInstance().getSingleplayerServer();
        if (integratedServer != null && (serverLevel = integratedServer.getLevel(entity.level().dimension())) != null) {
            return serverLevel.getEntity(entity.getId());
        }
        return null;
    }
}
