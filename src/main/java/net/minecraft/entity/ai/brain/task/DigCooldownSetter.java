package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.memory.MemStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.warden.WardenEntity;
import net.minecraft.util.Unit;
import net.minecraft.world.server.ServerWorld;

public class DigCooldownSetter extends Task<WardenEntity> {

   public DigCooldownSetter() {
      super(ImmutableMap.of(MemoryModuleType.DIG_COOLDOWN, MemStatus.REGISTERED));
   }

   protected void start(ServerWorld p_212831_1_, WardenEntity p_212831_2_, long p_212831_3_) {
      p_212831_2_.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200L);
   }
}