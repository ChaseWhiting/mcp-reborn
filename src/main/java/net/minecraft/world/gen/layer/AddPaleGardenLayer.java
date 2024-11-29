package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

public enum AddPaleGardenLayer implements IC1Transformer {
   INSTANCE;

   public int apply(INoiseRandom random, int biomeCheck) {
      return random.nextRandom(35) == 0 && biomeCheck == 29 ? 200 : biomeCheck;
   }
}