package net.danygames2014.buildcraft.api.core;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.math.Direction;

public class BlockEntityBuffer {
    private BlockState blockState = null;
    private BlockEntity blockEntity;
    private final SafeTimeTracker tracker = new SafeTimeTracker(20);
    private final World world;
    final int x, y, z;
    private final boolean loadUnloaded;

    public BlockEntityBuffer(World world, int x, int y, int z, boolean loadUnloaded) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.loadUnloaded = loadUnloaded;

        refresh();
    }

    public final void refresh() {
        blockEntity = null;
        blockState = null;
        if (!loadUnloaded && world.isAir(x, y, z)) {
            return;
        }
        blockState = world.getBlockState(this.x, this.y, this.z);

        if (blockState != null && Block.BLOCKS_WITH_ENTITY[blockState.getBlock().id]) {
            blockEntity = world.getBlockEntity(this.x, this.y, this.z);
        }
    }

    public void set(Block block, BlockEntity blockEntity) {
        this.blockState = block.getDefaultState();
        this.blockEntity = blockEntity;
        tracker.markTime(world);
    }

    public Block getBlock() {
        if (blockEntity != null && !blockEntity.isRemoved())
            return blockState.getBlock();

        if (tracker.markTimeIfDelay(world)) {
            refresh();

            if (blockEntity != null && !blockEntity.isRemoved())
                return blockState.getBlock();
        }

        return null;
    }

    public BlockEntity getBlockEntity() {
        if (blockEntity != null && !blockEntity.isRemoved())
            return blockEntity;

        if (tracker.markTimeIfDelay(world)) {
            refresh();

            if (blockEntity != null && !blockEntity.isRemoved())
                return blockEntity;
        }

        return null;
    }

    public boolean exists() {
        if(blockEntity != null && !blockEntity.isRemoved())
            return true;
        return !world.isAir(x, y, z);
    }

    public static BlockEntityBuffer[] makeBuffer(World world, int x, int y, int z, boolean loadUnloaded) {
        BlockEntityBuffer[] buffer = new BlockEntityBuffer[6];
        for (int i = 0; i < 6; i++) {
            Direction d = Direction.byId(i);
            buffer[i] = new BlockEntityBuffer(world, x + d.getOffsetX(), y + d.getOffsetY(), z + d.getOffsetZ(), loadUnloaded);
        }
        return buffer;
    }
}
