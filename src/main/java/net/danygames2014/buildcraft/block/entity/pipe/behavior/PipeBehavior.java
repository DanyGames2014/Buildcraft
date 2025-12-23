package net.danygames2014.buildcraft.block.entity.pipe.behavior;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.api.energy.IPowerEmitter;
import net.danygames2014.buildcraft.api.energy.IPowerReceptor;
import net.danygames2014.buildcraft.api.energy.PowerHandler;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeConnectionType;
import net.danygames2014.buildcraft.block.entity.pipe.PipeType;
import net.danygames2014.buildcraft.block.entity.pipe.PoweredPipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter.HandOffResult;
import net.danygames2014.buildcraft.config.Config;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.fluidhandler.FluidHandlerBlockCapability;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.danygames2014.uniwrench.api.WrenchMode;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

/**
 * This class governs the behavior of a pipe
 * T
 */
public class PipeBehavior {
    public PipeBehavior() {
    }

    /**
     * @param blockEntity       The block entity of the pipe this is called on
     * @param otherBlockEntity  The block entity of the pipe we want to connect to
     * @param otherPipeBehavior The behavior of the pipe we want to connect to
     * @param side              The side on which the other pipe is
     * @return If we can connect to the other pipe
     */
    public PipeConnectionType canConnectToPipe(PipeBlockEntity blockEntity, PipeBlockEntity otherBlockEntity, PipeBehavior otherPipeBehavior, Direction side) {
        if (otherBlockEntity.transporter.getType() == blockEntity.transporter.getType()) {
            return PipeConnectionType.NORMAL;
        }

        return PipeConnectionType.NONE;
    }

    /**
     * Get the connection type for a side/block
     *
     * @param type        The type of this pipe
     * @param blockEntity The block entity of this pipe
     * @param world       The world the pipe is in
     * @param x           The x position of the pipe
     * @param y           The y position of the pipe
     * @param z           The z position of the pipe
     * @param side        The side where the target block is located
     * @return The connection type to the target block
     */
    public PipeConnectionType getConnectionType(PipeType type, PipeBlockEntity blockEntity, World world, int x, int y, int z, Direction side) {
        switch (type) {
            case ITEM -> {
                ItemHandlerBlockCapability cap = CapabilityHelper.getCapability(world, x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ(), ItemHandlerBlockCapability.class);

                if (cap != null) {
                    return cap.canConnectItem(side.getOpposite()) ? PipeConnectionType.NORMAL : PipeConnectionType.NONE;
                }
            }

            case FLUID -> {
                FluidHandlerBlockCapability cap = CapabilityHelper.getCapability(world, x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ(), FluidHandlerBlockCapability.class);

                if (cap != null) {
                    return cap.canConnectFluid(side.getOpposite()) ? PipeConnectionType.NORMAL : PipeConnectionType.NONE;
                }
            }

            case ENERGY -> {
                BlockEntity other = world.getBlockEntity(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ());
                if (other instanceof IPowerEmitter powerEmitter) {
                    return powerEmitter.canEmitPowerFrom(side.getOpposite()) ? PipeConnectionType.NORMAL : PipeConnectionType.NONE;
                }

                if (other instanceof IPowerReceptor powerReceptor) {
                    return powerReceptor.getPowerReceiver(side.getOpposite()) != null ? PipeConnectionType.NORMAL : PipeConnectionType.NONE;
                }
            }
        }

        return PipeConnectionType.NONE;
    }

    /**
     * Pick a direction in which the item will continue travelling
     *
     * @param blockEntity           The block entity of the blockEntity this is called on
     * @param validOutputDirections The directions in which the item can travel
     * @param item                  The item that we are picking a direction for
     * @return The direction in which the item will travel, or null if there is no valid direction
     */
    public Direction routeItem(PipeBlockEntity blockEntity, ObjectArrayList<Direction> validOutputDirections, TravellingItemEntity item) {
        ObjectArrayList<Direction> directions = new ObjectArrayList<>(validOutputDirections);
        directions.remove(item.input);

        if (directions.isEmpty()) {
            return null;
        }

        return directions.get(blockEntity.random.nextInt(directions.size()));
    }

    public boolean isValidOutputDirection(PipeBlockEntity blockEntity, Direction side, PipeConnectionType connectionType) {
        return connectionType != PipeConnectionType.NONE;
    }
    
    public HandOffResult getFailedInsertResult(PipeBlockEntity blockEntity, ItemPipeTransporter transporter, TravellingItemEntity item) {
        return Config.PIPE_CONFIG.failedInsertResult;
    }

    public double modifyItemSpeed(TravellingItemEntity item) {
        return TravellingItemEntity.DEFAULT_SPEED;
    }
    
    public boolean isFluidInputOpen(PipeBlockEntity blockEntity, Direction side, PipeConnectionType connectionType) {
        return true;
    }
    
    public boolean isFluidOutputOpen(PipeBlockEntity blockEntity, Direction side, PipeConnectionType connectionType) {
        return true;
    }

    /**
     * <p> **NOTE: This is only called on pipes which use the {@link PoweredPipeBlockEntity}**
     * <p> This is called when the pipes energy exceeds the activation energy threshold
     *
     * @param blockEntity  The block entity of the pipe this is called on
     * @param powerHandler The power handler of the pipe
     */
    public void doWork(PoweredPipeBlockEntity blockEntity, PowerHandler powerHandler) {

    }

    public boolean wrenchRightClick(PipeBlockEntity blockEntity, ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side, WrenchMode wrenchMode) {
        return false;            
    }
}
