package net.minecraft.item.tool.terraria;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.MeowmereProjectileEntity;
import net.minecraft.entity.projectile.StarfuryStarEntity;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class MeowmereItem extends TerrariaSwordItem {

    private static final int MAX_BOUNCES = 4;
    private static final float BOUNCE_DAMAGE_MULTIPLIER = 1.25f;
    
    public MeowmereItem() {
        super(ItemTier.MEOWMERE, 15, -1.6F, ItemTier.MEOWMERE.getUses(), Rarity.REDD);
    }

    @Override
    public float getKnockbackPower() {
        return 0;
    }

    @Override
    public float getVerticalKnockbackPower() {
        return 0;
    }

    @Override
    public double getMaxKnockPower() {
        return 0;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        // Cooldown check and sound
        if (!player.getCooldowns().isOnCooldown(this)) {
            player.getCooldowns().addCooldown(this, 14);
          //  world.playSound(player, player.blockPosition(), SoundEvents.MEOWMERE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
            
            // Spawn cat head projectile
            Vector3d lookDir = player.getLookAngle().normalize().scale(1.2);
            MeowmereProjectileEntity catProjectile = new MeowmereProjectileEntity(world, player, lookDir);
            catProjectile.setOwner(player);
            world.addFreshEntity(catProjectile);
            
            itemStack.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(hand));
        }
        return ActionResult.success(itemStack);
    }

    @Override
    public void appendText(ItemStack sword, World level, List<ITextComponent> flags, ITooltipFlag tooltip) {

    }

    @Override
    public double getMaxRayTraceDistance() {
        return 0;
    }
}
