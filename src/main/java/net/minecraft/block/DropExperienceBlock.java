package net.minecraft.block;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.Validate;

import java.util.Arrays;

public class DropExperienceBlock extends Block {
    private final int[] xpRange;

    public DropExperienceBlock(AbstractBlock.Properties properties, int number) {
        this(properties, new int[]{number});
    }

    public DropExperienceBlock(AbstractBlock.Properties properties) {
        this(properties, 1);
    }

    public DropExperienceBlock(AbstractBlock.Properties properties, int[] xpRange) {
        super(properties);
        Validate.validState(xpRange.length <= 2, "xpRange must not be over 2 numbers! " + Arrays.toString(xpRange));
        Validate.validState(xpRange.length != 0, "xpRange must not be empty! " + Arrays.toString(xpRange));
        this.xpRange = xpRange;
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
        super.spawnAfterBreak(state, world, pos, stack);
        boolean silkTouch = EnchantmentHelper.has(stack, Enchantments.SILK_TOUCH);

        if (!silkTouch) {
            if (xpRange.length == 1) {
                this.popExperience(world, pos, xpRange[0]); return;
            }
            this.popExperience(world, pos, MathHelper.randomBetweenInclusive(world.random, xpRange[0], xpRange[1]));
        }
    }
}
