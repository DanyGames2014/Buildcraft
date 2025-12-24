package net.danygames2014.buildcraft.block.entity.pipe.behavior;

import com.google.common.collect.Multiset;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.block.entity.pipe.ForgeDirection;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeConnectionType;
import net.danygames2014.buildcraft.block.entity.pipe.PipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.FluidPipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter.FailedPathingResult;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.modificationstation.stationapi.api.util.math.Direction;

public class VoidPipeBehavior extends PipeBehavior {
    @Override
    public PipeConnectionType canConnectToPipe(PipeBlockEntity blockEntity, PipeBlockEntity otherBlockEntity, PipeBehavior otherPipeBehavior, Direction side) {
        if (otherPipeBehavior instanceof VoidPipeBehavior) {
            return PipeConnectionType.NONE;
        }
        
        return super.canConnectToPipe(blockEntity, otherBlockEntity, otherPipeBehavior, side);
    }

    @Override
    public Direction routeItem(PipeBlockEntity blockEntity, ObjectArrayList<Direction> validOutputDirections, TravellingItemEntity item) {
        return null;
    }

    @Override
    public FailedPathingResult getFailedPathingResult(PipeBlockEntity blockEntity, ItemPipeTransporter transporter, TravellingItemEntity item) {
        return FailedPathingResult.VOID;
    }

    @Override
    public Multiset<ForgeDirection> routeFluid(PipeBlockEntity blockEntity, Multiset<ForgeDirection> directions, FluidStack fluidStack) {
        directions.clear();
        return directions;
    }

    @Override
    public void transporterTick(PipeBlockEntity blockEntity, PipeTransporter transporter) {
        if (transporter instanceof FluidPipeTransporter fluidTransporter) {
            for (FluidPipeTransporter.PipeSection section : fluidTransporter.sections) {
                section.drain(Integer.MAX_VALUE, true);
            }
        }
    }
}
