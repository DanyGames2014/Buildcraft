package net.danygames2014.buildcraft.util;

import net.modificationstation.stationapi.api.util.math.Direction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TextureMatrix {
    private final int[] textureIndexes = new int[7];
    private  boolean dirty = false;

    public int getTextureIndex(Direction direction){
        return textureIndexes[direction.ordinal()];
    }

    public void setTextureIndex(Direction direction, int value){
        if(textureIndexes[direction.ordinal()] != value){
            textureIndexes[direction.ordinal()] = value;
            dirty = true;
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clean(){
        dirty = false;
    }

    public void write(DataOutputStream stream) throws IOException {
        for(int index : textureIndexes){
            stream.writeByte(index);
        }
    }

    public void read(DataInputStream stream) throws IOException {
        for(int i = 0; i < textureIndexes.length; i++){
            int index = stream.readByte();
            if(textureIndexes[i] != index){
                textureIndexes[i] = index;
                dirty = true;
            }
        }
    }
}
