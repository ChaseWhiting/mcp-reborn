package net.minecraft.world.level;


public enum GameEvent {
    BLOCK_DESTROY("block_destroy"),
    BLOCK_BREAKING("block_breaking"),
    BLOCK_BROKEN("block_broken");


    final String name;
    final int hearingDistance;


    GameEvent(int hearingDistance, String id) {
        this.hearingDistance = hearingDistance;
        this.name = id;
    }

    GameEvent(String id) {
        this(16, id);
    }

}
