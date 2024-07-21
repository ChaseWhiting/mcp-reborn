package net.minecraft.util;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mob;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DebugUtils {

    public static boolean isDebugging() {
        return java.lang.management.ManagementFactory.getRuntimeMXBean()
                .getInputArguments().toString().contains("-agentlib:jdwp");
    }

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Sends an error message to the specified player in the world.
     *
     * @param player The player to send the message to.
     * @param world  The world in which the player exists.
     * @param msg    The error message to send.
     */
    public static void sendErrorMessage(ServerPlayerEntity player, World world, String msg) {
        if (world instanceof ServerWorld && player != null) {
            player.sendMessage(new StringTextComponent(msg), player.getUUID());
        }
    }

    /**
     * Finds the local player in the world.
     *
     * @param world The world to search in.
     * @return The local player, or null if not found.
     */
    public static ServerPlayerEntity findLocalPlayer(World world) {
        if (world instanceof ServerWorld) {
            for (ServerPlayerEntity player : ((ServerWorld) world).players()) {
                return player;
            }
        }
        return null;
    }

    /**
     * Gets the inventory of a player/entity and logs it to the debug folder. Used for debugging inventories in commands.
     *
     * @param context The context of the command. Context is the entity to get the inventory from.
     * @return Returns 1 if the command successfully executed; otherwise 0 if there was an error.
     */
    public static int getInventory(CommandContext<CommandSource> context) {

        CommandSource commandSource = context.getSource();
        MinecraftServer minecraftServer = commandSource.getServer();
        Entity entity;

        try {
            entity = EntityArgument.getEntity(context, "target");
        } catch (CommandSyntaxException e) {
            commandSource.sendFailure(new StringTextComponent("Failed to get the entity executing the command."));
            return 0;
        }

        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        String fileName = "inventory-" + entity.getUUID() + "-" + entity.getName().getString() + "-" + timestamp + ".txt";

        try {
            Path debugPath = minecraftServer.getFile("debug").toPath();
            Files.createDirectories(debugPath);
            Path filePath = debugPath.resolve(fileName);

            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(filePath))) {
                if (entity instanceof ServerPlayerEntity) {
                    ServerPlayerEntity player = (ServerPlayerEntity) entity;
                    writer.println("Inventory Contents of " + player.getName().getString() + ":");
                    for (int i = 0; i < player.inventory.getContainerSize(); i++) {
                        ItemStack itemStack = player.inventory.getItem(i);
                        if (!itemStack.isEmpty()) {
                            writer.println("Slot " + i + ": " + itemStack.getCount() + " x " + itemStack.getHoverName().getString());
                        }
                    }
                } else if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    writer.println("Held Items of " + livingEntity.getName().getString() + ":");
                    for (ItemStack itemStack : livingEntity.getHandSlots()) {
                        if (!itemStack.isEmpty()) {
                            writer.println(itemStack.getCount() + " x " + itemStack.getHoverName().getString());
                        }
                    }
                    for (ItemStack itemStack : livingEntity.getArmorSlots()) {
                        if (!itemStack.isEmpty()) {
                            writer.println(itemStack.getCount() + " x " + itemStack.getHoverName().getString());
                        }
                    }
                } else {
                    writer.println("Entity does not have an inventory or held items.");
                }
            }

            commandSource.sendSuccess(new TranslationTextComponent("Inventory saved to: " + fileName), false);
            return 1;
        } catch (IOException ioException) {
            LOGGER.error("Failed to save inventory report", ioException);
            commandSource.sendFailure(new TranslationTextComponent("commands.inventory.reportFailed"));
            return 0;
        }
    }


    /**
     * Logs the contents of the specified player's inventory.
     *
     * @param player The player whose inventory will be logged.
     */
    public static void logPlayerInventory(ServerPlayerEntity player) {
        if (player != null) {
            StringBuilder inventoryContents = new StringBuilder("Player Inventory Contents:\n");
            for (int i = 0; i < player.inventory.getContainerSize(); i++) {
                ItemStack itemStack = player.inventory.getItem(i);
                if (!itemStack.isEmpty()) {
                    inventoryContents.append("Slot ").append(i).append(": ")
                            .append(itemStack.getCount()).append(" x ")
                            .append(itemStack.getHoverName().getString()).append("\n");
                }
            }
            player.sendMessage(new StringTextComponent(inventoryContents.toString()), player.getUUID());
        }
    }


    /**
     * Gets the target position of an entity. Used for debugging entity pathfinding in commands.
     *
     * @param context Command context of the command. Context is the entity to check the target position for.
     * @return Returns 1 if the command successfully executed; otherwise 0 if there was an error.
     * Returns with an error if a player's target position is checked, as players do not have pathfinding.
     */
    public static int getWantedPos(CommandContext<CommandSource> context) {
        CommandSource commandSource = context.getSource();
        World world = commandSource.getLevel();
        Entity entity;

        try {
            entity = EntityArgument.getEntity(context, "target1");
        } catch (CommandSyntaxException e) {
            commandSource.sendFailure(new StringTextComponent("Failed to get the entity executing the command."));
            return 0;
        }

        if (entity instanceof Mob) {
            Mob livingEntity = (Mob) entity;

            BlockPos targetPos = livingEntity.getNavigation().getTargetPos();
            if (targetPos != null) {
                String s = "Target position of " + livingEntity.getName().getString() + " <" + livingEntity.getUUID() + "> " + targetPos.getX() + ", " + targetPos.getY() + ", " + targetPos.getZ();
                String s1 = "/tp @s " + targetPos.getX() + " " + targetPos.getY() + " " + targetPos.getZ();
                ITextComponent itextcomponent = (new StringTextComponent(s))
                        .setStyle(Style.EMPTY.withBold(false)
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Click to copy to clipboard")))
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, s1)));

                commandSource.sendSuccess(new StringTextComponent("").append(itextcomponent), true);
            } else {
                commandSource.sendFailure(new StringTextComponent("Entity has no target position."));
                return 0;
            }
        } else {
            commandSource.sendFailure(new StringTextComponent("Entity is not a mob."));
            return 0;
        }

        return 1;
    }

    public static int setWantedPos(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        int X = IntegerArgumentType.getInteger(context, "x");
        int Y = IntegerArgumentType.getInteger(context, "y");
        int Z = IntegerArgumentType.getInteger(context, "z");
        float speed = FloatArgumentType.getFloat(context, "speed");
        try {
            Entity entity = EntityArgument.getEntity(context, "target");
            if (entity instanceof Mob) {
                Mob mob = (Mob) entity;
                mob.getNavigation().moveTo((double) X, (double) Y, (double) Z, (double) speed);
                mob.getMoveControl().setWantedPosition((double) X, (double) Y, (double) Z, (double) speed);
                String s = new String(X + " " + Y + " " + Z);
                source.sendSuccess(new StringTextComponent("Successfully set wanted position of " + mob.getName().toString() + " to " + s), false);
            } else {
                source.sendFailure(new StringTextComponent("Entity is not a mob."));
            }
        } catch (CommandSyntaxException e) {
            source.sendFailure(new StringTextComponent("No target was specified."));
            return 0;
        }


        return 1;
    }

    /**
     * Gets the target position of the given entity. Returns null if the entity has no path.
     *
     * @param entity The entity to get the target position of.
     * @return The BlockPos of the target position of the entity, if it exists; otherwise null.
     */
    @Nullable
    public static BlockPos getWantedPos(LivingEntity entity) {
        if (entity instanceof Mob) {
            Mob entity1 = (Mob) entity;
            return entity1.getNavigation().getTargetPos();
        }
        return null;
    }

    public static net.minecraft.pathfinding.Path getPath(LivingEntity entity, World world) {
        if (entity instanceof Mob) {
            Mob entity1 = (Mob) entity;

            net.minecraft.pathfinding.Path path = entity1.getNavigation().getPath();

            if (path != null) {
                int nodeCount = path.getNodeCount();
                for (int i = 0; i < nodeCount; i++) {
                    PathPoint node = path.getNode(i);

                    System.out.println("Node " + i + ": (" + node.x + ", " + node.y + ", " + node.z + ")");
                }
            } else {
                System.out.println("No path available for the entity.");
            }
        }
        return null;
    }
}
