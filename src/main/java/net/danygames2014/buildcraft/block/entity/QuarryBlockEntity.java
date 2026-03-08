package net.danygames2014.buildcraft.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.blockentity.ControlMode;
import net.danygames2014.buildcraft.api.blockentity.Controllable;
import net.danygames2014.buildcraft.api.energy.IPowerReceptor;
import net.danygames2014.buildcraft.api.energy.PowerHandler;
import net.danygames2014.buildcraft.client.render.block.PipeWorldRenderer;
import net.danygames2014.buildcraft.entity.MechanicalArmEntity;
import net.danygames2014.buildcraft.util.BlockUtil;
import net.danygames2014.buildcraft.util.Constants;
import net.danygames2014.buildcraft.util.ItemUtil;
import net.danygames2014.nyalib.item.block.ManagedItemHandler;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.block.States;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class QuarryBlockEntity extends AreaWorkerBlockEntity implements IPowerReceptor, Controllable, ManagedItemHandler {
    public PowerHandler powerHandler;
    public MechanicalArmEntity arm;

    // State
    public QuarryStatus status = QuarryStatus.IDLE;
    public ControlMode controlMode = ControlMode.ON;
    public CurrentJob currentJob;

    private int targetX;
    private int targetY;
    private int targetZ;
    private double headPosX;
    private double headPosY;
    private double headPosZ;
    private boolean movingHorizontally;
    private boolean movingVertically;
    private double headTrajectory;
    private boolean inProcess;

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
    private final int miningCost = 60;

    public QuarryBlockEntity() {
        super();
        this.minHeight = 5;

        this.powerHandler = new PowerHandler(this, PowerHandler.Type.MACHINE);
        this.powerHandler.configure(25, 200, 25, 10000);
    }

    private void createArm() {
        world.spawnEntity(
                new MechanicalArmEntity(world,
                        workingArea.minX + PipeWorldRenderer.PIPE_MAX_POS,
                        y + workingArea.sizeY() - 1 + PipeWorldRenderer.PIPE_MIN_POS,
                        workingArea.minZ + PipeWorldRenderer.PIPE_MAX_POS,
                        workingArea.sizeX() - 2 + PipeWorldRenderer.PIPE_MIN_POS * 2,
                        workingArea.sizeZ() - 2 + PipeWorldRenderer.PIPE_MIN_POS * 2,
                        this));
    }

    // Callback from the arm once it's created
    public void setArm(MechanicalArmEntity arm) {
        this.arm = arm;
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
        if (robot != null) {
            robot.setLaserTexture(getRobotLaserTexture(powerHandler.getPowerReceiver()));
        }

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
                    createArm();

                    if (findTarget(false)) {
                        if (workingArea != null) {
                            if ((headPosX < workingArea.minX || headPosX > workingArea.maxX) || (headPosZ < workingArea.minZ || headPosZ > workingArea.maxZ)) {
                                setHead(workingArea.minX + 1, this.y + 2, workingArea.minZ + 1);
                            }
                            workingArea.clearLasers();
                        }
                    }
                    break;
                }

                performFrameBuilding();
                break;

            case MINING:
                if (workingArea != null) {
                    if ((headPosX < workingArea.minX || headPosX > workingArea.maxX) || (headPosZ < workingArea.minZ || headPosZ > workingArea.maxZ)) {
                        setHead(workingArea.minX + 1, this.y + 2, workingArea.minZ + 1);
                    }

                    if (!workingArea.lasers.isEmpty()) {
                        workingArea.clearLasers();
                    }
                }

                performDigging();
                break;

            case FINISHED:
                if (workingArea != null && !workingArea.lasers.isEmpty()) {
                    workingArea.clearLasers();
                }
                
                break;
        }

        if (status == QuarryStatus.CLEARING_AREA || status == QuarryStatus.BUILDING_FRAME) {
            if (robot == null) {
                createRobot();
            }
        } else {
            if (robot != null) {
                destroyRobot();
            }
        }
    }

    public void performClearing() {
        BlockPos nextPeek = currentJob.peek();
        if (world.getBlockState(nextPeek).isAir()) {
            currentJob.pop();
            return;
        }

        setRobotTarget(nextPeek);

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
            setRobotTarget(peek);
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

    public void performDigging() {
        // Head Moving
        if (inProcess) {
            double energyToUse = 2 + powerHandler.getEnergyStored() / 500;

            double energy = powerHandler.useEnergy(energyToUse, energyToUse, true);

            if (energy > 0) {
                moveHead(0.1 + energy / 200F);
            }

            return;
        }

        powerHandler.configure(100, 500, miningCost, 15000);
        if (powerHandler.useEnergy(miningCost, miningCost, true) != miningCost) {
            return;
        }

        if (!findTarget(true)) {
            // I believe the issue is box going null becuase of bad chunkloader positioning
            if (arm != null && workingArea != null) {
                setTarget(workingArea.minX + 1, y + 2, workingArea.minZ + 1);
            }

            status = QuarryStatus.FINISHED;
            return;
        }


        inProcess = true;
        movingHorizontally = true;
        movingVertically = true;
        double[] head = getHead();
        int[] target = getTarget();
        headTrajectory = Math.atan2(target[2] - head[2], target[0] - head[0]);
    }

    private final LinkedList<int[]> visitList = Lists.newLinkedList();

    public boolean findTarget(boolean doSet) {
        if (world.isRemote)
            return false;

        boolean columnVisitListIsUpdated = false;

        if (visitList.isEmpty()) {
            createColumnVisitList();
            columnVisitListIsUpdated = true;
        }

        if (!doSet)
            return !visitList.isEmpty();

        if (visitList.isEmpty())
            return false;

        int[] nextTarget = visitList.removeFirst();

        if (!columnVisitListIsUpdated) { // nextTarget may not be accurate, at least search the target column for changes
            for (int y = nextTarget[1] + 1; y < this.y + 3; y++) {
                if (isQuarriableBlock(nextTarget[0], y, nextTarget[2])) {
                    createColumnVisitList();
                    nextTarget = visitList.removeFirst();

                    break;
                }
            }
        }

        setTarget(nextTarget[0], nextTarget[1] + 1, nextTarget[2]);

        return true;
    }

    /**
     * Make the column visit list: called once per layer
     */
    private void createColumnVisitList() {
        visitList.clear();

        Integer[][] columnHeights = new Integer[workingArea.sizeX() - 2][workingArea.sizeZ() - 2];
        boolean[][] blockedColumns = new boolean[workingArea.sizeX() - 2][workingArea.sizeZ() - 2];
        for (int searchY = this.y + 3; searchY >= 0; --searchY) {
            int startX, endX, incX;

            if (searchY % 2 == 0) {
                startX = 0;
                endX = workingArea.sizeX() - 2;
                incX = 1;
            } else {
                startX = workingArea.sizeX() - 3;
                endX = -1;
                incX = -1;
            }

            for (int searchX = startX; searchX != endX; searchX += incX) {
                int startZ, endZ, incZ;

                if (searchX % 2 == searchY % 2) {
                    startZ = 0;
                    endZ = workingArea.sizeZ() - 2;
                    incZ = 1;
                } else {
                    startZ = workingArea.sizeZ() - 3;
                    endZ = -1;
                    incZ = -1;
                }

                for (int searchZ = startZ; searchZ != endZ; searchZ += incZ) {
                    if (!blockedColumns[searchX][searchZ]) {
                        Integer height = columnHeights[searchX][searchZ];
                        int bx = workingArea.minX + searchX + 1;
                        @SuppressWarnings("UnnecessaryLocalVariable")
                        int by = searchY;
                        int bz = workingArea.minZ + searchZ + 1;

                        if (height == null)
                            columnHeights[searchX][searchZ] = height = world.getTopY(bx, bz);

                        if (height < by)
                            continue;

                        if (!BlockUtil.canChangeBlock(world, bx, by, bz)) {
                            blockedColumns[searchX][searchZ] = true;
                        } else if (!BlockUtil.isSoftBlock(world, bx, by, bz)) {
                            visitList.add(new int[]{bx, by, bz});
                        }
                        // Stop at two planes - generally any obstructions will have been found and will force a recompute prior to this
                        if (visitList.size() > workingArea.sizeZ() * workingArea.sizeX() * 2)
                            return;
                    }
                }
            }
        }
    }

    public void moveHead(double instantSpeed) {
        int[] target = getTarget();
        double[] head = getHead();

        if (movingHorizontally) {
            if (Math.abs(target[0] - head[0]) < instantSpeed * 2 && Math.abs(target[2] - head[2]) < instantSpeed * 2) {
                head[0] = target[0];
                head[2] = target[2];

                movingHorizontally = false;

                if (!movingVertically) {
                    positionReached();
                    head[1] = target[1];
                }
            } else {
                head[0] += Math.cos(headTrajectory) * instantSpeed;
                head[2] += Math.sin(headTrajectory) * instantSpeed;
            }
            setHead(head[0], head[1], head[2]);
        }

        if (movingVertically) {
            if (Math.abs(target[1] - head[1]) < instantSpeed * 2) {
                head[1] = target[1];

                movingVertically = false;
                if (!movingHorizontally) {
                    positionReached();
                    head[0] = target[0];
                    head[2] = target[2];
                }
            } else {
                if (target[1] > head[1]) {
                    head[1] += instantSpeed;
                } else {
                    head[1] -= instantSpeed;
                }
            }
            setHead(head[0], head[1], head[2]);
        }

        updatePosition();
    }

    private void updatePosition() {
        if (arm != null /* && world.isRemote */) {
            arm.setHead(headPosX, headPosY, headPosZ);
            arm.updatePosition();
        }
    }

    public void positionReached() {
        inProcess = false;

        if (world.isRemote) {
            return;
        }

        int i = targetX;
        int j = targetY - 1;
        int k = targetZ;

        if (isQuarriableBlock(i, j, k)) {
            //powerProvider.getTimeTracker().markTime(world);

            List<ItemStack> drops = BlockUtil.getStacksFromBlock(world, i, j, k);

            if (drops != null) {
                for (ItemStack s : drops) {
                    if (s != null) {
                        mineStack(s);
                    }
                }
            }

            int blockId = world.getBlockId(i, j, k);
            world.worldEvent(null, 2001, i, j, k, blockId + (world.getBlockMeta(i, j, k) << 28));
            world.setBlock(i, j, k, 0);
        }

        // Collect any lost items laying around
        double[] head = getHead();
        Box axis = Box.create(head[0] - 2, head[1] - 2, head[2] - 2, head[0] + 3, head[1] + 3, head[2] + 3);
        @SuppressWarnings("rawtypes")
        List result = world.collectEntitiesByClass(ItemEntity.class, axis);
        for (Object o : result) {
            if (o instanceof ItemEntity entity) {
                if (entity.dead) {
                    continue;
                }

                ItemStack mineable = entity.stack;
                if (mineable.count <= 0) {
                    continue;
                }

                entity.markDead();
                mineStack(mineable);
            }
        }
    }

    private void mineStack(ItemStack stack) {
        // First, try to add to a nearby chest
        stack = ItemUtil.addToRandomInventory(stack, world, x, y, z);
        if (stack == null) {
            return;
        }

        // Second, try to add to adjacent pipes
        stack = ItemUtil.addToRandomPipeEntry(this, stack);
        if (stack == null) {
            return;
        }

        // Lastly, throw the object away
        if (stack.count > 0) {
            float xOffset = world.random.nextFloat() * 0.8F + 0.1F;
            float yOffset = world.random.nextFloat() * 0.8F + 0.1F;
            float zOffset = world.random.nextFloat() * 0.8F + 0.1F;

            ItemEntity itemEntity = new ItemEntity(world, x + xOffset, y + yOffset + 0.5F, z + zOffset, stack);

            itemEntity.pickupDelay = 10;

            float baseVelocity = 0.05F;
            itemEntity.velocityX = (float) world.random.nextGaussian() * baseVelocity;
            itemEntity.velocityY = (float) world.random.nextGaussian() * baseVelocity + 1.0F;
            itemEntity.velocityZ = (float) world.random.nextGaussian() * baseVelocity;
            world.spawnEntity(itemEntity);
        }
    }

    private boolean isQuarriableBlock(int bx, int by, int bz) {
        return BlockUtil.canChangeBlock(world, bx, by, bz) && !BlockUtil.isSoftBlock(world, bx, by, bz);
    }

    private int[] getTarget() {
        return new int[]{targetX, targetY, targetZ};
    }

    private void setTarget(int x, int y, int z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
    }

    private double[] getHead() {
        return new double[]{headPosX, headPosY, headPosZ};
    }

    private void setHead(double x, double y, double z) {
        this.headPosX = x;
        this.headPosY = y;
        this.headPosZ = z;
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


    public String getRobotLaserTexture(PowerHandler.PowerReceiver powerReceiver) {
        double avg = powerReceiver.getAveragePowerUsed();

        if (avg <= 10.0) {
            return Constants.LASER_TEXTURES[0];
        } else if (avg <= 20.0) {
            return Constants.LASER_TEXTURES[1];
        } else if (avg <= 30.0) {
            return Constants.LASER_TEXTURES[2];
        } else {
            return Constants.LASER_TEXTURES[3];
        }
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
        nbt.putInt("targetX", targetX);
        nbt.putInt("targetY", targetY);
        nbt.putInt("targetZ", targetZ);
        nbt.putDouble("headPosX", headPosX);
        nbt.putDouble("headPosY", headPosY);
        nbt.putDouble("headPosZ", headPosZ);
        nbt.putDouble("headTrajectory", headTrajectory);
        nbt.putBoolean("movingHorizontally", movingHorizontally);
        nbt.putBoolean("movingVertically", movingVertically);
        nbt.putBoolean("inProcess", inProcess);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        status = QuarryStatus.values()[nbt.getInt("status")];
        targetX = nbt.getInt("targetX");
        targetY = nbt.getInt("targetY");
        targetZ = nbt.getInt("targetZ");
        headPosX = nbt.getDouble("headPosX");
        headPosY = nbt.getDouble("headPosY");
        headPosZ = nbt.getDouble("headPosZ");
        headTrajectory = nbt.getDouble("headTrajectory");
        movingHorizontally = nbt.getBoolean("movingHorizontally");
        movingVertically = nbt.getBoolean("movingVertically");
        inProcess = nbt.getBoolean("inProcess");
    }

    @SuppressWarnings("UnusedReturnValue")
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
