package net.minecraft.profiler;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

public class TimeTracker {
   private final LongSupplier realTime;
   private final IntSupplier tickCount;
   private IResultableProfiler profiler = EmptyProfiler.INSTANCE;

   public TimeTracker(LongSupplier realTime, IntSupplier tc) {
      this.realTime = realTime;
      this.tickCount = tc;
   }

   public boolean isEnabled() {
      return this.profiler != EmptyProfiler.INSTANCE;
   }

   public void disable() {
      this.profiler = EmptyProfiler.INSTANCE;
   }

   public void enable() {
      this.profiler = new Profiler(this.realTime, this.tickCount, true);
   }

   public IProfiler getFiller() {
      return this.profiler;
   }

   public IProfileResult getResults() {
      return this.profiler.getResults();
   }
}