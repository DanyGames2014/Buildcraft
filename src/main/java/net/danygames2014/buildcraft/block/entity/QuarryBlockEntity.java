package net.danygames2014.buildcraft.block.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.blockentity.ControlMode;
import net.danygames2014.buildcraft.api.blockentity.Controllable;
import net.danygames2014.buildcraft.api.energy.IPowerReceptor;
import net.danygames2014.buildcraft.api.energy.PowerHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.block.States;
import net.modificationstation.stationapi.api.util.math.Direction;

public class QuarryBlockEntity extends AreaWorkerBlockEntity implements IPowerReceptor, Controllable {
    public PowerHandler powerHandler;

    // State
    public QuarryStatus status = QuarryStatus.IDLE;
    public ControlMode controlMode = ControlMode.ON;
    public CurrentJob currentJob;

    // Supported Control Mode
    private final ObjectArrayList<ControlMode> supportedControlModes = new ObjectArrayList<>() {
        {
            add(ControlMode.ON);
            add(ControlMode.OFF);
        }
    };

    // Energy Cost
    private final int clearingCost = 50;
    private final int frameCost = 50;
    private final int miningCost = 150;

    public QuarryBlockEntity() {
        super();
        this.minHeight = 5;

        this.powerHandler = new PowerHandler(this, PowerHandler.Type.MACHINE);
        this.powerHandler.configure(50, 200, 25, 10000);
        this.powerHandler.configurePowerPerdition(2, 1);
    }

    @Override
    public void tick() {
        super.tick();

        if (world.isRemote) {
            return;
        }

        if (workingArea == null) {
            return;
        }

        powerHandler.update();

        if (controlMode == ControlMode.OFF) {
            return;
        }

        switch (status) {
            case IDLE:
                status = QuarryStatus.CLEARING_AREA;
                break;

            case CLEARING_AREA:
                if (currentJob == null) {
                    currentJob = new CurrentJob();
                    currentJob.initializeClearingJob(this);
                }

                if (currentJob.isEmpty()) {
                    status = QuarryStatus.BUILDING_FRAME;
                    currentJob = null;
                    break;
                }

                performClearing();
                break;
            case BUILDING_FRAME:
                if (currentJob == null) {
                    currentJob = new CurrentJob();
                    currentJob.initializeFrameJob(this);
                }

                if (currentJob.isEmpty()) {
                    status = QuarryStatus.MINING;
                    break;
                }

                performFrameBuilding();
                break;

            case MINING:
                break;

            case FINISHED:
                break;
        }
    }

    public void performClearing() {
        BlockPos nextPeek = currentJob.peek();
        if (world.getBlockState(nextPeek).isAir()) {
            currentJob.pop();
            return;
        }

        if (powerHandler.getEnergyStored() >= clearingCost) {
            if (powerHandler.useEnergy(clearingCost, clearingCost, false) >= clearingCost) {
                if (clearBlock(currentJob.next())) {
                    powerHandler.useEnergy(clearingCost, clearingCost, true);
                }
            }
        }
    }

    public void performFrameBuilding() {
        BlockPos peek = currentJob.peek();
        if (world.getBlockState(peek).isAir()) {
            if (powerHandler.getEnergyStored() < frameCost) {
                return;
            }

            if (powerHandler.useEnergy(frameCost, frameCost, false) >= frameCost) {
                world.setBlockStateWithNotify(peek, Buildcraft.frame.getDefaultState());
                powerHandler.useEnergy(frameCost, frameCost, true);
                currentJob.pop();
            }
        } else {
            // If there is a block, clear it out
            if (powerHandler.getEnergyStored() >= clearingCost) {
                if (powerHandler.useEnergy(clearingCost, clearingCost, false) >= clearingCost) {
                    if (clearBlock(peek)) {
                        powerHandler.useEnergy(clearingCost, clearingCost, true);
                    }
                }
            }
        }
    }

    public boolean clearBlock(BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        if (state.isAir()) {
            return false;
        }

        if (state.getBlock().getHardness() > 100F) {
            return false;
        }

        world.setBlockStateWithNotify(pos, States.AIR.get());
        return true;
    }

    // IPowerReceptor
    @Override
    public PowerHandler.PowerReceiver getPowerReceiver(Direction side) {
        return powerHandler.getPowerReceiver();
    }

    @Override
    public void doWork(PowerHandler workProvider) {

    }

    @Override
    public World getWorld() {
        return world;
    }

    // Controllable
    @Override
    public ControlMode getControlMode() {
        return controlMode;
    }

    @Override
    public void setControlMode(ControlMode mode) {
        if (mode == ControlMode.OFF) {
            controlMode = ControlMode.OFF;
        } else if (mode == ControlMode.ON) {
            controlMode = ControlMode.ON;
        } else {
            Buildcraft.LOGGER.error("Tried to set an invalid control mode: " + mode);
        }
    }

    @Override
    public ObjectArrayList<ControlMode> getSupportedControlModes() {
        return supportedControlModes;
    }

    // NBT
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("status", status.ordinal());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        status = QuarryStatus.values()[nbt.getInt("status")];
    }

    public static class CurrentJob {
        public ObjectArrayList<BlockPos> positions = new ObjectArrayList<>();

        public void initializeClearingJob(QuarryBlockEntity quarry) {
            positions.clear();
            for (int y = quarry.workingArea.maxY; y >= quarry.workingArea.minY; y--) {
                for (int x = quarry.workingArea.minX; x <= quarry.workingArea.maxX; x++) {
                    for (int z = quarry.workingArea.minZ; z <= quarry.workingArea.maxZ; z++) {
                        if (!quarry.world.getBlockState(new BlockPos(x, y, z)).isAir()) {
                            positions.add(new BlockPos(x, y, z));
                        }
                    }
                }
            }
        }

        public void initializeFrameJob(QuarryBlockEntity quarry) {
            positions.clear();
            for (int y = quarry.workingArea.minY; y <= quarry.workingArea.maxY; y++) {
                for (int x = quarry.workingArea.minX; x <= quarry.workingArea.maxX; x++) {
                    for (int z = quarry.workingArea.minZ; z <= quarry.workingArea.maxZ; z++) {
                        int axisHits = 0;

                        if (x == quarry.workingArea.minX || x == quarry.workingArea.maxX) {
                            axisHits++;
                        }

                        if (y == quarry.workingArea.minY || y == quarry.workingArea.maxY) {
                            axisHits++;
                        }

                        if (z == quarry.workingArea.minZ || z == quarry.workingArea.maxZ) {
                            axisHits++;
                        }

                        if (axisHits >= 2) {
                            positions.add(new BlockPos(x, y, z));
                        }
                    }
                }
            }
        }

        public boolean isEmpty() {
            return positions.isEmpty();
        }

        public BlockPos peek() {
            return positions.get(0);
        }

        public BlockPos pop() {
            return positions.remove(0);
        }

        public BlockPos next() {
            return positions.remove(0);
        }

        public void clear() {
            positions.clear();
        }
    }

    public enum QuarryStatus {
        IDLE,
        CLEARING_AREA,
        BUILDING_FRAME,
        MINING,
        FINISHED
    }
}
