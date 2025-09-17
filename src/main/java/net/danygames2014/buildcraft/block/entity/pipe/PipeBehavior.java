package net.danygames2014.buildcraft.block.entity.pipe;

/**
 * This class governs the behavior of a pipe
 * T
 */
public class PipeBehavior {
    public boolean canConnectTo(PipeBlockEntity otherBlockEntity, PipeBehavior otherPipeBehavior){
        return otherPipeBehavior == this;
    }
}
