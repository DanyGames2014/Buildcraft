package net.danygames2014.buildcraft.api.transport.statement;

import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Collection;

public interface TriggerProvider {
    /**
     * Returns the list of triggers that are available from the object holding the gate.
     */
    Collection<TriggerInternal> getInternalTriggers(StatementContainer container);
    /**
     * Returns the list of triggers available to a gate next to the given block.
     */
    Collection<TriggerExternal> getExternalTriggers(Direction side, BlockEntity blockEntity);

}
