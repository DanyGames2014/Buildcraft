package net.danygames2014.buildcraft.block.entity.pipe;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.danygames2014.buildcraft.block.entity.pipe.TravellingFluid.FlowDirection;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.fluidhandler.FluidHandlerBlockCapability;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Map;
import java.util.Random;

public class FluidPipeTransporter extends PipeTransporter {
    public static final int MAXIMUM_FILL_LEVEL = 20;
    public static final int TRANSFER_DELAY = 10;
    public static final int FLOW_RATE = 10;
    // TODO: When pipe is updated, remove the keys belonging to non existent sides.
    public Object2ObjectOpenHashMap<FlowDirection, ObjectOpenHashSet<TravellingFluid>> contents;

    public FluidPipeTransporter(PipeBlockEntity blockEntity) {
        super(blockEntity);
        this.contents = new Object2ObjectOpenHashMap<>();
        initContents();
    }

    public void initContents() {
        for (FlowDirection flowDirection : FlowDirection.values()) {
            this.contents.put(flowDirection, new ObjectOpenHashSet<>());
        }
    }

    @Override
    public PipeType getType() {
        return PipeType.FLUID;
    }

    @Override
    public void tick() {
        super.tick();

        if (world.isRemote || world.getTime() % TRANSFER_DELAY != 0) {
            return;
        }

        for (FlowDirection section : contents.keySet()) {
            ObjectIterator<TravellingFluid> iterator = contents.get(section).iterator();
            while (iterator.hasNext()) {
                TravellingFluid fluid = iterator.next();

                if (fluid == null || fluid.invalid) {
                    iterator.remove();
                    continue;
                }

                fluid.tick();
                
                // Bounce Fluid
                if (fluid.bounceTimer >= 2) {
                    if (bounceFluid(fluid, section)) {
                        continue;
                    }
                }

                // Hand Off
                if (section != FlowDirection.CENTER) {
                    if (fluid.flowDirection != FlowDirection.CENTER) {
                        if (handOff(fluid, section)) {
                            iterator.remove();
                            continue;
                        }
                    }
                }

                // Flow to Sides
                if (section == FlowDirection.CENTER) {
                    if (flowToSides(fluid, section)) {
                        iterator.remove();
                        continue;
                    }
                }

                // Flow to Center
                if (section != FlowDirection.CENTER) {
                    if (fluid.flowDirection == FlowDirection.CENTER) {
                        if (flowToCenter(fluid, section)) {
                            iterator.remove();
                            continue;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onConnectionsUpdate() {
        for (Direction side : Direction.values()) {
            if (!blockEntity.validOutputDirections.contains(side)) {
                contents.get(FlowDirection.fromDirection(side)).clear();
            }

//            if (blockEntity.validOutputDirections.contains(direction) && !contents.containsKey(FlowDirection.fromDirection(direction))) {
//                contents.put(FlowDirection.fromDirection(direction), new ObjectOpenHashSet<>());
//            } else if (!blockEntity.validOutputDirections.contains(direction)) {
//                contents.remove(FlowDirection.fromDirection(direction));
//            }
        }
    }

    public int injectFluid(FluidStack stack, Direction side) {
        TravellingFluid travellingFluid = new TravellingFluid(world, this);
        travellingFluid.stack = stack;
//        travellingFluid.input = FlowDirection.fromDirection(side);
//        travellingFluid.flowDirection = FlowDirection.CENTER;
        travellingFluid.bounceTimer = 0;
        return injectFluid(travellingFluid, FlowDirection.fromDirection(side));
    }

    private int injectFluid(TravellingFluid fluid, Direction side) {
        return injectFluid(fluid, FlowDirection.fromDirection(side));
    }

    /**
     * Inject a fluid into the pipe
     *
     * @param fluid The travelling fluid to inject
     * @param side  The side to inject on
     * @return The amount of fluid that was injected
     */
    private int injectFluid(TravellingFluid fluid, FlowDirection side) {
        int sideCapacity = getSideRemainingCapacity(side);
        if (sideCapacity > 0) {
            var sideContents = contents.get(side);

            if (fluid.stack.amount <= sideCapacity) {
                fluid.input = side;
                fluid.flowDirection = FlowDirection.CENTER;
                sideContents.add(fluid);
                return fluid.stack.amount;
            } else {
                TravellingFluid splitFluid = fluid.split(sideCapacity);
                splitFluid.input = side;
                splitFluid.flowDirection = FlowDirection.CENTER;
                sideContents.add(splitFluid);
                return sideCapacity;
            }
        }

        return 0;
    }

    public boolean handOff(TravellingFluid fluid, FlowDirection section) {
        Direction side = FlowDirection.toDirecton(section);

        assert side != null;

        if (world.getBlockEntity(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ()) instanceof PipeBlockEntity pipe) {
            if (pipe.transporter instanceof FluidPipeTransporter otherTransporter) {
                int injectedAmount = otherTransporter.injectFluid(fluid, side.getOpposite());

                // If we injected any amount of fluid
                if (injectedAmount > 0) {
                    if (injectedAmount >= fluid.stack.amount) {
                        // Full amount was injected, the TravellingFluid object was transferred to the other transporter
                        return true;
                    } else {
                        // Full amount was not injected, a copy object was created in the other transporter. Reduce the amount of the original TravellingFluid object.
                        fluid.stack.amount -= injectedAmount;
                        return false;
                    }
                } else {
                    // No fluid was injected, increase the bounce timer.
                    fluid.bounceTimer++;
                    return false;
                }
            }
        }

        FluidHandlerBlockCapability cap = CapabilityHelper.getCapability(world, x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ(), FluidHandlerBlockCapability.class);
        if (cap != null) {
            if (cap.canInsertFluid(side.getOpposite())) {
                FluidStack returnedStack = cap.insertFluid(fluid.stack, side.getOpposite());
                
                if (returnedStack == null) {
                    return true;
                } else {
                    fluid.stack = returnedStack;
                    return false;
                }
            }
        }
        
        
        fluid.bounceTimer++;
        return false;
    }

    public boolean bounceFluid(TravellingFluid fluid, FlowDirection section) {
        if (fluid.flowDirection != FlowDirection.CENTER) {
            fluid.input = section;
            fluid.flowDirection = FlowDirection.CENTER;
        } else {
            fluid.input = FlowDirection.CENTER;
            fluid.flowDirection = FlowDirection.fromDirection(pickDirection());
        }
        fluid.bounceTimer = 0;
        return false;
    }

    public boolean flowToSides(TravellingFluid fluid, FlowDirection section) {
        int originalAmount = fluid.stack.amount;
        int injectedAmount = internalFlow(fluid, fluid.flowDirection);

        if (injectedAmount > 0) {
            return injectedAmount >= originalAmount;
        }
        
        fluid.bounceTimer++;
        return false;
    }

    public boolean flowToCenter(TravellingFluid fluid, FlowDirection section) {
        int originalAmount = fluid.stack.amount;
        int injectedAmount = internalFlow(fluid, FlowDirection.fromDirection(pickDirection(fluid)));

        if (injectedAmount > 0) {
            return injectedAmount >= originalAmount;
        }

        fluid.bounceTimer++;
        return false;
    }

    private int internalFlow(TravellingFluid fluid, FlowDirection newDirection) {
        int sideCapacity = getSideRemainingCapacity(fluid.flowDirection);
        if (sideCapacity > 0) {
            ObjectOpenHashSet<TravellingFluid> sideContents = contents.get(fluid.flowDirection);

            if (fluid.stack.amount <= sideCapacity) {
                fluid.flowDirection = newDirection;
                sideContents.add(fluid);
                return fluid.stack.amount;
            } else {
                TravellingFluid splitFluid = fluid.split(sideCapacity);
                splitFluid.flowDirection = newDirection;
                sideContents.add(splitFluid);
                return sideCapacity;
            }
        }

        return 0;
    }
    
    public Direction pickDirection() {
        ObjectArrayList<Direction> validDirections = new ObjectArrayList<>();
        
        for (Map.Entry<Direction, PipeConnectionType> con : blockEntity.connections.entrySet()) {
            if (con.getValue() == PipeConnectionType.NORMAL) {
                validDirections.add(con.getKey());
            }
        }
        
        if (validDirections.isEmpty()) {
            return null;
        } else {
            return validDirections.get(new Random().nextInt(validDirections.size()));
        }
    }

    public Direction pickDirection(TravellingFluid fluid) {
        ObjectArrayList<Direction> validDirections = new ObjectArrayList<>(blockEntity.validOutputDirections);
        
        if (validDirections.isEmpty()) {
            return null;
        } else {
            if (validDirections.size() == 1) {
                return validDirections.get(0);
            }
            
            validDirections.remove(FlowDirection.toDirecton(fluid.input));
            return validDirections.get(new Random().nextInt(validDirections.size()));
        }
    }

    // Capacity
    public int getSideRemainingCapacity(FlowDirection section) {
        return MAXIMUM_FILL_LEVEL - getSideFillLevel(section);
    }

    public int getSideFillLevel(FlowDirection section) {
        int fillevel = 0;

        if (!contents.containsKey(section)) {
            System.err.println(section);
            return fillevel;
        }

        for (TravellingFluid fluid : contents.get(section)) {
            fillevel += fluid.stack.amount;
        }
        return fillevel;
    }

    // NBT
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        initContents();
    }

    // Debug
    @Override
    public String toString() {
        return "FluidPipeTransporter{" +
                "contents=" + contents +
                '}';
    }
}
