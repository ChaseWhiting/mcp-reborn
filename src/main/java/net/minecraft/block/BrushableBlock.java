package net.minecraft.block;

import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.BrushableBlockEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class BrushableBlock extends ContainerBlock implements Fallable {
    public static final IntegerProperty DUSTED = BlockStateProperties.DUSTED;
    private final Block turnsInto;
    private final SoundEvent brushSound;
    private final SoundEvent brushCompletedSound;

    public BrushableBlock(Block block, AbstractBlock.Properties properties, SoundEvent soundEvent, SoundEvent soundEvent2) {
        super(properties);
        this.turnsInto = block;
        this.brushSound = soundEvent;
        this.brushCompletedSound = soundEvent2;
        this.registerDefaultState(this.stateDefinition.any().setValue(DUSTED, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(DUSTED);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlace(BlockState state, World world, BlockPos pos, BlockState state2, boolean b) {
        world.getBlockTicks().scheduleTick(pos, this, 2);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state2, IWorld world, BlockPos pos, BlockPos pos2) {
        world.getBlockTicks().scheduleTick(pos, this, 2);
        return super.updateShape(state, direction, state2, world, pos, pos2);
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        Object object;
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof BrushableBlockEntity) {
            object = (BrushableBlockEntity) tileEntity;
            ((BrushableBlockEntity)object).checkReset();
        }
        if (!FallingBlock.isFree(world.getBlockState(pos.below())) || pos.getY() <= 0) {
            return;
        }
        object = FallingBlockEntity.fall(world, pos, state);
        ((FallingBlockEntity)object).disableDrop();
    }

    @Override
    public void onBroken(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
        Vector3d vector3d = fallingBlockEntity.getBoundingBox().getCenter();
        world.levelEvent(2001, BlockPos.containing(vector3d.x, vector3d.y, vector3d.z), Block.getId(fallingBlockEntity.getBlockState()));
        world.gameEvent(fallingBlockEntity, GameEvent.BLOCK_DESTROY, vector3d);
    }

    public long brushCountResetsAtTick(long l) {
        return l + 40L;
    }

    public long coolDownEndsAtTick(long l) {
        return l + 10L;
    }

    public int brushCountToComplete(BlockPos worldPos, World level) {
        return 10;
    }

    public TileEntity newBlockEntity(IBlockReader reader) {
        return new BrushableBlockEntity();
    }

    public Block getTurnsInto() {
        return this.turnsInto;
    }

    public SoundEvent getBrushSound() {
        return this.brushSound;
    }

    public SoundEvent getBrushCompletedSound() {
        return this.brushCompletedSound;
    }

    public int getCompletionState(int brushCount, BlockPos worldPos, World level) {
        if (brushCount == 0) {
            return 0;
        }
        if (brushCount < 3) {
            return 1;
        }
        if (brushCount < 6) {
            return 2;
        }

        return 3;
    }
}
