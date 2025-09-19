package net.danygames2014.buildcraft.util;

import net.danygames2014.buildcraft.block.entity.pipe.PipeWire;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;

public class WireMatrix {
    private final BitSet hasWire = new BitSet(PipeWire.values().length);

    private final ConnectionMatrix[] wires = new ConnectionMatrix[PipeWire.values().length];
    private final int[] wireTextureOffsets = new int[PipeWire.values().length];

    private boolean dirty = false;

    public WireMatrix(){
        for(int i = 0; i < PipeWire.values().length; i++){
            wires[i] = new ConnectionMatrix();
        }
    }

    public boolean hasWire(PipeWire color){
        return hasWire.get(color.ordinal());
    }

    public void setWire(PipeWire color, boolean value){
        if(hasWire.get(color.ordinal()) != value){
            hasWire.set(color.ordinal(), value);
            dirty = true;
        }
    }

    public boolean isWireConnected(PipeWire color, Direction direction){
        return wires[color.ordinal()].isConnected(direction);
    }

    public void setWireConnected(PipeWire color, Direction direction, boolean value){
        wires[color.ordinal()].setConnected(direction, value);
    }

    public int getWireTextureIndex(PipeWire color){
        return wireTextureOffsets[color.ordinal()];
    }

    public void setWireTextureIndex(PipeWire color, int value){
        if(wireTextureOffsets[color.ordinal()] != value){
            wireTextureOffsets[color.ordinal()] = value;
            dirty = true;
        }
    }

    public boolean isDirty(){
        for(int i = 0; i < PipeWire.values().length; i++){
            if(wires[i].isDirty()){
                return true;
            }
        }
        return dirty;
    }

    public void clean(){
        for(int i = 0; i < PipeWire.values().length; i++){
            wires[i].clean();
        }
        dirty = false;
    }

    // TODO: implement this
    public void write(DataOutputStream stream) throws IOException {

    }

    public void read(DataInputStream stream) throws IOException {

    }
}
