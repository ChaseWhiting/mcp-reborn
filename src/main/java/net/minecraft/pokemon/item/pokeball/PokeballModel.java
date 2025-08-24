package net.minecraft.pokemon.item.pokeball;


import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.projectile.PokeballEntity;

public class PokeballModel extends HierarchicalModel<PokeballEntity> {
	private final ModelRenderer root;
	private final ModelRenderer top_ball;
	private final ModelRenderer bottom_ball;

	public PokeballModel() {
		texWidth = 32;
		texHeight = 32;

		root = new ModelRenderer(this, "root");
		root.setPos(0.0F, 21.0F, 0.0F);
		

		top_ball = new ModelRenderer(this, "top_ball");
		top_ball.setPos(0.0F, 0.0F, 3.0F);
		root.addChild(top_ball);
		top_ball.texOffs(0, 9).addBox(-3.0F, -3.0F, -6.0F, 6.0F, 3.0F, 6.0F, 0.0F, false);
		top_ball.texOffs(0, 18).addBox(-1.5F, -1.5F, -6.5F, 3.0F, 3.0F, 2.0F, -0.34F, false);
		top_ball.texOffs(27, 28).addBox(-1.5F, -0.1F, -6.15F, 0.5F, 1.5F, 1.0F, 0.0F, false);
		top_ball.texOffs(27, 26).addBox(-1.5F, -1.58F, -6.15F, 0.5F, 1.5F, 1.0F, -0.0002F, false);
		top_ball.texOffs(26, 27).addBox(1.0F, -1.58F, -6.15F, 0.5F, 1.5F, 1.0F, -0.0002F, false);
		top_ball.texOffs(27, 28).addBox(1.0F, -0.1F, -6.15F, 0.5F, 1.5F, 1.0F, 0.0F, false);
		top_ball.texOffs(24, 29).addBox(-1.0F, 0.9F, -6.15F, 2.0F, 0.5F, 1.0F, 0.0F, false);
		top_ball.texOffs(24, 28).addBox(-1.0F, -1.58F, -6.15F, 2.0F, 0.5F, 1.0F, 0.0F, false);
		top_ball.texOffs(25, 29).addBox(-3.0F, -0.35F, -6.15F, 2.0F, 0.5F, 0.5F, -0.0001F, false);
		top_ball.texOffs(25, 29).addBox(1.0F, -0.35F, -6.15F, 2.0F, 0.5F, 0.5F, -0.0001F, false);
		top_ball.texOffs(25, 29).addBox(1.0F, -0.35F, -0.4F, 2.0F, 0.5F, 0.5F, -0.0001F, false);
		top_ball.texOffs(25, 29).addBox(-1.0F, -0.35F, -0.4F, 2.0F, 0.5F, 0.5F, -0.0001F, false);
		top_ball.texOffs(25, 29).addBox(-3.0F, -0.35F, -0.4F, 2.0F, 0.5F, 0.5F, -0.0001F, false);
		top_ball.texOffs(25, 28).addBox(2.65F, -0.35F, -6.15F, 0.5F, 0.5F, 2.0F, -0.0002F, false);
		top_ball.texOffs(25, 28).addBox(-3.1F, -0.35F, -6.15F, 0.5F, 0.5F, 2.0F, -0.0002F, false);
		top_ball.texOffs(25, 28).addBox(2.65F, -0.35F, -4.15F, 0.5F, 0.5F, 2.0F, -0.0002F, false);
		top_ball.texOffs(25, 28).addBox(-3.1F, -0.35F, -4.15F, 0.5F, 0.5F, 2.0F, -0.0002F, false);
		top_ball.texOffs(24, 27).addBox(2.65F, -0.35F, -2.4F, 0.5F, 0.5F, 2.5F, -0.0003F, false);
		top_ball.texOffs(24, 27).addBox(-3.1F, -0.35F, -2.4F, 0.5F, 0.5F, 2.5F, -0.0003F, false);

		bottom_ball = new ModelRenderer(this, "bottom_bal");
		bottom_ball.setPos(0.0F, 0.0F, 3.0F);
		root.addChild(bottom_ball);
		bottom_ball.texOffs(0, 0).addBox(-3.0F, 0.0F, -6.0F, 6.0F, 3.0F, 6.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(PokeballEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		this.animate(entity.shakeOnce, PokeballModelAnimation.SHAKE_ONCE, ageInTicks);
		this.animate(entity.shakeTwice, PokeballModelAnimation.SHAKE_TWICE, ageInTicks);
		this.animate(entity.shakeThreeTimes, PokeballModelAnimation.SHAKE_THREE_TIMES, ageInTicks);
		this.animate(entity.shakeThreeTimesAndCatch, PokeballModelAnimation.SHAKE_THREE_TIMES_CATCH, ageInTicks);

	}

	@Override
	public ImmutableSet<ModelRenderer> getAllParts() {
		return ImmutableSet.of(this.top_ball, this.bottom_ball);
	}

	@Override
	public ModelRenderer root() {
		return this.root;
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		root.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}