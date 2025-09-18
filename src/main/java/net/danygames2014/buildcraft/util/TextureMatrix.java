package net.danygames2014.buildcraft.util;

import net.modificationstation.stationapi.api.util.math.Direction;

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

    // TODO implement this, and make it use a buffer instead. this will be used for networking.
    public void writeNbt(){

    }

    public void readNbt(){

    }
}
