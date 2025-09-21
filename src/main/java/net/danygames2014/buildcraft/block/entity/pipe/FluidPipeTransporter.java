package net.danygames2014.buildcraft.block.entity.pipe;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.fluidhandler.FluidHandlerBlockCapability;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Iterator;

public class FluidPipeTransporter extends PipeTransporter {
    public static final int MAXIMUM_FILL_LEVEL = 10;

    public ObjectOpenHashSet<TravellingFluid> contents;
    /**
     * Index 0-5 is {@link Direction} ids. Index 6 is the center;
     */
    public int[] fillLevel = new int[7];

    public FluidPipeTransporter(PipeBlockEntity blockEntity) {
        super(blockEntity);
        this.contents = new ObjectOpenHashSet<>();
    }

    @Override
    public PipeType getType() {
        return PipeType.FLUID;
    }

    @Override
    public void tick() {
        super.tick();

        validateFillLevel();

        Iterator<TravellingFluid> iterator = contents.iterator();
        while (iterator.hasNext()) {
            TravellingFluid fluid = iterator.next();

            if (fluid == null || fluid.invalid) {
                iterator.remove();
                continue;
            }

            fluid.tick();

            if (fluid.transferDelay <= 0) {
                switch (fluid.stage) {
                    case ENTER -> {
                        fluid.stage = TravellingFluid.TravelStage.MIDDLE;
                    }

                    case MIDDLE -> {
                        fluid.stage = TravellingFluid.TravelStage.EXIT;
                    }

                    case EXIT -> {

                    }
                }
            }

            fluid.transferDelay--;
        }
    }

    /**
     * Inject a fluid into the pipe
     *
     * @param fluid The travelling fluid to inject
     * @param side  The side to inject on
     * @return The amount of fluid that was injected
     */
    public int injectFluid(TravellingFluid fluid, Direction side) {
        // Check if the fluid is already in the pipe
        for (var content : contents) {
            if (!content.stack.isFluidEqual(fluid.stack)) {
                return 0;
            }
        }

        int sideCapacity = getSideCapacity(side);
        if (sideCapacity > 0) {
            if (sideCapacity >= fluid.stack.amount) {
                fillLevel[side.ordinal()] += fluid.stack.amount;
                contents.add(fluid);
                return fluid.stack.amount;
            } else {
                TravellingFluid newFluid = fluid.split(sideCapacity);
                fillLevel[side.ordinal()] += sideCapacity;
                contents.add(newFluid);
                return sideCapacity;
            }
        }

        return 0;
    }

    /**
     * @param fluid 
     * @return
     */
    public int flowInternally(TravellingFluid fluid) {
        int side = fluid.stage == TravellingFluid.TravelStage.MIDDLE ? fluid.travelDirection.getId() : 6;
        
        return 0;
    }

    /**
     * Hand-off fluid to another pipe or Fluid Handler
     * @param fluid The fluid to hand-off
     * @return WHether the fluid was fully handed off and can be removed
     */
    public boolean handOffFluid(TravellingFluid fluid) {
        Direction side = fluid.travelDirection;

        if (world.getBlockEntity(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ()) instanceof PipeBlockEntity pipe) {
            if (pipe.transporter instanceof FluidPipeTransporter otherTransporter) {
                int injectedAmount = otherTransporter.injectFluid(fluid, side.getOpposite());
                
                if (injectedAmount <= 0) {
                    return false;
                } else if (injectedAmount >= fluid.stack.amount) {
                    fillLevel[side.ordinal()] -= fluid.stack.amount;
                    return true;
                } else {
                    fluid.stack.amount -= injectedAmount;
                    fillLevel[side.ordinal()] -= injectedAmount;
                    return false;
                }
            }
        }

        FluidHandlerBlockCapability cap = CapabilityHelper.getCapability(world, x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ(), FluidHandlerBlockCapability.class);
        if (cap != null) {
            if (cap.canInsertFluid(side.getOpposite())) {
                FluidStack returnedStack = cap.insertFluid(fluid.stack, side.getOpposite());
                if (returnedStack == null) {
                    fillLevel[side.ordinal()] -= fluid.stack.amount;
                    fluid.invalid = true;
                    return true;
                } else {
                    fillLevel[side.ordinal()] -= fluid.stack.amount - returnedStack.amount;
                    fluid.stack = returnedStack;
                    return false;
                }
            }
        }
        
        return false;
    }

    public void validateFillLevel() {
        for (int i = 0; i < 6; i++) {
            if (fillLevel[i] > MAXIMUM_FILL_LEVEL) {
                Buildcraft.LOGGER.warn("Fill level for side " + i + " is too high! Setting it to " + MAXIMUM_FILL_LEVEL);
                fillLevel[i] = MAXIMUM_FILL_LEVEL;
            }
        }
    }

    public int getSideCapacity(Direction side) {
        if (side == null) {
            return MAXIMUM_FILL_LEVEL - fillLevel[6];
        }

        return MAXIMUM_FILL_LEVEL - fillLevel[side.ordinal()];
    }
}
