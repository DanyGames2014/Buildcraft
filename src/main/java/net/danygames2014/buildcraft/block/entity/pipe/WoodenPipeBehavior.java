package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.buildcraft.Buildcraft;
import net.modificationstation.stationapi.api.util.Identifier;

public class WoodenPipeBehavior extends PipeBehavior {
    public WoodenPipeBehavior(Identifier identifier) {
        super(identifier);
    }

    @Override
    public boolean canConnectTo(PipeBlockEntity blockEntity, PipeBlockEntity otherBlockEntity, PipeBehavior otherPipeBehavior) {
        return otherPipeBehavior != Buildcraft.woodenPipeBehavior;
    }
}
