package net.danygames2014.buildcraft.block.entity.pipe;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

public abstract class PipeTransporter {
    public final PipeBlockEntity blockEntity;
    public World world;
    public int x;
    public int y;
    public int z;
    
    public PipeTransporter(PipeBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }
    
    // Tick
    public void tick() {
        if (world == null) {
            world = blockEntity.world;
            x = blockEntity.x;
            y = blockEntity.y;
            z = blockEntity.z;
        }
    }

    // Init
    public void init() {
        
    }

    // Connections Update
    public void onConnectionsUpdate() {

    }

    // Type
    public abstract PipeType getType();
    
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
