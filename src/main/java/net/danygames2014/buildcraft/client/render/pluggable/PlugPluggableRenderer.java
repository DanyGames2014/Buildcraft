package net.danygames2014.buildcraft.client.render.pluggable;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.RenderBlock;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.client.render.PipePluggableRenderer;
import net.danygames2014.buildcraft.util.MatrixTransformation;
import net.minecraft.client.render.block.BlockRenderManager;
import net.modificationstation.stationapi.api.util.math.Direction;

public class PlugPluggableRenderer implements PipePluggableRenderer {
    public static final PlugPluggableRenderer INSTANCE = new PlugPluggableRenderer();
    private float zFightOffset = 1 / 4096.0F;

    @Override
    public void renderPluggable(BlockRenderManager blockRenderManager, PipeBlockEntity pipe, Direction side, PipePluggable pluggable, int x, int y, int z) {
        RenderBlock renderBlock = Buildcraft.renderBlock;

        float[][] zeroState = new float[3][2];

        // X START - END
        zeroState[0][0] = 0.25F + zFightOffset;
        zeroState[0][1] = 0.75F - zFightOffset;
        // Y START - END
        zeroState[1][0] = 0.125F;
        zeroState[1][1] = 0.251F;
        // Z START - END
        zeroState[2][0] = 0.25F + zFightOffset;
        zeroState[2][1] = 0.75F - zFightOffset;

        renderBlock.setTextureOffset(RenderBlock.DIAMOND_BLOCK.getTexture(0));

        float[][] rotated = MatrixTransformation.deepClone(zeroState);
        MatrixTransformation.transform(rotated, side);

        renderBlock.setBoundingBox(rotated[0][0], rotated[1][0], rotated[2][0], rotated[0][1], rotated[1][1], rotated[2][1]);
        blockRenderManager.renderFlat(renderBlock, x, y, z, 1f, 1f, 1f);

        // X START - END
        zeroState[0][0] = 0.25F + 0.125F / 2 + zFightOffset;
        zeroState[0][1] = 0.75F - 0.125F / 2 + zFightOffset;
        // Y START - END
        zeroState[1][0] = 0.25F;
        zeroState[1][1] = 0.25F + 0.125F;
        // Z START - END
        zeroState[2][0] = 0.25F + 0.125F / 2;
        zeroState[2][1] = 0.75F - 0.125F / 2;

        rotated = MatrixTransformation.deepClone(zeroState);
        MatrixTransformation.transform(rotated, side);

        renderBlock.setBoundingBox(rotated[0][0], rotated[1][0], rotated[2][0], rotated[0][1], rotated[1][1], rotated[2][1]);
        blockRenderManager.renderFlat(renderBlock, x, y, z, 1f, 1f, 1f);
    }
}
