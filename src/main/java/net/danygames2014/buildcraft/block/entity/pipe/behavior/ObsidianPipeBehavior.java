package net.danygames2014.buildcraft.block.entity.pipe.behavior;

import net.danygames2014.buildcraft.api.energy.PowerHandler;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeConnectionType;
import net.danygames2014.buildcraft.block.entity.pipe.PipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.PoweredPipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.List;

@SuppressWarnings("rawtypes")
public class ObsidianPipeBehavior extends PipeBehavior {
    @Override
    public PipeConnectionType canConnectToPipe(PipeBlockEntity blockEntity, PipeBlockEntity otherBlockEntity, PipeBehavior otherPipeBehavior, Direction side) {
        if (otherPipeBehavior instanceof ObsidianPipeBehavior) {
            return PipeConnectionType.NONE;
        }
        
        return super.canConnectToPipe(blockEntity, otherBlockEntity, otherPipeBehavior, side);
    }

    @Override
    public void blockEntityTick(PipeBlockEntity blockEntity, PipeTransporter transporter) {
        if (transporter instanceof ItemPipeTransporter itemTransporter) {
            if (!blockEntity.connections.containsValue(PipeConnectionType.NORMAL)) {
                 return;   
            }
            
            Box collisionBox = blockEntity.pipeBlock.getCollisionShape(blockEntity.world, blockEntity.x, blockEntity.y, blockEntity.z);

            collisionBox.expand(0.2D, 0.2D, 0.2D);
            List entities = blockEntity.world.collectEntitiesByClass(ItemEntity.class, collisionBox);
            
            for (Object entity : entities) {
                if (entity instanceof ItemEntity itemEntity && !(entity instanceof TravellingItemEntity) && !itemEntity.dead) {
                    itemTransporter.addItem(itemEntity.stack);
                    itemEntity.markDead();
                }
            }
            
        }
    }

    @Override
    public void doWork(PoweredPipeBlockEntity blockEntity, PowerHandler powerHandler) {
        super.doWork(blockEntity, powerHandler);

        if (!blockEntity.connections.containsValue(PipeConnectionType.NORMAL)) {
            return;
        }
        
        for (int distance = 1; distance <= 5; distance++) {
            boolean hasEnergy = powerHandler.useEnergy(1, distance, false) >= 1;
            
            if (!hasEnergy) {
                return;
            }
            
            if (!succ(blockEntity, blockEntity.transporter, powerHandler, distance)) {
                return;
            }
        }
    }
    
    public boolean succ(PipeBlockEntity blockEntity, PipeTransporter transporter, PowerHandler powerHandler, int distance) {
        if (transporter instanceof ItemPipeTransporter itemTransporter) {
            Box collisionBox = blockEntity.pipeBlock.getCollisionShape(blockEntity.world, blockEntity.x, blockEntity.y, blockEntity.z);

            collisionBox = collisionBox.expand(distance, distance, distance);
            List entities = blockEntity.world.collectEntitiesByClass(ItemEntity.class, collisionBox);

            int succableCount = (int) powerHandler.useEnergy(distance, 64 * distance, false);
            
            for (Object entity : entities) {
                if (succableCount <= 0) {
                    return false;
                }
                
                if (entity instanceof ItemEntity itemEntity && !(entity instanceof TravellingItemEntity) && !itemEntity.dead) {
                    if (succableCount >= itemEntity.stack.count) {
                        itemTransporter.addItem(itemEntity.stack);
                        itemEntity.markDead();
                    } else {
                        itemTransporter.addItem(itemEntity.stack.split(succableCount));
                        return false;
                    }
                }
            }
            
            return true;
        }
        
        return false;
    }
}
