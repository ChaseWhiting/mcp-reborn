package net.minecraft.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.Unit;

public interface IResourceManagerReloadListener extends IFutureReloadListener {
   default CompletableFuture<Void> reload(IFutureReloadListener.IStage p_215226_1_, IResourceManager p_215226_2_, IProfiler p_215226_3_, IProfiler p_215226_4_, Executor p_215226_5_, Executor p_215226_6_) {
      return p_215226_1_.wait(Unit.INSTANCE).thenRunAsync(() -> {
         p_215226_4_.startTick();
         p_215226_4_.push("listener");
         this.onResourceManagerReload(p_215226_2_);
         p_215226_4_.pop();
         p_215226_4_.endTick();
      }, p_215226_6_);
   }

   void onResourceManagerReload(IResourceManager p_195410_1_);
}