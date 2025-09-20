package net.danygames2014.buildcraft.client.render.block;

import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.util.ConnectionMatrix;
import net.modificationstation.stationapi.api.util.math.Direction;

public class PipePluggableState {
    private PipePluggable[] pluggables = new PipePluggable[Direction.values().length];
    private final ConnectionMatrix pluggableMatrix = new ConnectionMatrix();

    public PipePluggable[] getPluggables() {
        return pluggables;
    }

    public void setPluggables(PipePluggable[] pluggables) {
        this.pluggables = pluggables;
        this.pluggableMatrix.clean();
        for (Direction direction : Direction.values()) {
            this.pluggableMatrix.setConnected(direction, pluggables[direction.ordinal()] != null);
        }
    }
}
