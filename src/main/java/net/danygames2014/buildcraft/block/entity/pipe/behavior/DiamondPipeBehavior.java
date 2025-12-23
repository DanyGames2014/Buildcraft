package net.danygames2014.buildcraft.block.entity.pipe.behavior;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.block.entity.pipe.DiamondPipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

public class DiamondPipeBehavior extends PipeBehavior {
    @Override
    public Direction routeItem(PipeBlockEntity blockEntity, ObjectArrayList<Direction> validOutputDirections, TravellingItemEntity item) {
        ObjectArrayList<Direction> directions = new ObjectArrayList<>(validOutputDirections);
        directions.remove(item.input);
        if (blockEntity instanceof DiamondPipeBlockEntity diamondPipeBlockEntity) {
            directions.retainAll(diamondPipeBlockEntity.getItemRoutes(item.stack));
        }
        
        if (directions.isEmpty()) {
            return null;
        }

        return directions.get(blockEntity.random.nextInt(directions.size()));
    }
}
