package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

public class ItemPipeTransporter extends PipeTransporter {
    public ItemPipeTransporter(PipeBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public PipeType getType() {
        return PipeType.ITEM;
    }

    @Override
    public boolean canConnectTo(BlockEntity other, Direction side) {
        ItemHandlerBlockCapability cap = CapabilityHelper.getCapability(other.world, other.x, other.y, other.z, ItemHandlerBlockCapability.class);
        
        if (cap != null) {
            return cap.canConnectItem(side.getOpposite());
        }
        
        return false; 
    }
}
