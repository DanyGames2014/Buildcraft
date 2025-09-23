package net.danygames2014.buildcraft.api.transport.statement;

import net.modificationstation.stationapi.api.util.math.Direction;

public interface TriggerExternalOverride {
    enum Result {
        TRUE, FALSE, IGNORE
    }

    Result override(Direction side, StatementContainer source, StatementParameter[] parameters);
}
