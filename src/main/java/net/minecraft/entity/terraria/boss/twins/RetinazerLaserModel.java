package net.minecraft.entity.terraria.boss.twins;// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.Monster;

public class RetinazerLaserModel extends RetinazerModel {
	private final ModelRenderer laser;

	public RetinazerLaserModel() {
		super();
		texWidth = 256;
		texHeight = 256;

		laser = new ModelRenderer(this);
		laser.setPos(0.25F, -8.4433F, -11.6373F);
		head.addChild(laser);
		laser.texOffs(0, 245).addBox(-2.5F, -2.5567F, -5.3627F, 5.0F, 5.0F, 6.0F, 0.0F, false);
		laser.texOffs(2, 228).addBox(-3.0F, -3.0567F, -11.3627F, 6.0F, 6.0F, 2.0F, 0.0F, false);
		laser.texOffs(6, 228).addBox(1.435F, -2.8867F, -11.3997F, 2.0F, 6.0F, 2.0F, 0.0F, false);
		laser.texOffs(6, 228).addBox(-3.435F, -2.8867F, -11.3997F, 2.0F, 6.0F, 2.0F, 0.0F, true);
		laser.texOffs(0, 215).addBox(-3.5F, -3.5567F, -11.6127F, 7.0F, 7.0F, 3.0F, 0.0F, false);
		laser.texOffs(0, 236).addBox(-1.5F, -1.5567F, -10.3627F, 3.0F, 3.0F, 6.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(RetinazerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		root.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}