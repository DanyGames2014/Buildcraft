package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.buildcraft.Buildcraft;
import net.modificationstation.stationapi.api.util.Identifier;

public class WoodenPipeBehavior extends PipeBehavior {
    @Override
    public boolean canConnectTo(PipeBlockEntity blockEntity, PipeBlockEntity otherBlockEntity, PipeBehavior otherPipeBehavior) {
        return super.canConnectTo(blockEntity, otherBlockEntity, otherPipeBehavior) && otherPipeBehavior != Buildcraft.woodenPipeBehavior;
    }
}
