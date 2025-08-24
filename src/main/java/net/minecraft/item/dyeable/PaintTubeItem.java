package net.minecraft.item.dyeable;

import com.google.common.collect.Maps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaintTubeItem extends Item implements IDyeSource {
    private static final Map<DyeColor, PaintTubeItem> ITEM_BY_COLOR = Maps.newEnumMap(DyeColor.class);
    public static List<PaintTubeItem> PAINT_TUBES = new ArrayList<>();

    public DyeColor getDyeColor() {
        return dyeColor;
    }

    private final DyeColor dyeColor;

    public PaintTubeItem(DyeColor colour, Item.Properties properties) {
        super(properties.durability(100));
        this.dyeColor = colour;
        PAINT_TUBES.add(this);
        ITEM_BY_COLOR.put(colour, this);
    }


    @Override
    public boolean consumesDurability() {
        return true;
    }

    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        return interactEntity(stack, player, entity, hand);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        return useOnEntity(context);
    }



}
