package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.energy.EngineCoolant;
import net.danygames2014.buildcraft.api.energy.EngineCoolantRegistry;
import net.danygames2014.buildcraft.api.energy.EngineFuel;
import net.danygames2014.buildcraft.api.energy.EngineFuelRegistry;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.item.fluidhandler.FluidHandlerItemCapability;
import net.danygames2014.nyalib.fluid.*;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class CombustionEngineBlockEntity extends BaseEngineWithInventoryBlockEntity implements FluidHandler {
    // Constants
    public static final int FLUID_CAPACITY = 10000;
    public static float HEAT_PER_MJ = 0.0023F;
    public static float COOLDOWN_RATE = 0.05F;
    public static int MAX_COOLANT_PER_TICK = 40;

    // Burn Time
    int burnTime = 0;

    // Fluid Tank
    // 0 - Coolant
    // 1 - Fuel
    FluidStack[] fluidInventory;
    EngineFuel currentFuel = null;
    int penaltyCooling = 0;
    boolean lastPowered = false;

    float biomeTemperature = -1;

    public CombustionEngineBlockEntity() {
        super(1);
        fluidInventory = new FluidStack[2];
    }

    @Override
    protected void engineUpdate() {
        super.engineUpdate();

        // Inserting Fluid into tanks
        ItemStack fuelStack = getStack(0);
        if (fuelStack != null && fuelStack.getItem() instanceof FluidBucket bucketItem) {
            Fluid fluid = bucketItem.getFluid();

            if (fluid != null) {
                int bucketSize = fluid.getBucketSize();
                FluidStack remainder = new FluidStack(fluid, bucketSize);
                
                if (isValidCoolant(fluid)) {
                    if (getRemainingFluidCapacity(0, null) >= bucketSize) {
                        remainder = insertFluid(remainder, 0, null);
                    }
                } else if (isValidFuel(fluid)) {
                    if (getRemainingFluidCapacity(1, null) >= bucketSize) {
                        remainder = insertFluid(remainder, 1, null);
                    }
                }

                if (remainder == null) {
                    setStack(0, new ItemStack(BucketItem.BUCKET, 1));
                } else {
                    Buildcraft.LOGGER.warn("Remainder of fuel in a Combustion Engine was not zero!");
                }
            }
        }

        // Cooling the engine
        if (heat > MIN_HEAT && (penaltyCooling > 0 || !isRedstonePowered)) {
            heat -= COOLDOWN_RATE;
            coolEngine(MIN_HEAT);
            getEnergyStage();
        } else if (heat > IDEAL_HEAT) {
            coolEngine(IDEAL_HEAT);
        }

        // Penalty cooling
        if (heat <= MIN_HEAT && penaltyCooling > 0) {
            penaltyCooling--;
        }

        // Clamp the heat to minimum
        if (heat <= MIN_HEAT) {
            heat = MIN_HEAT;
        }
    }

    private void coolEngine(float idealHeat) {
        float extraHeat = heat - idealHeat;

        FluidStack coolant = fluidInventory[0];
        if (coolant == null) {
            return;
        }

        int coolantAmount = Math.min(MAX_COOLANT_PER_TICK, coolant.amount);
        EngineCoolant currentCoolant = EngineCoolantRegistry.get(coolant.fluid);
        if (currentCoolant != null) {
            float cooling = currentCoolant.getDegreesCooledPerMb(heat);
            cooling /= getBiomeTempScalar();
            if (coolantAmount * cooling > extraHeat) {
                this.extractFluid(0, Math.round(extraHeat / cooling), null);
                heat -= extraHeat;
            } else {
                this.extractFluid(0, coolantAmount, null);
                heat -= coolantAmount * cooling;
            }
        }
    }

    @Override
    public void updateHeatLevel() {
        // Do nothing. Heat is handled differently for the combustion engine.
    }

    @Override
    public boolean isActive() {
        return penaltyCooling <= 0;
    }

    // Fuel & Coolant
    @Override
    public void burnFuel() {
        FluidStack fuelStack = this.fluidInventory[1];
        if (currentFuel == null && fuelStack != null) {
            currentFuel = EngineFuelRegistry.get(fuelStack.fluid);
        }

        if (currentFuel == null) {
            return;
        }

        if (penaltyCooling <= 0 && isRedstonePowered) {

            lastPowered = true;

            if (burnTime > 0 || (fuelStack != null && fuelStack.amount > 0)) {
                if (burnTime > 0) {
                    burnTime--;
                }
                if (burnTime <= 0) {
                    if (fuelStack != null) {
                        if (--fuelStack.amount <= 0) {
                            setFluid(1, null, null);
                        }
                        burnTime = currentFuel.burnTime / currentFuel.fluid.getBucketSize();
                    } else {
                        currentFuel = null;
                        return;
                    }
                }
                addEnergy(currentFuel.powerPerCycle);
                heat += currentFuel.powerPerCycle * HEAT_PER_MJ * getBiomeTempScalar();
            }
        } else if (penaltyCooling <= 0) {
            if (lastPowered) {
                lastPowered = false;
                penaltyCooling = 30 * 20; // 30 second penalty cooling
            }
        }
    }

    public int getScaledCoolant(int i) {
        return fluidInventory[0] != null ? (int) (((float) fluidInventory[0].amount / (float) (FLUID_CAPACITY)) * i) : 0;
    }

    @Override
    public int getScaledBurnTime(int i) {
        return fluidInventory[1] != null ? (int) (((float) fluidInventory[1].amount / (float) (FLUID_CAPACITY)) * i) : 0;
    }

    @Override
    public boolean isBurning() {
        FluidStack fuel = fluidInventory[1];
        return fuel != null && fuel.amount > 0 && penaltyCooling == 0 && isRedstonePowered;
    }

    @Override
    public int getBurnTime() {
        return burnTime;
    }

    @Override
    public int getMaxBurnTime() {
        if (currentFuel != null) {
            return currentFuel.burnTime;
        }

        return 0;
    }

    public boolean isValidCoolant(Fluid fluid) {
        return EngineCoolantRegistry.get(fluid) != null;
    }

    public boolean isValidFuel(Fluid fluid) {
        return EngineFuelRegistry.get(fluid) != null;
    }

    // Energy
    @Override
    public double getMaxEnergy() {
        return 100000;
    }

    @Override
    public double getMaxEnergyReceived() {
        return 2000;
    }

    @Override
    public double getMaxEnergyExtracted() {
        return 500;
    }

    @Override
    public double getCurrentEnergyOutput() {
        if (currentFuel != null) {
            return currentFuel.powerPerCycle;
        }

        return 0;
    }

    // Properties
    @Override
    public float getExplosionRange() {
        return 4.0F;
    }

    private float getBiomeTempScalar() {
        if (biomeTemperature == -1) {
            biomeTemperature = (float) world.method_1781().getTemperature(x, z);
        }

        float tempScalar = biomeTemperature - 1.0F;
        tempScalar *= 0.5F;
        tempScalar += 1.0F;
        return tempScalar;
    }

    // FluidHandler
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
        return direction != facing;
    }

    @Override
    public FluidStack insertFluid(FluidStack fluidStack, int slot, @Nullable Direction direction) {
        switch (slot) {
            // Coolant
            case 0 -> {
                if (!isValidCoolant(fluidStack.fluid)) {
                    return fluidStack;
                }
            }

            // Fuel
            case 1 -> {
                if (!isValidFuel(fluidStack.fluid)) {
                    return fluidStack;
                }
            }
        }

        FluidStack currentStack = fluidInventory[slot];
        int remaining = fluidStack.amount;

        if (currentStack == null) {
            currentStack = new FluidStack(fluidStack.fluid, 0);
        }

        int addedAmount = Math.min(remaining, getFluidCapacity(slot, direction) - currentStack.amount);
        currentStack.amount += addedAmount;
        remaining -= addedAmount;

        fluidInventory[slot] = currentStack;

        if (remaining > 0) {
            fluidStack.amount -= remaining;
            return new FluidStack(fluidStack.fluid, remaining);
        }

        return null;
    }

    @Override
    public FluidStack insertFluid(FluidStack fluidStack, @Nullable Direction direction) {
        FluidStack insertedStack = fluidStack;

        for (int i = 0; i < fluidInventory.length; i++) {
            insertedStack = insertFluid(fluidStack, i, direction);
            if (insertedStack == null) {
                return null;
            }
        }

        return insertedStack.copy();
    }

    @Override
    public FluidStack getFluid(int slot, @Nullable Direction direction) {
        if (slot < 0 || slot >= fluidInventory.length) {
            return null;
        }

        return fluidInventory[slot];
    }

    @Override
    public boolean setFluid(int slot, FluidStack fluidStack, @Nullable Direction direction) {
        if (fluidStack == null) {
            return false;
        }
        
        switch (slot) {
            // Coolant
            case 0 -> {
                if (!isValidCoolant(fluidStack.fluid)) {
                    return false;
                }
                fluidInventory[slot] = fluidStack;
            }

            // Fuel
            case 1 -> {
                if (!isValidFuel(fluidStack.fluid)) {
                    return false;
                }
                fluidInventory[slot] = fluidStack;
            }

            default -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getFluidSlots(@Nullable Direction direction) {
        return fluidInventory.length;
    }

    @Override
    public int getFluidCapacity(int slot, @Nullable Direction direction) {
        return FLUID_CAPACITY;
    }

    @Override
    public FluidStack[] getFluids(@Nullable Direction direction) {
        return fluidInventory;
    }

    @Override
    public boolean canConnectFluid(Direction direction) {
        return direction != facing;
    }
    
    // Item Handler
    @Override
    public ItemStack insertItem(ItemStack stack, int slot, @Nullable Direction side) {
        if (!(stack.getItem() instanceof FluidBucket) && CapabilityHelper.getCapability(stack, FluidHandlerItemCapability.class) == null) {
            return stack;
        }
        
        return super.insertItem(stack, slot, side);
    }
}
