package net.danygames2014.buildcraft.block.entity.pipe.statement;

import net.danygames2014.buildcraft.api.blockentity.HasWork;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.TriggerExternal;
import net.danygames2014.buildcraft.api.transport.statement.TriggerInternal;
import net.danygames2014.buildcraft.api.transport.statement.TriggerProvider;
import net.danygames2014.buildcraft.api.transport.statement.container.RedstoneStatementContainer;
import net.danygames2014.buildcraft.block.entity.BaseEngineBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.init.StatementListener;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.energyhandler.EnergyStorageBlockCapability;
import net.danygames2014.nyalib.capability.block.fluidhandler.FluidHandlerBlockCapability;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Collection;
import java.util.LinkedList;

public class DefaultTriggerProvider implements TriggerProvider {
    @Override
    public Collection<TriggerInternal> getInternalTriggers(StatementContainer container) {
        LinkedList<TriggerInternal> res = new LinkedList<>();

        if (container instanceof RedstoneStatementContainer) {
            res.add(StatementListener.triggerRedstoneActive);
            res.add(StatementListener.triggerRedstoneInactive);
        }

        return res;
    }

    @Override
    public Collection<TriggerExternal> getExternalTriggers(Direction side, BlockEntity blockEntity) {
        LinkedList<TriggerExternal> res = new LinkedList<>();

        // maybe have this check for the side
        if(blockEntity != null && !(blockEntity instanceof PipeBlockEntity) && CapabilityHelper.getCapability(blockEntity.world, blockEntity.x, blockEntity.y, blockEntity.z, ItemHandlerBlockCapability.class) != null){
            res.add(StatementListener.triggerEmptyInventory);
            res.add(StatementListener.triggerContainsInventory);
            res.add(StatementListener.triggerSpaceInventory);
            res.add(StatementListener.triggerFullInventory);
            res.add(StatementListener.triggerInventoryBelow25);
            res.add(StatementListener.triggerInventoryBelow50);
            res.add(StatementListener.triggerInventoryBelow75);
        }

        if(blockEntity != null && CapabilityHelper.getCapability(blockEntity.world, blockEntity.x, blockEntity.y, blockEntity.z, FluidHandlerBlockCapability.class) != null){
            res.add(StatementListener.triggerEmptyFluid);
            res.add(StatementListener.triggerContainsFluid);
            res.add(StatementListener.triggerSpaceFluid);
            res.add(StatementListener.triggerFullFluid);
            res.add(StatementListener.triggerFluidContainerBelow25);
            res.add(StatementListener.triggerFluidContainerBelow50);
            res.add(StatementListener.triggerFluidContainerBelow75);
        }

        if (blockEntity instanceof HasWork) {
            res.add(StatementListener.triggerMachineActive);
            res.add(StatementListener.triggerMachineInactive);
        }

        if(blockEntity != null && CapabilityHelper.getCapability(blockEntity.world, blockEntity.x, blockEntity.y, blockEntity.z, EnergyStorageBlockCapability.class) != null){
            res.add(StatementListener.triggerEnergyHigh);
            res.add(StatementListener.triggerEnergyLow);
        }

        if(blockEntity instanceof BaseEngineBlockEntity){
            res.add(StatementListener.triggerBlueEngineHeat);
            res.add(StatementListener.triggerGreenEngineHeat);
            res.add(StatementListener.triggerYellowEngineHeat);
            res.add(StatementListener.triggerRedEngineHeat);
            res.add(StatementListener.triggerEngineOverheat);
        }

        return res;
    }
}
