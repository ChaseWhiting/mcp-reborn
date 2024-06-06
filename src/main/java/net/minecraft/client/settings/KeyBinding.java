package net.minecraft.client.settings;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyBinding implements Comparable<KeyBinding> {
   private static final Map<String, KeyBinding> ALL = Maps.newHashMap();
   private static final Map<InputMappings.Input, KeyBinding> MAP = Maps.newHashMap();
   private static final Set<String> CATEGORIES = Sets.newHashSet();
   private static final Map<String, Integer> CATEGORY_SORT_ORDER = Util.make(Maps.newHashMap(), (p_205215_0_) -> {
      p_205215_0_.put("key.categories.movement", 1);
      p_205215_0_.put("key.categories.gameplay", 2);
      p_205215_0_.put("key.categories.inventory", 3);
      p_205215_0_.put("key.categories.creative", 4);
      p_205215_0_.put("key.categories.multiplayer", 5);
      p_205215_0_.put("key.categories.ui", 6);
      p_205215_0_.put("key.categories.misc", 7);
   });
   private final String name;
   private final InputMappings.Input defaultKey;
   private final String category;
   private InputMappings.Input key;
   private boolean isDown;
   private int clickCount;

   public static void click(InputMappings.Input p_197981_0_) {
      KeyBinding keybinding = MAP.get(p_197981_0_);
      if (keybinding != null) {
         ++keybinding.clickCount;
      }

   }

   public static void set(InputMappings.Input p_197980_0_, boolean p_197980_1_) {
      KeyBinding keybinding = MAP.get(p_197980_0_);
      if (keybinding != null) {
         keybinding.setDown(p_197980_1_);
      }

   }

   public static void setAll() {
      for(KeyBinding keybinding : ALL.values()) {
         if (keybinding.key.getType() == InputMappings.Type.KEYSYM && keybinding.key.getValue() != InputMappings.UNKNOWN.getValue()) {
            keybinding.setDown(InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keybinding.key.getValue()));
         }
      }

   }

   public static void releaseAll() {
      for(KeyBinding keybinding : ALL.values()) {
         keybinding.release();
      }

   }

   public static void resetMapping() {
      MAP.clear();

      for(KeyBinding keybinding : ALL.values()) {
         MAP.put(keybinding.key, keybinding);
      }

   }

   public KeyBinding(String p_i45001_1_, int p_i45001_2_, String p_i45001_3_) {
      this(p_i45001_1_, InputMappings.Type.KEYSYM, p_i45001_2_, p_i45001_3_);
   }

   public KeyBinding(String p_i47675_1_, InputMappings.Type p_i47675_2_, int p_i47675_3_, String p_i47675_4_) {
      this.name = p_i47675_1_;
      this.key = p_i47675_2_.getOrCreate(p_i47675_3_);
      this.defaultKey = this.key;
      this.category = p_i47675_4_;
      ALL.put(p_i47675_1_, this);
      MAP.put(this.key, this);
      CATEGORIES.add(p_i47675_4_);
   }

   public boolean isDown() {
      return this.isDown;
   }

   public String getCategory() {
      return this.category;
   }

   public boolean consumeClick() {
      if (this.clickCount == 0) {
         return false;
      } else {
         --this.clickCount;
         return true;
      }
   }

   private void release() {
      this.clickCount = 0;
      this.setDown(false);
   }

   public String getName() {
      return this.name;
   }

   public InputMappings.Input getDefaultKey() {
      return this.defaultKey;
   }

   public void setKey(InputMappings.Input p_197979_1_) {
      this.key = p_197979_1_;
   }

   public int compareTo(KeyBinding p_compareTo_1_) {
      return this.category.equals(p_compareTo_1_.category) ? I18n.get(this.name).compareTo(I18n.get(p_compareTo_1_.name)) : CATEGORY_SORT_ORDER.get(this.category).compareTo(CATEGORY_SORT_ORDER.get(p_compareTo_1_.category));
   }

   public static Supplier<ITextComponent> createNameSupplier(String p_193626_0_) {
      KeyBinding keybinding = ALL.get(p_193626_0_);
      return keybinding == null ? () -> {
         return new TranslationTextComponent(p_193626_0_);
      } : keybinding::getTranslatedKeyMessage;
   }

   public boolean same(KeyBinding p_197983_1_) {
      return this.key.equals(p_197983_1_.key);
   }

   public boolean isUnbound() {
      return this.key.equals(InputMappings.UNKNOWN);
   }

   public boolean matches(int p_197976_1_, int p_197976_2_) {
      if (p_197976_1_ == InputMappings.UNKNOWN.getValue()) {
         return this.key.getType() == InputMappings.Type.SCANCODE && this.key.getValue() == p_197976_2_;
      } else {
         return this.key.getType() == InputMappings.Type.KEYSYM && this.key.getValue() == p_197976_1_;
      }
   }

   public boolean matchesMouse(int p_197984_1_) {
      return this.key.getType() == InputMappings.Type.MOUSE && this.key.getValue() == p_197984_1_;
   }

   public ITextComponent getTranslatedKeyMessage() {
      return this.key.getDisplayName();
   }

   public boolean isDefault() {
      return this.key.equals(this.defaultKey);
   }

   public String saveString() {
      return this.key.getName();
   }

   public void setDown(boolean p_225593_1_) {
      this.isDown = p_225593_1_;
   }
}