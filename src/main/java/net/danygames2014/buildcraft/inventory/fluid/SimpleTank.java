package net.danygames2014.buildcraft.inventory.fluid;

import net.danygames2014.nyalib.fluid.FluidHandler;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class SimpleTank implements FluidHandler {
    @Override
    public boolean canExtractFluid(@Nullable Direction direction) {
        return false;
    }

    @Override
    public FluidStack extractFluid(int i, int i1, @Nullable Direction direction) {
        return null;
    }

    @Override
    public boolean canInsertFluid(@Nullable Direction direction) {
        return false;
    }

    @Override
    public FluidStack insertFluid(FluidStack fluidStack, int i, @Nullable Direction direction) {
        return null;
    }

    @Override
    public FluidStack insertFluid(FluidStack fluidStack, @Nullable Direction direction) {
        return null;
    }

    @Override
    public FluidStack getFluid(int i, @Nullable Direction direction) {
        return null;
    }

    @Override
    public boolean setFluid(int i, FluidStack fluidStack, @Nullable Direction direction) {
        return false;
    }

    @Override
    public int getFluidSlots(@Nullable Direction direction) {
        return 0;
    }

    @Override
    public int getFluidCapacity(int i, @Nullable Direction direction) {
        return 0;
    }

    @Override
    public FluidStack[] getFluids(@Nullable Direction direction) {
        return new FluidStack[0];
    }

    @Override
    public boolean canConnectFluid(Direction direction) {
        return false;
    }
}
