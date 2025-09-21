/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.util.FuelUtil;
import net.danygames2014.buildcraft.util.MathUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class StirlingEngineBlockEntity extends BaseEngineWithInventoryBlockEntity {
    final float MAX_OUTPUT = 1f;
    final float MIN_OUTPUT = MAX_OUTPUT / 3;
    final float TARGET_OUTPUT = 0.375f;
    final float kp = 1f;
    final float ki = 0.05f;
    final double eLimit = (MAX_OUTPUT - MIN_OUTPUT) / ki;
    
    int burnTime = 0;
    int maxBurnTime = 0;
    double esum = 0;

    public StirlingEngineBlockEntity() {
        super(1);
    }

    // Burning Fuel
    @Override
    public void burnFuel() {
        if (burnTime > 0) {
            burnTime--;

            double output = getCurrentEnergyOutput();
            addEnergy(output);
        }

        if (burnTime == 0 && isRedstonePowered) {
            burnTime = maxBurnTime = FuelUtil.getEngineFuelTime(getStack(0));

            if (burnTime > 0) {
                getStack(0).count--;
                if (getStack(0).count == 0) {
                    setStack(0, null);
                }
            }
        }
    }

    @Override
    public boolean isBurning() {
        return burnTime > 0;
    }

    @Override
    public int getBurnTime() {
        return burnTime;
    }

    @Override
    public int getMaxBurnTime() {
        return maxBurnTime;
    }

    // Energy
    @Override
    public double getMaxEnergy() {
        return 10000;
    }

    @Override
    public double getMaxEnergyReceived() {
        return 200;
    }

    @Override
    public double getMaxEnergyExtracted() {
        return 100;
    }

    @Override
    public double getCurrentEnergyOutput() {
        double e = TARGET_OUTPUT * getMaxEnergy() - energy;
        esum = MathUtil.clamp(esum + e, -eLimit, eLimit);
        return MathUtil.clamp(e * kp + esum * ki, MIN_OUTPUT, MAX_OUTPUT);
    }

    // Item Handler
    @Override
    public ItemStack insertItem(ItemStack stack, int slot, @Nullable Direction side) {
        if (FuelUtil.getEngineFuelTime(stack) <= 0) {
            return stack;
        }

        return super.insertItem(stack, slot, side);
    }

    // Properties
    @Override
    public float getExplosionRange() {
        return 2.0F;
    }

    // NBT
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        burnTime = nbt.getInt("burnTime");
        maxBurnTime = nbt.getInt("totalBurnTime");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("burnTime", burnTime);
        nbt.putInt("totalBurnTime", maxBurnTime);
    }
}
