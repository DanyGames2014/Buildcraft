package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.fluidhandler.FluidHandlerBlockCapability;
import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

public class FluidPipeTransporter extends PipeTransporter {
    public FluidPipeTransporter(PipeBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public PipeType getType() {
        return PipeType.FLUID;
    }
}
