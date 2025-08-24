package net.minecraft.entity.ai.brain.declarative;

import com.mojang.datafixers.kinds.*;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Unit;
import groovyjarjarantlr4.v4.runtime.misc.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;
import java.util.function.*;

public class BehaviorBuilder<E extends LivingEntity, M> implements App<BehaviorBuilder.Mu<E>, M> {
    private final TriggerWithResult<E, M> trigger;

    BehaviorBuilder(TriggerWithResult<E, M> triggerWithResult) {
        this.trigger = triggerWithResult;
    }

    static interface TriggerWithResult<E extends LivingEntity, R> {
        @Nullable
        public R tryTrigger(ServerWorld var1, E var2, long var3);

        public String debugString();
    }

    public static <E extends LivingEntity, M> BehaviorBuilder<E, M> unbox(App<Mu<E>, M> app) {
        return (BehaviorBuilder<E, M>)app;
    }

    public static <E extends LivingEntity> Instance<E> instance() {
        return new Instance();
    }

    public static <E extends LivingEntity> OneShot<E> create(Function<Instance<E>, ? extends App<Mu<E>, Trigger<E>>> function) {
        final TriggerWithResult<E, Trigger<E>> triggerWithResult = BehaviorBuilder.get(function.apply(BehaviorBuilder.instance()));
        return new OneShot<E>() {

            public boolean trigger(ServerWorld serverWorld, E e, long time) {
                Trigger trigger1 = (Trigger)triggerWithResult.tryTrigger(serverWorld, e, time);
                if (trigger1 == null) {
                    return false;
                }
                return trigger1.trigger(serverWorld, e, time);
            }

            public String debugString() {
                return "OneShot[" + triggerWithResult.debugString() + "]";
            }

            @Override
            public String toString() {
                return this.debugString();
            }
        };
    }

    static <E extends LivingEntity, M> TriggerWithResult<E, M> get(App<Mu<E>, M> app) {
        return BehaviorBuilder.unbox(app).trigger;
    }



    public static <E extends LivingEntity> OneShot<E> triggerIf(Predicate<E> predicate) {
        return BehaviorBuilder.create((Instance<E> instance) -> instance.point((serverLevel, livingEntity, l) -> predicate.test(livingEntity)));
    }

    public static <E extends LivingEntity> OneShot<E> triggerIf(BiPredicate<ServerWorld, E> biPredicate) {
        return BehaviorBuilder.create((Instance<E> instance) -> instance.point((serverLevel, livingEntity, l) -> biPredicate.test(serverLevel, livingEntity)));
    }

    static <E extends LivingEntity, M> BehaviorBuilder<E, M> create(TriggerWithResult<E, M> triggerWithResult) {
        return new BehaviorBuilder<E, M>(triggerWithResult);
    }


    public static final class Instance<E extends LivingEntity>
            implements Applicative<BehaviorBuilder.Mu<E>, Instance.Mu<E>> {
        public <Value> Optional<Value> tryGet(MemoryAccessor<OptionalBox.Mu, Value> memoryAccessor) {
            return OptionalBox.unbox(memoryAccessor.value());
        }

        public <Value> Value get(MemoryAccessor<IdF.Mu, Value> memoryAccessor) {
            return (Value)IdF.get(memoryAccessor.value());
        }

        public <Value> BehaviorBuilder<E, MemoryAccessor<OptionalBox.Mu, Value>> registered(MemoryModuleType<Value> memoryModuleType) {
            return new PureMemory(new MemoryCondition.Registered<Value>(memoryModuleType));
        }

        public <Value> BehaviorBuilder<E, MemoryAccessor<IdF.Mu, Value>> present(MemoryModuleType<Value> memoryModuleType) {
            return new PureMemory(new MemoryCondition.Present<Value>(memoryModuleType));
        }

        public <Value> BehaviorBuilder<E, MemoryAccessor<Const.Mu<Unit>, Value>> absent(MemoryModuleType<Value> memoryModuleType) {
            return new PureMemory(new MemoryCondition.Absent<Value>(memoryModuleType));
        }

        public BehaviorBuilder<E, Unit> ifTriggered(Trigger<? super E> trigger) {
            return new TriggerWrapper<E>(trigger);
        }

        public <A> BehaviorBuilder<E, A> point(A a) {
            return new Constant(a);
        }

        public <A> BehaviorBuilder<E, A> point(Supplier<String> supplier, A a) {
            return new Constant(a, supplier);
        }

        public <A, R> Function<App<BehaviorBuilder.Mu<E>, A>, App<BehaviorBuilder.Mu<E>, R>> lift1(App<BehaviorBuilder.Mu<E>, Function<A, R>> app) {
            return app2 -> {
                final TriggerWithResult triggerWithResult = BehaviorBuilder.get(app2);
                final TriggerWithResult triggerWithResult2 = BehaviorBuilder.get(app);
                return BehaviorBuilder.create(new TriggerWithResult<E, R>(){

                    @Override
                    public R tryTrigger(ServerWorld serverLevel, E e, long l) {
                        A r = (A) triggerWithResult.tryTrigger(serverLevel, e, l);
                        if (r == null) {
                            return null;
                        }
                        Function<A, R> function = (Function<A, R>) triggerWithResult2.tryTrigger(serverLevel, e, l);
                        if (function == null) {
                            return null;
                        }
                        return function.apply(r);
                    }

                    @Override
                    public String debugString() {
                        return triggerWithResult2.debugString() + " * " + triggerWithResult.debugString();
                    }

                    public String toString() {
                        return this.debugString();
                    }
                });
            };
        }

        public <T, R> BehaviorBuilder<E, R> map(final Function<? super T, ? extends R> function, App<BehaviorBuilder.Mu<E>, T> app) {
            final TriggerWithResult<E, T> triggerWithResult = BehaviorBuilder.get(app);
            return BehaviorBuilder.create(new TriggerWithResult<E, R>(){

                @Override
                public R tryTrigger(ServerWorld serverLevel, E e, long l) {
                    T r = triggerWithResult.tryTrigger(serverLevel, e, l);
                    if (r == null) {
                        return null;
                    }
                    return function.apply(r);
                }

                @Override
                public String debugString() {
                    return triggerWithResult.debugString() + ".map[" + function + "]";
                }

                public String toString() {
                    return this.debugString();
                }
            });
        }

        public <A, B, R> BehaviorBuilder<E, R> ap2(App<BehaviorBuilder.Mu<E>, BiFunction<A, B, R>> app, App<BehaviorBuilder.Mu<E>, A> app2, App<BehaviorBuilder.Mu<E>, B> app3) {
            final TriggerWithResult<E, A> triggerWithResult = BehaviorBuilder.get(app2);
            final TriggerWithResult<E, B> triggerWithResult2 = BehaviorBuilder.get(app3);
            final TriggerWithResult<E, BiFunction<A, B, R>> triggerWithResult3 = BehaviorBuilder.get(app);

            return BehaviorBuilder.create(new TriggerWithResult<E, R>() {

                @Override
                public R tryTrigger(ServerWorld serverLevel, E e, long l) {
                    A r = (A) triggerWithResult.tryTrigger(serverLevel, e, l);
                    if (r == null) {
                        return null;
                    }
                    B r2 = (B) triggerWithResult2.tryTrigger(serverLevel, e, l);
                    if (r2 == null) {
                        return null;
                    }
                    BiFunction<A, B, R> biFunction = (BiFunction<A, B, R>) triggerWithResult3.tryTrigger(serverLevel, e, l);
                    if (biFunction == null) {
                        return null;
                    }
                    return biFunction.apply(r, r2);
                }

                @Override
                public String debugString() {
                    return triggerWithResult3.debugString() + " * " + triggerWithResult.debugString() + " * " + triggerWithResult2.debugString();
                }

                public String toString() {
                    return this.debugString();
                }
            });
        }


        public <T1, T2, T3, R> BehaviorBuilder<E, R> ap3(App<BehaviorBuilder.Mu<E>, Function3<T1, T2, T3, R>> app, App<BehaviorBuilder.Mu<E>, T1> app2, App<BehaviorBuilder.Mu<E>, T2> app3, App<BehaviorBuilder.Mu<E>, T3> app4) {
            final TriggerWithResult<E, T1> triggerWithResult = BehaviorBuilder.get(app2);
            final TriggerWithResult<E, T2> triggerWithResult2 = BehaviorBuilder.get(app3);
            final TriggerWithResult<E, T3> triggerWithResult3 = BehaviorBuilder.get(app4);
            final TriggerWithResult<E, Function3<T1, T2, T3, R>> triggerWithResult4 = BehaviorBuilder.get(app);
            return BehaviorBuilder.create(new TriggerWithResult<E, R>(){

                @Override
                public R tryTrigger(ServerWorld serverLevel, E e, long l) {
                    T1 r = (T1) triggerWithResult.tryTrigger(serverLevel, e, l);
                    if (r == null) {
                        return null;
                    }
                    T2 r2 = (T2) triggerWithResult2.tryTrigger(serverLevel, e, l);
                    if (r2 == null) {
                        return null;
                    }
                    T3 r3 = (T3) triggerWithResult3.tryTrigger(serverLevel, e, l);
                    if (r3 == null) {
                        return null;
                    }
                    Function3<T1, T2, T3, R> function3 = (Function3<T1, T2, T3, R>) triggerWithResult4.tryTrigger(serverLevel, e, l);
                    if (function3 == null) {
                        return null;
                    }
                    return function3.apply(r, r2, r3);
                }

                @Override
                public String debugString() {
                    return triggerWithResult4.debugString() + " * " + triggerWithResult.debugString() + " * " + triggerWithResult2.debugString() + " * " + triggerWithResult3.debugString();
                }

                public String toString() {
                    return this.debugString();
                }
            });
        }

        public <T1, T2, T3, T4, R> BehaviorBuilder<E, R> ap4(App<BehaviorBuilder.Mu<E>, Function4<T1, T2, T3, T4, R>> app, App<BehaviorBuilder.Mu<E>, T1> app2, App<BehaviorBuilder.Mu<E>, T2> app3, App<BehaviorBuilder.Mu<E>, T3> app4, App<BehaviorBuilder.Mu<E>, T4> app5) {
            final TriggerWithResult<E, T1> triggerWithResult = BehaviorBuilder.get(app2);
            final TriggerWithResult<E, T2> triggerWithResult2 = BehaviorBuilder.get(app3);
            final TriggerWithResult<E, T3> triggerWithResult3 = BehaviorBuilder.get(app4);
            final TriggerWithResult<E, T4> triggerWithResult4 = BehaviorBuilder.get(app5);
            final TriggerWithResult<E, Function4<T1, T2, T3, T4, R>> triggerWithResult5 = BehaviorBuilder.get(app);
            return BehaviorBuilder.create(new TriggerWithResult<E, R>(){

                @Override
                public R tryTrigger(ServerWorld serverLevel, E e, long l) {
                    T1 r = (T1) triggerWithResult.tryTrigger(serverLevel, e, l);
                    if (r == null) {
                        return null;
                    }
                    T2 r2 = (T2) triggerWithResult2.tryTrigger(serverLevel, e, l);
                    if (r2 == null) {
                        return null;
                    }
                    T3 r3 = (T3) triggerWithResult3.tryTrigger(serverLevel, e, l);
                    if (r3 == null) {
                        return null;
                    }
                    T4 r4 = (T4) triggerWithResult4.tryTrigger(serverLevel, e, l);
                    if (r4 == null) {
                        return null;
                    }
                    Function4<T1, T2, T3, T4, R> function4 = (Function4<T1, T2, T3, T4, R>) triggerWithResult5.tryTrigger(serverLevel, e, l);
                    if (function4 == null) {
                        return null;
                    }
                    return function4.apply(r, r2, r3, r4);
                }


                @Override
                public String debugString() {
                    return triggerWithResult5.debugString() + " * " + triggerWithResult.debugString() + " * " + triggerWithResult2.debugString() + " * " + triggerWithResult3.debugString() + " * " + triggerWithResult4.debugString();
                }

                public String toString() {
                    return this.debugString();
                }
            });
        }

        static final class Mu<E extends LivingEntity>
                implements Applicative.Mu {
            private Mu() {
            }
        }
    }



    static final class TriggerWrapper<E extends LivingEntity>
            extends BehaviorBuilder<E, Unit> {
        TriggerWrapper(final Trigger<? super E> trigger) {
            super(new TriggerWithResult<E, Unit>(){

                @Override
                @Nullable
                public Unit tryTrigger(ServerWorld serverLevel, E e, long l) {
                    return trigger.trigger(serverLevel, e, l) ? Unit.INSTANCE : null;
                }

                @Override
                public String debugString() {
                    return "T[" + trigger + "]";
                }
            });
        }
    }

    static final class Constant<E extends LivingEntity, A>
            extends BehaviorBuilder<E, A> {
        Constant(A a) {
            this(a, () -> "C[" + a + "]");
        }

        Constant(final A a, final Supplier<String> supplier) {
            super(new TriggerWithResult<E, A>(){

                @Override
                public A tryTrigger(ServerWorld serverLevel, E e, long l) {
                    return a;
                }

                @Override
                public String debugString() {
                    return (String)supplier.get();
                }

                public String toString() {
                    return this.debugString();
                }
            });
        }
    }

    static final class PureMemory<E extends LivingEntity, F extends K1, Value>
            extends BehaviorBuilder<E, MemoryAccessor<F, Value>> {
        PureMemory(final MemoryCondition<F, Value> memoryCondition) {
            super(new TriggerWithResult<E, MemoryAccessor<F, Value>>(){

                @Override
                public MemoryAccessor<F, Value> tryTrigger(ServerWorld serverLevel, E entity, long l) {
                    Brain<?> brain = entity.getBrain();
                    Optional<Value> optional = brain.getMemoryInternal(memoryCondition.memory());

                    if (optional == null) {
                        return null;
                    }

                    // Handle based on memory condition type
                    if (memoryCondition instanceof MemoryCondition.Present) {
                        if (optional.isEmpty()) {
                            return null; // Skip trigger – required memory not present
                        }
                        return memoryCondition.createAccessor(brain, optional);
                    }

                    // For 'registered' or 'absent', we can continue even if empty
                    return memoryCondition.createAccessor(brain, optional);
                }



                @Override
                public String debugString() {
                    return "M[" + memoryCondition + "]";
                }

                public String toString() {
                    return this.debugString();
                }
            });
        }
    }



    public static final class Mu<E extends LivingEntity>
            implements K1 {
    }
}
