package net.minecraft.client.renderer.entity.model;// Made with Blockbench 4.10.1
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.OwlEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OwlModel<T extends OwlEntity> extends AgeableModel<T> {
    private final ModelRenderer head;

    private final ModelRenderer head_r1;
    private final ModelRenderer beak;
    private final ModelRenderer body;
    private final ModelRenderer tails;
    private final ModelRenderer left_wing;
    private final ModelRenderer right_wing;
    private final ModelRenderer left_leg;
    private final ModelRenderer right_leg;
    private final ModelRenderer left_ear_tuft_r1;
    private final ModelRenderer right_ear_tuft_r1;
    private final ModelRenderer beak_r1;
    private final ModelRenderer beak_r2;
    private final ModelRenderer body_r1;
    private final ModelRenderer tail_r1;
    private final ModelRenderer tail_r2;
    private final ModelRenderer tail_r3;
    //private final ModelRenderer tail_r4;
    private final ModelRenderer right_wing_upper_r1;
    private final ModelRenderer left_wing_upper_r1;

    public OwlModel() {
        texWidth = 64;
        texHeight = 32;

        head = new ModelRenderer(this);
        head.setPos(0.0F, 12.5F, -0.25F);
        head.texOffs(0, 14).addBox(-2.0F, -3.25F, -2.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        head.texOffs(0, 14).addBox(1.0F, -3.25F, -2.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        head.texOffs(0, 12).addBox(0.75F, -3.25F, -2.6F, 1.0F, 1.0F, 1.0F, -0.34F, false);
        head.texOffs(0, 12).addBox(-1.75F, -3.25F, -2.6F, 1.0F, 1.0F, 1.0F, -0.34F, false);

        left_ear_tuft_r1 = new ModelRenderer(this);
        left_ear_tuft_r1.setPos(3.0F, -5.5F, -3.0F);
        head.addChild(left_ear_tuft_r1);
        setRotationAngle(left_ear_tuft_r1, -1.2005F, -0.3623F, 1.865F);
        left_ear_tuft_r1.texOffs(56, 14).addBox(-1.0F, -5.0F, 1.0F, 4.0F, 5.0F, 0.0F, 0.0F, false);

        right_ear_tuft_r1 = new ModelRenderer(this);
        right_ear_tuft_r1.setPos(-3.0F, -5.5F, -3.0F);
        head.addChild(right_ear_tuft_r1);
        setRotationAngle(right_ear_tuft_r1, -1.2005F, 0.3623F, -1.865F);
        right_ear_tuft_r1.texOffs(56, 14).addBox(-3.0F, -5.0F, 1.0F, 4.0F, 5.0F, 0.0F, 0.0F, true);

        head_r1 = new ModelRenderer(this);
        head_r1.setPos(-0.5F, 0.25F, 0.0F);
        head.addChild(head_r1);
        setRotationAngle(head_r1, 0.0436F, 0.0F, 0.0F);
        head_r1.texOffs(28, 20).addBox(-3.0F, -5.95F, -1.9F, 7.0F, 6.0F, 6.0F, 0.01F, false);

        beak = new ModelRenderer(this);
        beak.setPos(0.0F, -2.0F, 0.25F);
        head.addChild(beak);


        beak_r1 = new ModelRenderer(this);
        beak_r1.setPos(0.5F, 1.75F, -2.25F);
        beak.addChild(beak_r1);
        setRotationAngle(beak_r1, 0.1309F, 0.0F, 0.0F);
        beak_r1.texOffs(60, 0).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 1.0F, -0.002F, false);

        beak_r2 = new ModelRenderer(this);
        beak_r2.setPos(0.5F, -0.25F, -2.5F);
        beak.addChild(beak_r2);
        setRotationAngle(beak_r2, -0.5672F, 0.0F, 0.0F);
        beak_r2.texOffs(60, 0).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.017F, false);

        body = new ModelRenderer(this);
        body.setPos(0.0F, 17.5F, 0.5F);
        setRotationAngle(body, 0.0436F, 0.0F, 0.0F);


        body_r1 = new ModelRenderer(this);
        body_r1.setPos(0.0F, 0.0F, -0.5F);
        body.addChild(body_r1);
        setRotationAngle(body_r1, 0.1309F, 0.0F, 0.0F);
        body_r1.texOffs(6, 18).addBox(-3.0F, -5.0F, -1.0F, 6.0F, 9.0F, 5.0F, 0.0F, false);

        tails = new ModelRenderer(this);
        tails.setPos(0.0F, 0.0F, 2.0F);
        body.addChild(tails);


        tail_r1 = new ModelRenderer(this);
        tail_r1.setPos(-0.75F, 2.5F, 3.0F);
        tails.addChild(tail_r1);
        setRotationAngle(tail_r1, 0.5324F, -0.2527F, -0.0692F);
        tail_r1.texOffs(14, 5).addBox(-4.0F, -2.0F, -1.0F, 7.0F, 6.0F, 0.0F, 0.0F, true);

        tail_r2 = new ModelRenderer(this);
        tail_r2.setPos(0.75F, 2.5F, 3.0F);
        tails.addChild(tail_r2);
        setRotationAngle(tail_r2, 0.5324F, 0.2527F, 0.0692F);
        tail_r2.texOffs(14, 5).addBox(-3.0F, -2.0F, -1.0F, 7.0F, 6.0F, 0.0F, 0.0F, false);

        tail_r3 = new ModelRenderer(this);
        tail_r3.setPos(0.0F, 1.5F, 2.75F);
        tails.addChild(tail_r3);
        setRotationAngle(tail_r3, 0.4363F, 0.0F, 0.0F);
        tail_r3.texOffs(0, 5).addBox(-3.0F, -2.0F, -1.0F, 6.0F, 7.0F, 0.0F, 0.0F, false);

        left_wing = new ModelRenderer(this);
        left_wing.setPos(2.85F, 14.0F, 0.25F);


        left_wing_upper_r1 = new ModelRenderer(this);
        left_wing_upper_r1.setPos(1.65F, 2.5F, 0.0F);
        left_wing.addChild(left_wing_upper_r1);
        setRotationAngle(left_wing_upper_r1, 0.1827F, 0.0125F, -0.1301F);
        left_wing_upper_r1.texOffs(56, 2).addBox(-1.03F, -3.96F, -0.96F, 0.0F, 8.0F, 4.0F, 0.0F, false);

        right_wing = new ModelRenderer(this);
        right_wing.setPos(-2.85F, 14.0F, 0.25F);


        right_wing_upper_r1 = new ModelRenderer(this);
        right_wing_upper_r1.setPos(-1.9F, 2.5F, 0.0F);
        right_wing.addChild(right_wing_upper_r1);
        setRotationAngle(right_wing_upper_r1, 0.1827F, -0.0125F, 0.1301F);
        right_wing_upper_r1.texOffs(56, 2).addBox(1.28F, -3.96F, -0.96F, 0.0F, 8.0F, 4.0F, 0.0F, true);

        left_leg = new ModelRenderer(this);
        left_leg.setPos(1.5F, 21.5F, 1.0F);
        left_leg.texOffs(1, 28).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 3.0F, 1.0F, -0.0002F, false);
        left_leg.texOffs(-4, 19).addBox(-1.5F, 2.5F, -2.25F, 4.0F, 0.0F, 4.0F, 0.0F, false);

        right_leg = new ModelRenderer(this);
        right_leg.setPos(-1.5F, 21.5F, 1.0F);
        right_leg.texOffs(1, 28).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 3.0F, 1.0F, -0.0002F, false);
        right_leg.texOffs(0, 14).addBox(-2.5F, 2.5F, -2.25F, 4.0F, 0.0F, 4.0F, 0.0F, false);
    }

    @Override
    public void setupAnim(OwlEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

       // this.head.xRot = headPitch * ((float)Math.PI / 180F);
       // this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);


        if (!entity.isOnGround()) {

            this.left_wing.setPos(2.85F, 15.8F, -0.85F);
            this.right_wing.setPos(-2.85F, 15.8F, -0.85F);
            this.body.xRot = 1F;
            this.head.setPos(0.1F, 17F, -6.5F);
            this.right_leg.setPos(-1.5F, 19.4F, 1.6F);
            this.left_leg.setPos(1.5F, 19.4F, 1.6F);
            this.left_leg.xRot = 0.6F;
            this.right_leg.xRot = 0.6F;
           // this.head.xRot = -0.06F;
        } else {
            this.left_wing.zRot = -0.1301F;
            this.right_wing.zRot = 0.1301F;
            this.right_wing.setPos(-2.85F, 14.0F, 0.25F);
            this.left_wing.setPos(2.85F, 14.0F, 0.25F);
            this.head.setPos(0.0F, 12.5F, -0.27F);
            this.body.setPos(0.0F, 17.5F, 0.5F);
            this.right_leg.setPos(-1.5F, 21.5F, 1.0F);
            this.left_leg.setPos(1.5F, 21.5F, 1.0F);
            this.right_leg.xRot = 0;
            this.left_leg.xRot = 0;
            this.body.xRot = 0.1309F;
        }
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        head.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        body.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        left_wing.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        right_wing.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        left_leg.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        right_leg.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    protected Iterable<ModelRenderer> headParts() {
        return ImmutableList.of(this.head);
    }

    protected Iterable<ModelRenderer> bodyParts() {
        return ImmutableList.of(this.body);
    }



    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}