package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Creature;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.EquipmentSlotType;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

abstract class AbstractFindItemsGoal extends Goal {
        protected final Predicate<ItemEntity> itemFilter;
        protected final Creature mob;

        public AbstractFindItemsGoal(Creature mob, Predicate<ItemEntity> itemFilter) {
            this.itemFilter = itemFilter;
            this.setFlags(EnumSet.of(Flag.MOVE));
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            if (!mob.getItemBySlot(EquipmentSlotType.MAINHAND).isEmpty()) {
                return false;
            } else if (mob.getTarget() == null && mob.getLastHurtByMob() == null) {
                if (mob.getRandom().nextInt(10) != 0) {
                    return false;
                } else {
                    List<ItemEntity> list = findNearbyItems();
                    return !list.isEmpty() && itemFilter.test(list.get(0));
                }
            } else {
                return false;
            }
        }

        @Override
        public void tick() {
            List<ItemEntity> list = findNearbyItems();
            if (!list.isEmpty() && itemFilter.test(list.get(0))) {
                move(list);
            }
        }

        @Override
        public void start() {
            List<ItemEntity> list = findNearbyItems();
            if (!list.isEmpty() && itemFilter.test(list.get(0))) {
                move(list);
            }
        }

        public void move(List<ItemEntity> list) {
            mob.getNavigation().moveTo(list.get(0), getMoveSpeed());
        }

        public List<ItemEntity> findNearbyItems() {
            return mob.level.getEntitiesOfClass(
                    ItemEntity.class,
                    mob.getBoundingBox().inflate(searchDistanceXZ(), searchDistanceY(), searchDistanceXZ()),
                    item -> !item.hasPickUpDelay() && item.isAlive() && itemFilter.test(item)
            );
        }

        public double searchDistanceXZ() {
            return 8.0D;
        }

        public double searchDistanceY() {
            return 8.0D;
        }

        protected abstract float getMoveSpeed();
    }