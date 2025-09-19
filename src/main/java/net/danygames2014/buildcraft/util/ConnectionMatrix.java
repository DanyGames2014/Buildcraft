package net.danygames2014.buildcraft.util;

import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ConnectionMatrix {
    private int mask = 0;
    private boolean dirty = false;

    public boolean isConnected(Direction direction){
        return (mask & (1 << direction.ordinal())) != 0;
    }

    public void setConnected(Direction direction, boolean value){
        if(isConnected(direction) != value){
            mask ^= 1 << direction.ordinal();
            dirty = true;
        }
    }

    public int getMask() {
        return mask;
    }

    public boolean isDirty(){
        return dirty;
    }

    public void clean(){
        dirty = false;
    }

    public void write(DataOutputStream stream) throws IOException {
        stream.writeByte(mask);
    }

    public void read(DataInputStream stream) throws IOException {
        byte newMask = stream.readByte();

        if(newMask != mask){
            mask = newMask;
            dirty = true;
        }
    }
}
