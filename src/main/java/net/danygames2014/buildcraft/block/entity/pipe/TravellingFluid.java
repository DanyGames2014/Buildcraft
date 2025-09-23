package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.nyalib.fluid.FluidStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

public class TravellingFluid {
    public FluidPipeTransporter transporter;
    public World world;

    public FluidStack stack;
    public FlowDirection input;
    public FlowDirection flowDirection;
    public int bounceTimer = 0;
    public double transferDelay = 0;
    public boolean invalid = false;
    
    public TravellingFluid(World world, FluidPipeTransporter transporter) {
        this.world = world;
        this.transporter = transporter;
    }
    
    public void tick() {
        // Transfer delay
//        double transferDelay = transporter.blockEntity.behavior.modifyFluidTransferDelay(this);
//        
//        if (this.transferDelay != transferDelay) {
//            if (transferDelay < this.transferDelay) {
//                this.transferDelay = Math.min(transferDelay, transferDelay * ACCELERATION_MODIFIER);
//            } else if (transferDelay > this.transferDelay) {
//                this.transferDelay *= DECCELERATION_MODIFIER;
//            }
//            
//            if (this.transferDelay < MINIMUM_TRANSFER_DELAY) {
//                this.transferDelay = MINIMUM_TRANSFER_DELAY;
//            } else if (this.transferDelay > MAXIMUM_TRANSFER_DELAY) {
//                this.transferDelay = MAXIMUM_TRANSFER_DELAY;
//            }
//        }
    }
    
    public TravellingFluid split(int amount) {
        if (stack.amount <= amount) {
            return this;
        }
        
        FluidStack splitStack = stack.copy();
        splitStack.amount = amount;
        this.stack.amount -= splitStack.amount;
        TravellingFluid splitFluid = new TravellingFluid(world, transporter);
        splitFluid.stack = splitStack;
        return splitFluid;
    }
    
    // NBT
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("input", input.ordinal());
        nbt.putInt("travelDirection", flowDirection.ordinal());
        nbt.putDouble("transferDelay", transferDelay);
    }

    public void read(NbtCompound nbt) {
        input = FlowDirection.values()[nbt.getInt("input")];
        flowDirection = FlowDirection.values()[nbt.getInt("travelDirection")];
        transferDelay = nbt.getDouble("transferDelay");
    }

    @Override
    public String toString() {
        return "TravellingFluid{" +
                "input=" + input +
                ", travelDirection=" + flowDirection +
                ", stack=" + stack +
                '}';
    }
    
    public enum FlowDirection {
        DOWN,
        UP,
        EAST,
        WEST,
        NORTH,
        SOUTH,
        CENTER;
        
        public static FlowDirection fromDirection(Direction direction) {
            switch (direction) {
                case DOWN -> {
                    return DOWN;
                }
                case UP -> {
                    return UP;
                }
                case EAST -> {
                    return EAST;
                }
                case WEST -> {
                    return WEST;
                }
                case NORTH -> {
                    return NORTH;
                }
                case SOUTH -> {
                    return SOUTH;
                }
                default -> {
                    return null;
                }
            }
        }
        
        public static Direction toDirecton(FlowDirection direction) {
            switch (direction) {
                case NORTH -> {
                    return Direction.NORTH;
                }
                case SOUTH -> {
                    return Direction.SOUTH;
                }
                case EAST -> {
                    return Direction.EAST;
                }
                case WEST -> {
                    return Direction.WEST;
                }
                case UP -> {
                    return Direction.UP;
                }
                case DOWN -> {
                    return Direction.DOWN;
                }
                default -> {
                    return null;
                }
            }
        }
    }
}
