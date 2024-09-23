package net.minecraft.client.util;

import net.minecraft.client.main.GroovyScriptLoader;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.server.Scripts;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class ScriptLoader extends ReloadListener<List<String>> {
   private Scripts scripts;

   public ScriptLoader() {
      this.scripts = new Scripts();
   }


   protected List<String> prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
      scripts = new Scripts();
      GroovyScriptLoader.LOGGER.info(GroovyScriptLoader.MARKER, "Starting script load");
       return List.of();
   }

   protected void apply(List<String> p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      scripts.loadAll(false);
   }

}