package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface IStringSerializable {
   String getSerializedName();

   static <E extends Enum<E> & IStringSerializable> Codec<E> fromEnum(Supplier<E[]> p_233023_0_, Function<? super String, ? extends E> p_233023_1_) {
      E[] ae = p_233023_0_.get();
      return fromStringResolver(Enum::ordinal, (p_233026_1_) -> {
         return ae[p_233026_1_];
      }, p_233023_1_);
   }

   public static <E extends Enum<E>> EnumCodec<E> fromEnum(Supplier<E[]> supplier) {
      return fromEnumWithMapping(supplier, string -> string);
   }



   public static <T extends IStringSerializable> Function<String, T> createNameLookup(T[] TArray, Function<String, String> function) {
      if (TArray.length > 16) {
         Map<String, IStringSerializable> map = Arrays.stream(TArray).collect(Collectors.toMap(stringRepresentable -> (String)function.apply(stringRepresentable.getSerializedName()), stringRepresentable -> stringRepresentable));
         return string -> string == null ? null : (T) map.get(string);
      }
      return string -> {
         for (IStringSerializable stringRepresentable : TArray) {
            if (!((String)function.apply(stringRepresentable.getSerializedName())).equals(string)) continue;
            return (T) stringRepresentable;
         }
         return null;
      };
   }

   public static <E extends Enum<E>> EnumCodec<E> fromEnumWithMapping(Supplier<E[]> supplier, Function<String, String> function) {
      Enum[] enumArray = (Enum[])supplier.get();
      if (enumArray.length > 16) {
         Map<String, Enum> map = Arrays.stream(enumArray).collect(Collectors.toMap(enum_ -> (String)function.apply(((IStringSerializable)((Object)enum_)).getSerializedName()), enum_ -> enum_));
         return new EnumCodec(enumArray, string -> string == null ? null : (Enum)map.get(string));
      }
      return new EnumCodec(enumArray, string -> {
         for (Enum enum_ : enumArray) {
            if (!((String)function.apply(((IStringSerializable)((Object)enum_)).getSerializedName())).equals(string)) continue;
            return enum_;
         }
         return null;
      });
   }

   static <E extends IStringSerializable> Codec<E> fromStringResolver(final ToIntFunction<E> p_233024_0_, final IntFunction<E> p_233024_1_, final Function<? super String, ? extends E> p_233024_2_) {
      return new Codec<E>() {
         public <T> DataResult<T> encode(E p_encode_1_, DynamicOps<T> p_encode_2_, T p_encode_3_) {
            return p_encode_2_.compressMaps() ? p_encode_2_.mergeToPrimitive(p_encode_3_, p_encode_2_.createInt(p_233024_0_.applyAsInt(p_encode_1_))) : p_encode_2_.mergeToPrimitive(p_encode_3_, p_encode_2_.createString(p_encode_1_.getSerializedName()));
         }

         public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> p_decode_1_, T p_decode_2_) {
            return p_decode_1_.compressMaps() ? p_decode_1_.getNumberValue(p_decode_2_).flatMap((p_233034_1_) -> {
               return Optional.ofNullable(p_233024_1_.apply(p_233034_1_.intValue())).map(DataResult::success).orElseGet(() -> {
                  return DataResult.error("Unknown element id: " + p_233034_1_);
               });
            }).map((p_233035_1_) -> {
               return Pair.of(p_233035_1_, p_decode_1_.empty());
            }) : p_decode_1_.getStringValue(p_decode_2_).flatMap((p_233033_1_) -> {
               return Optional.ofNullable(p_233024_2_.apply(p_233033_1_)).map(DataResult::success).orElseGet(() -> {
                  return DataResult.error("Unknown element name: " + p_233033_1_);
               });
            }).map((p_233030_1_) -> {
               return Pair.of(p_233030_1_, p_decode_1_.empty());
            });
         }

         public String toString() {
            return "StringRepresentable[" + p_233024_0_ + "]";
         }
      };
   }

   static Keyable keys(final IStringSerializable[] p_233025_0_) {
      return new Keyable() {
         public <T> Stream<T> keys(DynamicOps<T> p_keys_1_) {
            return p_keys_1_.compressMaps() ? IntStream.range(0, p_233025_0_.length).mapToObj(p_keys_1_::createInt) : Arrays.stream(p_233025_0_).map(IStringSerializable::getSerializedName).map(p_keys_1_::createString);
         }
      };
   }

   public static class EnumCodec<E extends Enum<E>>
           implements Codec<E> {
      private final Codec<E> codec;
      private final Function<String, E> resolver;

      public EnumCodec(E[] EArray, Function<String, E> function) {
         this.codec = ExtraCodecs.orCompressed(ExtraCodecs.stringResolverCodec(object -> ((IStringSerializable)object).getSerializedName(), function), ExtraCodecs.idResolverCodec(object -> ((Enum)object).ordinal(), n -> n >= 0 && n < EArray.length ? EArray[n] : null, -1));
         this.resolver = function;
      }

      public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> dynamicOps, T t) {
         return this.codec.decode(dynamicOps, t);
      }

      public <T> DataResult<T> encode(E e, DynamicOps<T> dynamicOps, T t) {
         return this.codec.encode(e, dynamicOps, t);
      }

      @Nullable
      public E byName(@Nullable String string) {
         return this.resolver.apply(string);
      }

      public E byName(@Nullable String string, E e) {
         return Objects.requireNonNullElse(this.byName(string), e);
      }
   }
}
