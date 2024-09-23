package net.minecraft.util.concurrent;

public class TickTask {
   private final String taskName;

   public TickTask(String taskName) {
      this.taskName = taskName;

   }

   public String getTaskName() {
      return taskName;
   }
}