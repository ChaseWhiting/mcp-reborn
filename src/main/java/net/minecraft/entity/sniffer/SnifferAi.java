package net.minecraft.entity.sniffer;

public class SnifferAi {



//
//
//
//    public static Ingredient getTemptations() {
//        return Ingredient.of(Items.TORCHFLOWER_SEEDS);
//    }
//
//    protected static Brain<?> makeBrain(Brain<Sniffer> brain) {
//        SnifferAi.initCoreActivity(brain);
//        SnifferAi.initIdleActivity(brain);
//        SnifferAi.initSniffingActivity(brain);
//        SnifferAi.initDigActivity(brain);
//        brain.setCoreActivities(Set.of(Activity.CORE));
//        brain.setDefaultActivity(Activity.IDLE);
//        brain.useDefaultActivity();
//        return brain;
//    }
//
//    static Sniffer resetSniffing(Sniffer sniffer) {
//        sniffer.getBrain().eraseMemory(MemoryModuleType.SNIFFER_DIGGING);
//        sniffer.getBrain().eraseMemory(MemoryModuleType.SNIFFER_SNIFFING_TARGET);
//        return sniffer.transitionTo(Sniffer.State.IDLING);
//    }
//
//    private static void initCoreActivity(Brain<Sniffer> brain) {
//        brain.addActivity(Activity.CORE, 0, ImmutableList.of(new SwimTask(0.8f), new AnimalPanicTask(2.0f){
//
//            @Override
//            protected void start(ServerWorld serverLevel, Creature pathfinderMob, long l) {
//                SnifferAi.resetSniffing((Sniffer)pathfinderMob);
//                super.start(serverLevel, pathfinderMob, l);
//            }
//
//        }, new MoveToTargetSink(10000, 15000), new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)));
//    }

//    private static void initIdleActivity(Brain<Sniffer> brain) {
//        brain.addActivityWithConditions(Activity.IDLE, ImmutableList.of(Pair.of(0, new RegularBreedTask(EntityType.SNIFFER, 1.0f){
//
//            @Override
//            protected void start(ServerWorld serverLevel, Animal animal, long l) {
//                SnifferAi.resetSniffing((Sniffer)animal);
//                super.start(serverLevel, animal, l);
//            }
//        }), Pair.of(1, new FollowTemptation(livingEntity -> Float.valueOf(1.25f), livingEntity -> livingEntity.isBaby() ? 2.5F : 3.5F){
//
//            @Override
//            protected void start(ServerWorld serverLevel, Mob pathfinderMob, long l) {
//                SnifferAi.resetSniffing((Sniffer)pathfinderMob);
//                super.start(serverLevel, pathfinderMob, l);
//            }
//        }), Pair.of(2, new LookAtTargetSink(45, 90)), Pair.of(3, new FeelingHappy(40, 100)), (Object)Pair.of((Object)4, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new WalkTowardsLookTargetTask(1.0F, 3), 2), Pair.of(new Scenting(40, 80), (Object)1), (Object)Pair.of((Object)new Sniffing(40, 80), (Object)1), (Object)Pair.of(SetEntityLookTarget.create(EntityType.PLAYER, 6.0f), (Object)1), (Object)Pair.of(RandomStroll.stroll(1.0f), (Object)1), (Object)Pair.of((Object)new DoNothing(5, 20), (Object)2))))), Set.of(Pair.of(MemoryModuleType.SNIFFER_DIGGING, (Object)((Object)MemoryStatus.VALUE_ABSENT))));
//    }
}
