package net.danygames2014.buildcraft.client.render;

import net.danygames2014.buildcraft.util.ConnectionMatrix;
import net.danygames2014.buildcraft.util.TextureMatrix;
import net.danygames2014.buildcraft.util.WireMatrix;
import net.minecraft.nbt.NbtCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// TODO: update as we add wires, etc
public class PipeRenderState {
    public final ConnectionMatrix pipeConnectionMatrix = new ConnectionMatrix();
    public final TextureMatrix textureMatrix = new TextureMatrix();
    public final WireMatrix wireMatrix = new WireMatrix();

    private boolean dirty = false;

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
    public void write(DataOutputStream stream) throws IOException {
        pipeConnectionMatrix.write(stream);
        textureMatrix.write(stream);
    }

    public void read(DataInputStream stream) throws IOException {
        pipeConnectionMatrix.read(stream);
        textureMatrix.read(stream);
    }
}
