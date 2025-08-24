package net.minecraft.entity.axolotl;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.declarative.TaskBuilder;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class PlayDead {


    public static <T extends AxolotlEntity> Task<T> playDead() {
        TaskBuilder.Builder<T> builder = new TaskBuilder.Builder<T>().createNewBuilder();

        return builder.extraStartConditionsBehavior((level, axolotl) -> {
            return axolotl.isInWater();
        }).canStillUseBehavior((level, axolotl, time) -> {
            return axolotl.isInWater() && axolotl.getBrain().hasMemoryValue(MemoryModuleType.PLAY_DEAD_TICKS);
        }).startBehavior((level, axolotl, time) -> {
            Brain<AxolotlEntity> brain = (Brain<AxolotlEntity>) axolotl.getBrain();
            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
            brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
            axolotl.addEffect(new EffectInstance(Effects.REGENERATION, 200, 0));
        }).build(ImmutableMap.of(MemoryModuleType.PLAY_DEAD_TICKS, MemoryModuleStatus.VALUE_PRESENT,
                MemoryModuleType.HURT_BY, MemoryModuleStatus.VALUE_PRESENT), 200);
    }
}
