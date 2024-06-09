package net.minecraft.entity.projectile.custom.arrow;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomArrowRenderer extends ArrowRenderer<CustomArrowEntity> {
    public static final ResourceLocation FROZEN_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/frozen_arrow.png");
    public static final ResourceLocation BURNING_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/burning_arrow.png");
    public static final ResourceLocation POISON_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/poison_arrow.png");
    public static final ResourceLocation TELEPORTATION_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/teleportation_arrow.png");
    public static final ResourceLocation HEALING_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/healing_arrow.png");

    public static final ResourceLocation ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/arrow.png");

    public CustomArrowRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public ResourceLocation getTextureLocation(CustomArrowEntity arrow) {
        return getArrowTexture(arrow.getArrowType());
    }

    private ResourceLocation getArrowTexture(CustomArrowType type) {
        int id = type.getType();
        switch (id) {
            case 1:
                return BURNING_ARROW_LOCATION;
            case 2:
                return POISON_ARROW_LOCATION;
            case 3:
                return TELEPORTATION_ARROW_LOCATION;
            case 4:
                return HEALING_ARROW_LOCATION;
        }
        return ARROW_LOCATION;
    }
}
