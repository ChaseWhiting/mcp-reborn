package net.minecraft.terraria.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import net.minecraft.bundle.SlotAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.inventory.container.ClickAction;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.stats.Stats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TreasureBagItem extends Item {
    private static final Map<EntityType<?>, TreasureBagItem> BY_ID = Maps.newIdentityHashMap();
    private final EntityType<?> entity;
    private final DropTable dropTable;

    public TreasureBagItem(EntityType<?> entity, Consumer<DropTable> dropSetup) {
        super(new Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE).tab(ItemGroup.TAB_COMBAT).weight(12));
        this.entity = entity;
        this.dropTable = new DropTable();
        dropSetup.accept(this.dropTable);
        BY_ID.put(entity, this);
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public static TreasureBagItem byId(@Nullable EntityType<?> p_200889_0_) {
        return BY_ID.get(p_200889_0_);
    }

    public static Iterable<TreasureBagItem> bags() {
        return Iterables.unmodifiableIterable(BY_ID.values());
    }

    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack incomingStack, Slot slot, ClickAction clickAction, PlayerEntity player, SlotAccess slotAccess, ClickType clickType) {
        if (!incomingStack.isEmpty() || clickAction == ClickAction.PRIMARY) return false;

        List<ItemStack> drops = openBag(player);
        stack.shrink(1);
        player.awardStat(Stats.ITEM_USED.get(this), 1);
        for (ItemStack stacks : drops) {
            player.addOrDrop(stacks);
        }
        player.inventory.setChanged();
        return true;
    }

    public List<ItemStack> openBag(PlayerEntity player) {
        return dropTable.generateDrops(player);
    }
}
