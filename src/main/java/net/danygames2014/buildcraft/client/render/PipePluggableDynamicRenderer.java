package net.danygames2014.buildcraft.client.render;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.pluggable.PipePluggable;
import net.modificationstation.stationapi.api.util.math.Direction;

public interface PipePluggableDynamicRenderer {
    void renderPluggable(PipeBlockEntity pipe, Direction side, PipePluggable pluggable, double x, double y, double z);
}
