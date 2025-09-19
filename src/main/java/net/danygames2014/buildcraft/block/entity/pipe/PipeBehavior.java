package net.danygames2014.buildcraft.block.entity.pipe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

/**
 * This class governs the behavior of a pipe
 * T
 */
public class PipeBehavior {
    public PipeBehavior() {
    }

    /**
     * @param blockEntity The block entity of the pipe this is called on
     * @param otherBlockEntity The block entity of the pipe we want to connect to
     * @param otherPipeBehavior The behavior of the pipe we want to connect to
     * @return If we can connect to the other pipe
     */
    public boolean canConnectTo(PipeBlockEntity blockEntity, PipeBlockEntity otherBlockEntity, PipeBehavior otherPipeBehavior){
        return otherBlockEntity.transporter.getType() == blockEntity.transporter.getType();
    }

    /**
     * Pick a direction in which the item will continue travelling
     * @param blockEntity The block entity of the blockEntity this is called on
     * @param validOutputDirections The directions in which the item can travel
     * @param item The item that we are picking a direction for
     * @return The direction in which the item will travel, or null if there is no valid direction
     */
    public Direction routeItem(PipeBlockEntity blockEntity, ObjectArrayList<Direction> validOutputDirections, TravellingItemEntity item) {
        ObjectArrayList<Direction> directions = new ObjectArrayList<>(validOutputDirections);
        directions.remove(item.input);

        if (directions.isEmpty()) {
            return null;
        }

        return directions.get(blockEntity.random.nextInt(directions.size()));
    }
    
    public double modifyItemSpeed(TravellingItemEntity item) {
        return 0.01D;
    }
}
