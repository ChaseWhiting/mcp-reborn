package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fallout.Addiction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class RadXItem extends Chem {
    public static RadXItem instance = new RadXItem(Chem.properties);

    public RadXItem(Properties properties) {
        super(properties, 30 * 20, Chem.RAD_X_EFFECTS, 0.1F, Addiction.Addictions.RAD_X_ADDICTION);
    }


    @Override
    void onChemUsed(World world, PlayerEntity player, Hand hand) {


        player.radiationManager.addResistanceBuff("RadX", 6000); // Example: lasts for 5 minutes (6000 ticks)

        // Additional logic to handle item consumption, cooldowns, etc.
    }


    @Override
    public boolean hasNegativeEffect() {
        return true;
    }

    @Override
    void becomeAddicted(World world, PlayerEntity player) {

    }

}