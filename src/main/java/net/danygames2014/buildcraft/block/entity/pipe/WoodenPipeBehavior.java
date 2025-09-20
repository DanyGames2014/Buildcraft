package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.energy.IPowerEmitter;
import net.danygames2014.buildcraft.api.energy.IPowerReceptor;
import net.danygames2014.buildcraft.api.energy.PowerHandler;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.fluidhandler.FluidHandlerBlockCapability;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.NotNull;

public class WoodenPipeBehavior extends PipeBehavior {
    @Override
    public PipeConnectionType canConnectToPipe(PipeBlockEntity blockEntity, PipeBlockEntity otherBlockEntity, PipeBehavior otherPipeBehavior) {
        if (super.canConnectToPipe(blockEntity, otherBlockEntity, otherPipeBehavior) != PipeConnectionType.NONE && otherPipeBehavior != Buildcraft.woodenPipeBehavior) {
            return PipeConnectionType.NORMAL;
        }
        return PipeConnectionType.NONE;
    }

    @Override
    public PipeConnectionType getConnectionType(PipeType type, PipeBlockEntity blockEntity, World world, int x, int y, int z, Direction side) {
        switch (type) {
            case ITEM -> {
                ItemHandlerBlockCapability cap = CapabilityHelper.getCapability(world, x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ(), ItemHandlerBlockCapability.class);

                if (cap != null) {
                    if (cap.canConnectItem(side.getOpposite())) {
                        return getPipeConnectionType(blockEntity, side);
                    }
                }
            }

            case FLUID -> {
                FluidHandlerBlockCapability cap = CapabilityHelper.getCapability(world, x, y, z, FluidHandlerBlockCapability.class);

                if (cap != null) {
                    if (cap.canConnectFluid(side.getOpposite())) {
                        return getPipeConnectionType(blockEntity, side);
                    }
                }
            }

            case ENERGY -> {
                BlockEntity other = world.getBlockEntity(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ());
                if (other instanceof IPowerEmitter powerEmitter) {
                    if (powerEmitter.canEmitPowerFrom(side.getOpposite())) {
                        return getPipeConnectionType(blockEntity, side);
                    }
                }

                if (other instanceof IPowerReceptor powerReceptor) {
                    if (powerReceptor.getPowerReceiver(side.getOpposite()) != null) {
                        return getPipeConnectionType(blockEntity, side);
                    }
                }
            }
        }

        return PipeConnectionType.NONE;
    }

    private PipeConnectionType getPipeConnectionType(PipeBlockEntity blockEntity, Direction side) {
        if (blockEntity.connections.containsValue(PipeConnectionType.ALTERNATE) && blockEntity.connections.get(side) != PipeConnectionType.ALTERNATE) {
            return PipeConnectionType.NORMAL;
        } else {
            return PipeConnectionType.ALTERNATE;
        }
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
                            if (cap.canExtractItem(side.getOpposite())){
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
    }
}
