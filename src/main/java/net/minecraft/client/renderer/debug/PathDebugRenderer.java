package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@OnlyIn(Dist.CLIENT)
public class PathDebugRenderer implements DebugRenderer.IDebugRenderer {
    private final Minecraft minecraft;
    private final Map<Integer, PathInfo> pathInfoMap = new ConcurrentHashMap<>();

    public PathDebugRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void addOrUpdatePathsCalculation(PathInfo pathInfo) {
        pathInfoMap.put(pathInfo.entityId, pathInfo);
    }

    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, double cameraX, double cameraY, double cameraZ) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();

        for (PathInfo pathInfo : pathInfoMap.values()) {
            renderPath(matrixStack, buffer, pathInfo, cameraX, cameraY, cameraZ);
        }

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private void renderPath(MatrixStack matrixStack, IRenderTypeBuffer buffer, PathInfo pathInfo, double cameraX, double cameraY, double cameraZ) {
        for (PathPoint point : pathInfo.pathPoints) {
            BlockPos pos = new BlockPos(point.x, point.y, point.z);
            DebugRenderer.renderFilledBox(pos.offset(-0.5D, -0.5D, -0.5D), pos.offset(0.5D, 0.5D, 0.5D), 0.0F, 1.0F, 0.0F, 0.5F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class PathInfo {
        public final int entityId;
        public final List<PathPoint> pathPoints;

        public PathInfo(int entityId, List<PathPoint> pathPoints) {
            this.entityId = entityId;
            this.pathPoints = pathPoints;
        }
    }
}
