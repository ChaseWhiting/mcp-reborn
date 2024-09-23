package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.bogged.BoggedEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class BoggedModel<T extends BoggedEntity> extends SkeletonModel<T> {
    private final ModelRenderer mushroom1;
    private final ModelRenderer mushroom2;
    private final ModelRenderer mushroom3;
    private final ModelRenderer mushroom4;
    private final ModelRenderer mushroom5;
    private final ModelRenderer mushroom6;
    private final ModelRenderer mushroom7;

    public BoggedModel() {
        this(0.0F, false);
    }

    public BoggedModel(float f, boolean b) {
        super(f, b);
        mushroom1 = new ModelRenderer(this);
        mushroom1.setPos(3.0F, -8.0F, 3.0F);
        mushroom1.xRot = 0.0F;
        mushroom1.yRot = 0.7854F;
        mushroom1.zRot = 0.0F;
        mushroom1.texOffs(50, 16).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 4.0F, 0.0F, 0.0F, false);
        mushroom1.texOffs(50, 10).addBox(0.0F, -3.0F, -3.0F, 0.0F, 4.0F, 6.0F, 0.0F, false);

        mushroom2 = new ModelRenderer(this);
        mushroom2.setPos(-3.0F, -8.0F, -3.0F);
        mushroom2.xRot = 0.0F;
        mushroom2.yRot = 0.7854F;
        mushroom2.zRot = 0.0F;
        mushroom2.texOffs(50, 28).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 4.0F, 0.0F, 0.0F, false);
        mushroom2.texOffs(50, 22).addBox(0.0F, -3.0F, -3.0F, 0.0F, 4.0F, 6.0F, 0.0F, false);

        mushroom3 = new ModelRenderer(this);
        mushroom3.setPos(-2.0F, -1.0F, 5.0F);
        mushroom3.xRot = -1.5708F;
        mushroom3.yRot = 0.0F;
        mushroom3.zRot = 2.3562F;
        mushroom3.texOffs(50, 28).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 4.0F, 0.0F, 0.0F, false);
        mushroom3.texOffs(50, 22).addBox(0.0F, -3.0F, -3.0F, 0.0F, 4.0F, 6.0F, 0.0F, false);

        mushroom4 = new ModelRenderer(this);
        mushroom4.setPos(4.5F, -4.5F, -1.0F);
        setRotationAngle(mushroom4, 0.4574F, -0.0979F, 1.2383F);
        mushroom4.texOffs(50, 28).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 4.0F, 0.0F, 0.0F, false);
        mushroom4.texOffs(50, 22).addBox(0.0F, -3.0F, -3.0F, 0.0F, 4.0F, 6.0F, 0.0F, false);

        mushroom5 = new ModelRenderer(this);
        mushroom5.setPos(4.0F, -4.5F, 4.25F);
        setRotationAngle(mushroom5, -1.0697F, -0.0979F, 1.2383F);
        mushroom5.texOffs(50, 28).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 4.0F, 0.0F, 0.0F, false);
        mushroom5.texOffs(50, 22).addBox(0.0F, -3.0F, -3.0F, 0.0F, 4.0F, 6.0F, 0.0F, false);

        mushroom6 = new ModelRenderer(this);
        mushroom6.setPos(-3.0F, -4.5F, 4.25F);
        setRotationAngle(mushroom6, -2.3915F, -0.7128F, 1.785F);
        mushroom6.texOffs(50, 28).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 4.0F, 0.0F, 0.0F, false);
        mushroom6.texOffs(50, 22).addBox(0.0F, -3.0F, -3.0F, 0.0F, 4.0F, 6.0F, 0.0F, false);

        mushroom7 = new ModelRenderer(this);
        mushroom7.setPos(-5.0F, -4.5F, -1.75F);
        setRotationAngle(mushroom7, -3.046F, -0.7128F, 1.785F);
        mushroom7.texOffs(50, 28).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 4.0F, 0.0F, 0.0F, false);
        mushroom7.texOffs(50, 22).addBox(0.0F, -3.0F, -3.0F, 0.0F, 4.0F, 6.0F, 0.0F, false);

        this.head.addChildren(mushroom1, mushroom2, mushroom3, mushroom4, mushroom5, mushroom6, mushroom7);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderToBuffer(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

    }


    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTicks) {
        boolean flag = !entity.hasItemInSlot(EquipmentSlotType.HEAD) && !entity.isSheared() && entity.hasHeadAccessory() || entity.canAlwaysBeSheared() && entity.hasHeadAccessory();
        boolean flag2 = entity.multipleMushrooms();
        this.mushroom1.visible = flag;
        this.mushroom2.visible = flag;
        this.mushroom3.visible = flag;
        this.mushroom4.visible = flag && flag2;
        this.mushroom5.visible = flag && flag2;
        this.mushroom6.visible = flag && flag2;
        this.mushroom7.visible = flag && flag2;


        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
    }
}
