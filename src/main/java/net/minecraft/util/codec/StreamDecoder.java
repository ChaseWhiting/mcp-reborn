package net.minecraft.util.codec;

@FunctionalInterface
public interface StreamDecoder<I, T> {
    public T decode(I var1);
}