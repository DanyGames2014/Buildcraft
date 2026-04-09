package net.danygames2014.buildcraft.block.entity.pipe.behavior;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.energy.IPowerEmitter;
import net.danygames2014.buildcraft.api.energy.IPowerReceptor;
import net.danygames2014.buildcraft.api.energy.PowerHandler;
import net.danygames2014.buildcraft.block.entity.pipe.*;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.FluidPipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.fluidhandler.FluidHandlerBlockCapability;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.danygames2014.uniwrench.api.WrenchMode;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

public class WoodenPipeBehavior extends PipeBehavior implements IPipeTransportPowerHook {
    @Override
    public PipeConnectionType canConnectToPipe(PipeBlockEntity blockEntity, PipeBlockEntity otherBlockEntity, PipeBehavior otherPipeBehavior, Direction side) {
        if (super.canConnectToPipe(blockEntity, otherBlockEntity, otherPipeBehavior, side) != PipeConnectionType.NONE && otherPipeBehavior != Buildcraft.woodenPipeBehavior) {
            return PipeConnectionType.NORMAL;
        }
        return PipeConnectionType.NONE;
    }

    @Override
    public PipeConnectionType getConnectionType(PipeType type, PipeBlockEntity pipe, World world, int x, int y, int z, Direction side) {
        switch (type) {
            case ITEM -> {
                ItemHandlerBlockCapability cap = CapabilityHelper.getCapability(world, x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ(), ItemHandlerBlockCapability.class);

                if (cap != null) {
                    if (cap.canConnectItem(side.getOpposite())) {
                        if (pipe.connections.containsValue(PipeConnectionType.ALTERNATE) && pipe.connections.get(side) != PipeConnectionType.ALTERNATE) {
                            return PipeConnectionType.NORMAL;
                        } else {
                            if (!cap.canExtractItem(side.getOpposite())) {
                                return PipeConnectionType.NORMAL;
                            }
                            return PipeConnectionType.ALTERNATE;
                        }
                    }
                }
            }

            case FLUID -> {
                FluidHandlerBlockCapability cap = CapabilityHelper.getCapability(world, x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ(), FluidHandlerBlockCapability.class);
                
                if (cap != null) {
                    if (cap.canConnectFluid(side.getOpposite())) {
                        if (pipe.connections.containsValue(PipeConnectionType.ALTERNATE) && pipe.connections.get(side) != PipeConnectionType.ALTERNATE) {
                            return PipeConnectionType.NORMAL;
                        } else {
                            if (!cap.canExtractFluid(side.getOpposite())) {
                                return PipeConnectionType.NORMAL;
                            }
                            return PipeConnectionType.ALTERNATE;
                        }
                    }
                }
            }

            case ENERGY -> {
                BlockEntity other = world.getBlockEntity(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ());
                if (other instanceof IPowerEmitter powerEmitter) {
                    if (powerEmitter.canEmitPowerFrom(side.getOpposite())) {
                        return PipeConnectionType.ALTERNATE;
                    }
                }

                if (other instanceof IPowerReceptor powerReceptor) {
                    if (powerReceptor.getPowerReceiver(side.getOpposite()) != null) {
                        return PipeConnectionType.NORMAL;
                    }
                }
            }
        }

        return PipeConnectionType.NONE;
    }

    @Override
    public void doWork(PoweredPipeBlockEntity blockEntity, PowerHandler powerHandler) {
        if (blockEntity.transporter instanceof ItemPipeTransporter itemTransporter) {
            // Determine if we can extract from somewhere
            if (blockEntity.connections.containsValue(PipeConnectionType.ALTERNATE)) {
                // Find which side we can extract from
                for (var connection : blockEntity.connections.entrySet()) {
                    if (connection.getValue() == PipeConnectionType.ALTERNATE) {
                        Direction side = connection.getKey();

                        // Obtain the capability of the block we are extracting from
                        ItemHandlerBlockCapability cap = CapabilityHelper.getCapability(blockEntity.world, blockEntity.x + side.getOffsetX(), blockEntity.y + side.getOffsetY(), blockEntity.z + side.getOffsetZ(), ItemHandlerBlockCapability.class);
                        if (cap != null) {
                            // Check if we can extract from the block
                            if (cap.canExtractItem(side.getOpposite())) {
                                int energyAvalible = MathHelper.floor(powerHandler.useEnergy(1, 16, false));
                                if (energyAvalible > 0) {
                                    ItemStack extractedStack = cap.extractItem(energyAvalible, side.getOpposite());

                                    if (extractedStack != null) {
                                        powerHandler.useEnergy(extractedStack.count, extractedStack.count, true);
                                        itemTransporter.injectItem(extractedStack, side);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (blockEntity.transporter instanceof FluidPipeTransporter fluidTransporter) {
            // Determine if we can extract from somewhere
            if (!blockEntity.connections.containsValue(PipeConnectionType.ALTERNATE)) {
                return;
            }

            // Find which side we can extract from
            for (var connection : blockEntity.connections.entrySet()) {
                if (connection.getValue() != PipeConnectionType.ALTERNATE) {
                    continue;
                }

                Direction side = connection.getKey();

                // Obtain the capability of the block we are extracting from
                FluidHandlerBlockCapability cap = CapabilityHelper.getCapability(blockEntity.world, blockEntity.x + side.getOffsetX(), blockEntity.y + side.getOffsetY(), blockEntity.z + side.getOffsetZ(), FluidHandlerBlockCapability.class);
                if (cap == null) {
                    return;
                }

                // Check if we can extract from the block
                if (cap.canExtractFluid(side.getOpposite())) {
                    if (ForgeDirection.fromDirection(side) == null) {
                        continue;
                    }
                    
                    int avalibleCapacity = fluidTransporter.getSideRemainingCapacity(ForgeDirection.fromDirection(side));
                    if (avalibleCapacity <= 0) {
                        return;
                    }

                    int energyAvalible = MathHelper.floor(powerHandler.useEnergy(1, Math.min(avalibleCapacity, 5), false));
                    if (energyAvalible <= 0) {
                        return;
                    }

                    FluidStack extractedStack = cap.extractFluid(energyAvalible * 4, side.getOpposite());

                    if (extractedStack != null) {
                        powerHandler.useEnergy(extractedStack.amount, extractedStack.amount, true);
                        fluidTransporter.injectFluid(extractedStack, ForgeDirection.fromDirection(side));
                    }
                }
            }
        }
    }

    @Override
    public float receiveEnergy(PipeBlockEntity pipe, ForgeDirection from, float val) {
        return -1;
    }

    @Override
    public float requestEnergy(PipeBlockEntity pipe, ForgeDirection from, float amount) {
        if (from.getDirection() != null && pipe.getBlockEntity(from.getDirection()) instanceof PipeBlockEntity) {
            return amount;
        }
        
        return 0;
    }

    @Override
    public boolean wrenchRightClick(PipeBlockEntity blockEntity, ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side, WrenchMode wrenchMode) {
        Object2ObjectOpenHashMap<Direction, PipeConnectionType> connections = blockEntity.connections;

        // Find the current normal side
        Direction currentAlternate = null;
        for (var entry : connections.object2ObjectEntrySet()) {
            if (entry.getValue() == PipeConnectionType.ALTERNATE) {
                currentAlternate = entry.getKey();
                break;
            }
        }

        // If there is none, return
        if (currentAlternate == null) {
            return true;
        }

        // Find a next normal side
        Direction[] dirs = Direction.values();
        Direction nextSide = null;

        int startOffset = currentAlternate.ordinal() + 1;

        for (int i = 0; i < dirs.length; i++) {
            Direction candidate = dirs[(startOffset + i) % dirs.length];

            if (connections.get(candidate) == PipeConnectionType.NORMAL) {
                nextSide = candidate;
                break;
            }
        }

        // Switch to the new side
        if (nextSide != null) {
            connections.put(currentAlternate, PipeConnectionType.NORMAL);
            connections.put(nextSide, PipeConnectionType.ALTERNATE);

            blockEntity.neighborUpdate();
            world.blockUpdateEvent(x, y, z);
        }

        return true;
    }
}
