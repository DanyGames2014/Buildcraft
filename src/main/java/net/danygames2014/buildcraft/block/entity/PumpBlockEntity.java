package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.api.blockentity.HasWork;
import net.danygames2014.buildcraft.api.core.BlockEntityBuffer;
import net.danygames2014.buildcraft.api.core.BlockIndex;
import net.danygames2014.buildcraft.api.core.SafeTimeTracker;
import net.danygames2014.buildcraft.api.energy.IPowerReceptor;
import net.danygames2014.buildcraft.api.energy.PowerHandler;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.FluidPipeTransporter;
import net.danygames2014.buildcraft.client.render.block.PipeWorldRenderer;
import net.danygames2014.buildcraft.config.Config;
import net.danygames2014.buildcraft.entity.EntityBlock;
import net.danygames2014.buildcraft.init.TextureListener;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.fluidhandler.FluidHandlerBlockCapability;
import net.danygames2014.nyalib.fluid.Fluid;
import net.danygames2014.nyalib.fluid.FluidRegistry;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.danygames2014.nyalib.fluid.Fluids;
import net.danygames2014.nyalib.fluid.block.ManagedFluidHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;


public class PumpBlockEntity extends SyncedBlockEntity implements ManagedFluidHandler, IPowerReceptor, HasWork {
    public static final int REBUILD_DELAY = 512;
    public static int MAX_LIQUID = 1000 * 16;
    public EntityBlock tube;
    private final TreeMap<Integer, Deque<BlockIndex>> pumpLayerQueues = new TreeMap<>();
    double tubeY = Double.NaN;
    int aimY = 0;
    private final PowerHandler powerHandler;
    private BlockEntityBuffer[] blockEntityBuffer = null;
    private final SafeTimeTracker timer = new SafeTimeTracker(REBUILD_DELAY);
    private final Random random = new Random();
    private int tick = random.nextInt();
    private int numFluidBlocksFound = 0;
    private boolean powered = false;

    public PumpBlockEntity() {
        powerHandler = new PowerHandler(this, PowerHandler.Type.MACHINE);
        initPowerProvider();
        addFluidSlot(MAX_LIQUID);
    }

    private void initPowerProvider() {
        powerHandler.configure(1, 15, 10, 100);
        powerHandler.configurePowerPerdition(1, 100);
    }

    @Override
    public void tick() {
        super.tick();

        if (powered) {
            pumpLayerQueues.clear();
            destroyTube();
        } else {
            createTube();
        }

        if (tube != null && world.isRemote) {
            setTubePosition();
        }

        if (world.isRemote) {
            return;
        }

        pushToConsumers();

        if (powered) {
            return;
        }

        if (tube == null) {
            return;
        }

        if (tube.y - aimY > 0.01) {
            tubeY = tube.y - 0.01;
            setTubePosition();
            sendNetworkUpdate();
            return;
        }

        tick++;

        if (tick % 16 != 0) {
            return;
        }

        BlockIndex index = getNextIndexToPump(false);

        FluidStack fluidToPump = index != null ? drainBlock(world, index.x, index.y, index.z, false) : null;
        if (fluidToPump != null) {
            if (isFluidAllowed(fluidToPump.fluid) && getFluidCapacity(0, null) >= fluidToPump.amount) {
                if (powerHandler.useEnergy(10, 10, true) == 10) {
                    if (fluidToPump.fluid != Fluids.WATER || Config.MACHINE_CONFIG.pump.consumeWaterSources || numFluidBlocksFound < 9) {
                        index = getNextIndexToPump(true);
                        if (index != null) {
                            drainBlock(world, index.x, index.y, index.z, true);
                        }
                    }

                    insertFluid(fluidToPump, 0, null);
                }
            }
        } else {
            if (tick % 128 == 0) {
                rebuildQueue();

                if (getNextIndexToPump(false) == null) {
                    for (int y = this.y - 1; y > 0; --y) {
                        if (isPumpableFluid(this.x, y, this.z)) {
                            aimY = y;
                            return;
                        } else if (isBlocked(this.x, y, this.z)) {
                            return;
                        }
                    }
                }
            }
        }
    }

    public void neighborUpdate() {
        boolean p = world.isPowered(x, y, z);

        if (powered != p) {
            powered = p;

            if (!world.isRemote) {
                sendNetworkUpdate();
            }
        }
    }

    private boolean isBlocked(int x, int y, int z) {
        Material mat = world.getMaterial(x, y, z);

        return mat.blocksMovement();
    }

    private void pushToConsumers() {
        if (blockEntityBuffer == null) {
            blockEntityBuffer = BlockEntityBuffer.makeBuffer(world, x, y, z, false);
        }

        pushFluidToConsumers(getFluid(0, null), 400, blockEntityBuffer);
    }


    public void pushFluidToConsumers(FluidStack tank, int flowCap, BlockEntityBuffer[] beBuffer) {
        if (tank == null) {
            return;
        }

        if (tank.amount <= 0) {
            setFluid(0, null, null);
            return;
        }
        
        int amountToPush = Math.min(tank.amount, flowCap);

        for (Direction side : Direction.values()) {
            if (amountToPush <= 0) {
                break;
            }

            if (tank.amount <= 0) {
                setFluid(0, null, null);
                return;
            }

            BlockEntity blockEntity = beBuffer[side.ordinal()].getBlockEntity();

            FluidHandlerBlockCapability capability = blockEntity != null ? CapabilityHelper.getCapability(blockEntity, FluidHandlerBlockCapability.class) : null;

            if (capability != null) {
                int slots = capability.getFluidSlots(side.getOpposite());
                for (int i = 0; i < slots; i++) {
                    int toAdd = Math.min(amountToPush, tank.amount);

                    FluidStack remainder = capability.insertFluid(new FluidStack(tank.fluid, toAdd), i, side.getOpposite());

                    int actuallyMoved = toAdd - (remainder == null ? 0 : remainder.amount);

                    tank.amount -= actuallyMoved;
                    amountToPush -= actuallyMoved;

                    if (amountToPush <= 0) return;
                }
            } else if (blockEntity instanceof PipeBlockEntity pipe) {
                if (pipe.transporter instanceof FluidPipeTransporter fluidTransporter) {
                    if (fluidTransporter.isPipeConnected(side.getOpposite())) {
                        int toAdd = Math.min(amountToPush, tank.amount);

                        FluidStack remainder = fluidTransporter.insertFluid(new FluidStack(tank.fluid, toAdd), side.getOpposite());

                        int actuallyMoved = toAdd - (remainder == null ? 0 : remainder.amount);

                        tank.amount -= actuallyMoved;
                        amountToPush -= actuallyMoved;

                        if (amountToPush <= 0) return;
                    }
                }
            }
        }
    }

    private BlockEntity getBlockEntity(Direction side) {
        if (blockEntityBuffer == null) {
            blockEntityBuffer = BlockEntityBuffer.makeBuffer(world, x, y, z, false);
        }

        return blockEntityBuffer[side.ordinal()].getBlockEntity();
    }

    private void createTube() {
        if (tube == null) {
            tube = newPumpTube(world);

            if (!Double.isNaN(tubeY)) {
                tube.y = tubeY;
            } else {
                tube.y = this.y;
            }

            tubeY = tube.y;

            if (aimY == 0) {
                aimY = this.y;
            }

            setTubePosition();

//            world.spawnEntity(tube);

            if (!world.isRemote) {
                sendNetworkUpdate();
            }
        }
    }

    private void destroyTube() {
        if (tube != null) {
            removeEntity(tube);
            tube = null;
            tubeY = Double.NaN;
            aimY = 0;
        }
    }

    private BlockIndex getNextIndexToPump(boolean remove) {
        if (pumpLayerQueues.isEmpty()) {
            if (timer.markTimeIfDelay(world)) {
                rebuildQueue();
            }

            return null;
        }

        Deque<BlockIndex> topLayer = pumpLayerQueues.lastEntry().getValue();

        if (topLayer != null) {
            if (topLayer.isEmpty()) {
                pumpLayerQueues.pollLastEntry();
            }

            if (remove) {
                return topLayer.pollLast();
            } else {
                return topLayer.peekLast();
            }
        } else {
            return null;
        }
    }

    private Deque<BlockIndex> getLayerQueue(int layer) {
        return pumpLayerQueues.computeIfAbsent(layer, k -> new LinkedList<>());
    }

    public void rebuildQueue() {
        numFluidBlocksFound = 0;
        pumpLayerQueues.clear();
        int x = this.x;
        int y = aimY;
        int z = this.z;
        Fluid pumpingFluid = FluidRegistry.get(world.getBlockId(x, y, z));

        if (pumpingFluid == null) {
            return;
        }

        if (!getFluidSlot(0, null).isFluidAllowed(pumpingFluid)) {
            return;
        }

        Set<BlockIndex> visitedBlocks = new HashSet<>();
        Deque<BlockIndex> fluidsFound = new LinkedList<>();

        queueForPumping(x, y, z, visitedBlocks, fluidsFound, pumpingFluid);

        while (!fluidsFound.isEmpty()) {
            Deque<BlockIndex> fluidsToExpand = fluidsFound;
            fluidsFound = new LinkedList<>();

            for (BlockIndex index : fluidsToExpand) {
                queueForPumping(index.x, index.y + 1, index.z, visitedBlocks, fluidsFound, pumpingFluid);
                queueForPumping(index.x + 1, index.y, index.z, visitedBlocks, fluidsFound, pumpingFluid);
                queueForPumping(index.x - 1, index.y, index.z, visitedBlocks, fluidsFound, pumpingFluid);
                queueForPumping(index.x, index.y, index.z + 1, visitedBlocks, fluidsFound, pumpingFluid);
                queueForPumping(index.x, index.y, index.z - 1, visitedBlocks, fluidsFound, pumpingFluid);

                if (pumpingFluid == Fluids.WATER && !Config.MACHINE_CONFIG.pump.consumeWaterSources && numFluidBlocksFound >= 9) {
                    return;
                }
            }
        }
    }

    public void queueForPumping(int x, int y, int z, Set<BlockIndex> visitedBlocks, Deque<BlockIndex> fluidsFound, Fluid pumpingFluid) {
        BlockIndex index = new BlockIndex(x, y, z);
        if (visitedBlocks.add(index)) {
            if ((x - this.x) * (x - this.x) + (z - this.z) * (z - this.z) > 64 * 64) {
                return;
            }

            int block = world.getBlockId(x, y, z);

            if (FluidRegistry.get(block) == pumpingFluid) {
                fluidsFound.add(index);
            }

            if (canDrainBlock(block, x, y, z, pumpingFluid)) {
                getLayerQueue(y).add(index);
                numFluidBlocksFound++;
            }
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean isPumpableFluid(int x, int y, int z) {
        Fluid fluid = FluidRegistry.get(world.getBlockId(x, y, z));

        if (fluid == null) {
            return false;
        } else if (!isFluidAllowed(fluid)) {
            return false;
        } else if (!getFluidSlot(0, null).isFluidAllowed(fluid)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean canDrainBlock(int block, int x, int y, int z, Fluid fluid) {
        if (!isFluidAllowed(fluid)) {
            return false;
        }

        FluidStack fluidStack = drainBlock(block, world, x, y, z, false);

        if (fluidStack == null || fluidStack.amount <= 0) {
            return false;
        } else {
            return fluidStack.fluid == fluid;
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean isFluidAllowed(Fluid fluid) {
        if (Config.fluidBlacklistMap.containsKey(world.dimension.id) && Config.fluidBlacklistMap.get(world.dimension.id).contains(fluid)) {
            return false;
        }

        if (Config.fluidBlacklistMap.containsKey(-999) && Config.fluidBlacklistMap.get(-999).contains(fluid)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean hasWork() {
        BlockIndex next = getNextIndexToPump(false);

        if (next != null) {
            return isPumpableFluid(next.x, next.y, next.z);
        } else {
            return false;
        }
    }

    private void setTubePosition() {
        if (tube != null) {
            tube.setXSize(PipeWorldRenderer.PIPE_MAX_POS - PipeWorldRenderer.PIPE_MIN_POS);
            tube.setZSize(PipeWorldRenderer.PIPE_MAX_POS - PipeWorldRenderer.PIPE_MIN_POS);
            tube.setYSize((float) (this.y - tube.y));

            tube.setPosition(this.x + PipeWorldRenderer.PIPE_MIN_POS, tubeY, this.z + PipeWorldRenderer.PIPE_MIN_POS);
        }
    }


    public EntityBlock newPumpTube(World w) {
        EntityBlock eb = new EntityBlock(w);
        eb.setTextureIdentifier(TextureListener.pumpTube);
        return eb;
    }

    @Override
    public void markRemoved() {
        pumpLayerQueues.clear();
        destroyTube();
    }

    public void removeEntity(Entity entity) {
        if (entity.world.isRemote) {
            entity.world.remove(entity);
        }
    }

    public static FluidStack drainBlock(World world, int x, int y, int z, boolean doDrain) {
        return drainBlock(world.getBlockId(x, y, z), world, x, y, z, doDrain);
    }

    public static FluidStack drainBlock(int blockId, World world, int x, int y, int z, boolean doDrain) {
        Fluid fluid = FluidRegistry.get(blockId);

        if (fluid != null) {
            int meta = world.getBlockMeta(x, y, z);

            if (meta != 0) {
                return null;
            }

            if (doDrain) {
                world.setBlock(x, y, z, 0);
            }

            return new FluidStack(fluid, fluid.getBucketSize());
        } else {
            return null;
        }
    }

    @Override
    public boolean canInsertFluid(@Nullable Direction direction) {
        return direction == null;
    }

    @Override
    public PowerHandler.PowerReceiver getPowerReceiver(Direction side) {
        return powerHandler.getPowerReceiver();
    }

    @Override
    public void doWork(PowerHandler workProvider) {

    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        powered = nbt.getBoolean("powered");

        aimY = nbt.getInt("aimY");
        tubeY = nbt.getFloat("tubeY");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putBoolean("powered", powered);

        nbt.putInt("aimY", aimY);

        if (tube != null) {
            nbt.putFloat("tubeY", (float) tube.y);
        } else {
            nbt.putFloat("tubeY", this.y);
        }
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void writeData(DataOutputStream stream) throws IOException {
        stream.writeShort(aimY);
        stream.writeFloat((float) tubeY);
        stream.writeBoolean(powered);
    }

    @Override
    public void readData(DataInputStream stream) throws IOException {
        aimY = stream.readShort();
        tubeY = stream.readFloat();
        powered = stream.readBoolean();

        setTubePosition();
    }
}
