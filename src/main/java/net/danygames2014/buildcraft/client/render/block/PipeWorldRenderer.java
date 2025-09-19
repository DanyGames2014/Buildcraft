package net.danygames2014.buildcraft.client.render.block;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.RenderBlock;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.client.render.PipeRenderState;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.world.BlockView;
import net.modificationstation.stationapi.api.util.math.Direction;


public class PipeWorldRenderer {
    public static final float PIPE_MIN_POS = 0.25F;
    public static final float PIPE_MAX_POS = 0.75F;
    public void renderPipe(BlockRenderManager blockRenderManager, BlockView blockView, PipeBlockEntity pipeBlockEntity, int x, int y, int z){
        PipeRenderState state = pipeBlockEntity.renderState;
        RenderBlock renderBlock = Buildcraft.renderBlock;

        int connectivity = state.pipeConnectionMatrix.getMask();
        float[] dim = new float[6];

        if(connectivity != 0x3F){
            resetToCenterDimensions(dim);

            renderBlock.setTextureOffset(state.textureMatrix.getTextureIndex(Direction.UP));

            renderTwoWayBlock(blockRenderManager, renderBlock, x, y, z, dim, connectivity ^ 0x3F);
        }

        for(int dir = 0; dir < 6; dir++){
            int mask = 1 << dir;

            if((connectivity & mask) == 0){
                continue;
            }

            resetToCenterDimensions(dim);

            dim[dir / 2] = dir % 2 == 0 ? 0 : PIPE_MAX_POS;
            dim[dir / 2 + 3] = dir % 2 == 0 ? PIPE_MIN_POS : 1;

            int renderMask = (3 << (dir & 0x6)) ^ 0x3f;

            renderBlock.setTextureOffset(state.textureMatrix.getTextureIndex(Direction.byId(dir)));

            renderTwoWayBlock(blockRenderManager, renderBlock, x, y, z, dim, renderMask);

            if(Minecraft.INSTANCE.options.fancyGraphics){
                Direction side = Direction.byId(dir);
                int px = x + side.getOffsetX();
                int py = y + side.getOffsetY();
                int pz = z + side.getOffsetZ();
                Block block = Block.BLOCKS[blockView.getBlockId(px, py, pz)];
                if(!(block instanceof PipeBlock) && !block.isOpaque()){
                    double[] blockBB;
                    block.updateBoundingBox(blockView, px, py, pz);

                    blockBB = new double[]{
                            block.minX,
                            block.minX,
                            block.minZ,
                            block.maxY,
                            block.maxX,
                            block.maxZ
                    };

                    if((dir % 2 == 1 && blockBB[dir / 2] != 0) || (dir % 2 == 0 && blockBB[dir / 2 + 3] != 1)){
                        resetToCenterDimensions(dim);

                        if (dir % 2 == 1) {
                            dim[dir / 2] = 0;
                            dim[dir / 2 + 3] = (float) blockBB[dir / 2];
                        } else {
                            dim[dir / 2] = (float) blockBB[dir / 2 + 3];
                            dim[dir / 2 + 3] = 1;
                        }

                        renderTwoWayBlock(blockRenderManager, renderBlock, px, py, pz, dim, renderMask);
                    }
                }
            }
        }
        renderBlock.setColor(0xFFFFFF);
        renderBlock.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    private void resetToCenterDimensions(float[] dim){
        for (int i = 0; i < 3; i++) {
            dim[i] = PIPE_MIN_POS;
            dim[i + 3] = PIPE_MAX_POS;
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

        stateHost.setRenderMask((mask & 0x15) << 1 | (mask & 0x2a) >> 1); // pairwise swapped mask
        stateHost.setBoundingBox(dim[5], dim[3], dim[4], dim[2], dim[0], dim[1]);
        blockRenderManager.renderFlat(stateHost, x, y, z, r * 0.67f, g * 0.67f, b * 0.67f);

        stateHost.setRenderAllSides();
    }
}
