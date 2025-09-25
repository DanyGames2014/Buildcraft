package net.danygames2014.buildcraft.util;

import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TextureMatrix {
    private final Identifier[] textureIndexes = new Identifier[7];
    private  boolean dirty = false;

    public Identifier getTextureIdentifier(Direction direction){
        return textureIndexes[DirectionUtil.getOrdinal(direction)];
    }

    public void setTextureIdentifier(Direction direction, Identifier value){
        if(textureIndexes[DirectionUtil.getOrdinal(direction)] != value){
            textureIndexes[DirectionUtil.getOrdinal(direction)] = value;
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
        for(Identifier identifier : textureIndexes){
            stream.writeUTF(identifier.toString());
        }
    }

    public void read(DataInputStream stream) throws IOException {
        for(int i = 0; i < textureIndexes.length; i++){
            Identifier identifier = Identifier.tryParse(stream.readUTF());
            if(textureIndexes[i] != identifier){
                textureIndexes[i] = identifier;
                dirty = true;
            }
        }
    }
}
