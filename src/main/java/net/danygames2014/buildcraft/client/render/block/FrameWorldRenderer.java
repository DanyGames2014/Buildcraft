package net.danygames2014.buildcraft.client.render.block;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.RenderBlock;
import net.danygames2014.buildcraft.config.Config;
import net.danygames2014.buildcraft.util.Constants;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.world.BlockView;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public class FrameWorldRenderer {
    public void renderFrame(BlockRenderManager blockRenderManager, BlockView blockView, int x, int y, int z, int blockId, Identifier texture){
        RenderBlock renderBlock = Buildcraft.renderBlock;
        renderBlock.setTextureIdentifier(texture);

        int connectivity = 0;
        int connections = 0;

        float[] dim = new float[6];
        resetToCenterDimensions(dim);

        for (int i = 0; i < 6; i++) {
            Direction d = Direction.byId(i);
            if (blockView.getBlockId(x + d.getOffsetX(), y + d.getOffsetY(), z + d.getOffsetZ()) == blockId) {
                connectivity |= 1 << i;
                connections++;
            }
        }

        if (connections != 2) {
            renderTwoWayBlock(blockRenderManager, renderBlock, x, y, z, dim, 0x3f);
        } else {
            renderTwoWayBlock(blockRenderManager, renderBlock, x, y, z, dim, connectivity ^ 0x3f);
        }

        // render the connecting pipe faces
        for (int dir = 0; dir < 6; dir++) {
            int mask = 1 << dir;

            if ((connectivity & mask) == 0) {
                continue; // no connection towards dir
            }

            // center piece offsets
            resetToCenterDimensions(dim);

            // extend block towards dir as it's connected to there
            dim[dir / 2] = dir % 2 == 0 ? 0 : Constants.PIPE_MAX_POS;
            dim[dir / 2 + 3] = dir % 2 == 0 ? Constants.PIPE_MIN_POS : 1;

            // the mask points to all faces perpendicular to dir, i.e. dirs 0+1 -> mask 111100, 1+2 -> 110011, 3+5 -> 001111
            int renderMask = (3 << (dir & 0x6)) ^ 0x3f;
            renderTwoWayBlock(blockRenderManager, renderBlock, x, y, z, dim, renderMask);
        }

        renderBlock.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    private void resetToCenterDimensions(float[] dim) {
        for (int i = 0; i < 3; i++) {
            dim[i] = Constants.PIPE_MIN_POS;
            dim[i + 3] = Constants.PIPE_MAX_POS;
        }
    }

    private void renderTwoWayBlock(BlockRenderManager blockRenderManager, RenderBlock stateHost, int x, int y, int z, float[] dim, int mask) {
        assert mask != 0;

        int c = stateHost.getColor(0);
        float r = ((c & 0xFF0000) >> 16) / 255.0f;
        float g = ((c & 0x00FF00) >> 8) / 255.0f;
        float b = (c & 0x0000FF) / 255.0f;

        stateHost.setRenderMask(mask);
        stateHost.setBoundingBox(dim[2], dim[0], dim[1], dim[5], dim[3], dim[4]);
        blockRenderManager.renderFlat(stateHost, x, y, z, r, g, b);

        if(Config.PIPE_CONFIG.renderInnerPipe){
            stateHost.setRenderMask((mask & 0x15) << 1 | (mask & 0x2a) >> 1); // pairwise swapped mask
            stateHost.setBoundingBox(dim[5], dim[3], dim[4], dim[2], dim[0], dim[1]);
            blockRenderManager.renderFlat(stateHost, x, y, z, r * 0.67f, g * 0.67f, b * 0.67f);
        }

        stateHost.setRenderAllSides();
    }
}
