package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FrisbeeEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;

public class FrisbeeBehaviours {

    @FunctionalInterface
    public interface OnHitEntityBehavior {
        void onHitEntity(FrisbeeEntity frisbee, EntityRayTraceResult result);
    }

    @FunctionalInterface
    public interface OnHitBlockBehavior {
        void onHitBlock(FrisbeeEntity frisbee, BlockRayTraceResult result);
    }

    @FunctionalInterface
    public interface OnThrowBehavior {
        void onThrow(FrisbeeEntity frisbee, PlayerEntity player);
    }

    @FunctionalInterface
    public interface OnReturnBehavior {
        void onReturn(FrisbeeEntity frisbee, PlayerEntity player);
    }

    @FunctionalInterface
    public interface WhileFlyingBehavior {
        void onFlyingTick(FrisbeeEntity frisbee);
    }
}
