package net.danygames2014.buildcraft.block.entity;

public enum PowerMode {
    M2(20), M4(40), M8(80), M16(160), M32(320), M64(640), M128(1280);
    public static final PowerMode[] VALUES = values();
    public final int maxPower;

    PowerMode(int max) {
        this.maxPower = max;
    }

    public PowerMode getNext() {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }

    public PowerMode getPrevious() {
        return VALUES[(ordinal() + VALUES.length - 1) % VALUES.length];
    }

    public static PowerMode fromId(int id) {
        if (id < 0 || id >= VALUES.length) {
            return M128;
        }
        return VALUES[id];
    }
}
