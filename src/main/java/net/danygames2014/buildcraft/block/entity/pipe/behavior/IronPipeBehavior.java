package net.danygames2014.buildcraft.block.entity.pipe.behavior;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeConnectionType;
import net.danygames2014.buildcraft.block.entity.pipe.PipeType;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.danygames2014.uniwrench.api.WrenchMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

public class IronPipeBehavior extends PipeBehavior {
    @Override
    public PipeConnectionType canConnectToPipe(PipeBlockEntity blockEntity, PipeBlockEntity otherBlockEntity, PipeBehavior otherPipeBehavior, Direction side) {
        PipeConnectionType connectionType = super.canConnectToPipe(blockEntity, otherBlockEntity, otherPipeBehavior, side);

        if (connectionType == PipeConnectionType.NORMAL) {
            if (blockEntity.connections.containsValue(PipeConnectionType.NORMAL) && blockEntity.connections.get(side) != PipeConnectionType.NORMAL) {
                return PipeConnectionType.ALTERNATE;
            } else {
                return PipeConnectionType.NORMAL;
            }
        }

        return connectionType;
    }

    @Override
    public PipeConnectionType getConnectionType(PipeType type, PipeBlockEntity blockEntity, World world, int x, int y, int z, Direction side) {
        PipeConnectionType connectionType = super.getConnectionType(type, blockEntity, world, x, y, z, side);

        if (connectionType == PipeConnectionType.NORMAL) {
            if (blockEntity.connections.containsValue(PipeConnectionType.NORMAL) && blockEntity.connections.get(side) != PipeConnectionType.NORMAL) {
                return PipeConnectionType.ALTERNATE;
            } else {
                return PipeConnectionType.NORMAL;
            }
        }
        
        return connectionType;
    }

    @Override
    public boolean isValidOutputDirection(PipeBlockEntity blockEntity, Direction side, PipeConnectionType connectionType) {
        return connectionType == PipeConnectionType.NORMAL;
    }

    @Override
    public Direction routeItem(PipeBlockEntity blockEntity, ObjectArrayList<Direction> validOutputDirections, TravellingItemEntity item) {
        if (validOutputDirections.isEmpty()) {
            return null;
        }

        return validOutputDirections.get(blockEntity.random.nextInt(validOutputDirections.size()));
    }

    @Override
    public boolean isFluidOutputOpen(PipeBlockEntity blockEntity, Direction side, PipeConnectionType connectionType) {
        return connectionType == PipeConnectionType.NORMAL;
    }

    @Override
    public boolean wrenchRightClick(PipeBlockEntity blockEntity, ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side, WrenchMode wrenchMode) {
        Object2ObjectOpenHashMap<Direction, PipeConnectionType> connections = blockEntity.connections;
        
        // Check if there is a side to switch to
        // If there isn't return
        if (!(connections.containsValue(PipeConnectionType.NORMAL) && connections.containsValue(PipeConnectionType.ALTERNATE))) {
            return true;
        }
        
        // Get the iterator over connections
        var iter = connections.object2ObjectEntrySet().fastIterator();
        
        // Try to find a normal side with a following alternate side
        while (iter.hasNext()) {
            var nextSide = iter.next();
            // If we find a normal side, continue iterating to find an alternate side
            if (nextSide.getValue() == PipeConnectionType.NORMAL) {
                Direction normalSide = nextSide.getKey();
                while (iter.hasNext()) {
                    var next = iter.next();
                    // If we find a following alternative side 
                    // we set it to normal and the normal side to alternative and return
                    if (next.getValue() == PipeConnectionType.ALTERNATE) {
                        connections.put(next.getKey(), PipeConnectionType.NORMAL);
                        connections.put(normalSide, PipeConnectionType.ALTERNATE);
                        blockEntity.neighborUpdate();
                        world.blockUpdateEvent(x,y,z);
                        return true;
                    }
                }
                
                // We have reached the end of the connections and havent found an alternate side, we need to loop over it again from start
                iter = connections.object2ObjectEntrySet().fastIterator();
                while (iter.hasNext()) {
                    var next = iter.next();
                    
                    // Check if we havent gone back to start
                    if (next.getKey() == normalSide) {
                        //noinspection StringConcatenationArgumentToLogCall
                        Buildcraft.LOGGER.warn("IronPipeBehavior.wrenchRightClick iteration looped over. " + connections);
                        return true;
                    }
                    
                    // If we find a following alternative side 
                    // we set it to normal and the normal side to alternative and return
                    if (next.getValue() == PipeConnectionType.ALTERNATE) {
                        connections.put(next.getKey(), PipeConnectionType.NORMAL);
                        connections.put(normalSide, PipeConnectionType.ALTERNATE);
                        blockEntity.neighborUpdate();
                        world.blockUpdateEvent(x,y,z);
                        return true;
                    }
                }
            }
        }
        
        return true;
    }
}
