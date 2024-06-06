package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.Position;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.RandomObjectDescriptor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RaccoonDebugRenderer implements DebugRenderer.IDebugRenderer {
    private final Minecraft minecraft;
    private final Map<UUID, RaccoonInfo> raccoonInfos = Maps.newHashMap();
    private UUID lastLookedAtUuid;

    public RaccoonDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void clear() {
        this.raccoonInfos.clear();
        this.lastLookedAtUuid = null;
    }

    public void addOrUpdateRaccoonInfo(RaccoonInfo info) {
        this.raccoonInfos.put(info.uuid, info);
    }

    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, double cameraX, double cameraY, double cameraZ) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        this.clearRemovedRaccoons();
        this.doRender();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
        if (!this.minecraft.player.isSpectator()) {
            this.updateLastLookedAtUuid();
        }
    }

    private void clearRemovedRaccoons() {
        this.raccoonInfos.entrySet().removeIf(entry -> this.minecraft.level.getEntity(entry.getValue().id) == null);
    }

    private void doRender() {
        BlockPos cameraPos = this.getCamera().getBlockPosition();
        this.raccoonInfos.values().forEach(info -> {
            if (this.isPlayerCloseEnoughToMob(info)) {
                this.renderRaccoonInfo(info);
            }
        });
    }

    private void renderRaccoonInfo(RaccoonInfo info) {
        int i = 0;
        renderTextOverMob(info.position, i++, RandomObjectDescriptor.getEntityName(info.uuid), -1, 0.03F);
        if (info.isLeader) {
            renderTextOverMob(info.position, i++, "Leader", -16711936, 0.02F);
        }
        if (info.homePos.getX() == 0 && info.homePos.getY() == 0 && info.homePos.getZ() == 0) {
            renderTextOverMob(info.position, i++, "No home", -98404, 0.02F);
        } else {
            DebugRenderer.renderFilledBox(info.homePos.offset(-0.5D, -0.5D, -0.5D), info.homePos.offset(1.5D, 1.5D, 1.5D), 1.0F, 0.0F, 0.0F, 0.15F);
            DebugRenderer.renderFloatingText(("Home of: " + RandomObjectDescriptor.getEntityName(info.uuid)), info.homePos.getX(), info.homePos.getY() + 0.5, info.homePos.getZ(), 16759603, 0.023F, false, 0.5F, true);
            renderTextOverMob(info.position, i++, "Home: " + this.getPosDescription(info, info.homePos), -256, 0.02F);
        }
        renderTextOverMob(info.position, i++, "Home Members: " + info.homeMembers.size(), -256, 0.02F);
        for (UUID member : info.homeMembers) {
            renderTextOverMob(info.position, i++, RandomObjectDescriptor.getEntityName(member), -256, 0.02F);
        }
        renderTextOverMob(info.position, i++, "Hunger: " + info.hunger, 16759603, 0.02F);
        renderTextOverMob(info.position, i++, "Thirst: " + info.thirst, 3381759, 0.02F);
        renderTextOverMob(info.position, i++, "Dirty Countdown: " + info.dirtiness, -256, 0.02F);

        if (info.path != null) {
            if (info.path.getEndNode() != null) {
                IPosition targetPosition = new Position(info.path.getEndNode().asBlockPos().getX(), info.path.getEndNode().asBlockPos().getY(), info.path.getEndNode().asBlockPos().getZ());
                renderTextOverMob(info.position, i++, "Target position:" + info.path.getEndNode().asBlockPos(), -256, 0.02F);
                DebugRenderer.renderFilledBox(info.path.getEndNode().asBlockPos(), 0.05F, 0.8F, 0.8F, 0.0F, 0.3F);
                DebugRenderer.renderFloatingText("Target position", targetPosition.x(),targetPosition.y(),targetPosition.z(), -256, 0.02F, false, 0.5F, true);
            }
            PathfindingDebugRenderer.renderPath(info.path, 0.5F, false, false, this.getCamera().getPosition().x(), this.getCamera().getPosition().y(), this.getCamera().getPosition().z());
        }
    }

    private static void renderTextOverMob(IPosition pos, int lineNumber, String text, int color, float scale) {
        double x = pos.x() + 0.5D;
        double y = pos.y() + 2.4D + lineNumber * 0.25D;
        double z = pos.z() + 0.5D;
        DebugRenderer.renderFloatingText(text, x, y, z, color, scale, false, 0.5F, true);
    }

    private ActiveRenderInfo getCamera() {
        return this.minecraft.gameRenderer.getMainCamera();
    }

    private String getPosDescription(RaccoonInfo info, BlockPos pos) {
        float distance = MathHelper.sqrt(pos.distSqr(info.position.x(), info.position.y(), info.position.z(), true));
        double roundedDistance = (double) Math.round(distance * 10.0F) / 10.0D;
        return pos.toShortString() + " (dist " + roundedDistance + ")";
    }

    private boolean isPlayerCloseEnoughToMob(RaccoonInfo info) {
        PlayerEntity player = this.minecraft.player;
        BlockPos playerPos = new BlockPos(player.getX(), info.position.y(), player.getZ());
        BlockPos raccoonPos = new BlockPos(info.position);
        return playerPos.closerThan(raccoonPos, 30.0D);
    }

    private void updateLastLookedAtUuid() {
        DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent(entity -> this.lastLookedAtUuid = entity.getUUID());
    }

    @OnlyIn(Dist.CLIENT)
    public static class RaccoonInfo {
        public final UUID uuid;
        public final int id;
        public final IPosition position;
        public final BlockPos homePos;
        public final Path path;
        public final boolean isLeader;
        public final List<UUID> homeMembers;
        public final int hunger;
        public final int thirst;
        public final BlockPos targetPos;
        public final int dirtiness;

        public RaccoonInfo(UUID uuid, int id, IPosition position, BlockPos homePos, Path path, boolean isLeader, List<UUID> homeMembers, int hunger, int thirst, BlockPos targetPos, int dirtiness) {
            this.uuid = uuid;
            this.id = id;
            this.position = position;
            this.homePos = homePos;
            this.path = path;
            this.isLeader = isLeader;
            this.homeMembers = homeMembers;
            this.hunger = hunger;
            this.thirst = thirst;
            this.targetPos = targetPos;
            this.dirtiness = dirtiness;
        }

        public String toString() {
            return RandomObjectDescriptor.getEntityName(this.uuid);
        }
    }
}