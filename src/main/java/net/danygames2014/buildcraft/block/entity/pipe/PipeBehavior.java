package net.danygames2014.buildcraft.block.entity.pipe;

import net.modificationstation.stationapi.api.util.Identifier;

/**
 * This class governs the behavior of a pipe
 * T
 */
public class PipeBehavior {
    public PipeBehavior() {
    }

    /**
     * @param blockEntity The block entity of the pipe this is called on
     * @param otherBlockEntity The block entity of the pipe we want to connect to
     * @param otherPipeBehavior The behavior of the pipe we want to connect to
     * @return If we can connect to the other pipe
     */
    public boolean canConnectTo(PipeBlockEntity blockEntity, PipeBlockEntity otherBlockEntity, PipeBehavior otherPipeBehavior){
        return otherPipeBehavior == this && otherBlockEntity.transporter.getType() == blockEntity.transporter.getType();
    }
}
