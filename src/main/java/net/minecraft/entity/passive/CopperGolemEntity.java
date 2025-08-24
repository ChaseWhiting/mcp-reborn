package net.minecraft.entity.passive;

import com.clearspring.analytics.util.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.client.animation.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoneyCombItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tool.AxeItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CopperGolemEntity extends GolemEntity {
    private static final DataParameter<WeatheringCopper.WeatherState> WEATHER_STATE = EntityDataManager.defineId(CopperGolemEntity.class, DataSerializers.WEATHER_STATE);
    private static final DataParameter<Integer> BUTTON_PUSH_TICKS = EntityDataManager.defineId(CopperGolemEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> HEAD_SPIN_TICKS = EntityDataManager.defineId(CopperGolemEntity.class, DataSerializers.INT);

    private final WalkingGoal GOAL = new WalkingGoal(this, 0.8);
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BUTTON_PUSH_TICKS, 0);
        this.entityData.define(WEATHER_STATE, WeatheringCopper.WeatherState.UNAFFECTED);
        this.entityData.define(HEAD_SPIN_TICKS, 0);

    }
    public final AnimationState buttonPush = new AnimationState();
    public final AnimationState spinningHead = new AnimationState();
    @Nullable
    private ButtonData currentButton;
    private int pushButtonCooldown = 0;
    private boolean isWaxed = false;
    private int oxidationTimer;
    private static final int MIN_OXIDATION_TIME = TimeConstants.TEN_MINUTES;
    private static final int MAX_OXIDATION_TIME = TimeConstants.TWENTY_MINUTES;

    private int headSpinningCooldown = 0;

    private void resetHeadSpinCooldown() {
        this.headSpinningCooldown = switch(this.getWeatherState()) {
            case UNAFFECTED -> TickRangeConverter.rangeOfSeconds(35, 50).randomValue(random);
            case EXPOSED -> TickRangeConverter.rangeOfSeconds(45, 70).randomValue(random);
            case WEATHERED -> TickRangeConverter.rangeOfSeconds(60, 85).randomValue(random);
            case OXIDIZED -> TickRangeConverter.rangeOfSeconds(75, 110).randomValue(random);
        };
    }


    protected ActionResultType mobInteract(PlayerEntity playerEntity, Hand hand) {
        ItemStack itemstack = playerEntity.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (item instanceof HoneyCombItem && !isWaxed) {
            this.level.levelEvent(3003, this.blockPosition(), 0);
            if (!playerEntity.abilities.instabuild) {
                itemstack.shrink(1);
            }
            this.isWaxed = true;
            return ActionResultType.SUCCESS;
        }
        if (item instanceof AxeItem && this.isWaxed) {
            this.level.levelEvent(3004, this.blockPosition(), 0);
            itemstack.hurtAndBreak(1, playerEntity, (p) -> {
                p.broadcastBreakEvent(hand);
            });
            this.isWaxed = false;
            this.oxidationTimer = getRandomOxidationTime();
            level.playSound(playerEntity, this.blockPosition(), SoundEvents.AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return ActionResultType.SUCCESS;
        }
        if (item instanceof AxeItem && this.getWeatherState() != WeatheringCopper.WeatherState.UNAFFECTED) {
            this.level.levelEvent(3005, this.blockPosition(), 0);
            itemstack.hurtAndBreak(1, playerEntity, (p) -> {
                p.broadcastBreakEvent(hand);
            });
            this.setWeatherState(switch(this.getWeatherState()) {
                case EXPOSED -> WeatheringCopper.WeatherState.UNAFFECTED;
                case WEATHERED -> WeatheringCopper.WeatherState.EXPOSED;
                case OXIDIZED -> WeatheringCopper.WeatherState.WEATHERED;
                default -> WeatheringCopper.WeatherState.UNAFFECTED;
            });
            level.playSound(playerEntity, this.blockPosition(), SoundEvents.AXE_SCRAPE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return ActionResultType.SUCCESS;
        }






        return ActionResultType.PASS;
    }

    private int getNewButtonCooldown() {
        RangedInteger rangedInteger = switch(this.getWeatherState()) {
            case UNAFFECTED -> TickRangeConverter.rangeOfSeconds(8, 15);
            case EXPOSED -> TickRangeConverter.rangeOfSeconds(15, 25);
            case WEATHERED -> TickRangeConverter.rangeOfSeconds(25, 35);
            case OXIDIZED -> TickRangeConverter.rangeOfSeconds(35, 45);
        };

        return rangedInteger.randomValue(random);
    }

    protected float getSoundVolume() {
        return 3F;
    }

    public void thunderHit(ServerWorld world, LightningBoltEntity entity) {
        this.setWeatherState(switch(this.getWeatherState()) {
            case EXPOSED -> WeatheringCopper.WeatherState.UNAFFECTED;
            case WEATHERED -> WeatheringCopper.WeatherState.EXPOSED;
            case OXIDIZED -> WeatheringCopper.WeatherState.WEATHERED;
            default -> WeatheringCopper.WeatherState.UNAFFECTED;
        });
    }

    public void spinHead() {
        this.resetHeadSpinCooldown();
        this.setHeadSpinTicks(20);
    }

    public int getHeadSpinTicks() {
        return entityData.get(HEAD_SPIN_TICKS);
    }

    public void setHeadSpinTicks(int b) {
        this.entityData.set(HEAD_SPIN_TICKS, b);
    }

    public CopperGolemEntity(EntityType<? extends CopperGolemEntity> entityType, World world) {
        super(entityType, world);
        this.lookControl = new CGLC(this);
        this.jumpControl = new CGJC(this);
        this.bodyRotationControl = new CGBC(this);
        this.moveControl = new CGMC(this);
        this.oxidationTimer = getRandomOxidationTime();
    }


    private int getRandomOxidationTime() {
        return MIN_OXIDATION_TIME + this.random.nextInt(MAX_OXIDATION_TIME - MIN_OXIDATION_TIME);
    }

    static class CGLC extends LookController {
        final CopperGolemEntity copperGolemEntity;
        public CGLC(CopperGolemEntity copperGolem) {
            super(copperGolem);
            this.copperGolemEntity = copperGolem;
        }
        public void tick() {
            if (copperGolemEntity.isOxidized()) {
                this.hasWanted = false;
            } else {
                super.tick();
            }
        }
    }
    static class CGJC extends JumpController {
        final CopperGolemEntity copperGolemEntity;
        public CGJC(CopperGolemEntity copperGolem) {
            super(copperGolem);
            this.copperGolemEntity = copperGolem;
        }
        public void tick() {
            if (copperGolemEntity.isOxidized()) {
                copperGolemEntity.setJumping(false);
            } else {
                super.tick();
            }
        }
    }
    static class CGMC extends MovementController {
        final CopperGolemEntity copperGolemEntity;
        public CGMC(CopperGolemEntity copperGolem) {
            super(copperGolem);
            this.copperGolemEntity = copperGolem;
        }
        public void tick() {
            if (!copperGolemEntity.isOxidized()) {
                super.tick();
            }
        }
    }
    static class CGBC extends BodyController {
        private final CopperGolemEntity mob;

        public CGBC(CopperGolemEntity mob) {
            super(mob);
            this.mob = mob;
        }

        @Override
        public void clientTick() {
            if (!mob.isOxidized()) {
                super.clientTick();
            }
        }


    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.COPPER_STEP, 0.15F, 0.85F);
    }

    public SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.COPPER_HIT;
    }

    public SoundEvent getDeathSound() {
        return SoundEvents.COPPER_BULB_BREAK;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D)
                .add(Attributes.ARMOR_TOUGHNESS, 15.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    public WeatheringCopper.WeatherState getWeatherState() {
        return this.entityData.get(WEATHER_STATE);
    }

    public void setWeatherState(WeatheringCopper.WeatherState weatherState) {
        this.entityData.set(WEATHER_STATE, weatherState);
        this.GOAL.setSpeed(switch(weatherState) {
            case UNAFFECTED -> 0.8F;
            case EXPOSED -> 0.7F;
            case WEATHERED -> 0.4F;
            case OXIDIZED -> 0.0F;
        });
    }

    public void pushButton(BlockState state, BlockPos buttonPos) {
        boolean flag = this.validateButton(state, buttonPos);
        if (!flag) {
            return;
        }
        this.entityData.set(BUTTON_PUSH_TICKS, 33);
        this.pushButtonCooldown = getNewButtonCooldown();
        level.playSound(null, buttonPos, SoundEvents.COPPER_BULB_TURN_ON, SoundCategory.BLOCKS, 1F, 0.6f);
        level.setBlock(buttonPos, state.setValue(AbstractButtonBlock.POWERED, true), 3);
        level.updateNeighborsAt(buttonPos, state.getBlock());
        level.updateNeighborsAt(buttonPos.relative(AbstractButtonBlock.getConnectedDirection(state).getOpposite()), state.getBlock());
        level.getBlockTicks().scheduleTick(buttonPos, state.getBlock(), ((CopperButtonBlock)state.getBlock()).getPressDuration());

        this.currentButton = null;
    }

    public boolean validateButton(BlockState state, BlockPos pos) {
        return state.getBlock() instanceof CopperButtonBlock && state.getValue(AbstractButtonBlock.POWERED) == false && level.getBlockState(pos).equals(state);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new RunFromAxeWielders(this));
        this.goalSelector.addGoal(1, new PushButtonGoal(this));
        this.goalSelector.addGoal(2, new FindButtonGoal(this));
        this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 10f) {
            @Override
            public boolean canUse() {
                return super.canUse() && !CopperGolemEntity.this.isOxidized();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !CopperGolemEntity.this.isOxidized();
            }
        });
    }

    static class RunFromAxeWielders extends AvoidEntityGoal<LivingEntity> {
        final CopperGolemEntity mob;

        public RunFromAxeWielders(CopperGolemEntity copperGolem) {
            super(copperGolem, LivingEntity.class, 12, 0.85F, 0.9F, entity -> {
                return (entity.getMainHandItem().get() instanceof AxeItem || entity.getOffhandItem().get() instanceof AxeItem) && copperGolem.isWaxed;
            });
            this.mob = copperGolem;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !mob.isOxidized();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && !mob.isOxidized();
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    protected int decreaseAirSupply(int as) {
        return as;
    }

    public int getButtonTicks() {
        return entityData.get(BUTTON_PUSH_TICKS);
    }

    public void setButtonTicks(int b) {
        this.entityData.set(BUTTON_PUSH_TICKS, b);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (!this.goalSelector.getAvailableGoals().anyMatch((goal) -> goal.getGoal() == GOAL) && GOAL != null) {
            this.goalSelector.addGoal(4, GOAL);
        }
        this.pushButtonCooldown = Math.max(0, pushButtonCooldown - 1);


        if (!this.isWaxed) {
            if (oxidationTimer <= 0) {
                attemptOxidation();
                oxidationTimer = getRandomOxidationTime();
            } else {
                oxidationTimer--;
            }
        }
    }

    public void tick() {
        super.tick();

        if (this.getButtonTicks() > 0) {
            this.setButtonTicks(this.getButtonTicks() - 1);
        }
        this.buttonPush.animateWhen(getButtonTicks() > 0, tickCount);

        if (--headSpinningCooldown <= 0) {
            spinHead();
        }
        if (this.getHeadSpinTicks() > 0) {
            this.setHeadSpinTicks(this.getHeadSpinTicks() - 1);
        }
        this.spinningHead.animateWhen(getHeadSpinTicks() > 0, tickCount);
    }

    private void attemptOxidation() {
        WeatheringCopper.WeatherState currentState = this.getWeatherState();
        WeatheringCopper.WeatherState nextState = switch (currentState) {
            case UNAFFECTED -> WeatheringCopper.WeatherState.EXPOSED;
            case EXPOSED -> WeatheringCopper.WeatherState.WEATHERED;
            case WEATHERED -> WeatheringCopper.WeatherState.OXIDIZED;
            default -> currentState;
        };

        if (nextState != currentState) {
            this.setWeatherState(nextState);
        }
    }

    public boolean isImmobile() {
        return this.isOxidized();
    }

    public boolean isOxidized() {
        return this.getWeatherState() == WeatheringCopper.WeatherState.OXIDIZED;
    }

    private static class WalkingGoal extends WaterAvoidingRandomWalkingGoal {
        final CopperGolemEntity copperGolemEntity;
        public WalkingGoal(CopperGolemEntity mob, double speed) {
            super(mob, speed);
            this.copperGolemEntity = mob;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !copperGolemEntity.isOxidized() && copperGolemEntity.currentButton == null;
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && !copperGolemEntity.isOxidized() && copperGolemEntity.currentButton == null;
        }

        public void setSpeed(float speed) {
            this.speedModifier = speed;
        }
    }

    static class ButtonData {
        public static final Codec<ButtonData> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        BlockPos.VANILLA_COMPOUND_CODEC.fieldOf("pos")
                        .forGetter(ButtonData::getPos),
                        WeatheringCopper.WeatherState.CODEC.fieldOf("buttonState")
                                .forGetter(ButtonData::getButtonState)).apply(instance, ButtonData::new));
        private final WeatheringCopper.WeatherState buttonState;
        private final BlockPos pos;

        public ButtonData(WeatheringCopper.WeatherState state, BlockPos pos) {
            this.buttonState = state;
            this.pos = pos;
        }

        public ButtonData(BlockPos pos, WeatheringCopper.WeatherState state) {
            this.buttonState = state;
            this.pos = pos;
        }

        public BlockPos getPos() {
            return pos;
        }

        public WeatheringCopper.WeatherState getButtonState() {
            return buttonState;
        }
    }

    public void addAdditionalSaveData(CompoundNBT nbt) {
        if (currentButton != null) {
            CompoundNBT data = new CompoundNBT();
            data.putString("WeatherState", currentButton.getButtonState().getName());
            data.put("BlockPos", NBTUtil.writeBlockPos(currentButton.getPos()));

            nbt.put("ButtonData", data);
        }
        nbt.putInt("OxidationTimer", this.oxidationTimer);
        nbt.putBoolean("IsWaxed", this.isWaxed);
        nbt.putString("WeatherState", this.getWeatherState().getName());
        nbt.putInt("ButtonCooldown", this.pushButtonCooldown);
        nbt.putInt("HeadSpinCooldown", this.headSpinningCooldown);
        super.addAdditionalSaveData(nbt);
    }

    public boolean hurt(DamageSource source, float damage) {
        return super.hurt(source, damage / 2);
    }


    public void readAdditionalSaveData(CompoundNBT nbt) {
        if (nbt.contains("ButtonData")) {
            CompoundNBT data = nbt.getCompound("ButtonData");
            if (data.contains("WeatherState") && data.contains("BlockPos")) {
                BlockPos pos = NBTUtil.readBlockPos(data.getCompound("BlockPos"));
                WeatheringCopper.WeatherState state = WeatheringCopper.WeatherState.fromName(data.getString("WeatherState"));
                if (!pos.equals(BlockPos.ZERO)) {
                    this.currentButton = new ButtonData(state, pos);
                }
            }
        }
        this.oxidationTimer = nbt.getInt("OxidationTimer");
        this.isWaxed = nbt.getBoolean("IsWaxed");
        this.pushButtonCooldown = nbt.getInt("ButtonCooldown");
        this.headSpinningCooldown = nbt.getInt("HeadSpinCooldown");
        this.setWeatherState(WeatheringCopper.WeatherState.fromName(nbt.getString("WeatherState")));

        super.readAdditionalSaveData(nbt);
    }

    private static class FindButtonGoal extends Goal {
        private final CopperGolemEntity mob;
        private final static int SEARCH_AREA = 10;

        public FindButtonGoal(CopperGolemEntity copperGolem) {
            this.mob = copperGolem;
        }

        @Override
        public boolean canUse() {
            return mob.currentButton == null && mob.pushButtonCooldown <= 0;
        }

        @Override
        public void tick() {
            List<BlockPos> potentialButtons = Lists.newArrayList();

            for (BlockPos blockPos : BlockPos.withinManhattan(mob.blockPosition(), SEARCH_AREA, SEARCH_AREA, SEARCH_AREA)) {
                if (mob.level.getBlockState(blockPos).getBlock() instanceof CopperButtonBlock) {
                    potentialButtons.add(blockPos.immutable());
                }
            }

            if (potentialButtons.isEmpty()) {
                return;
            }

            Collections.shuffle(potentialButtons);
            BlockPos chosenButton = potentialButtons.get(0);
            BlockState state = mob.level.getBlockState(chosenButton);
            if (state.getBlock() instanceof CopperButtonBlock) {
                CopperButtonBlock button = (CopperButtonBlock) state.getBlock();
                mob.currentButton = new ButtonData(button.getAge(), chosenButton);
                stop();
            }
        }




    }

    private static class PushButtonGoal extends Goal {
        private final CopperGolemEntity mob;

        public PushButtonGoal(CopperGolemEntity copperGolem) {
            this.mob = copperGolem;
        }

        @Override
        public boolean canUse() {
            return mob.currentButton != null && mob.pushButtonCooldown <= 0;
        }

        public void tick() {
            if (mob.currentButton != null) {
                if (mob.validateButton(mob.level.getBlockState(mob.currentButton.getPos()),mob.currentButton.getPos())) {
                    mob.getNavigation().moveTo(mob.currentButton.getPos().asVector(), switch(mob.getWeatherState()) {
                        case UNAFFECTED -> 0.8F;
                        case EXPOSED -> 0.7F;
                        case WEATHERED -> 0.4F;
                        case OXIDIZED -> 0.0F;
                    });
                    if (mob.blockPosition().equals(mob.currentButton.getPos())) {
                        mob.pushButton(mob.level.getBlockState(mob.currentButton.getPos()), mob.currentButton.getPos());
                        stop();
                    }
                } else {
                    mob.currentButton = null;
                }
            }
        }
    }

}
