def world = helper.getWorld()

def player = helper.getPlayer("ChasePixel_")

def position = player.position().asBlockPos()

println("Hello, ${player.getName().getString()}, your position is: ${position}")
