import net.minecraft.entity.EntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.entity.player.PlayerEntity

// Assuming 'helper' is an instance of MinecraftHelper passed to this script
def spawnZombieNearPlayer(playerName, xOffset, yOffset, zOffset) {
    // Get the player by name
    def player = helper.getPlayer(playerName)

    if (player == null) {
        println "Player '$playerName' not found."
        return
    }

    // Calculate the spawn position relative to the player
    def spawnPos = new BlockPos(player.getX() + xOffset, player.getY() + yOffset, player.getZ() + zOffset)

    // Create a zombie entity at the calculated position
    def zombie = helper.createEntity(EntityType.ZOMBIE, spawnPos)

    if (zombie != null) {
        // Spawn the zombie in the world
        helper.spawnEntity(zombie)
        println "Zombie spawned near $playerName at $spawnPos"
    } else {
        println "Failed to create the zombie entity."
    }
}

// Example usage
spawnZombieNearPlayer("ChasePixel_", 2, 0, 2)
