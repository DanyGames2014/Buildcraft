package net.danygames2014.buildcraft.block.entity.pipe;

import net.minecraft.nbt.NbtCompound;

/**
 * This class holds the current state of the pipe
 * An instance of this class is instantiated per PipeBlockEntity
 */
public class Pipe {
    public PipeBlockEntity blockEntity;
    public PipeBehavior behavior;
    
    public Pipe(PipeBlockEntity blockEntity, PipeBehavior behavior) {
        this.blockEntity = blockEntity;
        this.behavior = behavior;
    }

    // NBT
    public void writeNbt(NbtCompound nbt) {
    }

    public void readNbt(NbtCompound nbt) {
    }
}
