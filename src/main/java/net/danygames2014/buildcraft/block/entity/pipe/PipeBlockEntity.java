package net.danygames2014.buildcraft.block.entity.pipe;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public class PipeBlockEntity extends BlockEntity {
    public PipeBehavior behavior;

    // Empty constructor for loading
    public PipeBlockEntity() {
    }

    // Normal constructor for when the pipe is first created
    public PipeBlockEntity(PipeBehavior behavior) {
        this.behavior = behavior;
    }
    
    private boolean hasInit = false;
    public void init() {
        
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!hasInit) {
            init();
            hasInit = true;
        }
    }

    // Pipe Logic
    /**
     * @param x The x position of this block
     * @param y The y position of this block
     * @param z The z position of this block
     * @param side The side on which the target block is located
     * @return Whether this pipe can connect to the target block
     */
    public boolean canConnectTo(int x, int y, int z, Direction side) {
        BlockEntity blockEntity = world.getBlockEntity(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ());
        
        if (blockEntity instanceof PipeBlockEntity pipe) {
            return behavior.canConnectTo(this, pipe, pipe.behavior);
        }
        
        // TODO: Connecting to other things
        
        return false;
    }

    // NBT
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("behavior", behavior.identifier.toString());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.behavior = PipeBehaviorRegistry.get(Identifier.of(nbt.getString("behavior")));
    }
}
