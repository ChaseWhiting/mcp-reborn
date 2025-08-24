package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.BrushableBlockEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BrushableBlockRenderer extends TileEntityRenderer<BrushableBlockEntity> {
    private final ItemRenderer itemRenderer;
    @Nullable
    private final World world;

    public BrushableBlockRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
        this.world = dispatcher.level;
    }

    @Override
    public void render(BrushableBlockEntity brushableBlockEntity, float f, MatrixStack poseStack, IRenderTypeBuffer multiBufferSource, int n, int n2) {
        //if (brushableBlockEntity.getLevel() == null) return;


        int n3 = brushableBlockEntity.getBlockState().getValue(BlockStateProperties.DUSTED);
        if (n3 <= 0) return;
        Direction direction = brushableBlockEntity.getHitDirection();
        if (direction == null) return;
        ItemStack stack = brushableBlockEntity.getItem();
        if (stack.isEmpty()) return;
        poseStack.pushPose();
        poseStack.translate(0.0f, 0.5f, 0.0f);
        float[] fArray = this.translations(direction, n3);
        poseStack.translate(fArray[0], fArray[1], fArray[2]);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(75.0F));
        boolean bl = direction == Direction.EAST || direction == Direction.WEST;
        poseStack.mulPose(Vector3f.YP.rotationDegrees((bl ? 90 : 0) + 11));
        poseStack.scale(0.5F, 0.5F, 0.5F);
        int n4;

        if (world == null) {
            n4 = 15728880;
        } else {
            n4 = WorldRenderer.getLightColor(world, brushableBlockEntity.getBlockPos().relative(direction));
        }

        this.itemRenderer.renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, n4, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource);


        poseStack.popPose();
    }

    private float[] translations(Direction direction, int n) {
        float[] fArray = new float[]{0.5f, 0.0f, 0.5f};
        float f = (float)n / 10.0f * 0.75f;
        switch (direction) {
            case EAST: {
                fArray[0] = 0.73f + f;
                break;
            }
            case WEST: {
                fArray[0] = 0.25f - f;
                break;
            }
            case UP: {
                fArray[1] = 0.25f + f;
                break;
            }
            case DOWN: {
                fArray[1] = -0.23f - f;
                break;
            }
            case NORTH: {
                fArray[2] = 0.25f - f;
                break;
            }
            case SOUTH: {
                fArray[2] = 0.73f + f;
            }
        }
        return fArray;
    }
}
