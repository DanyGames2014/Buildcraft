package net.danygames2014.buildcraft.block.entity.pipe;

public class StonePipeBehavior extends PipeBehavior {
    @Override
    public boolean canConnectTo(PipeBlockEntity blockEntity, PipeBlockEntity otherBlockEntity, PipeBehavior otherPipeBehavior) {
        return super.canConnectTo(blockEntity, otherBlockEntity, otherPipeBehavior);
    }
}
