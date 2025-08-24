package net.minecraft.item.tool;

import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.custom.arrow.CustomArrowEntity;
import net.minecraft.entity.projectile.custom.arrow.CustomArrowType;
import net.minecraft.item.*;
import net.minecraft.item.tool.bow.BowReleaseInfo;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class AerialBaneItem extends TerrariaBowItem implements IVanishable {

    public AerialBaneItem(Properties properties) {
        super(6, properties);
    }

    @Override
    public void onReleaseUsing(BowReleaseInfo info) {
        PlayerEntity player = info.player();
        float power = info.getPower();
        ItemStack bowStack = info.getBowStack();
        ItemStack arrowStack = info.getArrowStack();

        if (!arrowStack.isEmpty() || info.infiniteArrows()) {
            CustomArrowEntity arrowEntity = this.getArrow(player.level, player, (ArrowItem) (arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW), bowStack, arrowStack, power);

            info.getLevel().addFreshEntity(arrowEntity);

            float[] spreadOffsets = {-22f, -12.5f, 12.5f, 22f};

            Vector3d arrowPos = arrowEntity.position();
            Vector3d baseDir = arrowEntity.getDeltaMovement().normalize();

            Vector3f upVector = new Vector3f(0, 1, 0);

            for (float offset : spreadOffsets) {
                CustomArrowEntity customArrow = new CustomArrowEntity(info.getLevel(), arrowEntity.getOwner().as(LivingEntity.class));
                customArrow.setPos(arrowPos);
                addEnchantmentsToArrow(customArrow, bowStack, arrowStack, power, player);
                customArrow.setArrowType(arrowEntity.getArrowType());
                customArrow.pickup = AbstractArrowEntity.PickupStatus.DISALLOWED;
                customArrow.setBaseDamage(arrowEntity.getBaseDamage());
                Vector3f dir = new Vector3f((float)baseDir.x, (float)baseDir.y, (float)baseDir.z);
                Quaternion rotation = new Quaternion(upVector, offset, true);
                dir.transform(rotation);

                float inaccuracy = 0.0f;

                customArrow.shoot(dir.x(), dir.y(), dir.z(), getVelocityInMCScale(power), inaccuracy);

                info.getLevel().addFreshEntity(customArrow);
            }
        }
    }

    @Override
    public CustomArrowEntity getArrow(World world, PlayerEntity player, ArrowItem arrowItem, ItemStack bowStack, ItemStack arrowStack, float power) {
        CustomArrowEntity arrowEntity = new CustomArrowEntity(world, player);
        arrowEntity.setArrowType(CustomArrowType.AERIAL_BANE);
        arrowEntity.shootFromRotation(player, player.xRot, player.yRot, 0.0F, getVelocityInMCScale(power), 1.0F);

        addEnchantmentsToArrow(arrowEntity, bowStack, arrowStack, power, player);

        return arrowEntity;
    }

    public double getMinDrawTime() {
        return 0.4D;
    }

    @Override
    public void playShootSound(World world, PlayerEntity player, float power) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AERIAL_BANE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + power * 0.5F);
    }

    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.BOW;
    }


    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.getItem() == Items.ARROW;
    }
}
