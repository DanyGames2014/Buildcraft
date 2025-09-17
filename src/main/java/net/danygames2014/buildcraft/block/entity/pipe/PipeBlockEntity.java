package net.danygames2014.buildcraft.block.entity.pipe;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

public class PipeBlockEntity extends BlockEntity {
    public Pipe pipe;

    public PipeBlockEntity(PipeBehavior behavior) {
        this.pipe = new Pipe(this, behavior);
    }

    // NBT
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        pipe.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        pipe.readNbt(nbt);
    }
}
