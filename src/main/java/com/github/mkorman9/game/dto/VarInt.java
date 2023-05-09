package com.github.mkorman9.game.dto;

import io.vertx.core.buffer.Buffer;
import lombok.Data;

@Data
public class VarInt {
    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    private final int value;
    private final byte length;

    private VarInt(int value, byte length) {
        this.value = value;
        this.length = length;
    }

    public static VarInt read(Buffer source) {
        int value = 0;
        int offset = 0;
        byte index = 0;

        while (true) {
            byte currentByte = source.getByte(index);
            value |= (currentByte & SEGMENT_BITS) << offset;

            if ((currentByte & CONTINUE_BIT) == 0) {
                break;
            }

            offset += 7;
            index++;

            if (offset >= 32) {
                throw new IllegalArgumentException("VarInt is too big");
            }
        }

        return new VarInt(value, (byte) (index + 1));
    }

    public static byte[] encode(int value) {
        var target = Buffer.buffer();

        while (true) {
            if ((value & ~SEGMENT_BITS) == 0) {
                target.appendByte((byte) value);
                return target.getBytes();
            }

            target.appendByte((byte) ((value & SEGMENT_BITS) | CONTINUE_BIT));

            value >>>= 7;
        }
    }
}
