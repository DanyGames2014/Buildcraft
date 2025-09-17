package net.danygames2014.buildcraft.block.entity.pipe;

import net.modificationstation.stationapi.api.util.Identifier;

public class StonePipeBehavior extends PipeBehavior {
    public StonePipeBehavior(Identifier identifier) {
        super(identifier);
    }

    @Override
    public boolean canConnectTo(PipeBlockEntity blockEntity, PipeBlockEntity otherBlockEntity, PipeBehavior otherPipeBehavior) {
        return true;
    }
}
