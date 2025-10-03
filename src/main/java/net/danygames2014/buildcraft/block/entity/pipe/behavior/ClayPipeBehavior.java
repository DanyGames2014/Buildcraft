package net.danygames2014.buildcraft.block.entity.pipe.behavior;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter.HandOffResult;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.util.math.Direction;

public class ClayPipeBehavior extends PipeBehavior {
    @Override
    public Direction routeItem(PipeBlockEntity blockEntity, ObjectArrayList<Direction> validOutputDirections, TravellingItemEntity item) {
        ObjectArrayList<Direction> directions = new ObjectArrayList<>(validOutputDirections);
        
        for (Direction side : directions) {
            var cap = CapabilityHelper.getCapability(blockEntity.world, blockEntity.x + side.getOffsetX(), blockEntity.y + side.getOffsetY(), blockEntity.z + side.getOffsetZ(), ItemHandlerBlockCapability.class);
            if (cap != null) {
                if (!cap.canInsertItem(side.getOpposite())) {
                    directions.remove(side);
                    continue;
                }

                boolean canInsert = false;
                for (ItemStack stack : cap.getInventory(side.getOpposite())) {
                    if (stack == null) {
                        canInsert = true;
                        break;
                    }
                    
                    if (stack.isItemEqual(item.stack) && stack.count < stack.getMaxCount()) {
                        canInsert = true;
                        break;
                    }
                }

                if (!canInsert) {
                    directions.remove(side);
                }
            }
        }
        
        if (directions.size() > 1) {
            directions.remove(item.input);
        }

        if (directions.isEmpty()) {
            return null;
        }

        return directions.get(blockEntity.random.nextInt(directions.size()));
    }

    @Override
    public HandOffResult getFailedInsertResult(PipeBlockEntity blockEntity, ItemPipeTransporter transporter, TravellingItemEntity item) {
        return HandOffResult.BOUNCE;
    }
}
