package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fallout.Addiction;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class RadAwayItem extends Chem {
    public static RadAwayItem instance = new RadAwayItem(Chem.properties);

    public RadAwayItem(Properties properties) {
        super(properties, 90, Chem.RADAWAY_EFFECTS, 0.1F, Addiction.Addictions.RAD_X_ADDICTION);
    }


    @Override
    void onChemUsed(World world, PlayerEntity player, Hand hand) {
        player.setRads(player.radiationManager.getRads() - 50);
    }

    @Override
    void becomeAddicted(World world, PlayerEntity player) {

    }
}