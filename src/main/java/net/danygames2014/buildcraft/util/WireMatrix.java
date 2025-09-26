package net.danygames2014.buildcraft.util;

import net.danygames2014.buildcraft.api.core.Serializable;
import net.danygames2014.buildcraft.block.entity.pipe.PipeWire;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;

public class WireMatrix implements Serializable {
    private final BitSet hasWire = new BitSet(PipeWire.values().length);

    private final ConnectionMatrix[] wires = new ConnectionMatrix[PipeWire.values().length];
    private final Identifier[] wireTextureIdentifiers = new Identifier[PipeWire.values().length];

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

    public Identifier getWireTextureIdentifier(PipeWire color){
        return wireTextureIdentifiers[color.ordinal()];
    }

    public void setWireTextureIdentifier(PipeWire color, Identifier value){
        if(wireTextureIdentifiers[color.ordinal()] != value){
            wireTextureIdentifiers[color.ordinal()] = value;
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
    public void writeData(DataOutputStream stream) throws IOException {

    }

    public void readData(DataInputStream stream) throws IOException {

    }
}
