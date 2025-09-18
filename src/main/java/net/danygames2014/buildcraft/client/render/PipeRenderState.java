package net.danygames2014.buildcraft.client.render;

import net.danygames2014.buildcraft.util.ConnectionMatrix;
import net.danygames2014.buildcraft.util.TextureMatrix;
import net.minecraft.nbt.NbtCompound;

// TODO: update as we add wires, etc
public class PipeRenderState {
    public final ConnectionMatrix pipeConnectionMatrix = new ConnectionMatrix();
    public final TextureMatrix textureMatrix = new TextureMatrix();

    private boolean dirty = false;

    public void clean(){
        dirty = false;
        pipeConnectionMatrix.clean();
        textureMatrix.clean();
    }

    public boolean isDirty(){
        return dirty || pipeConnectionMatrix.isDirty() || textureMatrix.isDirty();
    }

    public boolean needsRenderUpdate(){
        return pipeConnectionMatrix.isDirty() || textureMatrix.isDirty();
    }

    // TODO, make this write to buffer instead, for update packet
    public void writeNbt(NbtCompound nbt){
        pipeConnectionMatrix.writeNbt(nbt);
    }

    public void readNbt(NbtCompound nbt){
        pipeConnectionMatrix.readNbt(nbt);
    }
}
