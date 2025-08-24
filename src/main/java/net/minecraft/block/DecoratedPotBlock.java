package net.minecraft.block;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.*;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.DecoratedPotTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

import static net.minecraft.inventory.InventoryHelper.dropContents;

public class DecoratedPotBlock extends ContainerBlock implements IWaterLoggable {

    public static final ResourceLocation SHERDS_DYNAMIC_DROP_ID = new ResourceLocation("sherds");
    private static final VoxelShape BOUNDING_BOX = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    private static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty CRACKED = BlockStateProperties.CRACKED;
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;


    protected DecoratedPotBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HORIZONTAL_FACING, Direction.NORTH)).setValue(WATERLOGGED, false)).setValue(CRACKED, false));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state2, IWorld world, BlockPos pos, BlockPos pos2) {
        if (state.getValue(WATERLOGGED)) {
            world.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return super.updateShape(state, direction, state2, world, pos, pos2);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockItemUseContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return (this.defaultBlockState()).setValue(HORIZONTAL_FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER).setValue(CRACKED, false);
    }


    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (!(tileEntity instanceof DecoratedPotTileEntity decoratedPotTileEntity)) {
            return ActionResultType.PASS;
        }

        if (world.isClientSide) {
            return ActionResultType.CONSUME;
        }

        ItemStack handItem = player.getItemInHand(hand);
        ItemStack itemInPot = decoratedPotTileEntity.getTheItem();
        if (!(handItem.isEmpty()) && (itemInPot.isEmpty() || ItemStack.isSameItemSameTags(itemInPot, handItem) && itemInPot.getCount() < itemInPot.getMaxStackSize())) {
            float f;
            ItemStack stack2 = player.isCreative() ? handItem.copyWithCount(1) : handItem.split(1);
            decoratedPotTileEntity.wobble(DecoratedPotTileEntity.WobbleStyle.POSITIVE);
            if (decoratedPotTileEntity.isEmpty()) {
                decoratedPotTileEntity.setTheItem(stack2);
                f = (float) stack2.getCount() / stack2.getMaxStackSize();
            } else {
                itemInPot.grow(1);
                f = (float) itemInPot.getCount() / itemInPot.getMaxStackSize();
            }
            world.playSound(null, pos, SoundEvents.DECORATED_POT_INSERT, SoundCategory.BLOCKS, 1.0F, 0.7f + 0.5f * f);

            if (world instanceof ServerWorld serverWorld) {
                serverWorld.sendParticles(ParticleTypes.DUST_PLUME, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 7,
                        0, 0, 0, 0);
            }
            world.updateNeighbourForOutputSignal(pos, this);
        } else {
            world.playSound(null, pos, SoundEvents.DECORATED_POT_INSERT_FAIL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            decoratedPotTileEntity.wobble(DecoratedPotTileEntity.WobbleStyle.NEGATIVE);
        }
        world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        return ActionResultType.SUCCESS;
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (world.isClientSide) {
            if (world.getBlockEntity(pos) instanceof DecoratedPotTileEntity decoratedPotTileEntity) {
                decoratedPotTileEntity.setFromItem(stack);
            }
        }
    }

    @Override
    public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return BOUNDING_BOX;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, WATERLOGGED, CRACKED);
    }

    @Override
    public @Nullable TileEntity newBlockEntity(IBlockReader p_196283_1_) {
        return new DecoratedPotTileEntity();
    }

    @Override
    public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
        dropContentsOnDestroy(p_196243_1_, p_196243_4_, p_196243_2_, p_196243_3_);
        super.onRemove(p_196243_1_, p_196243_2_, p_196243_3_, p_196243_4_, p_196243_5_);
    }

    public static void dropContentsOnDestroy(BlockState blockState, BlockState blockState2, World level, BlockPos blockPos) {
        if (blockState.is(blockState2.getBlock())) {
            return;
        }
        TileEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof IInventory) {
            IInventory container = (IInventory)((Object)blockEntity);
            dropContents(level, blockPos, container);
            level.updateNeighbourForOutputSignal(blockPos, blockState.getBlock());
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState p_220076_1_, LootContext.Builder p_220076_2_) {
        TileEntity tileEntity = p_220076_2_.getOptionalParameter(LootParameters.BLOCK_ENTITY);
        if (tileEntity instanceof DecoratedPotTileEntity decoratedPotTileEntity) {
            p_220076_2_.withDynamicDrop(SHERDS_DYNAMIC_DROP_ID, (context, consumer) -> decoratedPotTileEntity.getDecorations().sorted().map(Item::getDefaultInstance).forEach(consumer));
        }

        return super.getDrops(p_220076_1_, p_220076_2_);
    }

    @Override
    public BlockState playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        ItemStack stack = player.getMainHandItem();


        BlockState blockState2 = state;
        if (stack.getItem().is(ItemTags.DESTROYS_DECORATED_POTS) && !EnchantmentHelper.has(stack, Enchantments.SILK_TOUCH)) {
            blockState2 = (BlockState)state.setValue(CRACKED, true);
            world.setBlock(pos, blockState2, 4);
        }

        return super.playerWillDestroy(world, pos, blockState2, player);
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        if (blockState.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(blockState);
    }

    @Override
    public SoundType getSoundType(BlockState blockState) {
        if (blockState.getValue(CRACKED).booleanValue()) {
            return SoundType.DECORATED_POT_CRACKED;
        }
        return SoundType.DECORATED_POT;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> list, ITooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);
        DecoratedPotTileEntity.Decorations decorations = DecoratedPotTileEntity.Decorations.load(BlockItem.getBlockEntityData(stack));
        if (decorations.equals(DecoratedPotTileEntity.Decorations.EMPTY)) {
            return;
        }

        list.add(new StringTextComponent(""));
        Stream.of(decorations.front(), decorations.left(), decorations.right(), decorations.back()).forEach(item -> list.add(new ItemStack((IItemProvider) item, 1).getHoverName().plainCopy().withStyle(TextFormatting.GRAY)));
    }

    @Override
    public void onProjectileHit(World p_220066_1_, BlockState p_220066_2_, BlockRayTraceResult p_220066_3_, ProjectileEntity p_220066_4_) {
        BlockPos pos = p_220066_3_.getBlockPos();
        if (!p_220066_1_.isClientSide) {
            p_220066_1_.setBlock(pos, p_220066_2_.setValue(CRACKED, true), 4);
            p_220066_1_.destroyBlock(pos, true, p_220066_4_);
        }
    }

    @Override
    public ItemStack getCloneItemStack(IBlockReader reader, BlockPos pos, BlockState state) {
        TileEntity tileEntity = reader.getBlockEntity(pos);
        if (tileEntity instanceof DecoratedPotTileEntity decoratedPotTileEntity) {
            return decoratedPotTileEntity.getPotAsItem();
        }
        return super.getCloneItemStack(reader, pos, state);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, World level, BlockPos blockPos) {
        return Container.getRedstoneSignalFromBlockEntity(level.getBlockEntity(blockPos));
    }
}
