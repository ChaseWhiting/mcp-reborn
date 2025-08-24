package net.minecraft.util.codec;

import io.netty.buffer.ByteBuf;

public class VarInt {
    private static final int MAX_VARINT_SIZE = 5;
    private static final int DATA_BITS_MASK = 127;
    private static final int CONTINUATION_BIT_MASK = 128;
    private static final int DATA_BITS_PER_BYTE = 7;

    public static int getByteSize(int n) {
        for (int i = 1; i < 5; ++i) {
            if ((n & -1 << i * 7) != 0) continue;
            return i;
        }
        return 5;
    }

    public static boolean hasContinuationBit(byte by) {
        return (by & 0x80) == 128;
    }

    public static int read(ByteBuf byteBuf) {
        byte by;
        int n = 0;
        int n2 = 0;
        do {
            by = byteBuf.readByte();
            n |= (by & 0x7F) << n2++ * 7;
            if (n2 <= 5) continue;
            throw new RuntimeException("VarInt too big");
        } while (VarInt.hasContinuationBit(by));
        return n;
    }

    public static ByteBuf write(ByteBuf byteBuf, int n) {
        while (true) {
            if ((n & 0xFFFFFF80) == 0) {
                byteBuf.writeByte(n);
                return byteBuf;
            }
            byteBuf.writeByte(n & 0x7F | 0x80);
            n >>>= 7;
        }
    }
}