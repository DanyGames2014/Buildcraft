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

    @Override
    public PipeConnectionType canConnectTo(BlockEntity other, Direction side) {
        FluidHandlerBlockCapability cap = CapabilityHelper.getCapability(other.world, other.x, other.y, other.z, FluidHandlerBlockCapability.class);
        
        if (cap != null) {
            return cap.canConnectFluid(side.getOpposite()) ? PipeConnectionType.NORMAL : PipeConnectionType.NONE;
        }
        
        return PipeConnectionType.NONE;
    }
}
