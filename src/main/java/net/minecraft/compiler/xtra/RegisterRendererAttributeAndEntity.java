//package net.minecraft.compiler.xtra;
//
//import net.minecraft.client.renderer.entity.EntityRenderer;
//import net.minecraft.client.renderer.entity.EntityRendererManager;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityType;
//import net.minecraft.entity.ai.attributes.AttributeModifierMap;
//import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
//import net.minecraft.util.registry.Registry;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//public class RegisterRendererAttributeAndEntity {
//
//    public <T extends Entity> void register(EntityType<T> entityType, EntityRenderer<? super T> renderer, AttributeModifierMap.MutableAttribute attributes) {
//        // Register entity type
//        registerEntityType(entityType);
//
//        // Register attributes
//        registerAttributes(entityType, attributes);
//
//        // Register renderer
//        registerRenderer(entityType, renderer);
//
//    }
//
//    private <T extends Entity> void registerEntityType(EntityType<T> entityType) {
//        Registry.register(Registry.ENTITY_TYPE, entityType.getDescriptionId(), entityType);
//    }
//
//    private <T extends Entity> void registerAttributes(EntityType<T> entityType, AttributeModifierMap.MutableAttribute attributes) {
//        GlobalEntityTypeAttributes.SUPPLIERS.put(entityType, attributes.create());
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    private <T extends Entity> void registerRenderer(EntityType<T> entityType, EntityRenderer<? super T> renderer) {
//        EntityRendererManager manager = new EntityRendererManager(); // Obtain the manager instance somehow
//        manager.register(entityType, renderer);
//    }
//}
