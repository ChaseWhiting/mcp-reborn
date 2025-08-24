package net.minecraft.item;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Supplier;

public class HoneyCombItem extends Item{
    public static final Supplier<BiMap<Block, Block>> WAXABLES = Suppliers.memoize(() ->
            ImmutableBiMap.<Block, Block>builder()
                    .put( Blocks.COPPER_BLOCK, Blocks.WAXED_COPPER_BLOCK)
                    .put(Blocks.EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER)
                    .put(Blocks.WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER)
                    .put(Blocks.OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER)
                    .put(Blocks.CUT_COPPER, Blocks.WAXED_CUT_COPPER)
                    .put(Blocks.EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER)
                    .put(Blocks.WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER)
                    .put(Blocks.OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER)
                    .put(Blocks.CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER_SLAB)
                    .put(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB)
                    .put(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB)
                    .put(Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB)

                    .put(Blocks.CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER_STAIRS)
                    .put(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS)
                    .put(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS)
                    .put(Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS)

                    .put(Blocks.COPPER_DOOR, Blocks.WAXED_COPPER_DOOR)
                    .put(Blocks.WEATHERED_COPPER_DOOR, Blocks.WAXED_WEATHERED_COPPER_DOOR)
                    .put(Blocks.EXPOSED_COPPER_DOOR, Blocks.WAXED_EXPOSED_COPPER_DOOR)
                    .put(Blocks.OXIDIZED_COPPER_DOOR, Blocks.WAXED_OXIDIZED_COPPER_DOOR)

                    .put(Blocks.COPPER_BUTTON, Blocks.WAXED_COPPER_BUTTON)
                    .put(Blocks.WEATHERED_COPPER_BUTTON, Blocks.WAXED_WEATHERED_COPPER_BUTTON)
                    .put(Blocks.EXPOSED_COPPER_BUTTON, Blocks.WAXED_EXPOSED_COPPER_BUTTON)
                    .put(Blocks.OXIDIZED_COPPER_BUTTON, Blocks.WAXED_OXIDIZED_COPPER_BUTTON)

                    .put(Blocks.COPPER_TRAPDOOR, Blocks.WAXED_COPPER_TRAPDOOR)
                    .put(Blocks.WEATHERED_COPPER_TRAPDOOR, Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR)
                    .put(Blocks.EXPOSED_COPPER_TRAPDOOR, Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR)
                    .put(Blocks.OXIDIZED_COPPER_TRAPDOOR, Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR)



                    .put(Blocks.CHISELED_COPPER, Blocks.WAXED_CHISELED_COPPER)
                    .put(Blocks.WEATHERED_CHISELED_COPPER, Blocks.WAXED_WEATHERED_CHISELED_COPPER)
                    .put(Blocks.EXPOSED_CHISELED_COPPER, Blocks.WAXED_EXPOSED_CHISELED_COPPER)
                    .put(Blocks.OXIDIZED_CHISELED_COPPER, Blocks.WAXED_OXIDIZED_CHISELED_COPPER)


                    .put(Blocks.COPPER_GRATE, Blocks.WAXED_COPPER_GRATE)
                    .put(Blocks.EXPOSED_COPPER_GRATE, Blocks.WAXED_EXPOSED_COPPER_GRATE)
                    .put(Blocks.WEATHERED_COPPER_GRATE, Blocks.WAXED_WEATHERED_COPPER_GRATE)
                    .put(Blocks.OXIDIZED_COPPER_GRATE, Blocks.WAXED_OXIDIZED_COPPER_GRATE)
                    .put(Blocks.COPPER_BULB, Blocks.WAXED_COPPER_BULB)
                    .put(Blocks.EXPOSED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER_BULB)
                    .put(Blocks.WEATHERED_COPPER_BULB, Blocks.WAXED_WEATHERED_COPPER_BULB)
                    .put(Blocks.OXIDIZED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER_BULB).build());
    public static final Supplier<BiMap<Block, Block>> WAX_OFF_BY_BLOCK = Suppliers.memoize(() -> WAXABLES.get().inverse());

    public HoneyCombItem(Item.Properties properties) {
        super(properties);
    }


    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);

        return getWaxed(blockState).map(newState -> {
            if (newState.getBlock() instanceof CopperButtonBlock && newState.getValue(AbstractButtonBlock.POWERED)) {
                return ActionResultType.PASS;
            }

            level.levelEvent(context.getPlayer(), 3003, blockPos, 0);
            if (newState.getBlock() instanceof DoorBlock) {
                // Handle doors with setDoorState
                setDoorState(level, blockPos, newState, context.getPlayer(), context);
            } else {
                // Non-door blocks
                level.setBlock(blockPos, newState, 11);
            }

            // Shrink the item stack and trigger advancements
            PlayerEntity player = context.getPlayer();
            if (player instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, blockPos, context.getItemInHand());
            }
            context.getItemInHand().shrink(1);
            level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, blockState));
            return ActionResultType.SUCCESS;
        }).orElse(ActionResultType.PASS);
    }

    private void setDoorState(World world, BlockPos pos, BlockState newState, PlayerEntity player, ItemUseContext context) {
        if (!world.isClientSide) {
            // Get the bottom position of the door
            DoubleBlockHalf half = newState.getValue(DoorBlock.HALF);
            BlockPos bottomPos = (half == DoubleBlockHalf.UPPER) ? pos.below() : pos;
            BlockPos topPos = bottomPos.above();

            // Remove both halves of the door silently
            world.removeBlock(bottomPos, true);
            world.removeBlock(topPos, true);

            // Re-place the bottom half
            world.setBlock(bottomPos, newState.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER), 11);

            // Use setPlacedBy to place the top half
            BlockState bottomState = world.getBlockState(bottomPos);
            if (bottomState.getBlock() instanceof DoorBlock && player != null) {
                bottomState.getBlock().setPlacedBy(world, bottomPos, bottomState, player, context.getItemInHand());
            }
        }
    }

    public static Optional<BlockState> getWaxed(BlockState blockState) {
        return Optional.ofNullable((Block)WAXABLES.get().get((Object)blockState.getBlock())).map(block -> block.withPropertiesOf(blockState));
    }

}
