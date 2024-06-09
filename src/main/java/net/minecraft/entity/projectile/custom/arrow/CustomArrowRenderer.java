package net.minecraft.entity.projectile.custom.arrow;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomArrowRenderer extends ArrowRenderer<CustomArrowEntity> {
    public static final ResourceLocation FROZEN_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/frozen_arrow.png");
    public static final ResourceLocation BURNING_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/burning_arrow.png");
    public static final ResourceLocation POISON_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/poison_arrow.png");
    public static final ResourceLocation TELEPORTATION_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/teleportation_arrow.png");
    public static final ResourceLocation ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/arrow.png"); // Default texture

    public CustomArrowRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public ResourceLocation getTextureLocation(CustomArrowEntity arrow) {
        return getArrowTexture(arrow.getArrowType());
    }

    private ResourceLocation getArrowTexture(CustomArrowType type) {
        if (type.equals(CustomArrowType.FROZEN)) {
            return FROZEN_ARROW_LOCATION;
        } else if (type.equals(CustomArrowType.BURNING)) {
            return BURNING_ARROW_LOCATION;
        } else if (type.equals(CustomArrowType.POISON)) {
            return POISON_ARROW_LOCATION;
        } else if (type.equals(CustomArrowType.TELEPORTATION)) {
            return TELEPORTATION_ARROW_LOCATION;
        }
        return ARROW_LOCATION; // Default case if needed
    }
}
