package net.danygames2014.buildcraft.block.entity.pipe.transporter;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.core.SafeTimeTracker;
import net.danygames2014.buildcraft.api.energy.IPowerReceptor;
import net.danygames2014.buildcraft.api.energy.PowerHandler;
import net.danygames2014.buildcraft.block.entity.pipe.*;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Arrays;

public class EnergyPipeTransporter extends PipeTransporter {
    private static final short MAX_DISPLAY = 100;
    private static final int DISPLAY_SMOOTHING = 10;
    private static final int OVERLOAD_TICKS = 60;
    
    public static final Object2IntOpenHashMap<Block> powerCapacities = new Object2IntOpenHashMap<>();

    static {
        powerCapacities.put(Buildcraft.woodenEnergyPipe, 32);
        powerCapacities.put(Buildcraft.cobblestoneEnergyPipe, 8);
        powerCapacities.put(Buildcraft.stoneEnergyPipe, 16);
        powerCapacities.put(Buildcraft.ironEnergyPipe, 128);
        powerCapacities.put(Buildcraft.sandstoneEnergyPipe, 16);
        powerCapacities.put(Buildcraft.goldenEnergyPipe, 256);
//        powerCapacities.put(PipePowerQuartz.class, 64);
//        powerCapacities.put(PipePowerDiamond.class, 1024);
    }
    
    private boolean needsInit = true;
    private BlockEntity[] neighborBlockEntities = new BlockEntity[6];
    public float[] displayPower = new float[6];
    private float[] prevDisplayPower = new float[6];
    public short[] clientDisplayPower = new short[6];
    public int overload;
    private int[] powerQuery = new int[6];
    public int[] nextPowerQuery = new int[6];
    private long currentDate;
    private float[] internalPower = new float[6];
    public float[] internalNextPower = new float[6];
    public int maxPower = 8;
    private double highestPower;
    SafeTimeTracker tracker = new SafeTimeTracker();

    public EnergyPipeTransporter(PipeBlockEntity blockEntity) {
        super(blockEntity);
        for (int i = 0; i < 6; ++i) {
            powerQuery[i] = 0;
        }
        maxPower = powerCapacities.getInt(blockEntity.pipeBlock);
    }

    @Override
    public PipeType getType() {
        return PipeType.ENERGY;
    }

    private BlockEntity getBlockEntityOnSide(ForgeDirection direction) {
        if (world == null) {
            return null;
        }

        return world.getBlockEntity(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ);
    }

    public boolean isPipeConnected(Direction direction) {
        return blockEntity.connections.get(direction) != PipeConnectionType.NONE;
    }

//    @Override
//    public boolean canPipeConnect(BlockEntity blockEntity, ForgeDirection side) {
//        if (blockEntity instanceof PipeBlockEntity otherPipe) {
//            return otherPipe.transporter instanceof EnergyPipeTransporter;
//        }
//
//        if (blockEntity instanceof IPowerReceptor) {
//            IPowerReceptor receptor = (IPowerReceptor) blockEntity;
//            PowerHandler.PowerReceiver receiver = receptor.getPowerReceiver(side.getOpposite());
//            if (receiver != null && receiver.getType().canReceiveFromPipes())
//                return true;
//        }
//
//        if (this.blockEntity.behavior instanceof WoodenPipeBehavior && blockEntity instanceof IPowerEmitter emitter) {
//            if (emitter.canEmitPowerFrom(side.getOpposite().getDirection())) {
//                return true;
//            }
//        }
//
//        return false;
//    }

    @Override
    public void onConnectionsUpdate() {
        super.onConnectionsUpdate();
        updateTiles();
    }
    
    private void updateTiles() {
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            BlockEntity tile = getBlockEntityOnSide(side);
            if (isPipeConnected(side.getDirection())) {
                neighborBlockEntities[side.ordinal()] = tile;
            } else {
                neighborBlockEntities[side.ordinal()] = null;
                internalPower[side.ordinal()] = 0;
                internalNextPower[side.ordinal()] = 0;
                displayPower[side.ordinal()] = 0;
            }
        }
    }

    public void pipeInit() {
        if (needsInit) {
            needsInit = false;
            currentDate = world.getTime();
            updateTiles();
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (world.isRemote) {
            return;
        }

        step();

        pipeInit();

        // Send the power to nearby pipes who requested it

        System.arraycopy(displayPower, 0, prevDisplayPower, 0, 6);
        Arrays.fill(displayPower, 0.0F);

        for (int i = 0; i < 6; ++i) {
            if (internalPower[i] > 0) {
                float totalPowerQuery = 0;

                for (int j = 0; j < 6; ++j) {
                    if (j != i && powerQuery[j] > 0)
                        if (neighborBlockEntities[j] instanceof PipeBlockEntity || neighborBlockEntities[j] instanceof IPowerReceptor) {
                            totalPowerQuery += powerQuery[j];
                        }
                }

                for (int j = 0; j < 6; ++j) {
                    if (j != i && powerQuery[j] > 0) {
                        float watts = 0.0F;

                        PowerHandler.PowerReceiver prov = getReceiverOnSide(ForgeDirection.VALID_DIRECTIONS[j]);
                        if (prov != null && prov.powerRequest() > 0) {
                            watts = (internalPower[i] / totalPowerQuery) * powerQuery[j];
                            watts = (float) prov.receiveEnergy(PowerHandler.Type.PIPE, watts, ForgeDirection.VALID_DIRECTIONS[j].getOpposite().getDirection());
                            internalPower[i] -= watts;
                        } else if (neighborBlockEntities[j] instanceof PipeBlockEntity nearbyTile) {
                            watts = (internalPower[i] / totalPowerQuery) * powerQuery[j];

                            EnergyPipeTransporter nearbyTransport = (EnergyPipeTransporter) nearbyTile.transporter;

                            watts = nearbyTransport.receiveEnergy(ForgeDirection.VALID_DIRECTIONS[j].getOpposite(), watts);
                            internalPower[i] -= watts;
                        }

                        displayPower[j] += watts;
                        displayPower[i] += watts;
                    }
                }
            }
        }

        highestPower = 0;
        for (int i = 0; i < 6; i++) {
            displayPower[i] = (prevDisplayPower[i] * (DISPLAY_SMOOTHING - 1.0F) + displayPower[i]) / DISPLAY_SMOOTHING;
            if (displayPower[i] > highestPower) {
                highestPower = displayPower[i];
            }
        }

        overload += highestPower > maxPower * 0.95 ? 1 : -1;
        if (overload < 0) {
            overload = 0;
        }
        if (overload > OVERLOAD_TICKS) {
            overload = OVERLOAD_TICKS;
        }

        // Compute the tiles requesting energy that are not power pipes

        for (int i = 0; i < 6; ++i) {
            PowerHandler.PowerReceiver prov = getReceiverOnSide(ForgeDirection.VALID_DIRECTIONS[i]);
            if (prov != null) {
                float request = (float) prov.powerRequest();

                if (request > 0) {
                    requestEnergy(ForgeDirection.VALID_DIRECTIONS[i], request);
                }
            }
        }

        // Sum the amount of energy requested on each side

        int[] transferQuery = new int[6];

        for (int i = 0; i < 6; ++i) {
            transferQuery[i] = 0;

            for (int j = 0; j < 6; ++j) {
                if (j != i) {
                    transferQuery[i] += powerQuery[j];
                }
            }

            transferQuery[i] = Math.min(transferQuery[i], maxPower);
        }

        // Transfer the requested energy to nearby pipes

        for (int i = 0; i < 6; ++i) {
            if (transferQuery[i] != 0) {
                if (neighborBlockEntities[i] != null) {
                    BlockEntity neighborBlockEntity = neighborBlockEntities[i];

                    if (neighborBlockEntity instanceof PipeBlockEntity nearbyTile) {
//                        if (nearbyTile.pipe == null) {
//                            continue;
//                        }

                        EnergyPipeTransporter nearbyTransport = (EnergyPipeTransporter) nearbyTile.transporter;
                        nearbyTransport.requestEnergy(ForgeDirection.VALID_DIRECTIONS[i].getOpposite(), transferQuery[i]);
                    }
                }
            }
        }

        if (tracker.markTimeIfDelay(world, 2 * 10)) {//BuildCraftCore.updateFactor)) {
            //PacketPowerUpdate packet = new PacketPowerUpdate(container.xCoord, container.yCoord, container.zCoord);

            double displayFactor = MAX_DISPLAY / 1024.0;
            for (int i = 0; i < clientDisplayPower.length; i++) {
                clientDisplayPower[i] = (short) (displayPower[i] * displayFactor + .9999);
            }

            //packet.displayPower = clientDisplayPower;
            //packet.overload = isOverloaded();
            //BuildCraftTransport.instance.sendToPlayers(packet, container.getWorldObj(), container.xCoord, container.yCoord, container.zCoord, DefaultProps.PIPE_CONTENTS_RENDER_DIST);
        }

    }

    private PowerHandler.PowerReceiver getReceiverOnSide(ForgeDirection side) {
        BlockEntity tile = neighborBlockEntities[side.ordinal()];
        if (!(tile instanceof IPowerReceptor receptor)) {
            return null;
        }
        
        PowerHandler.PowerReceiver receiver = receptor.getPowerReceiver(side.getOpposite().getDirection());
        
        if (receiver == null) {
            return null;
        }
        
        if (!receiver.getType().canReceiveFromPipes()) {
            return null;
        }
        
        return receiver;
    }

    public boolean isOverloaded() {
        return overload >= OVERLOAD_TICKS;
    }

    private void step() {
        if (world == null) {
            return;
        }
        
        if (currentDate != world.getTime()) {
            currentDate = world.getTime();

            powerQuery = nextPowerQuery;
            nextPowerQuery = new int[6];

            float[] next = internalPower;
            internalPower = internalNextPower;
            internalNextPower = next;
//			for (int i = 0; i < powerQuery.length; i++) {
//				int sum = 0;
//				for (int j = 0; j < powerQuery.length; j++) {
//					if (i != j) {
//						sum += powerQuery[j];
//					}
//				}
//				if (sum == 0 && internalNextPower[i] > 0) {
//					internalNextPower[i] -= 1;
//				}
//			}
        }
    }

    /**
     * Do NOT ever call this from outside Buildcraft. It is NOT part of the API.
     * All power input MUST go through designated input pipes, such as Wooden
     * Power Pipes or a subclass thereof.
     */
    public float receiveEnergy(ForgeDirection from, float val) {
        step();
        if (this.blockEntity.behavior instanceof IPipeTransportPowerHook transportPowerHook) {
            float ret = transportPowerHook.receiveEnergy(this.blockEntity, from, val);
            if (ret >= 0)
                return ret;
        }
        
        int side = from.ordinal();
        if (internalNextPower[side] > maxPower)
            return 0;

        internalNextPower[side] += val;

        if (internalNextPower[side] > maxPower) {
            val -= internalNextPower[side] - maxPower;
            internalNextPower[side] = maxPower;
            if (val < 0)
                val = 0;
        }
        return val;
    }

    public void requestEnergy(ForgeDirection from, float amount) {
        step();
        
        if (this.blockEntity.behavior instanceof IPipeTransportPowerHook transportPowerHook) {
            nextPowerQuery[from.ordinal()] += (int) transportPowerHook.requestEnergy(this.blockEntity, from, amount);
        } else {
            nextPowerQuery[from.ordinal()] += (int) amount;
        }
    }

//    public boolean isTriggerActive(ITrigger trigger) {
//        return false;
//    }

    /**
     * Client-side handler for receiving power updates from the server;
     */
//    public void handlePowerPacket(PacketPowerUpdate packetPower) {
//        clientDisplayPower = packetPower.displayPower;
//        overload = packetPower.overload ? OVERLOAD_TICKS : 0;
//    }

    /**
     * This can be use to provide a rough estimate of how much power is flowing
     * through a pipe. Measured in MJ/t.
     *
     * @return MJ/t
     */
    public double getCurrentPowerTransferRate() {
        return highestPower;
    }

    /**
     * This can be use to provide a rough estimate of how much power is
     * contained in a pipe. Measured in MJ.
     * <p>
     * Max should be around (throughput * internalPower.length * 2), ie 112 MJ for a Cobblestone Pipe.
     *
     * @return MJ
     */
    public double getCurrentPowerAmount() {
        double amount = 0.0;
        for (double d : internalPower) {
            amount += d;
        }
        for (double d : internalNextPower) {
            amount += d;
        }
        return amount;
    }

    @Override
    public void readNbt(NbtCompound nbttagcompound) {
        super.readNbt(nbttagcompound);

        for (int i = 0; i < 6; ++i) {
            powerQuery[i] = nbttagcompound.getInt("powerQuery[" + i + "]");
            nextPowerQuery[i] = nbttagcompound.getInt("nextPowerQuery[" + i + "]");
            internalPower[i] = (float) nbttagcompound.getDouble("internalPower[" + i + "]");
            internalNextPower[i] = (float) nbttagcompound.getDouble("internalNextPower[" + i + "]");
        }

    }

    @Override
    public void writeNbt(NbtCompound nbttagcompound) {
        super.writeNbt(nbttagcompound);

        for (int i = 0; i < 6; ++i) {
            nbttagcompound.putInt("powerQuery[" + i + "]", powerQuery[i]);
            nbttagcompound.putInt("nextPowerQuery[" + i + "]", nextPowerQuery[i]);
            nbttagcompound.putDouble("internalPower[" + i + "]", internalPower[i]);
            nbttagcompound.putDouble("internalNextPower[" + i + "]", internalNextPower[i]);
        }
    }

    @Override
    public String toString() {
        return "EnergyPipeTransporter{" +
                "displayPower=" + Arrays.toString(displayPower) +
                ", powerQuery=" + Arrays.toString(powerQuery) +
                ", internalPower=" + Arrays.toString(internalPower) +
                ", maxPower=" + maxPower +
                ", highestPower=" + highestPower +
                '}';
    }
}
