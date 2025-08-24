package net.minecraft.item;

import com.google.common.collect.Lists;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.custom.arrow.CustomArrowEntity;
import net.minecraft.entity.projectile.custom.arrow.CustomArrowType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class CustomArrowItem extends ArrowItem {
    CustomArrowType type;
    public CustomArrowItem(Item.Properties properties, CustomArrowType type) {
        super(properties);
        this.type = type;
    }



    public AbstractArrowEntity createArrow(World world, ItemStack stack, LivingEntity entity) {
        CustomArrowEntity arrowentity = new CustomArrowEntity(world, entity);
        arrowentity.setArrowType(this.type);
        if (stack.getItem() == Items.FIREWORK_ARROW) {
            if (stack.getOrCreateTag().contains("EntityTag")) {
                CompoundNBT tag = stack.getOrCreateTag().getCompound("EntityTag");
                if (tag.contains("Firework")) {
                    ItemStack firework = ItemStack.read(tag.getCompound("Firework"));
                    arrowentity.setFireworkStack(firework);
                }
            }
        }
        return arrowentity;
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack arrow, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
        if (this.type != CustomArrowType.FIREWORK) return;
        if (arrow.getOrCreateTag().contains("EntityTag")) {
            CompoundNBT tag = arrow.getOrCreateTag().getCompound("EntityTag");
            if (tag.contains("Firework")) {
                arrow = ItemStack.read(tag.getCompound("Firework"));
            } else {
                return;
            }
        }
        CompoundNBT compoundnbt = arrow.getTagElement("Fireworks");
        if (compoundnbt != null) {
            if (compoundnbt.contains("Flight", 99)) {
                p_77624_3_.add((new TranslationTextComponent("item.minecraft.firework_rocket.flight")).append(" ").append(String.valueOf((int)compoundnbt.getByte("Flight"))).withStyle(TextFormatting.GRAY));
            }

            ListNBT listnbt = compoundnbt.getList("Explosions", 10);
            if (!listnbt.isEmpty()) {
                for(int i = 0; i < listnbt.size(); ++i) {
                    CompoundNBT compoundnbt1 = listnbt.getCompound(i);
                    List<ITextComponent> list = Lists.newArrayList();
                    FireworkStarItem.appendHoverText(compoundnbt1, list);
                    if (!list.isEmpty()) {
                        for(int j = 1; j < list.size(); ++j) {
                            list.set(j, (new StringTextComponent("  ")).append(list.get(j)).withStyle(TextFormatting.GRAY));
                        }

                        p_77624_3_.addAll(list);
                    }
                }
            }

        }
    }
}