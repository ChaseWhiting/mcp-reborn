package net.minecraft.client.renderer.model;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IBakedModel {
   List<BakedQuad> getQuads(@Nullable BlockState p_200117_1_, @Nullable Direction p_200117_2_, Random p_200117_3_);

   boolean useAmbientOcclusion();

   boolean isGui3d();

   boolean usesBlockLight();

   boolean isCustomRenderer();

   TextureAtlasSprite getParticleIcon();

   ItemCameraTransforms getTransforms();

   ItemOverrideList getOverrides();
}