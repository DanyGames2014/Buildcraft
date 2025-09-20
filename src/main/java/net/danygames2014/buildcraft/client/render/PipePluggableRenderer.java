package net.danygames2014.buildcraft.client.render;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.minecraft.client.render.block.BlockRenderManager;
import net.modificationstation.stationapi.api.util.math.Direction;

public interface PipePluggableRenderer {
    void renderPluggable(BlockRenderManager blockRenderManager, PipeBlockEntity pipe, Direction side, PipePluggable pluggable, int x, int y, int z);
}
