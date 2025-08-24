package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fallout.Addiction;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MedXItem extends Chem {
    public static MedXItem instance = new MedXItem(Chem.properties);

    public MedXItem(Properties properties) {
        super(properties, 90 * 20, Chem.MED_X_EFFECTS, 0.1F, Addiction.Addictions.MED_X_ADDICTION);
    }


    @Override
    void onChemUsed(World world, PlayerEntity player, Hand hand) {

    }

    @Override
    void becomeAddicted(World world, PlayerEntity player) {

    }

}