package net.minecraft.item.tool;

import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AxeItem extends ToolItem {
    private static final Set<Material> DIGGABLE_MATERIALS = Sets.newHashSet(Material.WOOD, Material.NETHER_WOOD, Material.PLANT, Material.REPLACEABLE_PLANT, Material.BAMBOO, Material.VEGETABLE);
    private static final Set<Block> OTHER_DIGGABLE_BLOCKS = Sets.newHashSet(Blocks.LADDER, Blocks.SCAFFOLDING, Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON, Blocks.DARK_OAK_BUTTON, Blocks.ACACIA_BUTTON, Blocks.CRIMSON_BUTTON, Blocks.WARPED_BUTTON);
    protected static final Map<Block, Block> STRIPABLES = (new Builder<Block, Block>()).put(Blocks.PALE_OAK_WOOD, Blocks.STRIPPED_PALE_OAK_WOOD).put(Blocks.PALE_OAK_LOG, Blocks.STRIPPED_PALE_LOG).put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD).put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG).put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD).put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG).put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD).put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG).put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD).put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG).put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD).put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG).put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD).put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG).put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM).put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE).put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM).put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE).build();

    public AxeItem(IItemTier p_i48530_1_, float p_i48530_2_, float p_i48530_3_, Properties p_i48530_4_) {
        super(p_i48530_2_, p_i48530_3_, p_i48530_1_, BlockTags.MINEABLE_WITH_AXE, p_i48530_4_);
    }

    public float getDestroySpeed(ItemStack p_150893_1_, BlockState p_150893_2_) {
        Material material = p_150893_2_.getMaterial();
        return DIGGABLE_MATERIALS.contains(material) ? this.speed : super.getDestroySpeed(p_150893_1_, p_150893_2_);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState blockState = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        ItemStack offhand = player.getItemInHand(Hand.OFF_HAND);
        ItemStack heldItem = context.getItemInHand();


        Optional<BlockState> stripped = getStripped(blockState);
        if (stripped.isPresent()) {
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, stripped.get()));
            world.playSound(player, pos, SoundEvents.AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            setBlockAndDamageItem(world, pos, player, context, stripped.get());
            return ActionResultType.sidedSuccess(world.isClientSide);
        }

        Optional<BlockState> scrapedCopper = WeatheringCopper.getPrevious(blockState);
        if (scrapedCopper.isPresent()) {
            if (scrapedCopper.get().getBlock() instanceof CopperButtonBlock && scrapedCopper.get().getValue(AbstractButtonBlock.POWERED)) {
                return ActionResultType.PASS;
            }
            world.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.levelEvent(player, 3005, pos, 0);
            setBlockAndDamageItem(world, pos, player, context, scrapedCopper.get());
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, scrapedCopper.get()));
            if (scrapedCopper.get().getBlock() instanceof WeatheringCopperDoorBlock) {
                setDoorState(world, pos, scrapedCopper.get(), player, context);
            }
            return ActionResultType.sidedSuccess(world.isClientSide);
        }

        Optional<BlockState> unwaxed = Optional.ofNullable(HoneyCombItem.WAX_OFF_BY_BLOCK.get().get(blockState.getBlock()))
                .map(block -> block.withPropertiesOf(blockState));
        if (unwaxed.isPresent()) {
            if (unwaxed.get().getBlock() instanceof CopperButtonBlock && unwaxed.get().getValue(AbstractButtonBlock.POWERED)) {
                return ActionResultType.PASS;
            }
            world.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.levelEvent(player, 3004, pos, 0);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, unwaxed.get()));
            setBlockAndDamageItem(world, pos, player, context, unwaxed.get());
            Block block = unwaxed.get().getBlock();
            if (block instanceof WeatheringCopperDoorBlock) {
                setDoorState(world, pos, unwaxed.get(), player, context);
            }
            return ActionResultType.sidedSuccess(world.isClientSide);
        }

        return ActionResultType.PASS;
    }

    private void setBlockAndDamageItem(World world, BlockPos pos, PlayerEntity player, ItemUseContext context, BlockState newState) {
        if (!world.isClientSide) {
            world.setBlock(pos, newState, 11);
            if (player != null) {
                context.getItemInHand().hurtAndBreak(1, player, (p) -> {
                    p.broadcastBreakEvent(context.getHand());
                });
            }
        }
    }

    private Optional<BlockState> getStripped(BlockState state) {
        return Optional.ofNullable(STRIPABLES.get(state.getBlock()))
                .map(block -> block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)));
    }

    private void setDoorState(World world, BlockPos pos, BlockState newState, PlayerEntity player, ItemUseContext context) {
        if (!world.isClientSide) {
            DoubleBlockHalf half = newState.getValue(DoorBlock.HALF);

            BlockPos bottomPos = (half == DoubleBlockHalf.UPPER) ? pos.below() : pos;

            world.removeBlock(bottomPos, true);
            world.removeBlock(bottomPos.above(), true);

            world.setBlock(bottomPos, newState.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER), 11);

            BlockState bottomState = world.getBlockState(bottomPos);
            if (bottomState.getBlock() instanceof DoorBlock && player != null) {
                bottomState.getBlock().setPlacedBy(world, bottomPos, bottomState, player, context.getItemInHand());
            }

            if (player != null) {
                context.getItemInHand().hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(context.getHand()));
            }
        }
    }


    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);


        if (player.getItemInHand(hand.other()).get() instanceof BlockItem) {
            if (((BlockItem) player.getItemInHand(hand.other()).get()).getBlock() instanceof FlowerBlock) {
                AtomicBoolean flag = new AtomicBoolean(false);
                tryCraftIntoDyeItem(world, player.getItemInHand(hand.other())).ifPresent(dye -> {

                    player.level.playSound(player.asPlayer(), player.getX(), player.getY(), player.getZ(), SoundEvents.RESIN_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    if (!player.level.isClientSide) {

                        player.asPlayer().addOrDrop(dye);
                        player.getItemInHand(hand.other()).shrink(1);

                    }
                    flag.set(true);
                });
                if (flag.get()) {
                    return ActionResult.sidedSuccess(stack, player.level.isClientSide);
                }
            }
        }


        return super.use(world, player, hand);
    }

    public static Optional<ItemStack> tryCraftIntoDyeItem(World level, ItemStack input) {
        CraftingInventory craftingInventory = new CraftingInventory(new Container((ContainerType<?>) null, -1) {
            @Override
            public boolean stillValid(PlayerEntity player) {
                return true;
            }
        }, 1, 1);

        craftingInventory.setItem(0, input.copy());

        return level.getRecipeManager()
                .getRecipeFor(IRecipeType.CRAFTING, craftingInventory, level)
                .map(recipe -> recipe.assemble(craftingInventory, level.registryAccess()))
                .filter(stack -> stack.getItem() instanceof DyeItem);
    }

}