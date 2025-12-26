package net.danygames2014.buildcraft.client.render;

import net.danygames2014.buildcraft.api.core.Serializable;
import net.danygames2014.buildcraft.util.ConnectionMatrix;
import net.danygames2014.buildcraft.util.PipeConnectionMatrix;
import net.danygames2014.buildcraft.util.TextureMatrix;
import net.danygames2014.buildcraft.util.WireMatrix;
import net.minecraft.nbt.NbtCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// TODO: update as we add wires, etc
public class PipeRenderState implements Serializable {
    public final PipeConnectionMatrix pipeConnectionMatrix = new PipeConnectionMatrix();
    public final TextureMatrix textureMatrix = new TextureMatrix();
    public final WireMatrix wireMatrix = new WireMatrix();
    public boolean glassColorDirty = false;
    private byte glassColor = -127;

    private boolean dirty = true;

    public void clean(){
        dirty = false;
        glassColorDirty = false;
        pipeConnectionMatrix.clean();
        textureMatrix.clean();
        wireMatrix.clean();
    }

    public byte getGlassColor() {
        return glassColor;
    }

    public void setGlassColor(byte color) {
        this.glassColor = color;
    }

    public boolean isDirty(){
        return dirty || pipeConnectionMatrix.isDirty() || textureMatrix.isDirty() || wireMatrix.isDirty() || glassColorDirty;
    }

    public boolean needsRenderUpdate(){
        return pipeConnectionMatrix.isDirty() || textureMatrix.isDirty() || wireMatrix.isDirty() || glassColorDirty;
    }

    // TODO, make this write to buffer instead, for update packet
    public void writeData(DataOutputStream stream) throws IOException {
        stream.writeByte(glassColor < -1 ? -1 : glassColor);
        pipeConnectionMatrix.writeData(stream);
        textureMatrix.writeData(stream);
        wireMatrix.writeData(stream);
    }

    public void readData(DataInputStream stream) throws IOException {
        byte g = stream.readByte();
        if (g != glassColor) {
            this.glassColor = g;
            this.glassColorDirty = true;
        }
        pipeConnectionMatrix.readData(stream);
        textureMatrix.readData(stream);
        wireMatrix.readData(stream);
    }
}
