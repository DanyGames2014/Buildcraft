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

    private boolean dirty = true;

    public void clean(){
        dirty = false;
        pipeConnectionMatrix.clean();
        textureMatrix.clean();
        wireMatrix.clean();
    }

    public boolean isDirty(){
        return dirty || pipeConnectionMatrix.isDirty() || textureMatrix.isDirty() || wireMatrix.isDirty();
    }

    public boolean needsRenderUpdate(){
        return pipeConnectionMatrix.isDirty() || textureMatrix.isDirty() || wireMatrix.isDirty();
    }

    // TODO, make this write to buffer instead, for update packet
    public void writeData(DataOutputStream stream) throws IOException {
        pipeConnectionMatrix.writeData(stream);
        textureMatrix.writeData(stream);
        wireMatrix.writeData(stream);
    }

    public void readData(DataInputStream stream) throws IOException {
        pipeConnectionMatrix.readData(stream);
        textureMatrix.readData(stream);
        wireMatrix.readData(stream);
    }
}
