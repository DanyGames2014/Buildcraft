package net.danygames2014.buildcraft.block.entity.pipe.transporter;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;
import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.core.SafeTimeTracker;
import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.pipe.*;
import net.danygames2014.buildcraft.packet.PacketFluidUpdate;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.fluidhandler.FluidHandlerBlockCapability;
import net.danygames2014.nyalib.fluid.Fluid;
import net.danygames2014.nyalib.fluid.FluidHandler;
import net.danygames2014.nyalib.fluid.FluidRegistry;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;

@SuppressWarnings({"DataFlowIssue", "RedundantIfStatement", "StatementWithEmptyBody", "SameParameterValue", "ConstantValue", "PointlessArithmeticExpression", "MismatchedReadAndWriteOfArray"})
public class FluidPipeTransporter extends PipeTransporter implements FluidHandler {
    static final int pipeFluidsBaseFlowRate = 10;

    public static final HashMap<Block, Integer> fluidCapacities = new HashMap<net.minecraft.block.Block, Integer>();

    /**
     * The amount of liquid contained by a pipe section. For simplicity, all
     * pipe sections are assumed to be of the same volume.
     */
    public static int MAX_TRAVEL_DELAY = 12;
    public static short INPUT_TTL = 60; // 100
    public static short OUTPUT_TTL = 80; // 80
    public static short OUTPUT_COOLDOWN = 30; // 30

    private static final int NETWORK_SYNC_TICKS = 10 / 2; //BuildCraftCore.updateFactor / 2;
    private static final ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;
    private static final ForgeDirection[] orientations = ForgeDirection.values();

    public int getSideRemainingCapacity(ForgeDirection direction) {
        return capacity - sections[direction.ordinal()].amount;
    }

    public int getSideFillLevel(ForgeDirection side) {
        return sections[side.ordinal()].amount;
    }

    public class PipeSection {
        public int amount;

        private short currentTime = 0;
        private short[] incoming = new short[MAX_TRAVEL_DELAY];

        public int fill(int maxFill, boolean doFill) {
            int amountToFill = Math.min(getMaxFillRate(), maxFill);
            if (amountToFill <= 0) {
                return 0;
            }

            if (doFill) {
                incoming[currentTime] += (short) amountToFill;
                amount += amountToFill;
            }
            return amountToFill;
        }

        public int drain(int maxDrain, boolean doDrain) {
            int maxToDrain = getAvailable();
            if (maxToDrain > maxDrain) {
                maxToDrain = maxDrain;
            }
            if (maxToDrain > flowRate) {
                maxToDrain = flowRate;
            }
            if (maxToDrain <= 0) {
                return 0;
            } else {
                if (doDrain) {
                    amount -= maxToDrain;
                }
                return maxToDrain;
            }
        }

        public void moveFluids() {
            incoming[currentTime] = 0;
        }

        public void setTime(short newTime) {
            currentTime = newTime;
        }

        public void reset() {
            this.amount = 0;
            incoming = new short[MAX_TRAVEL_DELAY];
        }

        /**
         * Get the amount of fluid available to move. This nicely takes care
         * of the travel delay mechanic.
         */
        public int getAvailable() {
            int all = amount;
            for (short slot : incoming) {
                all -= slot;
            }
            return all;
        }

        public int getMaxFillRate() {
            return Math.min(getCapacity() - amount, flowRate - incoming[currentTime]);
        }

        public void readNbt(NbtCompound compoundTag) {
            this.amount = compoundTag.getShort("capacity");

            for (int i = 0; i < travelDelay; ++i) {
                incoming[i] = compoundTag.getShort("in[" + i + "]");
            }
        }

        public void writeNbt(NbtCompound subTag) {
            subTag.putShort("capacity", (short) amount);

            for (int i = 0; i < travelDelay; ++i) {
                subTag.putShort("in[" + i + "]", incoming[i]);
            }
        }

        @Override
        public String toString() {
            return "PipeSection{" +
                    "amount=" + amount +
                    '}';
        }
    }

    public PipeSection[] sections = new PipeSection[7];
    public Fluid fluidType;

    public FluidRenderData renderCache = new FluidRenderData();

    private final SafeTimeTracker networkSyncTracker = new SafeTimeTracker(NETWORK_SYNC_TICKS);
    private final TransferState[] transferState = new TransferState[directions.length];
    private final int[] inputPerTick = new int[directions.length];
    private final short[] inputTTL = new short[]{0, 0, 0, 0, 0, 0};
    private final short[] outputTTL = new short[]{OUTPUT_TTL, OUTPUT_TTL, OUTPUT_TTL, OUTPUT_TTL, OUTPUT_TTL, OUTPUT_TTL};
    private final short[] outputCooldown = new short[]{0, 0, 0, 0, 0, 0};
    private final boolean[] canReceiveCache = new boolean[6];
    private int clientSyncCounter = 0;
    private int capacity, flowRate;
    private int travelDelay = MAX_TRAVEL_DELAY;

    public enum TransferState {
        None, Input, Output
    }

    public FluidPipeTransporter(PipeBlockEntity pipe) {
        super(pipe);
        initFromPipe(pipe.pipeBlock);
        for (ForgeDirection direction : directions) {
            sections[direction.ordinal()] = new PipeSection();
            transferState[direction.ordinal()] = TransferState.None;
        }
        sections[6] = new PipeSection();
    }

    /**
     * This value has to be the same on client and server!
     */
    public int getCapacity() {
        return capacity;
    }

    public int getFlowRate() {
        return flowRate;
    }

    public void initFromPipe(PipeBlock pipeClass) {
        capacity = 25 * Math.min(1000, pipeFluidsBaseFlowRate);
        flowRate = fluidCapacities.get(pipeClass);
        travelDelay = MathHelper.clamp(Math.round(16F / ((float) flowRate / pipeFluidsBaseFlowRate)), 1, MAX_TRAVEL_DELAY);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public PipeType getType() {
        return PipeType.FLUID;
    }

    private BlockEntity getBlockEntityOnSide(ForgeDirection direction) {
        if (world == null) {
            return null;
        }
        
        return world.getBlockEntity(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ);
    }

    private boolean canReceiveFluid(ForgeDirection o) {
        BlockEntity tile = getBlockEntityOnSide(o);

        if (tile == null) {
            return false;
        }

        if (!isPipeConnected(o.getDirection())) {
            return false;
        }

        if (tile instanceof PipeBlockEntity pipe) {
            if (!inputOpen(o.getOpposite().getDirection())) {
                return false;
            }
        }

        FluidHandlerBlockCapability cap = CapabilityHelper.getCapability(world, tile.x, tile.y, tile.z, FluidHandlerBlockCapability.class);
        if (cap != null) {
            return true;
        }

        return false;
    }

    boolean initCache = false;

    @Override
    public void tick() {
        super.tick();

        if (world == null) {
            return;
        }

        if (blockEntity.world.isRemote) {
            return;
        }

        if (!initCache) {
            for (ForgeDirection d : directions) {
                canReceiveCache[d.ordinal()] = canReceiveFluid(d);
            }
        }

        moveFluids();

        if (networkSyncTracker.markTimeIfDelay(blockEntity.world)) {
            boolean init = false;
            if (++clientSyncCounter > 40) { // BuildCraftCore.longUpdateFactor * 2) {
                clientSyncCounter = 0;
                init = true;
            }
            PacketFluidUpdate packet = computeFluidUpdate(init, true);

            if (packet != null) {
                // TODO BuildCraftTransport.instance.sendToPlayers(packet, blockEntity.world, blockEntity.x, blockEntity.y, blockEntity.z, DefaultProps.PIPE_CONTENTS_RENDER_DIST);
            }
        }
    }

    private void moveFluids() {
        if (fluidType != null) {
            short newTimeSlot = (short) (blockEntity.world.getTime() % travelDelay);
            int outputCount = computeCurrentConnectionStatesAndTickFlows(newTimeSlot > 0 && newTimeSlot < travelDelay ? newTimeSlot : 0);

            if (fluidType != null) {
                moveFromPipe(outputCount);
                moveFromCenter();
                moveToCenter();
            }
        } else {
            computeTTLs();
        }
    }

    private void moveFromPipe(int outputCount) {
        // Move liquid from the non-center to the connected output blocks
        if (outputCount > 0) {
            for (ForgeDirection o : directions) {
                if (transferState[o.ordinal()] == TransferState.Output) {
                    if (getBlockEntityOnSide(o) instanceof PipeBlockEntity pipe && pipe.transporter instanceof FluidPipeTransporter fluidTransporter) {
                        PipeSection section = sections[o.ordinal()];
                        FluidStack liquidToPush = new FluidStack(fluidType, section.drain(flowRate, false));

                        if (liquidToPush.amount > 0) {
                            int originalAmount = liquidToPush.amount;
                            FluidStack resultStack = fluidTransporter.insertFluid(liquidToPush, o.getOpposite().getDirection());
                            int filled = resultStack == null ? originalAmount : originalAmount - resultStack.amount;

                            //int filled = ((IFluidHandler) target).fill(o.getOpposite(), liquidToPush, true);
                            if (filled <= 0) {
                                outputTTL[o.ordinal()]--;
                            } else {
                                section.drain(filled, true);
                            }
                        }
                    } else {
//                    BlockEntity target = getBlockEntityOnSide(o);
                        FluidHandlerBlockCapability cap = CapabilityHelper.getCapability(world, x + o.offsetX, y + o.offsetY, z + o.offsetZ, FluidHandlerBlockCapability.class);

//                    if (!(target instanceof IFluidHandler)) {
//                        continue;
//                    }
                        if (cap == null) {
                            continue;
                        }

                        PipeSection section = sections[o.ordinal()];
                        FluidStack liquidToPush = new FluidStack(fluidType, section.drain(flowRate, false));

                        if (liquidToPush.amount > 0) {
                            int originalAmount = liquidToPush.amount;
                            FluidStack resultStack = cap.insertFluid(liquidToPush, o.getOpposite().getDirection());
                            int filled = resultStack == null ? originalAmount : originalAmount - resultStack.amount;

                            //int filled = ((IFluidHandler) target).fill(o.getOpposite(), liquidToPush, true);
                            if (filled <= 0) {
                                outputTTL[o.ordinal()]--;
                            } else {
                                section.drain(filled, true);
                            }
                        }
                    }
                }
            }
        }
    }

    private void moveFromCenter() {
        // Split liquids moving to output equally based on flowrate, how much each side can accept and available liquid
        int pushAmount = sections[6].amount;
        int totalAvailable = sections[6].getAvailable();
        if (totalAvailable < 1 || pushAmount < 1) {
            return;
        }

        int testAmount = flowRate;
        // Move liquid from the center to the output sides
        Multiset<ForgeDirection> realDirections = EnumMultiset.create(ForgeDirection.class);
        for (ForgeDirection direction : directions) {
            if (transferState[direction.ordinal()] == TransferState.Output) {
                realDirections.add(direction);
            }
        }

        if (!realDirections.isEmpty()) {
            // TODO blockEntity.pipe.eventBus.handleEvent(PipeEventFluid.FindDest.class, new PipeEventFluid.FindDest(blockEntity.pipe, new FluidStack(fluidType, pushAmount), realDirections));


            float min = Math.min(flowRate * realDirections.size(), totalAvailable) / (float) flowRate / realDirections.size();

            for (ForgeDirection direction : realDirections.elementSet()) {
                int available = sections[direction.ordinal()].fill(testAmount, false);
                int amountToPush = (int) (available * min * realDirections.count(direction));
                if (amountToPush < 1) {
                    amountToPush++;
                }

                amountToPush = sections[6].drain(amountToPush, false);
                if (amountToPush > 0) {
                    int filled = sections[direction.ordinal()].fill(amountToPush, true);
                    sections[6].drain(filled, true);
                }
            }
        }
    }

    private void moveToCenter() {
        int transferInCount = 0;
        int spaceAvailable = getCapacity() - sections[6].amount;

        for (ForgeDirection dir : directions) {
            inputPerTick[dir.ordinal()] = 0;
            if (transferState[dir.ordinal()] != TransferState.Output) {
                inputPerTick[dir.ordinal()] = sections[dir.ordinal()].drain(flowRate, false);
                transferInCount++;
            }
        }

        float min = Math.min(flowRate * transferInCount, spaceAvailable) / (float) flowRate / transferInCount;
        for (ForgeDirection dir : directions) {
            // Move liquid from input sides to the center
            if (transferState[dir.ordinal()] != TransferState.Output && inputPerTick[dir.ordinal()] > 0) {

                int amountToDrain = (int) (inputPerTick[dir.ordinal()] * min);
                if (amountToDrain < 1) {
                    amountToDrain++;
                }

                int amountToPush = sections[dir.ordinal()].drain(amountToDrain, false);
                if (amountToPush > 0) {
                    int filled = sections[6].fill(amountToPush, true);
                    sections[dir.ordinal()].drain(filled, true);
                }
            }
        }
    }

    private void computeTTLs() {
        for (int i = 0; i < 6; i++) {
            if (transferState[i] == TransferState.Input) {
                if (inputTTL[i] > 0) {
                    inputTTL[i]--;
                } else {
                    transferState[i] = TransferState.None;
                }
            }

            if (outputCooldown[i] > 0) {
                outputCooldown[i]--;
            }
        }
    }

    private int computeCurrentConnectionStatesAndTickFlows(short newTimeSlot) {
        int outputCount = 0;
        int fluidAmount = 0;

        // Processes all internal tanks
        for (ForgeDirection direction : orientations) {
            int dirI = direction.ordinal();
            PipeSection section = sections[dirI];

            fluidAmount += section.amount;
            section.setTime(newTimeSlot);
            section.moveFluids();

            // Input processing
            if (direction == ForgeDirection.UNKNOWN) {
                continue;
            }
            if (transferState[dirI] == TransferState.Input) {
                inputTTL[dirI]--;
                if (inputTTL[dirI] <= 0) {
                    transferState[dirI] = TransferState.None;
                }
                continue;
            }
            if (!outputOpen(direction.getDirection())) {
                transferState[dirI] = TransferState.None;
                continue;
            }
            if (outputCooldown[dirI] > 0) {
                outputCooldown[dirI]--;
                continue;
            }
            if (outputTTL[dirI] <= 0) {
                transferState[dirI] = TransferState.None;
                outputCooldown[dirI] = OUTPUT_COOLDOWN;
                outputTTL[dirI] = OUTPUT_TTL;
                continue;
            }
            if (/* canReceiveCache[dirI] */ isPipeConnected(direction.getDirection()) && outputOpen(direction.getDirection())) {
                transferState[dirI] = TransferState.Output;
                outputCount++;
            }
        }

        if (fluidAmount == 0) {
            setFluidType((Fluid) null);
        }

        return outputCount;
    }

    /**
     * Computes the PacketFluidUpdate packet for transmission to a client
     *
     * @param initPacket    everything is sent, no delta stuff ( first packet )
     * @param persistChange The render cache change is persisted
     * @return PacketFluidUpdate liquid update packet
     */
    private PacketFluidUpdate computeFluidUpdate(boolean initPacket, boolean persistChange) {
        boolean changed = false;
        BitSet delta = new BitSet(8);

        FluidRenderData renderCacheCopy = this.renderCache;

        if (initPacket || (fluidType == null && renderCacheCopy.fluidId != null) || (fluidType != null && renderCacheCopy.fluidId != fluidType.getIdentifier())) {
            changed = true;
            renderCache.fluidId = fluidType != null ? fluidType.getIdentifier() : null;
            renderCache.color = fluidType != null ? fluidType.getColor() : 0;
            delta.set(0);
        }

        for (ForgeDirection dir : orientations) {
            int pamount = renderCache.amount[dir.ordinal()];
            int camount = sections[dir.ordinal()].amount;
            int displayQty = (pamount * 4 + camount) / 5;
            if (displayQty == 0 && camount > 0 || initPacket) {
                displayQty = camount;
            }
            displayQty = Math.min(getCapacity(), displayQty);

            if (pamount != displayQty || initPacket) {
                changed = true;
                renderCache.amount[dir.ordinal()] = displayQty;
                delta.set(dir.ordinal() + 1);
            }
        }

        if (persistChange) {
            this.renderCache = renderCacheCopy;
        }

        if (changed || initPacket) {
            PacketFluidUpdate packet = new PacketFluidUpdate(blockEntity.x, blockEntity.y, blockEntity.z, initPacket, getCapacity() > 255);
            packet.renderCache = renderCacheCopy;
            packet.delta = delta;
            return packet;
        }

        return null;
    }

    private void setFluidType(Fluid fluid) {
        fluidType = fluid;
    }

    private void setFluidType(FluidStack stack) {
        fluidType = stack.fluid;
    }

    /**
     * Initializes client
     */
    //@Override
    public void sendDescriptionPacket() {
        // TODO super.sendDescriptionPacket();

        PacketFluidUpdate update = computeFluidUpdate(true, true);
        // TODO BuildCraftTransport.instance.sendToPlayers(update, blockEntity.world, blockEntity.x, blockEntity.y, blockEntity.z, DefaultProps.PIPE_CONTENTS_RENDER_DIST);
    }

    public FluidStack getStack(ForgeDirection direction) {
        if (fluidType == null) {
            return null;
        } else {
            return new FluidStack(fluidType, sections[direction.ordinal()].amount);
        }
    }

    //@Override
    public void dropContents() {
        if (fluidType != null) {
            int totalAmount = 0;
            for (int i = 0; i < 7; i++) {
                totalAmount += sections[i].amount;
            }
            if (totalAmount > 0) {
                // TODO
                //FluidEvent.fireEvent(new FluidEvent.FluidSpilledEvent(
                //        new FluidStack(fluidType, totalAmount),
                //        world, blockEntity.x, blockEntity.y, blockEntity.z
                //));
            }
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        if (tag.contains("fluid")) {
            setFluidType(FluidRegistry.get(Identifier.of(tag.getString("fluid"))));
        } else {
            setFluidType((Fluid) null);
        }

        for (ForgeDirection direction : orientations) {
            if (tag.contains("tank[" + direction.ordinal() + "]")) {
                NbtCompound compound = tag.getCompound("tank[" + direction.ordinal() + "]");
                if (compound.contains("FluidType")) {
                    FluidStack stack = new FluidStack(compound);
                    if (fluidType == null) {
                        setFluidType(stack);
                    }
                    if (stack.fluid.equals(fluidType)) {
                        sections[direction.ordinal()].readNbt(compound);
                    }
                } else {
                    sections[direction.ordinal()].readNbt(compound);
                }
            }
            if (direction != ForgeDirection.UNKNOWN) {
                transferState[direction.ordinal()] = TransferState.values()[tag.getShort("transferState[" + direction.ordinal() + "]")];
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbttagcompound) {
        super.writeNbt(nbttagcompound);

        if (fluidType != null) {
            nbttagcompound.putString("fluid", fluidType.getIdentifier().toString());

            for (ForgeDirection direction : orientations) {
                NbtCompound subTag = new NbtCompound();
                sections[direction.ordinal()].writeNbt(subTag);
                nbttagcompound.put("tank[" + direction.ordinal() + "]", subTag);
                if (direction != ForgeDirection.UNKNOWN) {
                    nbttagcompound.putShort("transferState[" + direction.ordinal() + "]", (short) transferState[direction.ordinal()].ordinal());
                }
            }
        }
    }

    public boolean inputOpen(Direction side) {
        return blockEntity.behavior.isFluidInputOpen(blockEntity, side, blockEntity.connections.get(side));
    }

    public boolean outputOpen(Direction side) {
        return blockEntity.behavior.isFluidOutputOpen(blockEntity, side, blockEntity.connections.get(side));
    }

    public boolean isPipeConnected(Direction direction) {
        return blockEntity.connections.get(direction) != PipeConnectionType.NONE;
    }

    // Fluid Handler
    @Override
    public boolean canExtractFluid(@Nullable Direction direction) {
        return false;
    }

    @Override
    public FluidStack extractFluid(int slot, int amount, @Nullable Direction direction) {
        return null;
    }

    @Override
    public boolean canInsertFluid(@Nullable Direction direction) {
        return inputOpen(direction);
    }

    @Override
    public FluidStack insertFluid(FluidStack stack, int slot, @Nullable Direction direction) {
        int filled = fill(ForgeDirection.fromDirection(direction), stack, true);

        if (filled >= stack.amount) {
            return null;
        } else {
            FluidStack newStack = stack.copy();
            newStack.amount -= filled;
            return newStack;
        }
    }

    @Override
    public FluidStack insertFluid(FluidStack stack, @Nullable Direction direction) {
        int filled = fill(ForgeDirection.fromDirection(direction), stack, true);

        if (filled >= stack.amount) {
            return null;
        } else {
            FluidStack newStack = stack.copy();
            newStack.amount -= filled;
            return newStack;
        }
    }

    //@Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (from != ForgeDirection.UNKNOWN && !inputOpen(from.getDirection())) {
            return 0;
        }

        if (resource == null || (fluidType != null && !resource.fluid.equals(fluidType))) {
            return 0;
        }

        int filled;

//        if (this.blockEntity.pipe instanceof IPipeTransportFluidsHook) {
//            filled = ((IPipeTransportFluidsHook) this.blockEntity.pipe).fill(from, resource, doFill);
//        } else {
        filled = sections[from.ordinal()].fill(resource.amount, doFill);
//        }

        if (doFill && filled > 0) {
            if (fluidType == null) {
                setFluidType(resource.fluid);
            }
            if (from != ForgeDirection.UNKNOWN) {
                transferState[from.ordinal()] = TransferState.Input;
                inputTTL[from.ordinal()] = INPUT_TTL;
            }
        }

        return filled;
    }

    public int injectFluid(FluidStack stack, ForgeDirection side) {
        return fill(side, stack, true);
    }

    @Override
    public FluidStack getFluid(int slot, @Nullable Direction direction) {
        return null;
    }

    @Override
    public boolean setFluid(int slot, FluidStack stack, @Nullable Direction direction) {
        return false;
    }

    @Override
    public int getFluidSlots(@Nullable Direction direction) {
        return 1;
    }

    @Override
    public int getFluidCapacity(int slot, @Nullable Direction direction) {
        return fluidCapacities.getOrDefault(blockEntity.pipeBlock, 0);
    }

    @Override
    public FluidStack[] getFluids(@Nullable Direction direction) {
        return new FluidStack[]{new FluidStack(fluidType, sections[direction.ordinal()].amount)};
    }

    @Override
    public boolean canConnectFluid(Direction direction) {
        return false;
    }

    public void onConnectionsUpdate() {
        super.onConnectionsUpdate();

        for (ForgeDirection direction : ForgeDirection.values()) {
            if (!isPipeConnected(direction.getDirection())) {
                sections[direction.ordinal()].reset();
                transferState[direction.ordinal()] = TransferState.None;
                renderCache.amount[direction.ordinal()] = 0;
                if (direction != ForgeDirection.UNKNOWN) {
                    canReceiveCache[direction.ordinal()] = false;
                }
            } else {
                if (direction != ForgeDirection.UNKNOWN) {
                    canReceiveCache[direction.ordinal()] = canReceiveFluid(direction);
                }
            }
        }
    }

    //    @Override
    public boolean canPipeConnect(BlockEntity tile, ForgeDirection side) {
        Direction direction = side.getDirection();

        if (direction == null) {
            return false;
        } else {
            return blockEntity.canConnectTo(x, y, z, direction) != PipeConnectionType.NONE;
        }

//        if (tile instanceof IPipeTile) {
//            Pipe<?> pipe2 = (Pipe<?>) ((IPipeTile) tile).getPipe();
//            if (BlockGenericPipe.isValid(pipe2) && !(pipe2.transport instanceof PipeTransportFluids)) {
//                return false;
//            }
//        }
//
//        if (tile instanceof IFluidHandler) {
//            return true;
//        }
//
//        return tile instanceof IPipeTile;
    }

    static {
        fluidCapacities.put(Buildcraft.woodenFluidPipe, 1 * pipeFluidsBaseFlowRate);
        fluidCapacities.put(Buildcraft.cobblestoneFluidPipe, 1 * pipeFluidsBaseFlowRate);
        fluidCapacities.put(Buildcraft.stoneFluidPipe, 2 * pipeFluidsBaseFlowRate);
        fluidCapacities.put(Buildcraft.sandstoneFluidPipe, 2 * pipeFluidsBaseFlowRate);
        fluidCapacities.put(Buildcraft.goldenFluidPipe, 8 * pipeFluidsBaseFlowRate);
        fluidCapacities.put(Buildcraft.ironFluidPipe, 4 * pipeFluidsBaseFlowRate);
        fluidCapacities.put(Buildcraft.diamondFluidPipe, 8 * pipeFluidsBaseFlowRate);
//        fluidCapacities.put(PipeFluidsClay.class, 4 * BuildCraftTransport.pipeFluidsBaseFlowRate);
//        fluidCapacities.put(PipeFluidsEmerald.class, 4 * BuildCraftTransport.pipeFluidsBaseFlowRate);
//        fluidCapacities.put(PipeFluidsQuartz.class, 4 * BuildCraftTransport.pipeFluidsBaseFlowRate);
//        fluidCapacities.put(PipeFluidsVoid.class, 1 * BuildCraftTransport.pipeFluidsBaseFlowRate);
    }

    @Override
    public String toString() {
        return "FluidPipeTransporter{" +
                "sections=" + Arrays.toString(sections) +
                ", fluidType=" + fluidType +
                '}';
    }
}
