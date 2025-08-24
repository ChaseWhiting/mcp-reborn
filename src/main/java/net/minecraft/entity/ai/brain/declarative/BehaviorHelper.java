package net.minecraft.entity.ai.brain.declarative;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.kinds.App;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.camel.CamelEntity;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Function;
import java.util.function.Predicate;

public class BehaviorHelper {





    public static <E extends LivingEntity> NewOneShot<E> create(Function<BehaviorBuilder.Instance<E>, ? extends App<BehaviorBuilder.Mu<E>, Trigger<E>>> function) {
        final BehaviorBuilder.TriggerWithResult<E, Trigger<E>> triggerWithResult = BehaviorBuilder.get(function.apply(BehaviorBuilder.instance()));
        return new NewOneShot<E>(ImmutableMap.of()){

            @Override
            public boolean trigger(ServerWorld serverLevel, E e, long l) {
                Trigger trigger = (Trigger)triggerWithResult.tryTrigger(serverLevel, e, l);
                if (trigger == null) {
                    return false;
                }
                return trigger.trigger(serverLevel, e, l);
            }
        };
    }

    public static <E extends LivingEntity> NewOneShot<E> triggerIf(Predicate<E> predicate) {
        return create((BehaviorBuilder.Instance<E> instance) -> instance.point((serverLevel, livingEntity, time) -> predicate.test((E) livingEntity)));
    }

    public static <E extends LivingEntity> NewOneShot<E> sequence(Trigger<? super E> trigger, Trigger<? super E> trigger2) {
        return create((BehaviorBuilder.Instance<E> instance) ->
                instance.group(instance.ifTriggered(trigger))
                        .apply(instance, unit -> (serverLevel, entity, time) -> trigger2.trigger(serverLevel, entity, time))
        );
    }


    public static <E extends LivingEntity> NewOneShot<E> triggerIf(Predicate<E> predicate, NewOneShot<? super E> oneShot) {
        return sequence(BehaviorBuilder.triggerIf(predicate), oneShot);
    }

    public static <E extends CamelEntity> NewOneShot<E> triggerIfCamel(Predicate<E> predicate, NewOneShot<? super E> oneShot) {
        return sequence(BehaviorBuilder.triggerIf(predicate), oneShot);
    }

}
