/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.api.energy.EnergyStage;
import net.danygames2014.buildcraft.api.energy.IPowerEmitter;
import net.danygames2014.buildcraft.api.energy.IPowerReceptor;
import net.danygames2014.buildcraft.api.energy.PowerHandler;
import net.danygames2014.buildcraft.block.BaseEngineBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.States;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.util.math.Direction;

public abstract class BaseEngineBlockEntity extends BlockEntity implements IPowerReceptor, IPowerEmitter {
    // Constants
    public static final float MIN_HEAT = 20.0F;
    public static final float IDEAL_HEAT = 100.0F;
    public static final float MAX_HEAT = 250.0F;

    // Engine State
    protected PowerHandler powerHandler;
    public boolean isRedstonePowered = false;
    public float progress = 0;
    public double energy = 0;
    public float heat = MIN_HEAT;
    protected int progressPart = 0;

    public BaseEngineBlockEntity() {
        powerHandler = new PowerHandler(this, PowerHandler.Type.ENGINE);
        powerHandler.configurePowerPerdition(1, 100);
        init();
    }

    public void init() {
        if (!world.isRemote) {
            powerHandler.configure(getMinEnergyReceived(), getMaxEnergyReceived(), 1, getMaxEnergy());
            checkRedstonePower();
        }
    }

    // Engine Logic
    @Override
    public void tick() {
        super.tick();

        if (world.isRemote) {
            if (progressPart != 0) {
                progress += getPistonSpeed();

                if (progress > 1) {
                    progressPart = 0;
                    progress = 0;
                }
            } else if (this.isPumping()) {
                progressPart = 1;
            }

            return;
        }

//        if (checkOrienation) {
//            checkOrienation = false;
//
//            if (!isOrientationValid()) {
//                switchOrientation(true);
//            }
//        }

        if (!isRedstonePowered) {
            if (energy > 1) {
                energy--;
            }
        }

        updateHeatLevel();
        getEnergyStage();
        engineUpdate();

        //TileEntity tile = getTileBuffer(orientation).getTile();

        if (progressPart != 0) {
            progress += getPistonSpeed();

            if (progress > 0.5 && progressPart == 1) {
                progressPart = 2;
                sendPower(); // Comment out for constant power
            } else if (progress >= 1) {
                progress = 0;
                progressPart = 0;
            }
        } else if (isRedstonePowered && isActive()) {
//            if (isPoweredTile(tile, orientation)) {
//                if (getPowerToExtract() > 0) {
//                    progressPart = 1;
//                    setPumping(true);
//                } else {
//                    setPumping(false);
//                }
//            } else {
//                setPumping(false);
//            }
        } else {
            setPumping(false);
        }

        // Uncomment for constant power
//		if (isRedstonePowered && isActive()) {
//			sendPower();
//		} else currentOutput = 0;

        burnFuel();
    }

    protected void engineUpdate() {
        if (!isRedstonePowered) {
            if (energy >= 1) {
                energy -= 1;
            } else if (energy < 1) {
                energy = 0;
            }
        }
    }

    public boolean isActive() {
        return true;
    }

    public void burnFuel() {

    }

    // Blockstate Wrappers
    public boolean isPumping() {
        return world.getBlockState(x, y, z).get(BaseEngineBlock.PUMPING_PROPERTY);
    }

    public void setPumping(boolean pumping) {
        if (isPumping() == pumping) {
            return;
        }

        world.setBlockStateWithNotify(x, y, z, world.getBlockState(x, y, z).with(BaseEngineBlock.PUMPING_PROPERTY, pumping));
    }
    
    public Direction getFacing() {
        return world.getBlockState(x, y, z).get(Properties.FACING);
    }

    // Heat Level
    public void updateHeatLevel() {
        heat = (float) (((MAX_HEAT - MIN_HEAT) * getEnergyLevel()) + MIN_HEAT);
    }

    public float getHeatLevel() {
        return (heat - MIN_HEAT) / (MAX_HEAT - MIN_HEAT);
    }

    public float getIdealHeatLevel() {
        return heat / IDEAL_HEAT;
    }

    public float getHeat() {
        return heat;
    }

    // Energy Level
    public double getEnergyLevel() {
        return energy / getMaxEnergy();
    }

    public float getPistonSpeed() {
        if (!world.isRemote) {
            return Math.max(0.16f * getHeatLevel(), 0.01f);
        }

        return switch (getEnergyStage()) {
            case BLUE -> 0.02F;
            case GREEN -> 0.04F;
            case YELLOW -> 0.08F;
            case RED -> 0.16F;
            default -> 0;
        };
    }

    // Energy Stage
    protected EnergyStage calculateEnergyStage() {
        float heatLevel = getHeatLevel();

        if (heatLevel < 0.25f) {
            return EnergyStage.BLUE;
        } else if (heatLevel < 0.5f) {
            return EnergyStage.GREEN;
        } else if (heatLevel < 0.75f) {
            return EnergyStage.YELLOW;
        } else if (heatLevel < 1f) {
            return EnergyStage.RED;
        } else {
            return EnergyStage.OVERHEAT;
        }
    }

    public EnergyStage getEnergyStage() {
        EnergyStage energyStage = world.getBlockState(x, y, z).get(BaseEngineBlock.ENERGY_STAGE_PROPERTY);

        if (!world.isRemote) {
            if (energyStage == EnergyStage.OVERHEAT) {
                return energyStage;
            }
            EnergyStage newStage = calculateEnergyStage();

            if (newStage != energyStage) {
                world.setBlockStateWithNotify(x, y, z, world.getBlockState(x, y, z).with(BaseEngineBlock.ENERGY_STAGE_PROPERTY, newStage));
            }
        }

        return energyStage;
    }

    // Energy
    public void addEnergy(double addition) {
        energy += addition;

        if (getEnergyStage() == EnergyStage.OVERHEAT) {
            world.createExplosion(null, x, y, z, getExplosionRange(), true);
            world.setBlockStateWithNotify(x, y, z, States.AIR.get());
        }

        if (energy > getMaxEnergy()) {
            energy = getMaxEnergy();
        }
    }

    public double extractEnergy(double requestedMin, double requestedMax, boolean doExtract) {
        // If we don't have enough energy, return 0
        if (energy < requestedMin) {
            return 0;
        }

        // Cap the maximum energy to the engine's capabilities
        double max = Math.min(requestedMax, getMaxEnergyExtracted());

        // If the maximum energy is less than the requested minimum, return 0
        if (max < requestedMin) {
            return 0;
        }

        // Extract the energy. If the max is higher than the stored energy, cap it to stored energy
        double extracted = Math.min(energy, max);

        // If we are actually extracted, deduct the energy from the internal buffer
        if (doExtract) {
            energy -= extracted;
        }

        return extracted;
    }

    public double getEnergyStored() {
        return energy;
    }

    public abstract double getMaxEnergy();

    public double getMinEnergyReceived() {
        return 2D;
    }

    public abstract double getMaxEnergyReceived();

    public abstract double getMaxEnergyExtracted();

    public abstract double getCurrentEnergyOutput();

    private double getPowerToExtract() {
        // TODO: Figure out the tile buffer bs
//        BlockEntity tile = getTileBuffer(getFacing()).getTile();
//        PowerHandler.PowerReceiver receptor = ((IPowerReceptor) tile).getPowerReceiver(getFacing().getOpposite());
//        return extractEnergy(receptor.getMinEnergyReceived(), receptor.getMaxEnergyReceived(), false);
        // TODO: do
        return 0;
    }

    private void sendPower() {
        // TODO: do
//        BlockEntity tile = getTileBuffer(getFacing()).getTile();
//        if (isPoweredTile(tile, getFacing())) {
//            PowerHandler.PowerReceiver receptor = ((IPowerReceptor) tile).getPowerReceiver(getFacing().getOpposite());
//
//            double extracted = getPowerToExtract();
//            if (extracted > 0) {
//                double needed = receptor.receiveEnergy(PowerHandler.Type.ENGINE, extracted, getFacing().getOpposite());
//                extractEnergy(receptor.getMinEnergyReceived(), needed, true);
//            }
//        }
    }

    // Other Properties
    public abstract float getExplosionRange();

    // Redstone
    public void checkRedstonePower() {
        isRedstonePowered = world.isEmittingRedstonePower(x, y, z);
    }

    // IPowerReceptor
    @Override
    public PowerHandler.PowerReceiver getPowerReceiver(Direction side) {
        return powerHandler.getPowerReceiver();
    }

    @Override
    public void doWork(PowerHandler workProvider) {
        if (world.isRemote) {
            return;
        }

        addEnergy(powerHandler.useEnergy(1, getMaxEnergyReceived(), true) * 0.95D);
    }

    @Override
    public World getWorld() {
        return world;
    }

    // IPowerEmitter
    @Override
    public boolean canEmitPowerFrom(Direction side) {
        return world.getBlockState(x, y, z).get(Properties.FACING) == side;
    }
    
    // NBT
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putFloat("progress", progress);
        nbt.putDouble("energy", energy);
        nbt.putFloat("heat", heat);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        progress = nbt.getFloat("progress");
        energy = nbt.getDouble("energy");
        heat = nbt.getFloat("heat");
    }
}
