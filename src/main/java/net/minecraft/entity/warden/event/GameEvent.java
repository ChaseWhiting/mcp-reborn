package net.minecraft.entity.warden.event;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.VisibleForTesting;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class GameEvent {
    public static final List<Block> DAMPENS_VIBRATIONS = List.of
            (Blocks.WHITE_WOOL, Blocks.ORANGE_WOOL,
                    Blocks.MAGENTA_WOOL, Blocks.LIGHT_BLUE_WOOL, Blocks.YELLOW_WOOL,
                    Blocks.LIME_WOOL, Blocks.PINK_WOOL, Blocks.GRAY_WOOL, Blocks.LIGHT_GRAY_WOOL,
                    Blocks.CYAN_WOOL, Blocks.PURPLE_WOOL, Blocks.BLUE_WOOL, Blocks.BROWN_WOOL, Blocks.GREEN_WOOL,
                    Blocks.RED_WOOL, Blocks.BLACK_WOOL,
                    Blocks.WHITE_CARPET, Blocks.ORANGE_CARPET, Blocks.MAGENTA_CARPET, Blocks.LIGHT_BLUE_CARPET, Blocks.YELLOW_CARPET, Blocks.LIME_CARPET, Blocks.PINK_CARPET, Blocks.GRAY_CARPET, Blocks.LIGHT_GRAY_CARPET, Blocks.CYAN_CARPET, Blocks.PURPLE_CARPET, Blocks.BLUE_CARPET, Blocks.BROWN_CARPET, Blocks.GREEN_CARPET, Blocks.RED_CARPET, Blocks.BLACK_CARPET);

    public static final List<Block> OCCLUDES_VIBRATION_SIGNALS = List.of
            (Blocks.WHITE_WOOL, Blocks.ORANGE_WOOL,
                    Blocks.MAGENTA_WOOL, Blocks.LIGHT_BLUE_WOOL, Blocks.YELLOW_WOOL,
                    Blocks.LIME_WOOL, Blocks.PINK_WOOL, Blocks.GRAY_WOOL, Blocks.LIGHT_GRAY_WOOL,
                    Blocks.CYAN_WOOL, Blocks.PURPLE_WOOL, Blocks.BLUE_WOOL, Blocks.BROWN_WOOL, Blocks.GREEN_WOOL,
                    Blocks.RED_WOOL, Blocks.BLACK_WOOL,
                    Blocks.WHITE_CARPET, Blocks.ORANGE_CARPET, Blocks.MAGENTA_CARPET, Blocks.LIGHT_BLUE_CARPET, Blocks.YELLOW_CARPET, Blocks.LIME_CARPET, Blocks.PINK_CARPET, Blocks.GRAY_CARPET, Blocks.LIGHT_GRAY_CARPET, Blocks.CYAN_CARPET, Blocks.PURPLE_CARPET, Blocks.BLUE_CARPET, Blocks.BROWN_CARPET, Blocks.GREEN_CARPET, Blocks.RED_CARPET, Blocks.BLACK_CARPET);


    public static final GameEvent BLOCK_ACTIVATE = GameEvent.register("block_activate");
    public static final GameEvent BLOCK_ATTACH = GameEvent.register("block_attach");
    public static final GameEvent BLOCK_CHANGE = GameEvent.register("block_change");
    public static final GameEvent BLOCK_CLOSE = GameEvent.register("block_close");
    public static final GameEvent BLOCK_DEACTIVATE = GameEvent.register("block_deactivate");
    public static final GameEvent BLOCK_DESTROY = GameEvent.register("block_destroy");
    public static final GameEvent BLOCK_DETACH = GameEvent.register("block_detach");
    public static final GameEvent BLOCK_OPEN = GameEvent.register("block_open");
    public static final GameEvent BLOCK_PLACE = GameEvent.register("block_place");
    public static final GameEvent CONTAINER_CLOSE = GameEvent.register("container_close");
    public static final GameEvent CONTAINER_OPEN = GameEvent.register("container_open");
    public static final GameEvent DISPENSE_FAIL = GameEvent.register("dispense_fail");
    public static final GameEvent DRINK = GameEvent.register("drink");
    public static final GameEvent EAT = GameEvent.register("eat");
    public static final GameEvent ELYTRA_GLIDE = GameEvent.register("elytra_glide");
    public static final GameEvent ENTITY_DAMAGE = GameEvent.register("entity_damage");
    public static final GameEvent ENTITY_DIE = GameEvent.register("entity_die");
    public static final GameEvent ENTITY_INTERACT = GameEvent.register("entity_interact");
    public static final GameEvent ENTITY_PLACE = GameEvent.register("entity_place");
    public static final GameEvent ENTITY_ACTION = GameEvent.register("entity_action");
    public static final GameEvent ENTITY_ROAR = GameEvent.register("entity_roar");
    public static final GameEvent ENTITY_SHAKE = GameEvent.register("entity_shake");
    public static final GameEvent EQUIP = GameEvent.register("equip");
    public static final GameEvent EXPLODE = GameEvent.register("explode");
    public static final GameEvent FLAP = GameEvent.register("flap");
    public static final GameEvent FLUID_PICKUP = GameEvent.register("fluid_pickup");
    public static final GameEvent FLUID_PLACE = GameEvent.register("fluid_place");
    public static final GameEvent HIT_GROUND = GameEvent.register("hit_ground");
    public static final GameEvent INSTRUMENT_PLAY = GameEvent.register("instrument_play");
    public static final GameEvent ITEM_INTERACT_FINISH = GameEvent.register("item_interact_finish");
    public static final GameEvent ITEM_INTERACT_START = GameEvent.register("item_interact_start");
    public static final GameEvent JUKEBOX_PLAY = GameEvent.register("jukebox_play", 10);
    public static final GameEvent JUKEBOX_STOP_PLAY = GameEvent.register("jukebox_stop_play", 10);
    public static final GameEvent LIGHTNING_STRIKE = GameEvent.register("lightning_strike");
    public static final GameEvent NOTE_BLOCK_PLAY = GameEvent.register("note_block_play");
    public static final GameEvent PISTON_CONTRACT = GameEvent.register("piston_contract");
    public static final GameEvent PISTON_EXTEND = GameEvent.register("piston_extend");
    public static final GameEvent PRIME_FUSE = GameEvent.register("prime_fuse");
    public static final GameEvent PROJECTILE_LAND = GameEvent.register("projectile_land");
    public static final GameEvent PROJECTILE_SHOOT = GameEvent.register("projectile_shoot");
    public static final GameEvent SCULK_SENSOR_TENDRILS_CLICKING = GameEvent.register("sculk_sensor_tendrils_clicking");
    public static final GameEvent SHEAR = GameEvent.register("shear");
    public static final GameEvent SHRIEK = GameEvent.register("shriek", 32);
    public static final GameEvent SPLASH = GameEvent.register("splash");
    public static final GameEvent STEP = GameEvent.register("step");
    public static final GameEvent SWIM = GameEvent.register("swim");
    public static final GameEvent TELEPORT = GameEvent.register("teleport");
    public static final int DEFAULT_NOTIFICATION_RADIUS = 16;
    private final String name;
    private final int notificationRadius;

    public GameEvent(String string, int n) {
        this.name = string;
        this.notificationRadius = n;
    }

    public String getName() {
        return this.name;
    }

    public int getNotificationRadius() {
        return this.notificationRadius;
    }

    private static GameEvent register(String string) {
        return GameEvent.register(string, 16);
    }

    private static GameEvent register(String string, int n) {
        return Registry.register(Registry.GAME_EVENT, string, new GameEvent(string, n));
    }

    public String toString() {
        return "Game Event{ " + this.name + " , " + this.notificationRadius + "}";
    }


    public boolean is(ImmutableList<GameEvent> events) {
        return events.contains(this);
    }

    public static final class ListenerInfo
    implements Comparable<ListenerInfo> {
        private final GameEvent gameEvent;
        private final Vector3d source;
        private final Context context;
        private final GameEventListener recipient;
        private final double distanceToRecipient;

        public ListenerInfo(GameEvent gameEvent, Vector3d vector3D, Context context, GameEventListener gameEventListener, Vector3d vector32D) {
            this.gameEvent = gameEvent;
            this.source = vector3D;
            this.context = context;
            this.recipient = gameEventListener;
            this.distanceToRecipient = vector3D.distanceToSqr(vector32D);
        }

        @Override
        public int compareTo(ListenerInfo listenerInfo) {
            return Double.compare(this.distanceToRecipient, listenerInfo.distanceToRecipient);
        }

        public GameEvent gameEvent() {
            return this.gameEvent;
        }

        public Vector3d source() {
            return this.source;
        }

        public Context context() {
            return this.context;
        }

        public GameEventListener recipient() {
            return this.recipient;
        }


    }

    public static class Context {

        public Context(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
            this.sourceEntity = sourceEntity;
            this.affectedState = affectedState;
        }
        @Nullable
        public Entity sourceEntity;
        @Nullable
        public BlockState affectedState;

        public static Context of(@Nullable Entity entity) {
            return new Context(entity, null);
        }
        public static Context of(@Nullable BlockState state) {
            return new Context(null, state);
        }
        public static Context of(@Nullable Entity entity, @Nullable BlockState state) {
            return new Context(entity, state);
        }

        public String toString() {
            if (affectedState == null && sourceEntity == null) {
                return "Context:{sourceEntity==null,affectedState==null}";
            }
            if (affectedState == null && sourceEntity != null) {
                return "Context:{sourceEntity==" + sourceEntity.getStringUUID() + ",affectedState==null}";
            }
            if (affectedState != null && sourceEntity == null) {
                String aff = affectedState.toString();
                return "Context:{sourceEntity==null,affectedState==" + aff +"}";
            }
            if (affectedState != null && sourceEntity != null) {
                String aff = affectedState.toString();
                return "Context:{sourceEntity==" + sourceEntity.getStringUUID() + "affectedState==" + aff +"}";
            }
            return super.toString();
        }
    }


    @VisibleForTesting
    public static final GameEvent[] VIBRATIONS_EXCEPT_FLAP = new GameEvent[]{GameEvent.BLOCK_ATTACH, GameEvent.BLOCK_CHANGE, GameEvent.BLOCK_CLOSE, GameEvent.BLOCK_DESTROY, GameEvent.BLOCK_DETACH, GameEvent.BLOCK_OPEN, GameEvent.BLOCK_PLACE, GameEvent.BLOCK_ACTIVATE, GameEvent.BLOCK_DEACTIVATE, GameEvent.CONTAINER_CLOSE, GameEvent.CONTAINER_OPEN, GameEvent.DISPENSE_FAIL, GameEvent.DRINK, GameEvent.EAT, GameEvent.ELYTRA_GLIDE, GameEvent.ENTITY_DAMAGE, GameEvent.ENTITY_DIE, GameEvent.ENTITY_INTERACT, GameEvent.ENTITY_PLACE, GameEvent.ENTITY_ROAR, GameEvent.ENTITY_SHAKE, GameEvent.EQUIP, GameEvent.EXPLODE, GameEvent.FLUID_PICKUP, GameEvent.FLUID_PLACE, GameEvent.HIT_GROUND, GameEvent.INSTRUMENT_PLAY, GameEvent.ITEM_INTERACT_FINISH, GameEvent.LIGHTNING_STRIKE, GameEvent.NOTE_BLOCK_PLAY, GameEvent.PISTON_CONTRACT, GameEvent.PISTON_EXTEND, GameEvent.PRIME_FUSE, GameEvent.PROJECTILE_LAND, GameEvent.PROJECTILE_SHOOT, GameEvent.SHEAR, GameEvent.SPLASH, GameEvent.STEP, GameEvent.SWIM, GameEvent.TELEPORT, GameEvent.ENTITY_ACTION};


    public static final ImmutableList<GameEvent> VIBRATIONS = ImmutableList.<GameEvent>builder().addAll(Arrays.asList(VIBRATIONS_EXCEPT_FLAP)).add(FLAP).build();
    public static final ImmutableList<GameEvent> SHRIEKER_CAN_LISTEN = ImmutableList.of(SCULK_SENSOR_TENDRILS_CLICKING);
    public static final ImmutableList<GameEvent> IGNORE_VIBRATION_SNEAKING = ImmutableList.of(HIT_GROUND, PROJECTILE_SHOOT, STEP, SWIM, ITEM_INTERACT_START, ITEM_INTERACT_FINISH);
    public static final ImmutableList<GameEvent> ALLAY_CAN_LISTEN = ImmutableList.of(NOTE_BLOCK_PLAY);
    public static final ImmutableList<GameEvent> WARDEN_CAN_LISTEN = ImmutableList.<GameEvent>builder().addAll(Arrays.asList(VIBRATIONS_EXCEPT_FLAP)).add(SHRIEK).addAll(SHRIEKER_CAN_LISTEN.asList()).build();

}