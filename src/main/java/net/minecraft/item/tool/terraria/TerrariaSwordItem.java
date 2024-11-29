package net.minecraft.item.tool.terraria;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Rarity;
import net.minecraft.item.tool.SwordItem;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;

public abstract class TerrariaSwordItem extends SwordItem {



    public TerrariaSwordItem(ItemTier tier, int baseDamage, float attackSpeed, int baseDurability, Rarity rarity) {
        super(tier, baseDamage, attackSpeed, new Properties().durability(baseDurability).tab(ItemGroup.TAB_COMBAT).rarity(rarity));
    }

    public abstract float getKnockbackPower();

    public abstract float getVerticalKnockbackPower();

    public abstract double getMaxKnockPower();

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        return super.use(world, player, hand);
    }

    public boolean couldAttack(ItemStack sword, LivingEntity target, LivingEntity mob) {
        return true;
    }

    public boolean hurtEnemy(ItemStack sword, LivingEntity target, LivingEntity mob) {
        if (super.hurtEnemy(sword, target, mob)) {
            knockbackEntity(target, this.getKnockbackPower(), mob.getX() - target.getX(), mob.getZ() - target.getZ());
            return this.couldAttack(sword, target, mob);
        }

        return false;
    }

    private void knockbackEntity(LivingEntity entity, float strength, double xRatio, double zRatio) {
        strength = (float) ((double) strength * (1.0D - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));

        if (strength > 0.0F) {
            entity.hasImpulse = true;
            Vector3d currentMotion = entity.getDeltaMovement();
            Vector3d knockbackVector = (new Vector3d(xRatio, 0.0D, zRatio)).normalize().scale((double) strength);

            double verticalKnockback = getVerticalKnockbackPower() * strength;

            entity.setDeltaMovement(
                    currentMotion.x / 2.0D - knockbackVector.x,
                    Math.min(getMaxKnockPower(), currentMotion.y / 2.0D + verticalKnockback),
                    currentMotion.z / 2.0D - knockbackVector.z
            );
        }
    }

    public abstract void appendText(ItemStack sword, World level, List<ITextComponent> flags, ITooltipFlag tooltip);

    public void appendHoverText(ItemStack sword, World level, List<ITextComponent> flags, ITooltipFlag tooltip) {
        super.appendHoverText(sword, level, flags, tooltip);
        this.appendText(sword, level, flags, tooltip);
    }

    public BlockRayTraceResult getRayTrace(PlayerEntity player) {
        // Define the max distance for the ray trace, e.g., 20 blocks
        double maxDistance = getMaxRayTraceDistance();

        // Get the player's eye position
        Vector3d startVec = new Vector3d(player.getX(), player.getEyeY(), player.getZ());

        // Calculate the direction vector where the player is looking and extend it by maxDistance
        Vector3d lookVec = player.getLookAngle(); // player's looking direction
        Vector3d endVec = startVec.add(lookVec.x * maxDistance, lookVec.y * maxDistance, lookVec.z * maxDistance);

        // Create the RayTraceContext with relevant parameters
        RayTraceContext context = new RayTraceContext(
                startVec,
                endVec,
                RayTraceContext.BlockMode.COLLIDER, // can use COLLIDER or VISUAL if needed
                RayTraceContext.FluidMode.NONE,    // can change based on fluid interactions needed
                player
        );

        // Perform the ray trace

        // Return the result of the ray trace
        return player.level.clip(context);
    }

    public abstract double getMaxRayTraceDistance();

}
