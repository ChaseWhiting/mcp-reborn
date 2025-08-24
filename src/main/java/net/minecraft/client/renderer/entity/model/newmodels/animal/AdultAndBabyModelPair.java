package net.minecraft.client.renderer.entity.model.newmodels.animal;

import net.minecraft.client.renderer.model.Model;

public record AdultAndBabyModelPair<T extends Model>(T adultModel, T babyModel) {
    public T getModel(boolean bl) {
        return bl ? this.babyModel : this.adultModel;
    }
}

