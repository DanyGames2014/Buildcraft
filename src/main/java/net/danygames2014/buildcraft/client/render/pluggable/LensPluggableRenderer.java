package net.danygames2014.buildcraft.client.render.pluggable;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.RenderBlock;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.client.render.PipePluggableRenderer;
import net.danygames2014.buildcraft.init.TextureListener;
import net.danygames2014.buildcraft.pluggable.LensPluggable;
import net.danygames2014.buildcraft.util.MatrixTransformation;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.item.DyeItem;
import net.modificationstation.stationapi.api.util.math.Direction;

public class LensPluggableRenderer implements PipePluggableRenderer {
    public static final PipePluggableRenderer INSTANCE = new LensPluggableRenderer();
    private static final float zFightOffset = 1 / 4096.0F;

    @Override
    public void renderPluggable(BlockRenderManager blockRenderManager, PipeBlockEntity pipe, Direction side, PipePluggable pluggable, int x, int y, int z) {
        int renderPass = PipeBlock.currentRenderPass;
        RenderBlock renderBlock = Buildcraft.renderBlock;

        float[][] zeroState = new float[3][2];

        // X START - END
        zeroState[0][0] = 0.1875F;
        zeroState[0][1] = 0.8125F;
        // Y START - END
        zeroState[1][0] = 0.000F;
        zeroState[1][1] = 0.125F;
        // Z START - END
        zeroState[2][0] = 0.1875F;
        zeroState[2][1] = 0.8125F;

        if (renderPass == 1) {
            renderBlock.setRenderMask(1 << side.ordinal() | (1 << (side.ordinal() ^ 1)));

            for (int i = 0; i < 3; i++) {
                zeroState[i][0] += zFightOffset;
                zeroState[i][1] -= zFightOffset;
            }
            renderBlock.setTextureIdentifier(TextureListener.lensOverlay);
            renderBlock.setColor(DyeItem.colors[((LensPluggable) pluggable).color]);

            renderBlock.setRenderAllSides();
        } else {
            if (((LensPluggable) pluggable).isFilter) {
                renderBlock.setTextureIdentifier(TextureListener.filter);
            } else {
                renderBlock.setTextureIdentifier(TextureListener.lens);
            }
        }

        float[][] rotated = MatrixTransformation.deepClone(zeroState);
        MatrixTransformation.transform(rotated, side);

        renderBlock.setBoundingBox(rotated[0][0], rotated[1][0], rotated[2][0], rotated[0][1], rotated[1][1], rotated[2][1]);
        blockRenderManager.renderBlock(renderBlock, x, y, z);

        renderBlock.setColor(0xFFFFFF);
    }
}
