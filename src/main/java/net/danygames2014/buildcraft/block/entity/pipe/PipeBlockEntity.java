package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public class PipeBlockEntity extends BlockEntity {
    public PipeBlock pipeBlock;
    public PipeBehavior behavior;
    public PipeTransporter transporter;

    // Empty constructor for loading
    public PipeBlockEntity() {
    }

    // Normal constructor for when the pipe is first created
    public PipeBlockEntity(PipeBlock pipeBlock) {
        this.pipeBlock = pipeBlock;
        init();
    }

    // Init
    private boolean hasInit = false;

    public void init() {
        behavior = pipeBlock.behavior;
        transporter = pipeBlock.transporterFactory.create(this);
        transporter.init();
        hasInit = true;
    }

    // Tick
    @Override
    public void tick() {
        super.tick();

        if (!hasInit) {
            init();
        }
        
        transporter.tick();
    }

    // Pipe Logic

    /**
     * @param x    The x position of this block
     * @param y    The y position of this block
     * @param z    The z position of this block
     * @param side The side on which the target block is located
     * @return Whether this pipe can connect to the target block
     */
    public boolean canConnectTo(int x, int y, int z, Direction side) {
        BlockEntity other = world.getBlockEntity(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ());

        if (other == null) {
            return false;
        }
        
        if (other instanceof PipeBlockEntity pipe) {
            return behavior.canConnectTo(this, pipe, pipe.behavior);
        }

        //noinspection RedundantIfStatement
        if (transporter.canConnectTo(other, side)) {
            return true;
        }

        return false;
    }

    // NBT
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("pipeId", String.valueOf(BlockRegistry.INSTANCE.getId(pipeBlock)));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.pipeBlock = (PipeBlock) BlockRegistry.INSTANCE.get(Identifier.of(nbt.getString("pipeId")));
        init();
    }

    @Override
    public String toString() {
        return "PipeBlockEntity{" +
                "pipeBlock=" + pipeBlock +
                ", behavior=" + behavior +
                ", transporter=" + transporter +
                ", hasInit=" + hasInit +
                ", world=" + world +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
