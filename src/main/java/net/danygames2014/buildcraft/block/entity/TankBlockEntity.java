package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.api.core.Serializable;
import net.danygames2014.buildcraft.api.core.SynchedBlockEntity;
import net.danygames2014.buildcraft.client.render.TankRenderState;
import net.danygames2014.buildcraft.packet.clientbound.BlockEntityUpdatePacket;
import net.danygames2014.buildcraft.registry.StateRegistry;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.danygames2014.nyalib.fluid.block.FluidHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class TankBlockEntity extends BlockEntity implements FluidHandler, SynchedBlockEntity, DelayedBlockEntityUpdate {
    public static final int CAPACITY = 10000;
    public FluidStack fluid;
    public boolean hasUpdate = true;
    public boolean sendClientUpdate = false;

    public final TankRenderState tankRenderState = new TankRenderState();

    @Override
    public void tick() {
        super.tick();

        if(fluid != null){
            moveLiquidBelow();
        }
        if(hasUpdate){
            hasUpdate = false;
            tankRenderState.fluid = fluid;
            sendClientUpdate = true;
        }
        if(sendClientUpdate){
            sendClientUpdate = false;
            if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER){
                Packet updatePacket = getBlockEntityUpdatePacket();
                for(Object o : world.players){
                    PlayerEntity player = (PlayerEntity) o;
                    if(player.getDistance(x, y, z) < 40){
                        PacketHelper.sendTo(player, updatePacket);
                    }
                }
            }
        }
    }

    public TankBlockEntity getBottomTank(@Nullable FluidStack filter){
        TankBlockEntity lastTank = this;
        while (true) {
            TankBlockEntity below = getTankBelow(lastTank);
            if (below != null) {
                if (filter != null) {
                    if (below.fluid == null || !below.fluid.isFluidEqual(filter)) {
                        break;
                    }
                }
                if (lastTank.fluid != null && below.fluid != null && !below.fluid.isFluidEqual(lastTank.fluid)) {
                    break;
                }
                lastTank = below;
            } else {
                break;
            }
        }
        return lastTank;
    }

    public TankBlockEntity getTopTank(@Nullable FluidStack filter){
        TankBlockEntity lastTank = this;
        while (true) {
            TankBlockEntity above = getTankAbove(lastTank);
            if (above != null) {
                if (filter != null) {
                    if (above.fluid == null || !above.fluid.isFluidEqual(filter)) {
                        break;
                    }
                }
                if (lastTank.fluid != null && above.fluid != null && !above.fluid.isFluidEqual(lastTank.fluid)) {
                    break;
                }
                lastTank = above;
            } else {
                break;
            }
        }
        return lastTank;
    }

    public static TankBlockEntity getTankBelow(TankBlockEntity tank){
        BlockEntity below = tank.world.getBlockEntity(tank.x, tank.y - 1, tank.z);
        if(below instanceof TankBlockEntity belowTank){
            if(belowTank.fluid != null && tank.fluid != null && !belowTank.fluid.isFluidEqual(tank.fluid)){
                return null;
            }
            return belowTank;
        }
        else {
            return null;
        }
    }

    public static TankBlockEntity getTankAbove(TankBlockEntity tank){
        BlockEntity above = tank.world.getBlockEntity(tank.x, tank.y + 1, tank.z);
        if(above instanceof TankBlockEntity aboveTank){
            if(aboveTank.fluid != null && tank.fluid != null && !aboveTank.fluid.isFluidEqual(tank.fluid)){
                return null;
            }
            return aboveTank;
        }
        else {
            return null;
        }
    }

    public void moveLiquidBelow(){
        TankBlockEntity below = getTankBelow(this);
        if(below == null){
            return;
        }
        int used = getUsed(fluid, below.fluid);
        if(used > 0){
            if(below.fluid == null){
                below.fluid = new FluidStack(fluid.fluid, used);
            } else {
                below.fluid.amount += used;
            }
            fluid.amount -= used;
            if(fluid.amount <= 0){
                fluid = null;
            }

            hasUpdate = true;
            below.hasUpdate = true;
        }
//        fluid = below.insertFluid(fluid, Direction.UP);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if(nbt.contains("fluid")){
            NbtCompound fluidCompound = nbt.getCompound("fluid");
            fluid = new FluidStack(fluidCompound);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if(fluid == null){
            return;
        }
        NbtCompound fluidCompound = new NbtCompound();
        fluid.writeNbt(fluidCompound);
        nbt.put("fluid", fluidCompound);
    }

    @Override
    public boolean canExtractFluid(@Nullable Direction direction) {
        return true;
    }

    @Override
    public FluidStack extractFluid(int slot, int amount, @Nullable Direction direction) {
        return extractFluid(0, amount, true);
    }

    @Override
    public boolean canInsertFluid(@Nullable Direction direction) {
        return true;
    }

    @Override
    public FluidStack insertFluid(FluidStack stack, int slot, @Nullable Direction direction) {
        return this.insertFluid(stack, direction);
    }

    public FluidStack insertFluid(int tankIndex, FluidStack stack, boolean doInsert){
        if(tankIndex != 0 || stack == null){
            return stack;
        }
        FluidStack remainingToInsert = stack.copy();
        TankBlockEntity tankToFill = getBottomTank(stack);

        FluidStack fluid = tankToFill.fluid;
        if(fluid != null && fluid.amount > 0 && !fluid.isFluidEqual(stack)){
            return stack;
        }

        while (tankToFill != null && remainingToInsert.amount > 0) {
            int used = getUsed(remainingToInsert, tankToFill.fluid);
            if(used > 0) {
                if(doInsert) {
                    if(tankToFill.fluid == null){
                        tankToFill.fluid = new FluidStack(stack.fluid, used);
                    } else {
                        tankToFill.fluid.amount += used;
                    }
                    tankToFill.hasUpdate = true;
                }
                remainingToInsert.amount -= used;
            }
            tankToFill = getTankAbove(tankToFill);
        }
        return remainingToInsert;
    }

    public FluidStack extractFluid(int tankIndex, int maxDrain, boolean doDrain) {
        if (tankIndex != 0 || maxDrain <= 0) {
            return null;
        }

        TankBlockEntity currentTank = getTopTank(fluid);
        while (currentTank != null && (currentTank.fluid == null || currentTank.fluid.amount <= 0)) {
            currentTank = getTankBelow(currentTank);
        }

        if(currentTank == null){
            return null;
        }

        FluidStack fluid = currentTank.fluid.copy();
        int totalAvailable = 0;

        TankBlockEntity tempTank = currentTank;
        while (tempTank != null) {
            if (tempTank.fluid != null && tempTank.fluid.isFluidEqual(fluid)) {
                totalAvailable += tempTank.fluid.amount;
            }
            tempTank = getTankBelow(tempTank);
        }

        int toDrain = Math.min(totalAvailable, maxDrain);
        fluid.amount = toDrain;

        if (doDrain && toDrain > 0) {
            int remainingToDrain = toDrain;
            while (currentTank != null && remainingToDrain > 0) {
                if (currentTank.fluid != null && currentTank.fluid.isFluidEqual(fluid)) {
                    int drained = Math.min(currentTank.fluid.amount, remainingToDrain);

                    currentTank.fluid.amount -= drained;
                    remainingToDrain -= drained;

                    if (currentTank.fluid.amount <= 0) {
                        currentTank.fluid = null;
                    }
                    currentTank.hasUpdate = true;
                }
                currentTank = getTankBelow(currentTank);
            }
        }

        return fluid;
    }

    private int getUsed(FluidStack input, FluidStack existing){
        if(existing == null) return Math.min(TankBlockEntity.CAPACITY, input.amount);
        if(!existing.isFluidEqual(input)) return 0;

        int spaceAvailable = TankBlockEntity.CAPACITY - existing.amount;
        return Math.min(spaceAvailable, input.amount);
    }

    @Override
    public FluidStack insertFluid(FluidStack stack, @Nullable Direction direction) {
        return insertFluid(0, stack, true);
    }

    @Override
    public FluidStack getFluid(int slot, @Nullable Direction direction) {
        TankBlockEntity tank = getBottomTank(fluid);
        if(tank.fluid == null){
            return null;
        }

        FluidStack total = new FluidStack(tank.fluid.fluid, tank.fluid.amount);
        while (tank != null){
            tank = getTankAbove(tank);
            if(tank != null && tank.fluid != null){
                total.amount += tank.fluid.amount;
            }
        }
        return total;
    }

    @Override
    public boolean setFluid(int slot, FluidStack stack, @Nullable Direction direction) {
        fluid = stack;
        return true;
    }

    @Override
    public int getFluidSlots(@Nullable Direction direction) {
        return 1;
    }

    @Override
    public int getFluidCapacity(int slot, @Nullable Direction direction) {
        int totalCapacity = 0;
        TankBlockEntity current = getBottomTank(fluid);

        FluidStack existingFluid = current != null ? current.fluid : null;

        while (current != null) {
            if (existingFluid != null && current.fluid != null && !current.fluid.isFluidEqual(existingFluid)) {
                break;
            }

            totalCapacity += CAPACITY;
            current = getTankAbove(current);
        }
        return totalCapacity;
    }

    @Override
    public FluidStack[] getFluids(@Nullable Direction direction) {
        return new FluidStack[0];
    }

    @Override
    public boolean canConnectFluid(Direction direction) {
        return true;
    }

    @Override
    public Serializable getStateInstance(byte stateId) {
        Class<? extends Serializable> stateClass = StateRegistry.getClass(stateId);
        if(stateClass == TankRenderState.class){
            return tankRenderState;
        };
        throw new RuntimeException("Unknown state requested: " + stateId + " this is a bug!");
    }

    @Override
    public Packet createUpdatePacket() {
        return getBlockEntityUpdatePacket();
    }



    public BlockEntityUpdatePacket getBlockEntityUpdatePacket(){
        BlockEntityUpdatePacket packet = new BlockEntityUpdatePacket(x, y, z);
        packet.addStateForSerialization(tankRenderState);
        return packet;
    }

    @Override
    public void afterStateUpdated(byte stateId) {
        Class<? extends Serializable> stateClass = StateRegistry.getClass(stateId);
        if(stateClass == TankRenderState.class){
            if(!world.isRemote) return;
            if(tankRenderState.fluid != null){
                fluid = tankRenderState.fluid.copy();
            } else {
                fluid = null;
            }
        };
    }

    @Environment(EnvType.SERVER)
    @Override
    public void onBlockEntityUpdatePacket(ServerPlayerEntity player) {
        PacketHelper.sendTo(player, getBlockEntityUpdatePacket());
    }
}
