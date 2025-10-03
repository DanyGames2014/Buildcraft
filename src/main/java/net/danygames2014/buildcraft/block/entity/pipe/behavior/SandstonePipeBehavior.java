package net.danygames2014.buildcraft.block.entity.pipe.behavior;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeConnectionType;
import net.danygames2014.buildcraft.block.entity.pipe.PipeType;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

public class SandstonePipeBehavior extends PipeBehavior {
    @Override
    public PipeConnectionType getConnectionType(PipeType type, PipeBlockEntity blockEntity, World world, int x, int y, int z, Direction side) {
        return PipeConnectionType.NONE;
    }
}
