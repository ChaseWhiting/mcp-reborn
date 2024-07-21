package net.minecraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;

import java.util.concurrent.ThreadLocalRandom;

public class RandomEntityCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("randomentity")
                        .requires(source -> source.hasPermission(3))
                        .executes(context -> randomEntity(context.getSource()))
        );
    }
    
    private static int randomEntity(CommandSource source) {
        try {
            ServerPlayerEntity player = source.getPlayerOrException();
            EntityType<?> entityType = null;
            int attempts = 0;
            int maxAttempts = 5; // Adjust this number as needed

            while (entityType == null && attempts < maxAttempts) {
                entityType = Registry.ENTITY_TYPE.getRandom(ThreadLocalRandom.current());
                attempts++;
            }

            if (entityType == null) {
                throw new IllegalStateException("Unable to retrieve a valid EntityType after " + maxAttempts + " attempts.");
            }
            Entity entity = entityType.create(source.getLevel());
            if(entity != null) {
                entity.setPos(player.blockPosition().offset(0.5, 1, 0.5));
                if(entity instanceof Mob)
                    ((Mob)entity).finalizeSpawn(source.getLevel(), source.getLevel().getCurrentDifficultyAt(player.blockPosition()), SpawnReason.NATURAL, null, null);
                source.getLevel().addFreshEntity(entity);
            } else {
                source.sendFailure(new StringTextComponent("Failed to get a random entity."));
                return 0;
            }
        } catch (CommandSyntaxException e) {
            source.sendFailure(new StringTextComponent("No player found."));
            return 0;
        }

        return 1;
    }
}
