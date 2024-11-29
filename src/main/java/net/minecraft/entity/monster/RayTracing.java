package net.minecraft.entity.monster;

import net.minecraft.entity.Creature;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Mob;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class RayTracing
extends Creature {
    private static final String RAYS_NAME = "Ray Tracing";
    private static final List<String> RAYS_DEATH_LINES = Stream.of("That was just a warm-up, next time I'll be ready!", "I got caught off-guard. I won't make that mistake again", "It's not my fault, the lag made me miss my jump!", "I was distracted by that beautiful sunset", "I was just testing to see how much fall damage I could survive", "I was trying to show off my parkour skills, but it didn't go as planned", "I thought I had enough food to survive, but apparently not", "I underestimated the strength of those zombies", "I was practicing my speedrun strats and got a bit carried away", "I was trying to get a better view and accidentally fell").collect(Collectors.toList());
    private static final List<String> RAYS_RANDOM_LINES = Stream.of("I just found diamonds! Wait, no, it's just coal. Again.", "I'm a master builder. I built a dirt house once", "Creepers? Never heard of 'em", "I could've sworn I put that torch there", "If at first you don't succeed, dig straight down", "I'm not lost, I'm just exploring", "Is it just me, or do those cows have judgmental eyes?", "I've never been to the Nether, but I hear it's a nice place to vacation", "I heard if you punch trees long enough, they turn into diamonds", "Sometimes I feel like the only thing I'm good at is dying in lava", "I'm not lost, I'm just temporarily misplaced", "I like my Minecraft like I like my coffee: with extra sugar cubes", "I heard that if you stare at a pig long enough, it will give you its best pork chop", "I'm convinced that creepers were invented by the developers just to mess with us", "Why build a fancy castle when you can watch a video of someone else building one?").collect(Collectors.toList());
    private static final List<String> RAYS_INTRO_LINES = Stream.of("Did someone say cake? I'm here for the cake!", "Greetings, fellow Minecrafters! Let's build some amazing things together", "I come bearing gifts... of dirt. Lots and lots of dirt", "I hope everyone is ready for some serious block-placing action!", "I don't always play Minecraft, but when I do, I prefer to play with awesome people like you", "I heard there was a party happening here. Did I miss the memo?", "Hey everyone, can I join your Minecraft book club?", "I'm not saying I'm the best Minecraft player, but I did once build a castle out of wool", "I'm here to mine some blocks and chew bubblegum... and I'm all out of bubblegum", "Hello, is this the Minecraft support group? I think I'm addicted").collect(Collectors.toList());
    private static final List<String> RAYS_OUTRO_LINES = Stream.of("I have to go take care of my real-life sheep. See you all later!", "My mom is calling me for dinner. Gotta run!", "Sorry guys, I have a meeting with the Ender Dragon. It's urgent.", "I have to go put out a fire... in the real world. Bye!", "I'm sorry, I have to go study for my Minecraft finals", "My boss just messaged me. Apparently, there's a creeper invasion at work. Gotta go!", "I have to go feed my pet slime. They get cranky if I don't feed them on time", "I need to take a break. Bye everyone!", "I promised my little sibling I would play Minecraft with them. Time to go fulfill that promise!", "I'm sorry, but I have to go save the world from the zombie apocalypse. Wish me luck!").collect(Collectors.toList());
    private static final StringTextComponent FRENCH = new StringTextComponent("Omelette du fromage");
    public boolean firstJoin = true;
    private long nextLine;
    public static boolean RAY_TRACING = true;
    public static boolean FRENCH_MODE = false;

    public RayTracing(EntityType<RayTracing> $$0, World $$1) {
        super($$0, $$1);
        this.nextLine = $$1.getGameTime() + (long)MathHelper.nextInt($$1.random, 80, 600);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<ZombieEntity>(this, ZombieEntity.class, 8.0f, 1.0, 1.0));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<EvokerEntity>(this, EvokerEntity.class, 12.0f, 1.0, 1.0));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<VindicatorEntity>(this, VindicatorEntity.class, 8.0f, 1.0, 1.0));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<VexEntity>(this, VexEntity.class, 8.0f, 1.0, 1.0));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<PillagerEntity>(this, PillagerEntity.class, 15.0f, 1.0, 1.0));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<IllusionerEntity>(this, IllusionerEntity.class, 12.0f, 1.0, 1.0));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<ZoglinEntity>(this, ZoglinEntity.class, 10.0f, 1.0, 1.0));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.0));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.0, Ingredient.of(Items.DIAMOND), false));
        this.goalSelector.addGoal(2, new MoveTowardsRestrictionGoal(this, 1.0));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 1.0));
        this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(3, new LookAtGoal(this, Mob.class, 16.0f));
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.MOVEMENT_SPEED, 0.23f);
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.PLAYER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }

    @Override
    public void tick() {
        if (this.nextLine <= this.level.getGameTime()) {
            this.nextLine = this.level.getGameTime() + (long) MathHelper.nextInt(level.random, 600, 3600);
            if (this.firstJoin) {
                this.sayIntro();
                this.firstJoin = false;
            } else if (!RAY_TRACING) {
                World level;
                this.sayOutro();
                this.remove();
                if (!this.level.isClientSide && (level = this.level) instanceof ServerWorld) {
                    ServerWorld $$0 = (ServerWorld) level;
                    $$0.getServer().getPlayerList().broadcastMessage(new TranslationTextComponent("multiplayer.player.left", RAYS_NAME).withStyle(TextFormatting.YELLOW), ChatType.CHAT, Util.NIL_UUID);
                }
            } else {
                if (this.level instanceof ServerWorld) {
                    StringTextComponent component = new StringTextComponent(RAYS_RANDOM_LINES.get(this.random.nextInt(RAYS_RANDOM_LINES.size())));
                    ITextComponent itextcomponent = new TranslationTextComponent("chat.type.text", RAYS_NAME, component);
                    this.level.getServer().getPlayerList().broadcastMessage(itextcomponent, ChatType.CHAT, Util.NIL_UUID);
                }
            }
        }
        super.tick();
    }

    @Override
    protected void tickDeath() {
        World level;
        super.tickDeath();
        if (this.deathTime >= 20 && !this.level.isClientSide() && (level = this.level) instanceof ServerWorld) {
            ServerWorld $$0 = (ServerWorld)level;
            $$0.addRayTracing();
            StringTextComponent component = new StringTextComponent(RAYS_DEATH_LINES.get(this.random.nextInt(RAYS_DEATH_LINES.size())));
            ITextComponent itextcomponent = new TranslationTextComponent("chat.type.text", RAYS_NAME, component);

            this.say(new StringTextComponent(itextcomponent.getString()));
        }
    }

    @Override
    public void die(DamageSource $$0) {
        World level;
        if (!this.level.isClientSide && (level = this.level) instanceof ServerWorld) {
            ServerWorld $$1 = (ServerWorld)level;
            this.deathMessage($$1.getServer().getPlayerList());
        }
        super.die($$0);
    }

    private void deathMessage(PlayerList $$0) {
        boolean $$1 = this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);
        if ($$1) {
            $$0.broadcastMessage(this.getCombatTracker().getDeathMessage(), ChatType.CHAT,  Util.NIL_UUID);
        }
    }

    public void say(StringTextComponent $$0) {
        World level;
        if (!this.level.isClientSide() && (level = this.level) instanceof ServerWorld) {
            ServerWorld $$1 = (ServerWorld)level;
            if (FRENCH_MODE) {
                $$0 = FRENCH;
            }
            $$1.getServer().getPlayerList().broadcastMessage($$0, ChatType.CHAT, Util.NIL_UUID);
        }
    }

    public void sayIntro() {

        StringTextComponent component = new StringTextComponent(RAYS_INTRO_LINES.get(this.random.nextInt(RAYS_INTRO_LINES.size())));
        ITextComponent itextcomponent = new TranslationTextComponent("chat.type.text", RAYS_NAME, component);

        this.say(new StringTextComponent(itextcomponent.getString()));
    }

    public void sayOutro() {
        StringTextComponent component = new StringTextComponent(RAYS_OUTRO_LINES.get(this.random.nextInt(RAYS_OUTRO_LINES.size())));
        ITextComponent itextcomponent = new TranslationTextComponent("chat.type.text", RAYS_NAME, component);

        this.say(new StringTextComponent(itextcomponent.getString()));
    }
}
