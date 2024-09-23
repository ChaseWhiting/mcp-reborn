package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fallout.Addiction;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MentatsItem extends Chem {
    public static MentatsItem instance = new MentatsItem(Chem.properties);

    public MentatsItem(Properties properties) {
        super(properties, 45 * 20, Chem.MENTATS_EFFECTS, 0.1F, Addiction.Addictions.MENTATS_ADDICTION);
    }


    @Override
    void onChemUsed(World world, PlayerEntity player, Hand hand) {

    }


    @Override
    void becomeAddicted(World world, PlayerEntity player) {

    }

}