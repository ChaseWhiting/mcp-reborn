package net.minecraft.util.blueprint;

import com.twelvemonkeys.lang.Validate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class Blueprint {
    private final World world;
    private AxisAlignedBB boxToSave;
    private ConcurrentHashMap<Vector3i, BlockState> positionToState;

    public void setBoxToSave(AxisAlignedBB axisAlignedBB) {
        this.boxToSave = axisAlignedBB;
    }

    public void setBoxToSave(BlockPos s, BlockPos f) {
        this.boxToSave = new AxisAlignedBB(s, f);
    }

    public Blueprint(World world, BlockPos s, BlockPos f) {
        this(new AxisAlignedBB(s, f), world);
    }

    public Blueprint(AxisAlignedBB boxToSave, World world) {
        this.boxToSave = boxToSave;
        this.world = world;
    }

    public void initialize() {
        Validate.notNull(boxToSave, "Don't call 'initialize' before initializing boxToSave");
        Stream<BlockPos> bSt = BlockPos.betweenClosedStream(this.boxToSave);
        ConcurrentHashMap<Vector3i, BlockState> chm = new ConcurrentHashMap<>();
        bSt.sorted().forEach(pos -> {
            BlockState state = world.getBlockState(pos);
            chm.put(pos, state);
        });
        this.positionToState = chm;
    }


    public void add(Vector3i position, BlockState state) {
        this.positionToState.put(position, state);
    }

    public void add(Vector3d position, BlockState state) {
        add(new Vector3i(position.x,position.y,position.z), state);
    }

    public void place() {

    }
}
