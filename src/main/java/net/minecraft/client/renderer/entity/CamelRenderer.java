package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.CamelModel;
import net.minecraft.client.renderer.entity.model.newmodels.EquipmentClientInfo;
import net.minecraft.client.renderer.entity.model.newmodels.animal.layer.SimpleEquipmentLayer;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.entity.camel.CamelEntity;
import net.minecraft.entity.warden.LivingEntityEmissiveLayer;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class CamelRenderer extends MobRenderer<CamelEntity, CamelModel<CamelEntity>> {
   private static final ResourceLocation CAMEL = new ResourceLocation("textures/entity/camel.png");

   private static final Map<DyeColor, ResourceLocation> DYE_TO_RESOURCE = Util.make(new HashMap<>(), map -> {

      for (DyeColor dyeColor : DyeColor.values()) {
         map.put(dyeColor, new ResourceLocation("textures/entity/camel/camel_carpet_" + dyeColor.getName() + ".png"));
      }
   });

   public CamelRenderer(EntityRendererManager renderManager, ModelLayerLocation modelLayerLocation) {
      super(renderManager, new CamelModel<>(renderManager.bakeLayer(modelLayerLocation)), 0.7F);

      this.addLayer(new SimpleEquipmentLayer<>(this,

              camel -> camel.getCarpetColor().isPresent() ? DYE_TO_RESOURCE.get(camel.getCarpetColor().get()) :
                      new ResourceLocation("textures/entity/camel/camel_carpet_red.png"),
              camel -> camel.getCarpetColor().isPresent() ? Optional.of(Unit.INSTANCE) : Optional.empty(),
              new CamelModel<>(renderManager.bakeLayer(ModelLayers.CAMEL_CARPET)),
              new CamelModel<>(renderManager.bakeLayer(ModelLayers.CAMEL_BABY_CARPET))));

      this.addLayer(new SimpleEquipmentLayer<>(this,
              new ResourceLocation("textures/entity/camel/camel_lead.png"),
              camel -> camel.getLeashHolder() != null ? Optional.of(Unit.INSTANCE) : Optional.empty(),
              new CamelModel<>(renderManager.bakeLayer(ModelLayers.CAMEL_LEASH)),
              new CamelModel<>(renderManager.bakeLayer(ModelLayers.CAMEL_BABY_LEASH))));

//      this.addLayer(new LivingEntityEmissiveLayer<>(this, new ResourceLocation("textures/entity/camel/glowing_eyes.png"),
//              (camel, var1, var2) -> 1.0F, (model, entity) -> model.root().getAllPartsAsList(),
//              RenderType::eyes, true));
   }

   @Override
   public ResourceLocation getTextureLocation(CamelEntity entity) {
      return CAMEL;
   }
}
