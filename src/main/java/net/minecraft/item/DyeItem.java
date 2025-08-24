package net.minecraft.item;

import com.google.common.collect.Maps;

import java.util.Map;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.dyeable.IDyeSource;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;

public class DyeItem extends Item implements IDyeSource {
    private static final Map<DyeColor, DyeItem> ITEM_BY_COLOR = Maps.newEnumMap(DyeColor.class);
    private final DyeColor dyeColor;

    public DyeItem(DyeColor colour, Item.Properties properties) {
        super(properties);
        this.dyeColor = colour;
        ITEM_BY_COLOR.put(colour, this);
    }

    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        return interactEntity(stack, player, entity, hand);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        return useOnEntity(context);
    }


    public DyeColor getDyeColor() {
        return this.dyeColor;
    }

    public static DyeItem byColor(DyeColor p_195961_0_) {
        return ITEM_BY_COLOR.get(p_195961_0_);
    }
}