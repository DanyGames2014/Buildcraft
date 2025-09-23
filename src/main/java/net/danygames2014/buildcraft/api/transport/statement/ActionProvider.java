package net.danygames2014.buildcraft.api.transport.statement;

import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Collection;

public interface ActionProvider {
    /**
     * Returns the list of actions that are available from the statement container holding the
     * gate.
     */
    Collection<ActionInternal> getInternalActions(StatementContainer container);

    /**
     * Returns the list of actions available to a gate next to the given block.
     */
    Collection<ActionExternal> getExternalActions(Direction side, BlockEntity blockEntity);
}
