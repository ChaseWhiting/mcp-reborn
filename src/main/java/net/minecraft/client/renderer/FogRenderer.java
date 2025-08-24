package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.Difficulty;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class FogRenderer {
   // RGB values for fog color
   private static float fogRed;
   private static float fogGreen;
   private static float fogBlue;

   // Variables for biome-specific fog
   private static int targetBiomeFogColor = -1;
   private static int previousBiomeFogColor = -1;
   private static long biomeFogChangeTime = -1L;

   public static void setupColor(ActiveRenderInfo renderInfo, float partialTicks, ClientWorld world, int renderDistance, float fogDensity) {
      FluidState fluidInView = renderInfo.getFluidInCamera();

      // Handle water fog
      if (fluidInView.is(FluidTags.WATER)) {
         long currentTimeMillis = Util.getMillis();
         int waterFogColor = world.getBiome(new BlockPos(renderInfo.getPosition())).getWaterFogColor();

         if (biomeFogChangeTime < 0L) {
            targetBiomeFogColor = waterFogColor;
            previousBiomeFogColor = waterFogColor;
            biomeFogChangeTime = currentTimeMillis;
         }

         // Extract RGB components of the fog color
         int targetRed = (targetBiomeFogColor >> 16) & 255;
         int targetGreen = (targetBiomeFogColor >> 8) & 255;
         int targetBlue = targetBiomeFogColor & 255;

         int previousRed = (previousBiomeFogColor >> 16) & 255;
         int previousGreen = (previousBiomeFogColor >> 8) & 255;
         int previousBlue = previousBiomeFogColor & 255;

         // Interpolate between previous and target fog colors
         float transitionProgress = MathHelper.clamp((float)(currentTimeMillis - biomeFogChangeTime) / 5000.0F, 0.0F, 1.0F);
         float interpolatedRed = MathHelper.lerp(transitionProgress, (float)previousRed, (float)targetRed);
         float interpolatedGreen = MathHelper.lerp(transitionProgress, (float)previousGreen, (float)targetGreen);
         float interpolatedBlue = MathHelper.lerp(transitionProgress, (float)previousBlue, (float)targetBlue);

         fogRed = interpolatedRed / 255.0F;
         fogGreen = interpolatedGreen / 255.0F;
         fogBlue = interpolatedBlue / 255.0F;

         // Update the target fog color if it has changed
         if (targetBiomeFogColor != waterFogColor) {
            targetBiomeFogColor = waterFogColor;
            previousBiomeFogColor = MathHelper.floor(interpolatedRed) << 16 | MathHelper.floor(interpolatedGreen) << 8 | MathHelper.floor(interpolatedBlue);
            biomeFogChangeTime = currentTimeMillis;
         }
      }
      // Handle lava fog
      else if (fluidInView.is(FluidTags.LAVA)) {
         fogRed = 0.6F;
         fogGreen = 0.1F;
         fogBlue = 0.0F;
         biomeFogChangeTime = -1L;
      }
      // Handle normal fog (sky and terrain fog)
      else {
         float fogFactor = 0.25F + 0.75F * (float)renderDistance / 32.0F;
         fogFactor = 1.0F - (float)Math.pow((double)fogFactor, 0.25D);
         Vector3d skyColor = world.getSkyColor(renderInfo.getBlockPosition(), partialTicks);

         // Get sky color values
         float skyRed = (float)skyColor.x;
         float skyGreen = (float)skyColor.y;
         float skyBlue = (float)skyColor.z;

         // Interpolate between fog colors based on time of day
         float sunHeightFactor = MathHelper.clamp(MathHelper.cos(world.getTimeOfDay(partialTicks) * ((float)Math.PI * 2F)) * 2.0F + 0.5F, 0.0F, 1.0F);
         BiomeManager biomeManager = world.getBiomeManager();
         Vector3d fogPosition = renderInfo.getPosition().subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
         Vector3d interpolatedFogColor = CubicSampler.gaussianSampleVec3(fogPosition, (sampleX, sampleY, sampleZ) -> {
            return world.effects().getBrightnessDependentFogColor(Vector3d.fromRGB24(biomeManager.getNoiseBiomeAtQuart(sampleX, sampleY, sampleZ).getFogColor()), sunHeightFactor);
         });

         fogRed = (float)interpolatedFogColor.x();
         fogGreen = (float)interpolatedFogColor.y();
         fogBlue = (float)interpolatedFogColor.z();

         // Adjust fog color for sun angles
         if (renderDistance >= 4) {
            boolean flag = false;
            if (renderInfo.getEntity() != null && renderInfo.getEntity() instanceof ClientPlayerEntity) {
               ClientPlayerEntity player = (ClientPlayerEntity) renderInfo.getEntity();
               Optional<RegistryKey<Biome>> key = player.level.getBiomeName(player.blockPosition());

               if (key.isPresent() && key.get() == Biomes.PALE_GARDEN) {
                  flag = true;
               }
            }
            if (!flag) {
               float sunAngleSign = MathHelper.sin(world.getSunAngle(partialTicks)) > 0.0F ? -1.0F : 1.0F;
               Vector3f sunDirection = new Vector3f(sunAngleSign, 0.0F, 0.0F);
               float sunLookFactor = renderInfo.getLookVector().dot(sunDirection);

               if (sunLookFactor > 0.0F) {
                  float[] sunriseColors = world.effects().getSunriseColor(world.getTimeOfDay(partialTicks), partialTicks);
                  if (sunriseColors != null) {
                     sunLookFactor = sunLookFactor * sunriseColors[3];
                     fogRed = fogRed * (1.0F - sunLookFactor) + sunriseColors[0] * sunLookFactor;
                     fogGreen = fogGreen * (1.0F - sunLookFactor) + sunriseColors[1] * sunLookFactor;
                     fogBlue = fogBlue * (1.0F - sunLookFactor) + sunriseColors[2] * sunLookFactor;
                  }
               }
            }
         }

         // Modify fog colors based on rain and thunder levels
         fogRed += (skyRed - fogRed) * fogFactor;
         fogGreen += (skyGreen - fogGreen) * fogFactor;
         fogBlue += (skyBlue - fogBlue) * fogFactor;

         float rainLevel = world.getRainLevel(partialTicks);
         if (rainLevel > 0.0F) {
            fogRed *= (1.0F - rainLevel * 0.5F);
            fogGreen *= (1.0F - rainLevel * 0.5F);
            fogBlue *= (1.0F - rainLevel * 0.4F);
         }

         float thunderLevel = world.getThunderLevel(partialTicks);
         if (thunderLevel > 0.0F) {
            fogRed *= (1.0F - thunderLevel * 0.5F);
            fogGreen *= (1.0F - thunderLevel * 0.5F);
            fogBlue *= (1.0F - thunderLevel * 0.5F);
         }

         biomeFogChangeTime = -1L;
      }

      // Apply fog based on height
      double fogDistanceFactor = renderInfo.getPosition().y * world.getLevelData().getClearColorScale();

      if (renderInfo.getEntity() instanceof LivingEntity) {
         LivingEntity livingEntity = (LivingEntity) renderInfo.getEntity();

         // Handle Blindness effect
         if (livingEntity.hasEffect(Effects.BLINDNESS)) {
            int blindnessDuration = livingEntity.getEffect(Effects.BLINDNESS).getDuration();
            if (blindnessDuration < 20) {
               fogDistanceFactor *= (1.0F - (float) blindnessDuration / 20.0F);
            } else {
               fogDistanceFactor = 0.0D;
            }
         }

         // 🔹 Handle Darkness effect 🔹
         if (livingEntity.hasEffect(Effects.DARKNESS)) {
            EffectInstance darknessEffect = livingEntity.getEffect(Effects.DARKNESS);
            float darknessFactor = darknessEffect.getDuration() < 20
                    ? 1.0F - (float) darknessEffect.getDuration() / 20.0F
                    : 0.0F;

            // Reduce fog color intensity based on Darkness effect
            fogRed *= darknessFactor * 0.2F;
            fogGreen *= darknessFactor * 0.2F;
            fogBlue *= darknessFactor * 0.2F;
         }
      }

      if (fogDistanceFactor < 1.0D && !fluidInView.is(FluidTags.LAVA)) {
         if (fogDistanceFactor < 0.0D) {
            fogDistanceFactor = 0.0D;
         }
         fogDistanceFactor = fogDistanceFactor * fogDistanceFactor;
         fogRed = (float)((double)fogRed * fogDistanceFactor);
         fogGreen = (float)((double)fogGreen * fogDistanceFactor);
         fogBlue = (float)((double)fogBlue * fogDistanceFactor);
      }


      // Adjust fog based on fog density
      if (fogDensity > 0.0F) {
         fogRed = fogRed * (1.0F - fogDensity) + fogRed * 0.7F * fogDensity;
         fogGreen = fogGreen * (1.0F - fogDensity) + fogGreen * 0.6F * fogDensity;
         fogBlue = fogBlue * (1.0F - fogDensity) + fogBlue * 0.6F * fogDensity;
      }

      // Special water fog handling for player vision underwater
      if (fluidInView.is(FluidTags.WATER)) {
         float underwaterVisionFactor = 0.0F;
         if (renderInfo.getEntity() instanceof ClientPlayerEntity) {
            underwaterVisionFactor = ((ClientPlayerEntity)renderInfo.getEntity()).getWaterVision();
         }

         float fogClarity = Math.min(1.0F / fogRed, Math.min(1.0F / fogGreen, 1.0F / fogBlue));
         fogRed = fogRed * (1.0F - underwaterVisionFactor) + fogRed * fogClarity * underwaterVisionFactor;
         fogGreen = fogGreen * (1.0F - underwaterVisionFactor) + fogGreen * fogClarity * underwaterVisionFactor;
         fogBlue = fogBlue * (1.0F - underwaterVisionFactor) + fogBlue * fogClarity * underwaterVisionFactor;
      }


      // Handle night vision
      else if (renderInfo.getEntity() instanceof LivingEntity && ((LivingEntity)renderInfo.getEntity()).hasEffect(Effects.NIGHT_VISION)) {
         float nightVisionFactor = GameRenderer.getNightVisionScale((LivingEntity)renderInfo.getEntity(), partialTicks);
         float fogClarity = Math.min(1.0F / fogRed, Math.min(1.0F / fogGreen, 1.0F / fogBlue));
         fogRed = fogRed * (1.0F - nightVisionFactor) + fogRed * fogClarity * nightVisionFactor;
         fogGreen = fogGreen * (1.0F - nightVisionFactor) + fogGreen * fogClarity * nightVisionFactor;
         fogBlue = fogBlue * (1.0F - nightVisionFactor) + fogBlue * fogClarity * nightVisionFactor;
      }

      RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
   }

   public static void setupNoFog() {
      RenderSystem.fogDensity(0.0F);
      RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
   }

   public static void setupFog(ActiveRenderInfo renderInfo, FogRenderer.FogType fogType, float baseFogDensity, boolean isFoggy) {
      FluidState fluidInView = renderInfo.getFluidInCamera();
      Entity entity = renderInfo.getEntity();

      if (entity instanceof ClientPlayerEntity) {
         ClientPlayerEntity player = (ClientPlayerEntity) entity;
         Optional<RegistryKey<Biome>> key = player.level.getBiomeName(player.blockPosition());
         if (key.isPresent() && key.get() == Biomes.PALE_GARDEN || key.isPresent() && key.get() == Biomes.PALE_GARDEN_CRATER) {
            float fogStartDistance = 7.0F;
            float fogEndDistance = 30.0F;
            float fogDensity = 0.03F;

            if (player.level.isThundering()) {
               fogStartDistance = 4.0F;
               fogEndDistance = 15.0F;
               fogDensity = 0.09F;
            }

            if (player.level.isNight()) {
               fogStartDistance -= 0.5f;
               fogEndDistance -= 5;
               fogDensity += 0.05f;
            }

            if (player.level.getDifficulty() == Difficulty.HARD || player.veryHardmode()) {
               fogStartDistance -= 2.5F;
               fogEndDistance -= 6;
               fogDensity += 0.12F;
            }

            if (player.hasEffect(Effects.BLINDNESS)) {
               fogStartDistance -= 0.5F;
               fogEndDistance -= 2;
               fogDensity += 0.12F;
            }

            if (player.getItemBySlot(EquipmentSlotType.HEAD).getItem() == Items.WHITE_CARVED_PUMPKIN && Minecraft.getInstance().options.getCameraType().isFirstPerson() && !Minecraft.getInstance().options.hideGui) {
               return;
            }
            RenderSystem.fogDensity(fogDensity);
            RenderSystem.fogMode(GlStateManager.FogMode.EXP);
            RenderSystem.fogStart(fogStartDistance);
            RenderSystem.fogEnd(fogEndDistance);

            RenderSystem.setupNvFogDistance();
            return;
         }
      }


      // Water fog handling
      if (fluidInView.is(FluidTags.WATER)) {
         float fogDensity = 1.0F;
         fogDensity = 0.05F;
         if (entity instanceof ClientPlayerEntity) {
            ClientPlayerEntity player = (ClientPlayerEntity)entity;
            fogDensity -= player.getWaterVision() * player.getWaterVision() * 0.03F;
            Biome biome = player.level.getBiome(player.blockPosition());
            if (biome.getBiomeCategory() == Biome.Category.SWAMP) {
               fogDensity += 0.005F;
            }
         }

         RenderSystem.fogDensity(fogDensity);
         RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
      }
      // Lava fog handling
      else {
         float fogStartDistance;
         float fogEndDistance;

         if (fluidInView.is(FluidTags.LAVA)) {
            if (entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(Effects.FIRE_RESISTANCE)) {
               fogStartDistance = 0.0F;
               fogEndDistance = 3.0F;
            } else {
               fogStartDistance = 0.25F;
               fogEndDistance = 1.0F;
            }
         }

         else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(Effects.DARKNESS)) {
            EffectInstance darknessEffect = ((LivingEntity) entity).getEffect(Effects.DARKNESS);
            float darknessFactor = darknessEffect.getDuration() < 20 ? 1.0f - (float) darknessEffect.getDuration() / 20.0f : 0.0f;

            float darknessFogDensity = MathHelper.lerp(darknessFactor, baseFogDensity, 10.0F);

            if (fogType == FogRenderer.FogType.FOG_SKY) {
               fogStartDistance = 0.0F;
               fogEndDistance = darknessFogDensity * 0.8F;
            } else {
               fogStartDistance = darknessFogDensity * 0.3F;
               fogEndDistance = darknessFogDensity;
            }
         }

         // Blindness effect handling
         else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(Effects.BLINDNESS)) {
            int blindnessDurationInTicks = ((LivingEntity)entity).getEffect(Effects.BLINDNESS).getDuration();
            float interpolatedFogDensity = MathHelper.lerp(Math.min(1.0F, (float)blindnessDurationInTicks / 20.0F), baseFogDensity, 5.0F);

            if (fogType == FogRenderer.FogType.FOG_SKY) {
               fogStartDistance = 0.0F;
               fogEndDistance = interpolatedFogDensity * 0.8F;
            } else {
               fogStartDistance = interpolatedFogDensity * 0.25F;
               fogEndDistance = interpolatedFogDensity;
            }
         }
         // Handle custom fog settings
         else if (isFoggy) {
            fogStartDistance = baseFogDensity * 0.05F;
            fogEndDistance = Math.min(baseFogDensity, 192.0F) * 0.5F;
         }
         // Default fog for sky or terrain
         else if (fogType == FogRenderer.FogType.FOG_SKY) {
            fogStartDistance = 0.0F;
            fogEndDistance = baseFogDensity;
         } else {
            fogStartDistance = baseFogDensity * 0.75F;
            fogEndDistance = baseFogDensity;
         }

         // Set the fog start, end, and mode
         RenderSystem.fogStart(fogStartDistance);
         RenderSystem.fogEnd(fogEndDistance);
         RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
         RenderSystem.setupNvFogDistance();
      }
   }

   public static void levelFogColor() {
      RenderSystem.fog(2918, fogRed, fogGreen, fogBlue, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public static enum FogType {
      FOG_SKY,
      FOG_TERRAIN;
   }

   static class FogData {
      public final FogType mode;
      public float start;
      public float end;

      public FogData(FogType fogMode) {
         this.mode = fogMode;
      }
   }

   @Nullable
   private static MobEffectFogFunction getPriorityFogFunction(Entity entity, float f) {
      if (entity instanceof LivingEntity) {
         LivingEntity livingEntity = (LivingEntity)entity;
         return new DarknessFogFunction();
      }
      return null;
   }

   static interface MobEffectFogFunction {
      public Effect getMobEffect();

      public void setupFog(FogData var1, LivingEntity var2, EffectInstance var3, float var4, float var5);

      default public boolean isEnabled(LivingEntity livingEntity, float f) {
         return livingEntity.hasEffect(this.getMobEffect());
      }

      default public float getModifiedVoidDarkness(LivingEntity livingEntity, EffectInstance mobEffectInstance, float f, float f2) {
         EffectInstance mobEffectInstance2 = livingEntity.getEffect(this.getMobEffect());
         if (mobEffectInstance2 != null) {
            f = mobEffectInstance2.getDuration() < 20 ? 1.0f - (float)mobEffectInstance2.getDuration() / 20.0f : 0.0f;
         }
         return f;
      }
   }

   static class DarknessFogFunction
           implements MobEffectFogFunction {
      DarknessFogFunction() {
      }

      @Override
      public Effect getMobEffect() {
         return Effects.DARKNESS;
      }

      @Override
      public void setupFog(FogData fogData, LivingEntity livingEntity, EffectInstance mobEffectInstance, float f, float f2) {
         if (mobEffectInstance.getFactorData().isEmpty()) {
            return;
         }
         float f3 = MathHelper.lerp(mobEffectInstance.getFactorData().get().getFactor(livingEntity, f2), f, 15.0f);
         fogData.start = fogData.mode == FogType.FOG_SKY ? 0.0f : f3 * 0.75f;
         fogData.end = f3;
      }

      @Override
      public float getModifiedVoidDarkness(LivingEntity livingEntity, EffectInstance mobEffectInstance, float f, float f2) {
         if (mobEffectInstance.getFactorData().isEmpty()) {
            return 0.0f;
         }
         return 1.0f - mobEffectInstance.getFactorData().get().getFactor(livingEntity, f2);
      }
   }



}
