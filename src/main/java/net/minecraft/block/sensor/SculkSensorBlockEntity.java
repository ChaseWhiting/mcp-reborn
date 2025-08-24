package net.minecraft.block.sensor;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.entity.warden.event.GameEventListener;
import net.minecraft.entity.warden.event.position.BlockPositionSource;
import net.minecraft.entity.warden.event.vibrations.VibrationListener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SculkSensorBlockEntity extends TileEntity implements VibrationListener.VibrationListenerConfig, ITickableTileEntity {
    private static final Logger LOGGER = LogManager.getLogger();
    private VibrationListener listener;
    private int lastVibrationFrequency;

    public SculkSensorBlockEntity() {
        super(TileEntityType.SCULK_SENSOR);
        //this.listener = new VibrationListener(new BlockPositionSource(this.worldPosition), ((SculkSensorBlock)getBlockState().getBlock()).getListenerRange(), this);
    }

    @Override
    public void setLevelAndPosition(World world, BlockPos pos) {
        super.setLevelAndPosition(world, pos);
        this.level = world; // Ensure level is set

        if (this.level instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) this.level;

            if (this.listener == null) {
                this.listener = new VibrationListener(new BlockPositionSource(this.worldPosition),
                        ((SculkSensorBlock) getBlockState().getBlock()).getListenerRange(), this);
            }



            LOGGER.info("[DEBUG] Listener re-registered in setLevelAndPosition at " + this.worldPosition);
        }
    }



    @Override
    public void load(BlockState state, CompoundNBT compoundTag) {
        super.load(state, compoundTag);
        this.lastVibrationFrequency = compoundTag.getInt("last_vibration_frequency");
        if (compoundTag.contains("listener", 10)) {
            VibrationListener.codec(this).parse(NBTDynamicOps.INSTANCE, compoundTag.getCompound("listener"))
                    .resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent(vibrationListener -> {
                this.listener = vibrationListener;
            });
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putInt("last_vibration_frequency", this.lastVibrationFrequency);
        VibrationListener.codec(this)
                .encodeStart(NBTDynamicOps.INSTANCE,
                        this.listener).resultOrPartial(LOGGER::error)
                .ifPresent(tag -> nbt.put("listener", tag));

        return super.save(nbt);
    }

    public VibrationListener getListener() {
        return this.listener;
    }

    public int getLastVibrationFrequency() {
        return this.lastVibrationFrequency;
    }

    public boolean canTriggerAvoidVibration() {
        return true;
    }

    @Override
    public boolean shouldListen(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, GameEvent.Context context) {
        if (pos.equals(this.getBlockPos()) && (event == GameEvent.BLOCK_DESTROY) || event ==  GameEvent.BLOCK_PLACE) {
            return false;
        }

        return SculkSensorBlock.canActivate(this.getBlockState());
    }

    @Override
    public void onSignalReceive(ServerWorld serverWorld, GameEventListener gameEventListener, BlockPos blockPos, GameEvent gameEvent, @Nullable Entity entity, @Nullable Entity entity2, float f) {
        BlockState blockState = this.getBlockState();
        if (SculkSensorBlock.canActivate(blockState)) {
            LOGGER.info("Signal received (SculkSensorBlockEntity#onSignalReceive)");
            this.lastVibrationFrequency = VibrationListener.getGameEventFrequency(gameEvent);
            SculkSensorBlock.activate(entity, serverWorld, this.worldPosition, blockState, SculkSensorBlockEntity.getRedstoneStrengthForDistance(f, gameEventListener.getListenerRadius()));
        }
    }

    @Override
    public void onSignalSchedule() {
        this.setChanged();
    }

    public static int getRedstoneStrengthForDistance(float f, int n) {
        double d = (double)f / (double)n;
        return Math.max(1, 15 - MathHelper.floor(d * 15.0));
    }

    public void setLastVibrationFrequency(int n) {
        this.lastVibrationFrequency = n;
    }

    @Override
    public void tick() {
        if (this.listener != null && this.level instanceof ServerWorld) {
            this.listener.tick(this.level);
        }
    }
}
