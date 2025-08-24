package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.AttachedToLogsDecorator;

public class TreeDecoratorType<P extends TreeDecorator> {
   public static final TreeDecoratorType<TrunkVineTreeDecorator> TRUNK_VINE = register("trunk_vine", TrunkVineTreeDecorator.CODEC);
   public static final TreeDecoratorType<LeaveVineTreeDecorator> LEAVE_VINE = register("leave_vine", LeaveVineTreeDecorator.CODEC);
   public static final TreeDecoratorType<PaleMossDecorator> PALE_MOSS_DECORATE = register("pale_moss", PaleMossDecorator.CODEC);

   public static final TreeDecoratorType<CocoaTreeDecorator> COCOA = register("cocoa", CocoaTreeDecorator.CODEC);
   public static final TreeDecoratorType<BeehiveTreeDecorator> BEEHIVE = register("beehive", BeehiveTreeDecorator.CODEC);
   public static final TreeDecoratorType<AlterGroundTreeDecorator> ALTER_GROUND = register("alter_ground", AlterGroundTreeDecorator.CODEC);
   public static final TreeDecoratorType<PaleOakTreeGroundDecorator> PALE_OAK_GROUND = register("pale_oak_ground", PaleOakTreeGroundDecorator.CODEC);
   public static final TreeDecoratorType<LeafLitterTreeDecorator> LEAF_LITTER = register("leaf_litter", LeafLitterTreeDecorator.CODEC);
   public static final TreeDecoratorType<AttachedToLogsDecorator> ATTACHED_TO_LOGS = register("attached_to_logs", AttachedToLogsDecorator.CODEC);

   private final Codec<P> codec;

   private static <P extends TreeDecorator> TreeDecoratorType<P> register(String p_236877_0_, Codec<P> p_236877_1_) {
      return Registry.register(Registry.TREE_DECORATOR_TYPES, p_236877_0_, new TreeDecoratorType<>(p_236877_1_));
   }

   private TreeDecoratorType(Codec<P> p_i232052_1_) {
      this.codec = p_i232052_1_;
   }

   public Codec<P> codec() {
      return this.codec;
   }
}