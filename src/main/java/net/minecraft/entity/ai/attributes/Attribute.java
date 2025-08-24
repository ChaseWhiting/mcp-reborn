package net.minecraft.entity.ai.attributes;

public class Attribute {
   private final double defaultValue;
   private boolean syncable;
   private final String descriptionId;

   protected Attribute(String name, double defaultValue) {
      this.defaultValue = defaultValue;
      this.descriptionId = name;
   }

   public double getDefaultValue() {
      return this.defaultValue;
   }

   public boolean isClientSyncable() {
      return this.syncable;
   }

   public Attribute setSyncable(boolean p_233753_1_) {
      this.syncable = p_233753_1_;
      return this;
   }

   public double sanitizeValue(double p_111109_1_) {
      return p_111109_1_;
   }

   public String getDescriptionId() {
      return this.descriptionId;
   }
}