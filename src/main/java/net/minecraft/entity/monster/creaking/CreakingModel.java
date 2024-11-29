package net.minecraft.entity.monster.creaking;// Made with Blockbench 4.11.0
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.animation.HierarchicalModel;
import net.minecraft.client.animation.definitions.CreakingAnimation;
import net.minecraft.client.renderer.model.ModelRenderer;

public class CreakingModel<T extends CreakingEntity> extends HierarchicalModel<CreakingEntity> {
    private final ModelRenderer root;
    private final ModelRenderer upper_body;
    private final ModelRenderer head;
    private final ModelRenderer body;
    private final ModelRenderer right_arm;
    private final ModelRenderer left_arm;
    private final ModelRenderer right_leg;
    private final ModelRenderer left_leg;

    public CreakingModel() {
        texWidth = 64;
        texHeight = 64;

        // Root part
        root = new ModelRenderer(this, "root");
        root.setPos(0.0F, 24.0F, 0.0F);

        upper_body = new ModelRenderer(this, "upper_body");
        upper_body.setPos(-1.0F, -19.0F, 0.0F);
        root.addChild(upper_body);

        // Head
        head = new ModelRenderer(this, "head");
        head.setPos(-3.0F, -11.0F, 0.0F);
        upper_body.addChild(head);
        head.texOffs(0, 0).addBox(-3.0F, -10.0F, -3.0F, 6.0F, 10.0F, 6.0F);
        head.texOffs(28, 31).addBox(-3.0F, -13.0F, -3.0F, 6.0F, 3.0F, 6.0F);
        head.texOffs(12, 40).addBox(3.0F, -13.0F, 0.0F, 9.0F, 14.0F, 0.0F);
        head.texOffs(34, 12).addBox(-12.0F, -14.0F, 0.0F, 9.0F, 14.0F, 0.0F);

        // Body
        body = new ModelRenderer(this, "body");
        body.setPos(0.0F, -7.0F, 1.0F);
        upper_body.addChild(body);
        body.texOffs(0, 16).addBox(0.0F, -3.0F, -3.0F, 6.0F, 13.0F, 5.0F);
        body.texOffs(24, 0).addBox(-6.0F, -4.0F, -3.0F, 6.0F, 7.0F, 5.0F);

        // Right arm
        right_arm = new ModelRenderer(this, "right_arm");
        right_arm.setPos(-7.0F, -9.5F, 1.5F);
        upper_body.addChild(right_arm);
        right_arm.texOffs(22, 13).addBox(-2.0F, -1.5F, -1.5F, 3.0F, 21.0F, 3.0F);
        right_arm.texOffs(46, 0).addBox(-2.0F, 19.5F, -1.5F, 3.0F, 4.0F, 3.0F);

        // Left arm
        left_arm = new ModelRenderer(this, "left_arm");
        left_arm.setPos(6.0F, -9.0F, 0.5F);
        upper_body.addChild(left_arm);
        left_arm.texOffs(30, 40).addBox(0.0F, -1.0F, -1.5F, 3.0F, 16.0F, 3.0F);
        left_arm.texOffs(52, 12).addBox(0.0F, -5.0F, -1.5F, 3.0F, 4.0F, 3.0F);
        left_arm.texOffs(52, 19).addBox(0.0F, 15.0F, -1.5F, 3.0F, 4.0F, 3.0F);

        left_leg = new ModelRenderer(this, "left_leg");
        left_leg.setPos(1.5F, -16.0F, 0.5F);
        root.addChild(left_leg);
        left_leg.texOffs(42, 40).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 16.0F, 3.0F);
        left_leg.texOffs(45, 55).addBox(-1.5F, 15.7F, -4.5F, 5.0F, 0.0F, 9.0F);

        right_leg = new ModelRenderer(this, "right_leg");
        right_leg.setPos(-1.0F, -17.5F, 0.5F);
        root.addChild(right_leg);
        right_leg.texOffs(0, 34).addBox(-3.0F, -1.5F, -1.5F, 3.0F, 19.0F, 3.0F);
        right_leg.texOffs(45, 46).addBox(-5.0F, 17.2F, -4.5F, 5.0F, 0.0F, 9.0F);
        right_leg.texOffs(12, 34).addBox(-3.0F, -4.5F, -1.5F, 3.0F, 3.0F, 3.0F);
    }



    @Override
    public void setupAnim(CreakingEntity creaking, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(creaking, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.head.xRot = creaking.xRot * ((float)Math.PI / 180);
        this.head.yRot = creaking.yRot * ((float)Math.PI / 180);


        float speedFactor = creaking.hasTarget() ? 0.55f : 0.85f;

        float adjustedBaseSpeed = 5.5f * speedFactor;

        if (creaking.canMove() && limbSwingAmount > 0.1f) {
            this.animateWalk(CreakingAnimation.CREAKING_WALK, limbSwing, limbSwingAmount, adjustedBaseSpeed, 3.0f);
        } else {
            this.left_leg.xRot = 0.0F;
            this.right_leg.xRot = 0.0F;
        }

        this.animate(creaking.attackAnimationState, CreakingAnimation.CREAKING_ATTACK, ageInTicks);
        this.animate(creaking.invulnerabilityAnimationState, CreakingAnimation.CREAKING_INVULNERABLE, ageInTicks);
        this.animate(creaking.deathAnimationState, CreakingAnimation.CREAKING_DEATH, ageInTicks);

        this.headTurn(this.head, netHeadYaw, headPitch);
    }




    @Override
    public ImmutableSet<ModelRenderer> getAllParts() {
        return ImmutableSet.of(head, body, upper_body, left_arm, left_leg, right_arm, right_leg);
    }

    @Override
    public ModelRenderer root() {
        return this.root;
    }
}
