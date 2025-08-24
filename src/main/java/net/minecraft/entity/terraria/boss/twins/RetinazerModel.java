package net.minecraft.entity.terraria.boss.twins;

import net.minecraft.entity.terraria.monster.demoneye.EyeOfCthulhuModel;

public class RetinazerModel extends EyeOfCthulhuModel<RetinazerEntity> {

    public RetinazerLaserModel getOtherModel() {
        return new RetinazerLaserModel();
    }
}
