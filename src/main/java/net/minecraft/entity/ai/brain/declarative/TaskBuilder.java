package net.minecraft.entity.ai.brain.declarative;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;

public class TaskBuilder {
    @FunctionalInterface
    public interface TickBehavior<E extends LivingEntity> {
        void tick(ServerWorld world, E entity, long time);
    }
    @FunctionalInterface
    public interface StopBehavior<E extends LivingEntity> {
        void stop(ServerWorld world, E entity, long time);
    }
    @FunctionalInterface
    public interface StartBehavior<E extends LivingEntity> {
        void start(ServerWorld world, E entity, long time);
    }
    @FunctionalInterface
    public interface CanStillUseBehavior<E extends LivingEntity> {
        boolean canStillUse(ServerWorld world, E entity, long time);
    }
    @FunctionalInterface
    public interface TimedOutBehavior<E extends LivingEntity> {
        boolean timedOut(long time);
    }
    @FunctionalInterface
    public interface ExtraStartConditionsBehavior<E extends LivingEntity> {
        boolean checkExtraStartConditions(ServerWorld world, E entity);
    }




    public static class Builder<E extends LivingEntity> {

        public Builder<E> createNewBuilder() {
            return new Builder<E>();
        }

        private TickBehavior<E> tickBehavior = (serverWorld, entity, time) -> {
        };
        private StartBehavior<E> startBehavior = (serverWorld, entity, time) -> {
        };
        private StopBehavior<E> stopBehavior = (serverWorld, entity, time) -> {
        };
        private CanStillUseBehavior<E> canStillUseBehavior = (serverWorld, entity, time) -> {
            return false;
        };
        private TimedOutBehavior<E> timedOutBehavior = (time) -> {
            return false;
        };
        private ExtraStartConditionsBehavior<E> extraStartConditionsBehavior = (serverWorld, entity) -> {
            return true;
        };

        public Builder<E> tickBehavior(TickBehavior<E> tickBehavior) {
            this.tickBehavior = tickBehavior;
            return this;
        }

        public Builder<E> startBehavior(StartBehavior<E> startBehavior) {
            this.startBehavior = startBehavior;
            return this;
        }

        public Builder<E> stopBehavior(StopBehavior<E> stopBehavior) {
            this.stopBehavior = stopBehavior;
            return this;
        }

        public Builder<E> canStillUseBehavior(CanStillUseBehavior<E> canStillUseBehavior) {
            this.canStillUseBehavior = canStillUseBehavior;
            return this;
        }

        public Builder<E> timedOutBehavior(TimedOutBehavior<E> timedOutBehavior) {
            this.timedOutBehavior = timedOutBehavior;
            return this;
        }

        public Builder<E> extraStartConditionsBehavior(ExtraStartConditionsBehavior<E> extraStartConditionsBehavior) {
            this.extraStartConditionsBehavior = extraStartConditionsBehavior;
            return this;
        }

        public Task<E> build(Map<MemoryModuleType<?>, MemoryModuleStatus> map) {
            return build(map, 60);
        };

        public Task<E> build(Map<MemoryModuleType<?>, MemoryModuleStatus> map, int n) {
            return build(map, n, n);
        };

        public Task<E> build(Map<MemoryModuleType<?>, MemoryModuleStatus> map, int min, int max) {
            return new Task<E>(map, min, max) {

                @Override
                protected void start(ServerWorld world, E entity, long time) {
                    startBehavior.start(world, entity, time);
                }

                @Override
                protected void tick(ServerWorld world, E entity, long time) {
                    tickBehavior.tick(world, entity, time);
                }

                @Override
                protected void stop(ServerWorld world, E entity, long time) {
                    stopBehavior.stop(world, entity, time);
                }

                @Override
                protected boolean canStillUse(ServerWorld world, E entity, long time) {
                    return canStillUseBehavior.canStillUse(world, entity, time);
                }

                @Override
                protected boolean timedOut(long time) {
                    if (timedOutBehavior == null) {
                        return super.timedOut(time);
                    }

                    return timedOutBehavior.timedOut(time);
                }

                @Override
                protected boolean checkExtraStartConditions(ServerWorld world, E entity) {
                    return extraStartConditionsBehavior.checkExtraStartConditions(world, entity);
                }
            };
        }
    }
}
