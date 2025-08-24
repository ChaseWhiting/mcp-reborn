package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tileentity.DecoratedPotPatterns;
import net.minecraft.tileentity.DecoratedPotTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class DecoratedPotRenderer extends TileEntityRenderer<DecoratedPotTileEntity> {

    private static final String NECK = "neck";
    private static final String FRONT = "front";
    private static final String BACK = "back";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String TOP = "top";
    private static final String BOTTOM = "bottom";
    private final ModelPart neck;
    private final ModelPart frontSide;
    private final ModelPart backSide;
    private final ModelPart leftSide;
    private final ModelPart rightSide;
    private final ModelPart top;
    private final ModelPart bottom;
    private static final float WOBBLE_AMPLITUDE = 0.125f;
    private static final float[] SIN = Util.make(new float[65536], fArray -> {
        for (int i = 0; i < ((float[]) fArray).length; ++i) {
            fArray[i] = (float) java.lang.Math.sin((double) i * java.lang.Math.PI * 2.0 / 65536.0);
        }

    });


    public DecoratedPotRenderer(TileEntityRendererDispatcher context) {
        super(context);
        ModelPart modelPart = context.bakeLayer(ModelLayers.DECORATED_POT_BASE);
        this.neck = modelPart.getChild(NECK);
        this.top = modelPart.getChild(TOP);
        this.bottom = modelPart.getChild(BOTTOM);
        ModelPart modelPart2 = context.bakeLayer(ModelLayers.DECORATED_POT_SIDES);
        this.frontSide = modelPart2.getChild(FRONT);
        this.backSide = modelPart2.getChild(BACK);
        this.leftSide = modelPart2.getChild(LEFT);
        this.rightSide = modelPart2.getChild(RIGHT);
    }

    public static LayerDefinition createBaseLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        CubeDeformation cubeDeformation = new CubeDeformation(0.2f);
        CubeDeformation cubeDeformation2 = new CubeDeformation(-0.1f);
        partDefinition.addOrReplaceChild(NECK, CubeListBuilder.create().texOffs(0, 0).addBox(4.0f, 17.0f, 4.0f, 8.0f, 3.0f, 8.0f, cubeDeformation2).texOffs(0, 5).addBox(5.0f, 20.0f, 5.0f, 6.0f, 1.0f, 6.0f, cubeDeformation), PartPose.offsetAndRotation(0.0f, 37.0f, 16.0f, (float) Math.PI, 0.0f, 0.0f));
        CubeListBuilder cubeListBuilder = CubeListBuilder.create().texOffs(-14, 13).addBox(0.0f, 0.0f, 0.0f, 14.0f, 0.0f, 14.0f);
        partDefinition.addOrReplaceChild(TOP, cubeListBuilder, PartPose.offsetAndRotation(1.0f, 16.0f, 1.0f, 0.0f, 0.0f, 0.0f));
        partDefinition.addOrReplaceChild(BOTTOM, cubeListBuilder, PartPose.offsetAndRotation(1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f));
        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    public static LayerDefinition createSidesLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        CubeListBuilder cubeListBuilder = CubeListBuilder.create().texOffs(1, 0).addBox(0.0f, 0.0f, 0.0f, 14.0f, 16.0f, 0.0f, EnumSet.of(Direction.NORTH));
        partDefinition.addOrReplaceChild(BACK, cubeListBuilder, PartPose.offsetAndRotation(15.0f, 16.0f, 1.0f, 0.0f, 0.0f, (float) Math.PI));
        partDefinition.addOrReplaceChild(LEFT, cubeListBuilder, PartPose.offsetAndRotation(1.0f, 16.0f, 1.0f, 0.0f, -1.5707964f, (float) Math.PI));
        partDefinition.addOrReplaceChild(RIGHT, cubeListBuilder, PartPose.offsetAndRotation(15.0f, 16.0f, 15.0f, 0.0f, 1.5707964f, (float) Math.PI));
        partDefinition.addOrReplaceChild(FRONT, cubeListBuilder, PartPose.offsetAndRotation(1.0f, 16.0f, 15.0f, (float) Math.PI, 0.0f, 0.0f));
        return LayerDefinition.create(meshDefinition, 16, 16);
    }


    private static ResourceLocation getPotTexture(@Nullable Item item) {
        RegistryKey<String> patternKey = DecoratedPotPatterns.getResourceKey(item);
        if (patternKey == null) {
            patternKey = DecoratedPotPatterns.getResourceKey(Items.BRICK);
        }
        return DecoratedPotPatterns.location(patternKey);
    }


    @Override
    public void render(DecoratedPotTileEntity decoratedPotBlockEntity, float f, MatrixStack matrixStack, IRenderTypeBuffer multiBufferSource, int light, int overlay) {
        float f2;
        matrixStack.pushPose();
        Direction direction;

        if (decoratedPotBlockEntity.getLevel() != null) {
            direction = decoratedPotBlockEntity.getDirection();
        } else {
            direction = Direction.NORTH;
        }
        matrixStack.translate(0.5, 0.0, 0.5);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0f - direction.toYRot()));
        matrixStack.translate(-0.5, 0.0, -0.5);
        DecoratedPotTileEntity.WobbleStyle wobbleStyle = decoratedPotBlockEntity.lastWobbleStyle;
        if (wobbleStyle != null && decoratedPotBlockEntity.getLevel() != null && (f2 = ((float) (decoratedPotBlockEntity.getLevel().getGameTime() - decoratedPotBlockEntity.wobbleStartedAtTick) + f) / (float) wobbleStyle.duration) >= 0.0f && f2 <= 1.0f) {
            if (wobbleStyle == DecoratedPotTileEntity.WobbleStyle.POSITIVE) {
                float f3 = 0.015625f;
                float f4 = f2 * ((float) Math.PI * 2);
                float f5 = -1.5f * (cos(f4) + 0.5f) * sin(f4 / 2.0f);
                matrixStack.rotateAround(Vector3f.XP.rotation(f5 * 0.015625f), 0.5f, 0.0f, 0.5f);
                float f6 = sin(f4);
                matrixStack.rotateAround(Vector3f.ZP.rotation(f6 * 0.015625f), 0.5f, 0.0f, 0.5f);
            } else {
                float f7 = sin(-f2 * 3.0f * (float) Math.PI) * 0.125f * (1.0F - f2);
                //float f8 = 1.0f - f2;
                matrixStack.rotateAround(Vector3f.YP.rotation(f7), 0.5f, 0.0f, 0.5f);
            }
        }
        ResourceLocation baseTexture = DecoratedPotPatterns.location(DecoratedPotPatterns.BASE);
        IVertexBuilder vertexBuilder = multiBufferSource.getBuffer(RenderType.entitySolid(baseTexture));
        this.neck.render(matrixStack, vertexBuilder, light, overlay);
        this.top.render(matrixStack, vertexBuilder, light, overlay);
        this.bottom.render(matrixStack, vertexBuilder, light, overlay);
        DecoratedPotTileEntity.Decorations decorations = decoratedPotBlockEntity.getDecorations();
        this.renderSide(this.frontSide, matrixStack, multiBufferSource, light, overlay, decorations.front());
        this.renderSide(this.backSide, matrixStack, multiBufferSource, light, overlay, decorations.back());
        this.renderSide(this.leftSide, matrixStack, multiBufferSource, light, overlay, decorations.left());
        this.renderSide(this.rightSide, matrixStack, multiBufferSource, light, overlay, decorations.right());

        matrixStack.popPose();
    }

    private void renderSide(ModelPart modelPart, MatrixStack poseStack, IRenderTypeBuffer buffer, int light, int overlay, @Nullable Item item) {
        ResourceLocation texture = getPotTexture(item);
        IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.entitySolid(texture));
        modelPart.render(poseStack, vertexBuilder, light, overlay);
    }

    public static float sin(float f) {
        return SIN[(int) (f * 10430.378f) & 0xFFFF];
    }

    public static float cos(float f) {
        return SIN[(int)(f * 10430.378f + 16384.0f) & 0xFFFF];
    }

}
