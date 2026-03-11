package net.danygames2014.buildcraft.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.LiquidBlock;
import net.minecraft.block.StillLiquidBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;

import java.util.List;

@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "RedundantIfStatement"})
public class BlockUtil {

    public static final XorShift128Random RANDOM = new XorShift128Random();

    public static boolean canChangeBlock(World world, int x, int y, int z) {
        BlockState state = world.getBlockState(x, y, z);
        Block block = state.getBlock();

        if (block == null || state.isAir()) {
            return true;
        }

        if (block.getHardness(state,world, new BlockPos(x, y, z)) < 0){
            return false;
        }

        if (block instanceof LiquidBlock) {
            return false;
        }

        return true;
    }

    public static boolean isSoftBlock(World world, int x, int y, int z) {
        BlockState state = world.getBlockState(x, y, z);
        Block block = state.getBlock();
        int blockId = world.getBlockId(x, y, z);

        return blockId == 0 || block == null || /* BuildCraftAPI.softBlocks[blockID] ||*/ block instanceof LiquidBlock || state.isAir() || block.getHardness(state,world, new BlockPos(x, y, z)) == 0;
    }

    public static boolean isFullFluidBlock(Block block, World world, int x, int y, int z) {
        if (block instanceof StillLiquidBlock) {
            return world.getBlockMeta(x, y, z) == 0;
        }
        return false;
    }

    public static List<ItemStack> getStacksFromBlock(World world, int x, int y, int z) {
        BlockState state = world.getBlockState(x, y, z);
        Block block = state.getBlock();
        
        if (state.isAir() || block == null) {
            return null;
        }

        int blockMeta = world.getBlockMeta(x, y, z);
        List<ItemStack> stacks = state.getBlock().getDropList(world, x, y, z, state, blockMeta);
        
        if (stacks == null) {
            stacks = new ObjectArrayList<>();
            int itemId = block.getDroppedItemId(blockMeta, world.random);
            int meta = block.getDroppedItemMeta(blockMeta);
            int count = block.getDroppedItemCount(world.random);

            if (itemId != 0 && count > 0) {
                stacks.add(new ItemStack(itemId, count, meta));
            }
        }

        return stacks;
    }
}
