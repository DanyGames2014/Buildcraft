package net.danygames2014.buildcraft.block.entity.pipe;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;

public abstract class PipeTransporter {
    public final PipeBlockEntity blockEntity;
    
    public PipeTransporter(PipeBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }
    
    // Tick
    public void tick() {
        
    }

    // Init
    public void init() {
        
    }

    // Type
    public abstract PipeType getType();
    
    // Connecting Logic
    public abstract PipeConnectionType canConnectTo(BlockEntity other, Direction side);

    // Logic
    public void onBreak() {
    }
    
    // NBT
    public void writeNbt(NbtCompound nbt) {
        
    }

    public void readNbt(NbtCompound nbt) {
        
    }

    public interface PipeTransporterFactory {
        PipeTransporter create(PipeBlockEntity blockEntity);
    }
}
