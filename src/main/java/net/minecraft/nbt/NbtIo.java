package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;

public class NbtIo {


    public static void writeAnyTag(INBT tag, DataOutput dataOutput) throws IOException {
        dataOutput.writeByte(tag.getId());
        if (tag.getId() == 0) {
            return;
        }
        tag.write(dataOutput);
    }

}
