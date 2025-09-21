package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.nyalib.fluid.FluidHandler;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class TankBlockEntity extends BlockEntity implements FluidHandler {
    public static final int CAPACITY = 10000;
    public FluidStack fluid;

    @Override
    public void tick() {
        super.tick();

        if(fluid != null){
            moveLiquidBelow();
        }
    }

    public TankBlockEntity getBottomTank(){
        TankBlockEntity lastTank = this;
        while(true){
            TankBlockEntity below = getTankBelow(lastTank);
            if(below != null){
                lastTank = below;
            } else {
                break;
            }
        }
        return lastTank;
    }

    public TankBlockEntity getTopTank(){
        TankBlockEntity lastTank = this;
        while(true){
            TankBlockEntity above = getTankAbove(lastTank);
            if(above != null){
                lastTank = above;
            } else {
                break;
            }
        }
        return lastTank;
    }

    public static TankBlockEntity getTankBelow(TankBlockEntity tank){
        BlockEntity below = tank.world.getBlockEntity(tank.x, tank.y - 1, tank.z);
        if(below instanceof TankBlockEntity belowTank){
            return belowTank;
        }
        else {
            return null;
        }
    }

    public static TankBlockEntity getTankAbove(TankBlockEntity tank){
        BlockEntity above = tank.world.getBlockEntity(tank.x, tank.y + 1, tank.z);
        if(above instanceof TankBlockEntity aboveTank){
            return aboveTank;
        }
        else {
            return null;
        }
    }

    public void moveLiquidBelow(){
        TankBlockEntity below = getTankBelow(this);
        if(below == null){
            return;
        }
        fluid = below.insertFluid(fluid, Direction.UP);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if(nbt.contains("fluid")){
            NbtCompound fluidCompound = nbt.getCompound("fluid");
            fluid = new FluidStack(fluidCompound);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if(fluid == null){
            return;
        }
        NbtCompound fluidCompound = new NbtCompound();
        fluid.writeNbt(fluidCompound);
        nbt.put("fluid", fluidCompound);
    }

    @Override
    public boolean canExtractFluid(@Nullable Direction direction) {
        return true;
    }

    @Override
    public FluidStack extractFluid(int slot, int amount, @Nullable Direction direction) {
        FluidStack returnStack = fluid.copy();
        returnStack.amount = amount;
        return returnStack;
    }

    @Override
    public boolean canInsertFluid(@Nullable Direction direction) {
        return true;
    }

    @Override
    public FluidStack insertFluid(FluidStack stack, int slot, @Nullable Direction direction) {
        return this.insertFluid(stack, direction);
    }

    @Override
    public FluidStack insertFluid(FluidStack stack, @Nullable Direction direction) {
        TankBlockEntity currentTank = getBottomTank();
        while(stack.amount > 0 && currentTank != null){
            if(fluid != null && currentTank.fluid != null && !currentTank.fluid.isFluidEqual(fluid)){
                break;
            }
            FluidStack currentStack = currentTank.fluid;

            if (currentStack == null) {
                currentStack = new FluidStack(stack.fluid, 0);
            }
            int capacity = currentTank.getFluidCapacity(0, direction);
            int space = capacity - currentStack.amount;
            int addedAmount = Math.min(stack.amount, space);
            currentStack.amount += addedAmount;
            stack.amount -= addedAmount;

            currentTank.fluid = currentStack;

            currentTank = getTankAbove(currentTank);
        }
        return stack.amount > 0 ? stack : null;
    }

    @Override
    public FluidStack getFluid(int slot, @Nullable Direction direction) {
        return getBottomTank().fluid;
    }

    @Override
    public boolean setFluid(int slot, FluidStack stack, @Nullable Direction direction) {
        fluid = stack;
        return true;
    }

    @Override
    public int getFluidSlots(@Nullable Direction direction) {
        return 1;
    }

    @Override
    public int getFluidCapacity(int slot, @Nullable Direction direction) {
        int capacity = 0;
        TankBlockEntity currentTank = getBottomTank();
        while(currentTank != null){
            if(fluid != null && currentTank.fluid != null && !currentTank.fluid.isFluidEqual(fluid)){
                break;
            }
            capacity += CAPACITY;
            currentTank = getTankAbove(currentTank);
        }
        return capacity;
    }

    @Override
    public FluidStack[] getFluids(@Nullable Direction direction) {
        return new FluidStack[0];
    }

    @Override
    public boolean canConnectFluid(Direction direction) {
        return true;
    }
}
