package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fallout.Addiction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class Chem extends Item {
    public static Item.Properties properties = new Properties().tab(ItemGroup.TAB_FOOD);
    public static EffectInstance[] MED_X_EFFECTS = new EffectInstance[]{new EffectInstance(Effects.DAMAGE_RESISTANCE, 240 * 20, 3)};
    public static EffectInstance[] MENTATS_EFFECTS = new EffectInstance[]{new EffectInstance(Effects.HERO_OF_THE_VILLAGE, 6000, 2), new EffectInstance(Effects.LUCK, 6000, 1), new EffectInstance(Effects.DIG_SPEED, 6000, 0)};
    public static EffectInstance[] RADAWAY_EFFECTS = new EffectInstance[]{};
    public static EffectInstance[] RAD_X_EFFECTS = new EffectInstance[]{};

    private EffectInstance[] effects;
    private int cooldown;
    private float addictionChance;
    private Addiction addiction;

    public Chem(Properties properties, int cooldown, @Nullable EffectInstance[] effects, float addictionChance, Addiction addiction) {
        super(properties);
        this.cooldown = cooldown;
        this.effects = effects;
        this.addictionChance = addictionChance;
        this.addiction = addiction;
    }

    public boolean hasNegativeEffect() {
        return false;
    }

    public EffectInstance[] getEffects() {
        return effects != null ? effects : new EffectInstance[]{};
    }

    public float getAddictionChance() {
        return addictionChance;
    }

    abstract void onChemUsed(World world, PlayerEntity player, Hand hand);

    abstract void becomeAddicted(World world, PlayerEntity player);


    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);


            if (effects != null) {
                for (EffectInstance effect : effects) {
                    player.addEffect(effect);
                }
            }

            if (!player.isCreative()) {
                itemstack.shrink(1);
                player.getCooldowns().addCooldown(this, cooldown);
            }

        onChemUsed(world, player, hand);
        if (player.level.random.nextFloat() < this.getAddictionChance() && !player.addictions.hasAddiction(this.addiction)) {
            if(player.level.isClientSide)
                player.displayClientMessage(new StringTextComponent("You have become addicted to " + itemstack.getItem().getName(itemstack).getString()), true);

            if(player.level.isClientSide)
                player.addictions.addAddiction(this.addiction);

            becomeAddicted(world, player);
        }

        return ActionResult.consume(itemstack);
    }


    public int getUseDuration(ItemStack item) {
        return 1;
    }


}