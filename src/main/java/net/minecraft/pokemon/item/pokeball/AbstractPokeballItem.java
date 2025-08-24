package net.minecraft.pokemon.item.pokeball;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PokeballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pokemon.item.pokeball.data.PokeballData;
import net.minecraft.pokemon.item.pokeball.data.PokeballType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.UUID;

public abstract class AbstractPokeballItem extends Item {

    private final PokeballType type;

    public AbstractPokeballItem(final PokeballType type) {
        super(new Properties().stacksTo(64));
        this.type = type;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        PokeballData data = PokeballData.load(stack.getOrCreateTag(), world);

        if (!world.isClientSide) {
            PokeballEntity pokeball = new PokeballEntity(EntityType.POKEBALL, world, data);
            pokeball.setPos(player.getX(), player.getEyeY() - 0.1F, player.getZ());
            pokeball.shootFromRotation(player, player.xRot, player.yRot, 0.0f, 0.7f, 1.0f);
            pokeball.setOwner(player);
            world.addFreshEntity(pokeball);
        }
        player.swing(hand);
        return ActionResult.sidedSuccess(stack, world.isClientSide());
    }

    /**
     * Stores Pokémon data in the ItemStack.
     */
    public static void addPokemonToBall(ItemStack stack, LivingEntity entity, UUID trainerID) {
        CompoundNBT nbt = stack.getOrCreateTag();
        PokeballType type = PokeballType.from(nbt.getString("Type"));

        if (type == null) {
            return;
        }

        PokeballData data = new PokeballData(type, entity, trainerID);
        nbt.merge(data.save());
        stack.setTag(nbt);
    }

    /**
     * Loads Pokémon data from the ItemStack.
     */
    public static PokeballData getPokeballData(ItemStack stack, World world) {
        return PokeballData.load(stack.getOrCreateTag(), world);
    }
}
