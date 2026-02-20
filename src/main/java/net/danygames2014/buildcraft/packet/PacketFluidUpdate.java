package net.danygames2014.buildcraft.packet;

import net.danygames2014.buildcraft.block.entity.pipe.transporter.FluidRenderData;

import java.util.BitSet;

// TODO: Remove
public class PacketFluidUpdate {
    public FluidRenderData renderCache = new FluidRenderData();
    public BitSet delta;
    private boolean largeFluidCapacity;
    int x;
    int y;
    int z;
    boolean isChunkDataPacket;

    public PacketFluidUpdate(int xCoord, int yCoord, int zCoord) {
        this.x = xCoord;
        this.y = yCoord;
        this.z = zCoord;
    }

    public PacketFluidUpdate(int xCoord, int yCoord, int zCoord, boolean chunkPacket, boolean largeFluidCapacity) {
        this.x = xCoord;
        this.y = yCoord;
        this.z = zCoord;
        this.isChunkDataPacket = chunkPacket;
        this.largeFluidCapacity = largeFluidCapacity;
    }

    public PacketFluidUpdate() {
    }
}
