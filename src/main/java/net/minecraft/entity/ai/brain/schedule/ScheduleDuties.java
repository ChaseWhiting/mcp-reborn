package net.minecraft.entity.ai.brain.schedule;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import java.util.List;

public class ScheduleDuties {
   private final List<DutyTime> keyframes = Lists.newArrayList();
   private int previousIndex;

   public ScheduleDuties addKeyframe(int a, float b) {
      this.keyframes.add(new DutyTime(a, b));
      this.sortAndDeduplicateKeyframes();
      return this;
   }

   private void sortAndDeduplicateKeyframes() {
      Int2ObjectSortedMap<DutyTime> int2objectsortedmap = new Int2ObjectAVLTreeMap<>();
      this.keyframes.forEach((dutyTime) -> {
         DutyTime dutytime = int2objectsortedmap.put(dutyTime.getTimeStamp(), dutyTime);
      });
      this.keyframes.clear();
      this.keyframes.addAll(int2objectsortedmap.values());
      this.previousIndex = 0;
   }

   public float getValueAt(int a) {
      if (this.keyframes.size() <= 0) {
         return 0.0F;
      } else {
         DutyTime dutytime = this.keyframes.get(this.previousIndex);
         DutyTime dutytime1 = this.keyframes.get(this.keyframes.size() - 1);
         boolean flag = a < dutytime.getTimeStamp();
         int i = flag ? 0 : this.previousIndex;
         float f = flag ? dutytime1.getValue() : dutytime.getValue();

         for(int j = i; j < this.keyframes.size(); ++j) {
            DutyTime dutytime2 = this.keyframes.get(j);
            if (dutytime2.getTimeStamp() > a) {
               break;
            }

            this.previousIndex = j;
            f = dutytime2.getValue();
         }

         return f;
      }
   }
}