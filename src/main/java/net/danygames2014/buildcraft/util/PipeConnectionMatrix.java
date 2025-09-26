package net.danygames2014.buildcraft.util;

import net.danygames2014.buildcraft.api.core.Serializable;
import net.danygames2014.buildcraft.block.entity.pipe.PipeConnectionType;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PipeConnectionMatrix implements Serializable {
    private final int[] connections = new int[Direction.values().length];
    private  boolean dirty;

    public boolean isConnected(Direction direction){
        return getConnectionType(direction) != PipeConnectionType.NONE;
    }

    public void setConnected(Direction direction, PipeConnectionType type){
        if(getConnectionType(direction) != type){
            connections[direction.ordinal()] = type.ordinal();
            dirty = true;
        }
    }

    public int getMask(){
        int mask = 0;
        for (Direction direction : Direction.values()) {
            if (isConnected(direction)) {
                mask |= 1 << direction.ordinal();
            }
        }
        return mask;
    }

    public PipeConnectionType getConnectionType(Direction direction){
        return PipeConnectionType.values()[connections[direction.ordinal()]];
    }

    public boolean isDirty(){
        return dirty;
    }

    public void clean(){
        dirty = false;
    }

    public void writeData(DataOutputStream stream) throws IOException {
        for(int i = 0; i < connections.length; i++){
            stream.writeByte(connections[i]);
        }
    }

    public void readData(DataInputStream stream) throws IOException {
        for(int i = 0; i < connections.length; i++){
            byte connectionType = stream.readByte();
            if(connectionType != connections[i]){
                connections[i] = connectionType;
                dirty = true;
            }
        }
    }
}
