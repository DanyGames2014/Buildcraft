package net.danygames2014.buildcraft.block.entity.pipe.behavior;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeConnectionType;

public class CobblestonePipeBehavior extends PipeBehavior {
    @Override
    public PipeConnectionType canConnectToPipe(PipeBlockEntity blockEntity, PipeBlockEntity otherBlockEntity, PipeBehavior otherPipeBehavior) {
        if (otherPipeBehavior instanceof StonePipeBehavior) {
            return PipeConnectionType.NONE;
        }

        return super.canConnectToPipe(blockEntity, otherBlockEntity, otherPipeBehavior);
    }
}
