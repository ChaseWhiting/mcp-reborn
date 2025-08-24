package net.minecraft.util;

import javax.annotation.Nullable;

public interface IObjectIntIterable<T> extends Iterable<T> {
   int getId(T p_148757_1_);

   @Nullable
   T byId(int p_148745_1_);

   default public T byIdOrThrow(int n) {
      T t = this.byId(n);
      if (t == null) {
         throw new IllegalArgumentException("No value with id " + n);
      }
      return t;
   }

   default public int getIdOrThrow(T t) {
      int n = this.getId(t);
      if (n == -1) {
         throw new IllegalArgumentException("Can't find id for '" + String.valueOf(t) + "' in map " + String.valueOf(this));
      }
      return n;
   }
}