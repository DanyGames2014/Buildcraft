package net.danygames2014.buildcraft.block.entity.pipe.behavior;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeConnectionType;
import net.modificationstation.stationapi.api.util.math.Direction;

public class StonePipeBehavior extends PipeBehavior {
    @Override
    public PipeConnectionType canConnectToPipe(PipeBlockEntity blockEntity, PipeBlockEntity otherBlockEntity, PipeBehavior otherPipeBehavior, Direction side) {
        if (otherPipeBehavior instanceof CobblestonePipeBehavior) {
            return PipeConnectionType.NONE;
        }
        
        return super.canConnectToPipe(blockEntity, otherBlockEntity, otherPipeBehavior, side);
    }
}
