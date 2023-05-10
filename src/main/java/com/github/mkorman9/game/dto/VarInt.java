package com.github.mkorman9.game.dto;

import io.vertx.core.buffer.Buffer;
import lombok.Data;

/**
 * VarInt represents a <a href="https://en.wikipedia.org/wiki/Variable-length_quantity">Variable Length Quantity</a>
 */
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

    public static VarInt of(int value) {
        byte length = 1;

        while ((value & ~SEGMENT_BITS) != 0) {
            length++;
            value >>>= 7;
        }

        return new VarInt(value, length);
    }

    public static VarInt read(Buffer source) {
        return read(source, 0);
    }

    public static VarInt read(Buffer source, int index) {
        int value = 0;
        int offset = 0;
        byte length = 1;

        while (true) {
            byte current = source.getByte(index);
            index++;
            value |= (current & SEGMENT_BITS) << offset;

            if ((current & CONTINUE_BIT) == 0) {
                break;
            }

            offset += 7;
            length++;

            if (offset >= 32) {
                throw new IllegalArgumentException("VarInt is too big");
            }
        }

        return new VarInt(value, length);
    }

    public byte[] encode() {
        var target = Buffer.buffer();
        var v = value;

        for (int i = 0; i < length - 1; i++) {
            target.appendByte((byte) ((v & SEGMENT_BITS) | CONTINUE_BIT));
            v >>>= 7;
        }

        target.appendByte((byte) v);
        return target.getBytes();
    }
}
