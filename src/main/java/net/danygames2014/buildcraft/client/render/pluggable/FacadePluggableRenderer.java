package net.danygames2014.buildcraft.client.render.pluggable;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.RenderBlock;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.client.render.PipePluggableRenderer;
import net.danygames2014.buildcraft.client.render.block.PipeWorldRenderer;
import net.danygames2014.buildcraft.init.TextureListener;
import net.danygames2014.buildcraft.pluggable.FacadePluggable;
import net.danygames2014.buildcraft.util.MatrixTransformation;
import net.danygames2014.buildcraft.util.TextureUtil;
import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.world.BlockView;
import net.modificationstation.stationapi.api.util.math.Direction;

public class FacadePluggableRenderer implements PipePluggableRenderer {

    private static final float zFightOffset = 1F / 4096F;
    private static final float[][] zeroStateFacade = new float[3][2];
    private static final float[][] zeroStateSupport = new float[3][2];
    private static final float[] xOffsets = new float[6];
    private static final float[] yOffsets = new float[6];
    private static final float[] zOffsets = new float[6];

    private static BlockView blockView;

    public static FacadePluggableRenderer INSTANCE = new FacadePluggableRenderer();

    static {

        // X START - END
        zeroStateFacade[0][0] = 0.0F;
        zeroStateFacade[0][1] = 1.0F;
        // Y START - END
        zeroStateFacade[1][0] = 0.0F;
        zeroStateFacade[1][1] = FacadePluggable.FACADE_THICKNESS;
        // Z START - END
        zeroStateFacade[2][0] = 0.0F;
        zeroStateFacade[2][1] = 1.0F;

        // X START - END
        zeroStateSupport[0][0] = PipeWorldRenderer.PIPE_MIN_POS;
        zeroStateSupport[0][1] = PipeWorldRenderer.PIPE_MAX_POS;
        // Y START - END
        zeroStateSupport[1][0] = FacadePluggable.FACADE_THICKNESS;
        zeroStateSupport[1][1] = PipeWorldRenderer.PIPE_MIN_POS;
        // Z START - END
        zeroStateSupport[2][0] = PipeWorldRenderer.PIPE_MIN_POS;
        zeroStateSupport[2][1] = PipeWorldRenderer.PIPE_MAX_POS;

        xOffsets[0] = zFightOffset;
        xOffsets[1] = zFightOffset;
        xOffsets[2] = 0;
        xOffsets[3] = 0;
        xOffsets[4] = 0;
        xOffsets[5] = 0;

        yOffsets[0] = 0;
        yOffsets[1] = 0;
        yOffsets[2] = zFightOffset;
        yOffsets[3] = zFightOffset;
        yOffsets[4] = 0;
        yOffsets[5] = 0;

        zOffsets[0] = zFightOffset;
        zOffsets[1] = zFightOffset;
        zOffsets[2] = 0;
        zOffsets[3] = 0;
        zOffsets[4] = 0;
        zOffsets[5] = 0;
    }

    private static void setRenderBounds(RenderBlock renderBlock, float[][] rotated, Direction side) {
        renderBlock.setBoundingBox(
                rotated[0][0] + xOffsets[side.ordinal()],
                rotated[1][0] + yOffsets[side.ordinal()],
                rotated[2][0] + zOffsets[side.ordinal()],
                rotated[0][1] - xOffsets[side.ordinal()],
                rotated[1][1] - yOffsets[side.ordinal()],
                rotated[2][1] - zOffsets[side.ordinal()]);
    }

    public static void setBlockView(BlockView blockView){
        FacadePluggableRenderer.blockView = blockView;
    }

    @Override
    public void renderPluggable(BlockRenderManager blockRenderManager, PipeBlockEntity pipe, Direction side, PipePluggable pluggable, int x, int y, int z) {
        // TODO: when mutli renderpass is implemented, change this to be the actual renderpass
        int renderPass = 0;
        FacadePluggable facadePluggable = (FacadePluggable)pluggable;

        Block block = facadePluggable.getBlock();

        RenderBlock renderBlock = Buildcraft.renderBlock;

        renderBlock.setRenderMask(0);
        if(block != null){
            if(block.getRenderLayer() == renderPass){
                int meta = facadePluggable.getMeta();
                for(Direction dir : Direction.values()){
                    renderBlock.setTextureIdentifierForSide(dir, TextureUtil.getTerrainIdentifierFromOffset(block.getTexture(dir.ordinal(), meta)));
                    if(dir == side || dir == side.getOpposite()){
                        renderBlock.setRenderSide(dir, true);
                    } else {
                        if(!(pipe.getPipePluggable(dir) instanceof FacadePluggable)){
                            renderBlock.setRenderSide(dir, true);
                        } else {
                            FacadePluggable facadePluggable2 = (FacadePluggable) pipe.getPipePluggable(dir);
                            renderBlock.setRenderSide(dir, facadePluggable2.getBlock() == null);
                        }
                    }
                }

                int color = block.getColor(meta);
                float r = ((color & 0xFF0000) >> 16) / 255.0f;
                float g = ((color & 0x00FF00) >> 8) / 255.0f;
                float b = (color & 0x0000FF) / 255.0f;

                if(facadePluggable.isHollow()){
                    renderBlock.applyUVFix = true;
                    float[][] rotated = MatrixTransformation.deepClone(zeroStateFacade);
                    rotated[0][0] = PipeWorldRenderer.PIPE_MIN_POS - zFightOffset * 4;
                    rotated[0][1] = PipeWorldRenderer.PIPE_MAX_POS + zFightOffset * 4;
                    rotated[2][0] = 0.0F;
                    rotated[2][1] = PipeWorldRenderer.PIPE_MIN_POS - zFightOffset * 2;
                    MatrixTransformation.transform(rotated, side);
                    setRenderBounds(renderBlock, rotated, side);
                    blockRenderManager.renderFlat(renderBlock, x, y, z, r, g, b);

                    rotated = MatrixTransformation.deepClone(zeroStateFacade);
                    rotated[0][0] = PipeWorldRenderer.PIPE_MIN_POS - zFightOffset * 4;
                    rotated[0][1] = PipeWorldRenderer.PIPE_MAX_POS + zFightOffset * 4;
                    rotated[2][0] = PipeWorldRenderer.PIPE_MAX_POS + zFightOffset * 2;
                    MatrixTransformation.transform(rotated, side);
                    setRenderBounds(renderBlock, rotated, side);
                    blockRenderManager.renderFlat(renderBlock, x, y, z , r, g, b);

                    rotated = MatrixTransformation.deepClone(zeroStateFacade);
                    rotated[0][0] = 0.0F;
                    rotated[0][1] = PipeWorldRenderer.PIPE_MIN_POS - zFightOffset * 2;
                    MatrixTransformation.transform(rotated, side);
                    setRenderBounds(renderBlock, rotated, side);
                    blockRenderManager.renderFlat(renderBlock, x, y, z , r, g, b);

                    rotated = MatrixTransformation.deepClone(zeroStateFacade);
                    rotated[0][0] = PipeWorldRenderer.PIPE_MAX_POS + zFightOffset * 2;
                    rotated[0][1] = 1F;
                    MatrixTransformation.transform(rotated, side);
                    setRenderBounds(renderBlock, rotated, side);
                    blockRenderManager.renderFlat(renderBlock, x, y, z , r, g, b);
                    renderBlock.applyUVFix = false;
                } else {
                    float[][] rotated = MatrixTransformation.deepClone(zeroStateFacade);
                    MatrixTransformation.transform(rotated, side);
                    setRenderBounds(renderBlock, rotated, side);
                    blockRenderManager.renderFlat(renderBlock, x, y, z , r, g, b);
                }
            }
        }

        renderBlock.setRenderAllSides();

        if(renderPass == 0 && !((FacadePluggable) pluggable).isHollow()){
            float[][] rotated = MatrixTransformation.deepClone(zeroStateSupport);
            MatrixTransformation.transform(rotated, side);

            renderBlock.setTextureIdentifier(TextureListener.structurePipe);
            renderBlock.setBoundingBox(rotated[0][0], rotated[1][0], rotated[2][0], rotated[0][1], rotated[1][1], rotated[2][1]);
            blockRenderManager.renderFlat(renderBlock, x, y, z , 1.0F, 1.0F, 1.0F);
        }
    }

    private Block getFacadeBlock(BlockView blockView, int x, int y, int z, Direction side){
        if(!(blockView.getBlockEntity(x, y, z) instanceof PipeBlockEntity pipe)){
            return null;
        }
        PipePluggable pluggable = pipe.getPipePluggable(side);
        if(pluggable instanceof FacadePluggable facadePluggable){
            return facadePluggable.getBlock();
        }
        return null;
    }

    private int getFacadeMeta(BlockView blockView, int x, int y, int z, Direction side){
        if(!(blockView.getBlockEntity(x, y, z) instanceof PipeBlockEntity pipe)){
            return 0;
        }
        PipePluggable pluggable = pipe.getPipePluggable(side);
        if(pluggable instanceof FacadePluggable facadePluggable){
            return facadePluggable.getMeta();
        }
        return 0;
    }
}
