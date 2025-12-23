package net.danygames2014.buildcraft.statements;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.danygames2014.buildcraft.api.transport.statement.TriggerExternal;
import net.danygames2014.buildcraft.api.transport.statement.TriggerInternal;
import net.danygames2014.buildcraft.api.transport.statement.TriggerProvider;
import net.danygames2014.buildcraft.api.transport.statement.container.RedstoneStatementContainer;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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

        return res;
    }
}
