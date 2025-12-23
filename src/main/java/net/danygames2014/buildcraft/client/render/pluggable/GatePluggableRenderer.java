package net.danygames2014.buildcraft.client.render.pluggable;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.client.render.PipePluggableDynamicRenderer;
import net.danygames2014.buildcraft.client.render.PipePluggableRenderer;
import net.danygames2014.buildcraft.client.render.block.entity.PipeBlockEntityRenderer;
import net.danygames2014.buildcraft.pluggable.GatePluggable;
import net.minecraft.client.render.block.BlockRenderManager;
import net.modificationstation.stationapi.api.util.math.Direction;

public class GatePluggableRenderer implements PipePluggableRenderer, PipePluggableDynamicRenderer {
    public static final GatePluggableRenderer INSTANCE = new GatePluggableRenderer();
    @Override
    public void renderPluggable(PipeBlockEntity pipe, Direction side, PipePluggable pluggable, double x, double y, double z) {
        PipeBlockEntityRenderer.renderGate(x, y, z, (GatePluggable) pluggable, side);
    }

    @Override
    public void renderPluggable(BlockRenderManager blockRenderManager, PipeBlockEntity pipe, Direction side, PipePluggable pluggable, int x, int y, int z) {
        PipeBlockEntityRenderer.renderGateStatic(blockRenderManager, side, (GatePluggable) pluggable, x, y, z);
    }
}
