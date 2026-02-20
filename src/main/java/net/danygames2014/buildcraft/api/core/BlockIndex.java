package net.danygames2014.buildcraft.api.core;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class BlockIndex implements Comparable<BlockIndex> {
    public int x;
    public int y;
    public int z;

    public BlockIndex() {

    }

    /**
     * Creates an index for a block located on x, y. z
     */
    public BlockIndex(int x, int y, int z) {

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockIndex(NbtCompound c) {
        this.x = c.getInt("i");
        this.y = c.getInt("j");
        this.z = c.getInt("k");
    }

    public BlockIndex(Entity entity) {
        x = (int) Math.floor(entity.x);
        y = (int) Math.floor(entity.y);
        z = (int) Math.floor(entity.z);
    }

    public BlockIndex(BlockEntity blockEntity) {
        this(blockEntity.x, blockEntity.y, blockEntity.z);
    }


    @Override
    public int compareTo(@NotNull BlockIndex o) {
        if (o.x < x) {
            return 1;
        } else if (o.x > x) {
            return -1;
        } else if (o.z < z) {
            return 1;
        } else if (o.z > z) {
            return -1;
        } else if (o.y < y) {
            return 1;
        } else if (o.y > y) {
            return -1;
        } else {
            return 0;
        }
    }

    public void writeTo(NbtCompound c) {
        c.putInt("i", x);
        c.putInt("j", y);
        c.putInt("k", z);
    }

    public Block getBlock(World world) {
        return world.getBlockState(x, y, z).getBlock();
    }

    @Override
    public String toString() {
        return "{" + x + ", " + y + ", " + z + "}";
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockIndex b) {
            return b.x == x && b.y == y && b.z == z;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return (x * 37 + y) * 37 + z;
    }

    public boolean nextTo(BlockIndex blockIndex) {
        return (Math.abs(blockIndex.x - x) <= 1 && blockIndex.y == y && blockIndex.z == z)
                || (blockIndex.x == x && Math.abs(blockIndex.y - y) <= 1 && blockIndex.z == z)
                || (blockIndex.x == x && blockIndex.y == y && Math.abs(blockIndex.z - z) <= 1);
    }
}
