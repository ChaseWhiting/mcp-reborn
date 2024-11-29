package net.minecraft.entity.monster.creaking;

import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.util.math.GlobalPos;

public class CreakingBrain {

    protected static Brain<?> makeBrain(CreakingEntity creaking, Brain<CreakingEntity> brain) {
        brain.setDefaultActivity(Activity.IDLE);


        return brain;
    }

    protected static void memories(CreakingEntity creaking) {
        GlobalPos pos = GlobalPos.of(creaking.level.dimension(), creaking.blockPosition());
        creaking.getBrain().setMemory(MemoryModuleType.HOME, pos);
    }
}
