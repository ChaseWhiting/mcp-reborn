package net.minecraft.client.alexsmobsport.citadel.mob;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.monster.crimson_mosquito.CrimsonMosquitoEntity;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class LayerCrimsonMosquitoBlood extends LayerRenderer<CrimsonMosquitoEntity, ModelCrimsonMosquito> {
    public LayerCrimsonMosquitoBlood(CrimsonMosquitoRenderer renderCrimsonMosquito) {
        super(renderCrimsonMosquito);
    }

    private static final Map<Integer, ResourceLocation> FRAMES_01_TO_20 = create();

    private static HashMap<Integer, ResourceLocation> create() {
        HashMap<Integer, ResourceLocation> map = new HashMap<>();
        for (int i = 1; i <= 20; i++) {
            String formatted = String.format("%02d", i); // turns 1 -> "01", 9 -> "09", etc.
            map.put(i, cr(formatted));
        }
        return map;
    }


    private static ResourceLocation cr(int i) {
        return new ResourceLocation("textures/entity/mosquito/frame_" + i + ".png");
    }

    private static ResourceLocation cr(String i) {
        return new ResourceLocation("textures/entity/mosquito/frame_" + i + ".png");
    }

    private static final Map<Integer, ResourceLocation> TEXTURE_MAP =
            ImmutableMap.of(
                    CrimsonMosquitoEntity.BloodType.RED.id(), create(""),
                    CrimsonMosquitoEntity.BloodType.BLUE.id(), create("blue_"),
                    CrimsonMosquitoEntity.BloodType.ENDER_BLOOD.id(), create("purple_"),
                    CrimsonMosquitoEntity.BloodType.RESIN.id(), create("resin_"));

    private static ResourceLocation create(String s) {
        return new ResourceLocation("textures/entity/crimson_mosquito_" + s + "blood.png");
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, CrimsonMosquitoEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(entitylivingbaseIn.getBloodLevel() > 0){

// Use entity's ageInTicks and partialTicks to determine the current frame (0–19)
            int totalFrames = 20;
            float ticksWithPartial = entitylivingbaseIn.tickCount + partialTicks;

// Optional: add slight offset per entity to desync animations a bit
            int offset = Math.abs(entitylivingbaseIn.getId()) % totalFrames;

// Control animation speed here (e.g. one frame every 2 ticks = 0.5 animation speed)
            int frame = ((int)(ticksWithPartial / 2) + offset) % totalFrames;

// Frame map is 1–20, so shift by +1
            ResourceLocation chosenTexture = entitylivingbaseIn.getBloodType() == 4 ? FRAMES_01_TO_20.get(frame + 1) : TEXTURE_MAP.getOrDefault(entitylivingbaseIn.getBloodType(), create(""));



            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.eyes(chosenTexture));
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}