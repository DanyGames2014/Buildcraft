package net.danygames2014.buildcraft.block.entity.pipe;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.danygames2014.buildcraft.block.entity.pipe.TravellingFluid.FlowDirection;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;

public class FluidPipeTransporter extends PipeTransporter {
    public static final int MAXIMUM_FILL_LEVEL = 10;
    public static final int TRANSFER_DELAY = 4;
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
            if (flowDirection == FlowDirection.OUT) {
                continue;
            }
            
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

        for (FlowDirection section : FlowDirection.values()) {
            ObjectIterator<TravellingFluid> iterator = contents.get(section).iterator();
            while (iterator.hasNext()) {
                TravellingFluid fluid = iterator.next();
                
                if (fluid == null || fluid.invalid) {
                    iterator.remove();
                    continue;
                }
                
                fluid.tick();
                
                if (section != FlowDirection.CENTER) {
                    if (handOff(fluid, section)) {
                        iterator.remove();
                        continue;
                    }

                    if (fluid.bounceTimer >= 5) {
                        bounceFluid(fluid, section);
                    }
                }
                
                if (section == FlowDirection.CENTER) {
                    flowToSides(fluid, section);
                }
                
                if (section != FlowDirection.CENTER) {
                    flowToCenter(fluid, section);
                }
            }
        }
    }

    public int injectFluid(FluidStack stack, Direction side) {
        TravellingFluid travellingFluid = new TravellingFluid(world, this);
        travellingFluid.stack = stack;
        travellingFluid.input = FlowDirection.fromDirection(side);
        travellingFluid.flowDirection = FlowDirection.CENTER;
        travellingFluid.bounceTimer = 0;
        return injectFluid(travellingFluid, side);
    }

    /**
     * Inject a fluid into the pipe
     *
     * @param fluid The travelling fluid to inject
     * @param side  The side to inject on
     * @return The amount of fluid that was injected
     */
    private int injectFluid(TravellingFluid fluid, Direction side) {
        int sideCapacity = getSideRemainingCapacity(FlowDirection.fromDirection(side));
        if (sideCapacity > 0) {
            var sideContents = contents.get(FlowDirection.fromDirection(side));

            if (fluid.stack.amount <= sideCapacity) {
                sideContents.add(fluid);
                return fluid.stack.amount;
            } else {
                sideContents.add(fluid.split(sideCapacity));
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
        
        // TODO: Hand-off to FluidHandlerBlockCapability
        
        return false;
    }
    
    public void bounceFluid(TravellingFluid fluid, FlowDirection section) {
        fluid.input = section;
        fluid.flowDirection = FlowDirection.CENTER; 
        fluid.bounceTimer = 0;
    }
    
    public void flowToSides(TravellingFluid fluid, FlowDirection section) {
        
    }
    
    public void flowToCenter(TravellingFluid fluid, FlowDirection section) {
        
    }

    // Capacity
    public int getSideRemainingCapacity(FlowDirection section) {
        return MAXIMUM_FILL_LEVEL - getSideFillLevel(section);
    }

    public int getSideRemainingCapacity(int side) {
        return getSideRemainingCapacity(FlowDirection.values()[side]);
    }

    public int getSideFillLevel(FlowDirection section) {
        int fillevel = 0;
        for (TravellingFluid fluid : contents.get(section)) {
            fillevel += fluid.stack.amount;
        }
        return fillevel;
    }
    
    public int getSideFillLevel(int side) {
        return getSideFillLevel(FlowDirection.values()[side]);
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
