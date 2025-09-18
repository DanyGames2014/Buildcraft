package net.danygames2014.buildcraft.client.render.item;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.client.render.block.PipeWorldRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class PipeItemRenderer {
    public void renderPipeItem(BlockRenderManager blockRenderManager, Block block, int meta, float translateX, float translateY, float translateZ){
        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT); // Not used, unless we implement stained glass
        GL11.glPushMatrix();

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);

        Tessellator tessellator = Tessellator.INSTANCE;

        Block renderBlock = Buildcraft.renderBlock;
        renderBlock.setBoundingBox(PipeWorldRenderer.PIPE_MIN_POS, 0.0F, PipeWorldRenderer.PIPE_MIN_POS, PipeWorldRenderer.PIPE_MAX_POS, 1.0F, PipeWorldRenderer.PIPE_MAX_POS);

        GL11.glTranslatef(translateX, translateY, translateZ);

        drawBlockItem(blockRenderManager, tessellator, renderBlock, block.getTexture(0));

        renderBlock.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    // TODO: move to util class if we need this multiple times
    public static void drawBlockItem(BlockRenderManager blockRenderManager, Tessellator tessellator, Block block, int textureIndex) {
        tessellator.startQuads();
        tessellator.normal(0.0F, -1F, 0.0F);
        blockRenderManager.renderBottomFace(block, 0.0D, 0.0D, 0.0D, textureIndex);
        tessellator.normal(0.0F, 1.0F, 0.0F);
        blockRenderManager.renderTopFace(block, 0.0D, 0.0D, 0.0D, textureIndex);
        tessellator.normal(0.0F, 0.0F, -1F);
        blockRenderManager.renderNorthFace(block, 0.0D, 0.0D, 0.0D, textureIndex);
        tessellator.normal(0.0F, 0.0F, 1.0F);
        blockRenderManager.renderSouthFace(block, 0.0D, 0.0D, 0.0D, textureIndex);
        tessellator.normal(-1F, 0.0F, 0.0F);
        blockRenderManager.renderWestFace(block, 0.0D, 0.0D, 0.0D, textureIndex);
        tessellator.normal(1.0F, 0.0F, 0.0F);
        blockRenderManager.renderEastFace(block, 0.0D, 0.0D, 0.0D, textureIndex);
        tessellator.draw();
    }
}
