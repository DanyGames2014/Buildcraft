package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.api.energy.EnergyStage;
import net.danygames2014.buildcraft.api.energy.PowerHandler;

public class RedstoneEngineBlockEntity extends BaseEngineBlockEntity {
    public static final double OUTPUT = 0.05D;
    
    @Override
    public EnergyStage calculateEnergyStage() {
        double energyLevel = getEnergyLevel();
        if (energyLevel < 0.25f) {
            return EnergyStage.BLUE;
        } else if (energyLevel < 0.5f) {
            return EnergyStage.GREEN;
        } else if (energyLevel < 0.75f) {
            return EnergyStage.YELLOW;
        } else {
            return EnergyStage.RED;
        }
    }

    @Override
    public float getPistonSpeed() {
        if (!world.isRemote)
            return Math.max(0.08f * getHeatLevel(), 0.01f);
        return switch (getEnergyStage()) {
            case GREEN -> 0.02F;
            case YELLOW -> 0.04F;
            case RED -> 0.08F;
            default -> 0.01F;
        };
    }

    @Override
    public void engineUpdate() {
        super.engineUpdate();

        if (isRedstonePowered) {
            if (world.getTime() % 16 == 0) {
                addEnergy(1);
            }
        }
    }

    @Override
    public double getMaxEnergy() {
        return 1000;
    }

    @Override
    public double getMinEnergyReceived() {
        return 1;
    }

    @Override
    public double getMaxEnergyReceived() {
        return 50;
    }

    @Override
    public double getMaxEnergyExtracted() {
        return 1 + PowerHandler.PerditionCalculator.MIN_POWERLOSS;
    }

    @Override
    public double getCurrentEnergyOutput() {
        return OUTPUT;
    }

    @Override
    public float getExplosionRange() {
        return 1.0F;
    }
    
    // State
    @Override
    public boolean isBurning() {
        return this.isRedstonePowered;
    }

    // Burning
    @Override
    public int getBurnTime() {
        return 0;
    }

    @Override
    public int getMaxBurnTime() {
        return 0;
    }
}
