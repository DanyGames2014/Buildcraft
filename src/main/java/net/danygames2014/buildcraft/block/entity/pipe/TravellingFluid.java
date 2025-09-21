package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.nyalib.fluid.FluidStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

public class TravellingFluid {
    public static final int MINIMUM_TRANSFER_DELAY = 1;
    public static final int DEFAULT_TRANSFER_DELAY = 5;
    public static final int MAXIMUM_TRANSFER_DELAY = 20;
    public static final double ACCELERATION_MODIFIER = 0.5D;
    public static final double DECCELERATION_MODIFIER = 1.1D;
    
    public FluidPipeTransporter transporter;
    public double transferDelay = DEFAULT_TRANSFER_DELAY;
    public boolean invalid = false;


    /**
     * The direction the fluid is coming from. If this is null, its coming from the middle
     */
    public Direction input = null;
    /**
     * The direction this fluid is traveling to. If this is null, its going to the middle
     */
    public Direction travelDirection = null;
    
    public FluidStack stack;
    public World world;
    
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
        this.stack.amount -= amount;
        TravellingFluid splitFluid = new TravellingFluid(world, transporter);
        splitFluid.stack = splitStack;
        return splitFluid;
    }

    // NBT
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("input", input.getId());
        nbt.putInt("travelDirection", travelDirection.getId());
        nbt.putDouble("transferDelay", transferDelay);
    }

    public void read(NbtCompound nbt) {
        input = Direction.byId(nbt.getInt("input"));
        travelDirection = Direction.byId(nbt.getInt("travelDirection"));
        transferDelay = nbt.getDouble("transferDelay");
    }
    
    public enum TravelStage {
        ENTER,
        MIDDLE,
        EXIT;
    }
}
