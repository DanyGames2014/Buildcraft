package net.danygames2014.buildcraft.block.entity.pipe.behavior;

import com.google.common.collect.Multiset;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.block.entity.pipe.DiamondPipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.ForgeDirection;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.danygames2014.nyalib.fluid.FluidStack;
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

    @Override
    public Multiset<ForgeDirection> routeFluid(PipeBlockEntity blockEntity, Multiset<ForgeDirection> directions, FluidStack fluidStack) {
        if (blockEntity instanceof DiamondPipeBlockEntity diamondPipeBlockEntity) {
            ObjectArrayList<Direction> validRoutes = diamondPipeBlockEntity.getFluidRoutes(fluidStack.fluid);
            directions.retainAll(validRoutes.stream().map(ForgeDirection::fromDirection).toList());
            return directions;
        }
        
        return super.routeFluid(blockEntity, directions, fluidStack);
    }
}
