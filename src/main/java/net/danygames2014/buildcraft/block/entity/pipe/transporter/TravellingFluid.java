package net.danygames2014.buildcraft.block.entity.pipe.transporter;

import net.danygames2014.nyalib.fluid.FluidStack;
import net.minecraft.world.World;

public class TravellingFluid {
    public FluidPipeTransporter transporter;
    public World world;

    public FluidStack stack;
    
    public TravellingFluid(World world, FluidPipeTransporter transporter) {
        this.world = world;
        this.transporter = transporter;
    }
    
    // NBT
    @Override
    public String toString() {
        return "TravellingFluid{" +
                "stack=" + stack +
                '}';
    }
}
