package net.danygames2014.buildcraft.util;

import java.util.BitSet;

public class BitSetCodec {
    public byte encode(BitSet set) {
        byte result = 0;
        for (byte i = 0; i < 8; i++) {
            result |= set.get(i) ? (1 << i) : 0;
        }
        return result;
    }

    public void decode(byte data, BitSet target) {
        int t = 1;

        target.clear();

        for (byte i = 0; i < 8; i++) {
            target.set(i, (data & t) != 0);
            t <<= 1;
        }
    }
}
