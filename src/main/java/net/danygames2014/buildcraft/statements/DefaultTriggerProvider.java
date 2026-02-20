package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.blockentity.HasWork;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.TriggerExternal;
import net.danygames2014.buildcraft.api.transport.statement.TriggerInternal;
import net.danygames2014.buildcraft.api.transport.statement.TriggerProvider;
import net.danygames2014.buildcraft.api.transport.statement.container.RedstoneStatementContainer;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.nyalib.capability.CapabilityHelper;
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
            res.add(Buildcraft.triggerRedstoneActive);
            res.add(Buildcraft.triggerRedstoneInactive);
        }

        return res;
    }

    @Override
    public Collection<TriggerExternal> getExternalTriggers(Direction side, BlockEntity blockEntity) {
        LinkedList<TriggerExternal> res = new LinkedList<>();

        // maybe have this check for the side
        if(blockEntity != null && !(blockEntity instanceof PipeBlockEntity) && CapabilityHelper.getCapability(blockEntity.world, blockEntity.x, blockEntity.y, blockEntity.z, ItemHandlerBlockCapability.class) != null){
            res.add(Buildcraft.triggerEmptyInventory);
            res.add(Buildcraft.triggerContainsInventory);
            res.add(Buildcraft.triggerSpaceInventory);
            res.add(Buildcraft.triggerFullInventory);
            res.add(Buildcraft.triggerInventoryBelow25);
            res.add(Buildcraft.triggerInventoryBelow50);
            res.add(Buildcraft.triggerInventoryBelow75);
        }

        if(blockEntity != null && CapabilityHelper.getCapability(blockEntity.world, blockEntity.x, blockEntity.y, blockEntity.z, FluidHandlerBlockCapability.class) != null){
            res.add(Buildcraft.triggerEmptyFluid);
            res.add(Buildcraft.triggerContainsFluid);
            res.add(Buildcraft.triggerSpaceFluid);
            res.add(Buildcraft.triggerFullFluid);
            res.add(Buildcraft.triggerFluidContainerBelow25);
            res.add(Buildcraft.triggerFluidContainerBelow50);
            res.add(Buildcraft.triggerFluidContainerBelow75);
        }

        if (blockEntity instanceof HasWork) {
            res.add(Buildcraft.triggerMachineActive);
            res.add(Buildcraft.triggerMachineInactive);
        }

        return res;
    }
}
