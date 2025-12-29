package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.AreaWorkerBlockEntity;
import net.danygames2014.buildcraft.block.entity.QuarryBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public class QuarryBlock extends AreaWorkerBlock {
    public QuarryBlock(Identifier identifier, Material material) {
        super(identifier, material);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return super.getPlacementState(context).with(Properties.HORIZONTAL_FACING, context.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new QuarryBlockEntity();
    }

    @Override
    public void constructWorkingArea(World world, int x, int y, int z, AreaWorkerBlockEntity areaWorker) {
        super.constructWorkingArea(world, x, y, z, areaWorker);

        if (areaWorker.workingArea == null) {
            if (!world.getBlockState(x, y, z).contains(Properties.HORIZONTAL_FACING)) {
                return;
            }

            Direction facing = world.getBlockState(x, y, z).get(Properties.HORIZONTAL_FACING).getOpposite();

            BlockPos pos = new BlockPos(x, y, z);
            pos = pos.offset(facing.getAxis(), 6 * facing.getDirection().offset());

            areaWorker.constructWorkingArea(pos.x - 5, pos.y, pos.z - 5, pos.x + 5, pos.y + (areaWorker.minHeight - 1), pos.z + 5);
        }
    }

    @Override
    public void onBreak(World world, int x, int y, int z) {
        if (world.getBlockEntity(x, y, z) instanceof QuarryBlockEntity quarry) {
            AreaWorkerBlockEntity.WorkingArea workingArea = quarry.workingArea;
            
            if (workingArea == null) {
                Buildcraft.LOGGER.warn("Quarry block broken without working area");
                return;
            }
            
            for (int frameX = workingArea.minX; frameX <= workingArea.maxX; frameX++) {
                for (int frameY = workingArea.minY; frameY <= workingArea.maxY; frameY++) {
                    for (int frameZ = workingArea.minZ; frameZ <= workingArea.maxZ; frameZ++) {
                        if (world.getBlockState(frameX, frameY, frameZ).getBlock() instanceof FrameBlock) {
                            world.setBlockMeta(frameX, frameY, frameZ, 1);
                        }
                    }
                }
            }
        }

        super.onBreak(world, x, y, z);
    }
}
