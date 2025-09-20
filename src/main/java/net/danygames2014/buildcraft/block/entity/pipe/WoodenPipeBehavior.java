package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.energy.PowerHandler;

public class WoodenPipeBehavior extends PipeBehavior {
    @Override
    public PipeConnectionType canConnectTo(PipeBlockEntity blockEntity, PipeBlockEntity otherBlockEntity, PipeBehavior otherPipeBehavior) {
        if (super.canConnectTo(blockEntity, otherBlockEntity, otherPipeBehavior) != PipeConnectionType.NONE && otherPipeBehavior != Buildcraft.woodenPipeBehavior) {
            return PipeConnectionType.NORMAL;
        }
        return PipeConnectionType.NONE;
    }

    @Override
    public void doWork(PoweredPipeBlockEntity blockEntity, PowerHandler powerHandler) {
        if (blockEntity.transporter instanceof ItemPipeTransporter itemTransporter) {
        }
    }
}
