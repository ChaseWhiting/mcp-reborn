package net.minecraft.entity;

import net.minecraft.entity.passive.BeeEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface IHates {
    public ArrayList<Class<? extends Entity>> hatedList = new ArrayList<>(List.of());

    public abstract Class<? extends Entity>[] getHated();
}
