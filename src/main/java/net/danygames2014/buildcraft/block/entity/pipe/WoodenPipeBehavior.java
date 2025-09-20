package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.energy.PowerHandler;

public class WoodenPipeBehavior extends PipeBehavior {
    @Override
    public boolean canConnectTo(PipeBlockEntity blockEntity, PipeBlockEntity otherBlockEntity, PipeBehavior otherPipeBehavior) {
        return super.canConnectTo(blockEntity, otherBlockEntity, otherPipeBehavior) && otherPipeBehavior != Buildcraft.woodenPipeBehavior;
    }

    @Override
    public void doWork(PoweredPipeBlockEntity blockEntity, PowerHandler powerHandler) {
        if (blockEntity.transporter instanceof ItemPipeTransporter itemTransporter) {
        }
    }
}
