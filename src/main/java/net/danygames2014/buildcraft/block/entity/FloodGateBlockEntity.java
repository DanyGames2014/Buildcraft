package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.api.core.BlockIndex;
import net.danygames2014.buildcraft.util.BlockUtil;
import net.danygames2014.nyalib.fluid.Fluid;
import net.danygames2014.nyalib.fluid.FluidRegistry;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.danygames2014.nyalib.fluid.Fluids;
import net.danygames2014.nyalib.fluid.block.ManagedFluidHandler;
import net.minecraft.block.Block;
import net.minecraft.block.LiquidBlock;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class FloodGateBlockEntity extends SyncedBlockEntity implements ManagedFluidHandler {
    public static final int[] REBUILD_DELAY = new int[8];
    public static final int MAX_LIQUID = 1000 * 2;
    private final TreeMap<Integer, Deque<BlockIndex>> pumpLayerQueues = new TreeMap<>();
    private final Set<BlockIndex> visitedBlocks = new HashSet<>();
    private Deque<BlockIndex> fluidsFound = new LinkedList<>();
    private int rebuildDelay;
    private int tick = BlockUtil.RANDOM.nextInt();
    private boolean powered = false;
    private final boolean[] blockedSides = new boolean[6];

    static {
        REBUILD_DELAY[0] = 128;
        REBUILD_DELAY[1] = 256;
        REBUILD_DELAY[2] = 512;
        REBUILD_DELAY[3] = 1024;
        REBUILD_DELAY[4] = 2048;
        REBUILD_DELAY[5] = 4096;
        REBUILD_DELAY[6] = 8192;
        REBUILD_DELAY[7] = 16384;
    }

    public FloodGateBlockEntity(){
        addFluidSlot(MAX_LIQUID);
    }

    @Override
    public void tick() {
        if(world.isRemote){
            return;
        }

        if(powered){
            return;
        }

        tick++;
        if (tick % 16 == 0) {
            FluidStack fluidtoFill = getFluid(0, null);
            if (fluidtoFill != null && fluidtoFill.amount >= 1000) {
                Fluid fluid = fluidtoFill.fluid;
                if (fluid == null || !fluid.isPlaceableInWorld()) {
                    return;
                }

                if (fluid == Fluids.WATER && world.dimension.id == -1) {
                    extractFluid(1000, null);
                    return;
                }

                if (tick % REBUILD_DELAY[rebuildDelay] == 0) {
                    rebuildDelay++;
                    if (rebuildDelay >= REBUILD_DELAY.length) {
                        rebuildDelay = REBUILD_DELAY.length - 1;
                    }
                    rebuildQueue();
                }
                BlockIndex index = getNextIndexToFill(true);

                if (index != null && placeFluid(index.x, index.y, index.z, fluid)) {
                    extractFluid(1000, null);
                    rebuildDelay = 0;
                }
            }
        }
    }



    private boolean placeFluid(int x, int y, int z, Fluid fluid) {
        if(!fluid.isPlaceableInWorld()){
            return false;
        }
        BlockState blockState = world.getBlockState(x, y, z);

        if (canPlaceFluidAt(blockState.getBlock(), x, y, z)) {
            boolean placed;
            Block b = fluid.getStillBlock();
            if(b instanceof LiquidBlock){
                if(blockState.getBlock().id == b.id){
                    world.setBlockMeta(x, y, z, 0);
                    placed = true;
                } else {
                    placed = world.setBlock(x, y, z, b.id, 0);
                }
            } else {
                placed = world.setBlock(x, y, z, b.id);
            }

            if (placed) {
                queueAdjacent(x, y, z);
                expandQueue();
            }

            return placed;
        }

        return false;
    }

    @SuppressWarnings("SameParameterValue")
    private BlockIndex getNextIndexToFill(boolean remove) {
        if (pumpLayerQueues.isEmpty()) {
            return null;
        }

        Deque<BlockIndex> bottomLayer = pumpLayerQueues.firstEntry().getValue();

        if (bottomLayer != null) {
            if (bottomLayer.isEmpty()) {
                pumpLayerQueues.pollFirstEntry();
            }
            if (remove) {
                return bottomLayer.pollFirst();
            }
            return bottomLayer.peekFirst();
        }

        return null;
    }

    private Deque<BlockIndex> getLayerQueue(int layer) {
        return pumpLayerQueues.computeIfAbsent(layer, k -> new LinkedList<>());
    }

    /**
     * Nasty expensive function, don't call if you don't have to.
     */
    public void rebuildQueue() {
        pumpLayerQueues.clear();
        visitedBlocks.clear();
        fluidsFound.clear();

        queueAdjacent(x, y, z);

        expandQueue();
    }

    private void expandQueue() {
        if (getFluid(0, null) == null) {
            return;
        }
        while (!fluidsFound.isEmpty()) {
            Deque<BlockIndex> fluidsToExpand = fluidsFound;
            fluidsFound = new LinkedList<>();

            for (BlockIndex index : fluidsToExpand) {
                queueAdjacent(index.x, index.y, index.z);
            }
        }
    }

    public void queueAdjacent(int x, int y, int z) {
        if (getFluid(0, null) == null) {
            return;
        }
        for (int i = 0; i < 6; i++) {
            if (i != 1 && !blockedSides[i]) {
                Direction dir = Direction.byId(i);
                queueForFilling(x + dir.getOffsetX(), y + dir.getOffsetY(), z + dir.getOffsetZ());
            }
        }
    }

    public void queueForFilling(int x, int y, int z) {
        if (y < world.getBottomY() || y > world.getTopY()) {
            return;
        }
        BlockIndex index = new BlockIndex(x, y, z);
        if (visitedBlocks.add(index)) {
            if ((x - this.x) * (x - this.x) + (z - this.z) * (z - this.z) > 64 * 64) {
                return;
            }

            BlockState blockState = world.getBlockState(x, y, z);
            if (FluidRegistry.get(blockState.getBlock().id) == getFluid(0, null).fluid) {
                fluidsFound.add(index);
            }
            if (canPlaceFluidAt(blockState.getBlock(), x, y, z)) {
                getLayerQueue(y).addLast(index);
            }
        }
    }

    private boolean canPlaceFluidAt(Block block, int x, int y, int z) {
        return BlockUtil.isSoftBlock(world, x, y, z) && !BlockUtil.isFullFluidBlock(block, world, x, y, z);
    }

    public void neighborUpdate(){
        boolean p = world.isPowered(x, y, z);
        if (powered != p) {
            powered = p;
            if (!p) {
                rebuildQueue();
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        rebuildDelay = nbt.getByte("rebuildDelay");
        powered = nbt.getBoolean("powered");
        for (int i = 0; i < 6; i++) {
            blockedSides[i] = nbt.getBoolean("blocked[" + i + "]");
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putByte("rebuildDelay", (byte) rebuildDelay);
        nbt.putBoolean("powered", powered);
        for (int i = 0; i < 6; i++) {
            if (blockedSides[i]) {
                nbt.putBoolean("blocked[" + i + "]", true);
            }
        }
    }

    @Override
    public void writeData(DataOutputStream stream) throws IOException {
        for (int i = 0; i < 6; i++) {
            stream.writeBoolean(blockedSides[i]);
        }
    }

    @Override
    public void readData(DataInputStream stream) throws IOException {
        for (int i = 0; i < 6; i++) {
            blockedSides[i] = stream.readBoolean();
        }
    }

    public void switchSide(Direction side) {
        if (side.ordinal() != 1) {
            blockedSides[side.ordinal()] = !blockedSides[side.ordinal()];

            rebuildQueue();
            sendNetworkUpdate();
            world.setBlockDirty(x, y, z);
        }
    }

    public boolean isSideBlocked(int side) {
        return blockedSides[side];
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        pumpLayerQueues.clear();
    }
}
