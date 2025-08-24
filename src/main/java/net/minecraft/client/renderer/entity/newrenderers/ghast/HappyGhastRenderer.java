package net.minecraft.client.renderer.entity.newrenderers.ghast;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.newmodels.animal.layer.SimpleEquipmentLayer;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.entity.happy_ghast.HappyGhastEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.HarnessItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HappyGhastRenderer extends NewAgeableMobRenderer<HappyGhastEntity, HappyGhastModel<HappyGhastEntity>> {
    private static final ResourceLocation GHAST_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/ghast/happy_ghast.png");
    private static final ResourceLocation GHAST_BABY_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/ghast/happy_ghast_baby.png");
    private static final ResourceLocation GHAST_ROPES = ResourceLocation.withDefaultNamespace("textures/entity/ghast/happy_ghast_ropes.png");

    private static final Map<HarnessItem, ResourceLocation> HARNESS_TO_TEXTURE = Registry.ITEM.stream().filter(item -> item instanceof HarnessItem)
            .map(item -> (HarnessItem) item).collect(Collectors.toMap(harness -> harness, HarnessItem::getHarnessTextureFile));

    public HappyGhastRenderer(EntityRendererManager context) {
        super(context, new HappyGhastModel<HappyGhastEntity>(context.bakeLayer(ModelLayers.HAPPY_GHAST)), new HappyGhastModel<HappyGhastEntity>(context.bakeLayer(ModelLayers.HAPPY_GHAST_BABY)), 2.0f);

        this.addLayer(new SimpleEquipmentLayer<>(
                this,
                ghast -> ghast.getItemBySlot(EquipmentSlotType.CHEST).getItem().is(ItemTags.HARNESSES) ?
                        HARNESS_TO_TEXTURE.get(((HarnessItem)ghast.getItemBySlot(EquipmentSlotType.CHEST).getItem())) : MissingTextureSprite.getLocation(),
                ghast -> ghast.getItemBySlot(EquipmentSlotType.CHEST).getItem().is(ItemTags.HARNESSES) ? Optional.of(Unit.INSTANCE) : Optional.empty(),
                new HappyGhastHarnessModel(context.bakeLayer(ModelLayers.HAPPY_GHAST_HARNESS)),
                new HappyGhastHarnessModel(context.bakeLayer(ModelLayers.HAPPY_GHAST_BABY_HARNESS))
        ));

        this.addLayer(new RopesLayer<>(
                this,
                context.entityModels,
                GHAST_ROPES)
        );
    }

    @Override
    public ResourceLocation getTextureLocation(HappyGhastEntity ghast) {
        if (ghast.isBaby()) {
            return GHAST_BABY_LOCATION;
        }
        return GHAST_LOCATION;
    }

    @Override
    protected AxisAlignedBB getBoundingBoxForCulling(HappyGhastEntity happyGhast) {
        AxisAlignedBB aABB = super.getBoundingBoxForCulling(happyGhast);
        float f = happyGhast.getBbHeight();
        return aABB.setMinY(aABB.minY - (double)(f / 2.0f));
    }


}
