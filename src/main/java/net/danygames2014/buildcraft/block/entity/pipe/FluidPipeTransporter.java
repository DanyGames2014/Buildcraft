package net.danygames2014.buildcraft.block.entity.pipe;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.fluidhandler.FluidHandlerBlockCapability;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Arrays;
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
                    case ENTER, MIDDLE -> {
                        flowInternally(fluid);
                        fluid.transferDelay = TravellingFluid.DEFAULT_TRANSFER_DELAY;
                    }

                    case EXIT -> {
                        if (handOffFluid(fluid)) {
                            iterator.remove();
                        } else {
                            fluid.transferDelay = TravellingFluid.DEFAULT_TRANSFER_DELAY;
                        }
                    }
                }
            }

            fluid.transferDelay--;
        }
    }

    public int injectFluid(FluidStack stack, Direction side) {
        TravellingFluid travellingFluid = new TravellingFluid(world, this);
        travellingFluid.stack = stack;
        travellingFluid.travelDirection = side.getOpposite();
        travellingFluid.lastTravelDirection = travellingFluid.travelDirection;
        travellingFluid.stage = TravellingFluid.TravelStage.ENTER;
        travellingFluid.transferDelay = TravellingFluid.DEFAULT_TRANSFER_DELAY;
        return injectFluid(travellingFluid, side);
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
     * @param fluid The fluid to flow internally
     */
    public void flowInternally(TravellingFluid fluid) {
        int inputSide;
        int outputSide;

        switch (fluid.stage) {
            case ENTER -> {
                inputSide = fluid.input.ordinal();
                outputSide = 6;
            }
            case MIDDLE -> {
                inputSide = 6;
                outputSide = fluid.travelDirection.ordinal();
            }
            default -> {
                throw new IllegalStateException("flowInternally called with invalid stage: " + fluid.stage);
            }
        }

        int outputCapacity = getSideCapacity(outputSide);
        if (outputCapacity > 0) {
            if (outputCapacity >= fluid.stack.amount) {
                // We are moving the entire fluid stack
                fillLevel[outputSide] += fluid.stack.amount;
                fillLevel[inputSide] -= fluid.stack.amount;
                
                if (fluid.stage == TravellingFluid.TravelStage.ENTER) {
                    fluid.stage = TravellingFluid.TravelStage.MIDDLE;
                    fluid.travelDirection = blockEntity.behavior.routeFluid(blockEntity, blockEntity.validOutputDirections, fluid);
                } else {
                    fluid.stage = TravellingFluid.TravelStage.EXIT;
                }

            } else {
                // We are splitting of the amount from the fluid stack that will fit and moving that
                TravellingFluid newFluid = fluid.split(outputCapacity);
                fillLevel[outputSide] += outputCapacity;
                fillLevel[inputSide] -= outputCapacity;
                
                if (fluid.stage == TravellingFluid.TravelStage.ENTER) {
                    newFluid.stage = TravellingFluid.TravelStage.MIDDLE;
                    newFluid.travelDirection = blockEntity.behavior.routeFluid(blockEntity, blockEntity.validOutputDirections, newFluid);
                } else {
                    newFluid.stage = TravellingFluid.TravelStage.EXIT;
                }
                newFluid.transferDelay = TravellingFluid.DEFAULT_TRANSFER_DELAY;

                contents.add(newFluid);
            }
        }
    }

    /**
     * Hand-off fluid to another pipe or Fluid Handler
     *
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
            return getSideCapacity(6);
        }

        return getSideCapacity(side.ordinal());

    }

    public int getSideCapacity(int side) {
        return MAXIMUM_FILL_LEVEL - fillLevel[side];
    }

    @Override
    public String toString() {
        return "FluidPipeTransporter{" +
                "contents=" + contents +
                ", fillLevel=" + Arrays.toString(fillLevel) +
                '}';
    }
}
