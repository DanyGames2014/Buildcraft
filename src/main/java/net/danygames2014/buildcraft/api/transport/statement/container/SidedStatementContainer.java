package net.danygames2014.buildcraft.api.transport.statement.container;

import net.danygames2014.buildcraft.api.transport.statement.StatementContainer;
import net.modificationstation.stationapi.api.util.math.Direction;

/**
 * Created by asie on 3/14/15.
 */
public interface SidedStatementContainer extends StatementContainer {
    Direction getSide();
}
