package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Mob;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class FleeCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("flee")
                        .requires(source -> source.hasPermission(3))
                        .then(Commands.argument("speed", FloatArgumentType.floatArg(0.1F, 40F))
                                .then(Commands.argument("radius", IntegerArgumentType.integer(0, 200))
                                        .executes(context -> MobFlee(context.getSource(),
                                                FloatArgumentType.getFloat(context, "speed"),
                                                IntegerArgumentType.getInteger(context, "radius")))))


        );


    }

    private static int MobFlee(CommandSource source, float speed, int radius) {
        ServerWorld world = source.getLevel();
        int count = 0;
        try {
            BlockPos pos = source.getEntityOrException().blockPosition();
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos).inflate(radius);
            List<Mob> mobs = world.getEntitiesOfClass(Mob.class, axisAlignedBB);
            if (!mobs.isEmpty()) {
                if (mobs.size() > 100) {
                    limitFlee(mobs, source, speed, radius);
                } else {
                    flee(mobs, source, speed, radius);
                }
            } else {
                source.sendFailure(new StringTextComponent("No mobs within the specified radius."));
            }
        } catch (CommandSyntaxException e) {
            source.sendFailure(new StringTextComponent("No player found."));
            return 0;
        }

        return 1;
    }

    private static int flee(List<Mob> mobs, CommandSource source, float speed, int radius) {
        ServerWorld world = source.getLevel();
        int count = 0;
        for (Mob mob : mobs) {
            ++count;
            double X = mob.getRandomX(40D);
            double Z = mob.getRandomZ(40D);
            int Y = world.getHeight(Heightmap.Type.WORLD_SURFACE, (int) X, (int) Z);
            mob.getNavigation().moveTo(X, Y, Z, speed);
        }
        source.sendSuccess(new StringTextComponent("Made " + count + " mobs flee."), false);
        return 1;
    }

    private static int limitFlee(List<Mob> mobs, CommandSource source, float speed, int radius) {
        while (mobs.size() > 100) {
            mobs.remove(0);
        }
        flee(mobs, source, speed, radius);
        source.sendFailure(new StringTextComponent("Too many mobs. Limiting amount."));
        return 1;
    }
}
