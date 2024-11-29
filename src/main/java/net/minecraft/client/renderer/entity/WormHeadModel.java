package net.minecraft.client.renderer.entity;// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.terraria.creature.WormEntity;

public class WormHeadModel extends EntityModel<WormEntity> {
	private final ModelRenderer head;

	public WormHeadModel() {
		texWidth = 64;
		texHeight = 64;

		head = new ModelRenderer(this);
		head.setPos(0.0F, 21.5F, 4.7857F);
		head.texOffs(0, 0).addBox(-2.5F, -2.5F, -9.7857F, 5.0F, 5.0F, 10.0F, 0.0F, false);
		head.texOffs(0, 25).addBox(1.75F, -2.0F, -9.2857F, 1.0F, 4.0F, 9.0F, 0.0F, false);
		head.texOffs(31, 24).addBox(-2.0F, -3.0F, -9.2857F, 4.0F, 1.0F, 9.0F, 0.0F, false);
		head.texOffs(31, 24).addBox(-2.0F, 2.0F, -9.2857F, 4.0F, 1.0F, 9.0F, 0.0F, false);
		head.texOffs(20, 25).addBox(-2.75F, -2.0F, -9.2857F, 1.0F, 4.0F, 9.0F, 0.0F, false);
		head.texOffs(26, 20).addBox(1.0F, -1.0F, -12.2857F, 1.0F, 2.0F, 3.0F, 0.0F, false);
		head.texOffs(26, 20).addBox(-2.0F, -1.0F, -12.2857F, 1.0F, 2.0F, 3.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(WormEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		head.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}