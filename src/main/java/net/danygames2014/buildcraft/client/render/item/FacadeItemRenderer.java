package net.danygames2014.buildcraft.client.render.item;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.client.render.block.PipeWorldRenderer;
import net.danygames2014.buildcraft.item.FacadeItem;
import net.danygames2014.buildcraft.block.entity.pipe.pluggable.FacadePluggable;
import net.danygames2014.buildcraft.util.RenderHelper;
import net.minecraft.block.Block;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import org.lwjgl.opengl.GL11;

public class FacadeItemRenderer {

    public static FacadeItemRenderer INSTANCE = new FacadeItemRenderer();

    private void drawHollowCube(Tessellator tessellator, BlockRenderManager blockRenderManager, Block block, int meta) {
        float width = 1 - FacadePluggable.FACADE_THICKNESS;
        float cavity = (PipeWorldRenderer.PIPE_MAX_POS - PipeWorldRenderer.PIPE_MIN_POS) / 2F;
        double innerWidth = 1 - cavity;

        tessellator.startQuads();
        Buildcraft.renderBlock.setBoundingBox(0F, 0F, width, 1F, 1F, 1F);

        //Outside
        tessellator.normal(0, -1, 0);
        blockRenderManager.renderBottomFace(Buildcraft.renderBlock, 0, 0, 0, block.getTexture(0, meta));
        tessellator.normal(0, 1, 0);
        blockRenderManager.renderTopFace(Buildcraft.renderBlock, 0, 0, 0, block.getTexture(1, meta));
        tessellator.normal(-1, 0, 0);
        blockRenderManager.renderNorthFace(Buildcraft.renderBlock, 0, 0, 0, block.getTexture(4, meta));
        tessellator.normal(1, 0, 0);
        blockRenderManager.renderSouthFace(Buildcraft.renderBlock, 0, 0, 0, block.getTexture(5, meta));

        //Inside
        tessellator.normal(0, -1, 0);
        blockRenderManager.renderBottomFace(Buildcraft.renderBlock, 0, innerWidth, 0, block.getTexture(0, meta));
        tessellator.normal(0, 1, 0);
        blockRenderManager.renderTopFace(Buildcraft.renderBlock, 0, -innerWidth, 0, block.getTexture(1, meta));
        tessellator.normal(-1, 0, 0);
        blockRenderManager.renderNorthFace(Buildcraft.renderBlock, innerWidth, 0, 0, block.getTexture(4, meta));
        tessellator.normal(1, 0, 0);
        blockRenderManager.renderSouthFace(Buildcraft.renderBlock, -innerWidth, 0, 0, block.getTexture(5, meta));

        //Hollow
//        blockRenderManager.field_152631_f = true;
        Buildcraft.renderBlock.setBoundingBox(0, 0, width, cavity, 1, 1);
        tessellator.normal(0, 0, -1);
        blockRenderManager.renderEastFace(Buildcraft.renderBlock, 0, 0, 0, block.getTexture(2, meta));
        tessellator.normal(0, 0, 1);
        blockRenderManager.renderWestFace(Buildcraft.renderBlock, 0, 0, 0, block.getTexture(3, meta));
        Buildcraft.renderBlock.setBoundingBox((float) innerWidth, 0, width, 1, 1, 1);
        tessellator.normal(0, 0, -1);
        blockRenderManager.renderEastFace(Buildcraft.renderBlock, 0, 0, 0, block.getTexture(2, meta));
        tessellator.normal(0, 0, 1);
        blockRenderManager.renderWestFace(Buildcraft.renderBlock, 0, 0, 0, block.getTexture(3, meta));
//        blockRenderManager.field_152631_f = false;

        Buildcraft.renderBlock.setBoundingBox(cavity, 0, width, (float) innerWidth, cavity, 1);
        tessellator.normal(0, 0, -1);
        blockRenderManager.renderEastFace(Buildcraft.renderBlock, 0, 0, 0, block.getTexture(2, meta));
        tessellator.normal(0, 0, 1);
        blockRenderManager.renderWestFace(Buildcraft.renderBlock, 0, 0, 0, block.getTexture(3, meta));
        Buildcraft.renderBlock.setBoundingBox(cavity, (float) innerWidth, width, (float) innerWidth, 1, 1);
        tessellator.normal(0, 0, -1);
        blockRenderManager.renderEastFace(Buildcraft.renderBlock, 0, 0, 0, block.getTexture(2, meta));
        tessellator.normal(0, 0, 1);
        blockRenderManager.renderWestFace(Buildcraft.renderBlock, 0, 0, 0, block.getTexture(3, meta));

        tessellator.draw();
    }

    public void renderFacadeItem(BlockRenderManager blockRenderManager, ItemStack item, float translateX, float translateY, float translateZ) {
        Block block = FacadeItem.getBlock(item);
        int decodedMeta = FacadeItem.getMeta(item);
        boolean hollow = FacadeItem.isHollow(item);

        StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture();

        Tessellator tessellator = Tessellator.INSTANCE;

        // Render Facade
        GL11.glPushMatrix();

        if (hollow) {
            GL11.glTranslatef(translateX, translateY, translateZ);
            drawHollowCube(tessellator, blockRenderManager, block, decodedMeta);
        } else {
            Buildcraft.renderBlock.setBoundingBox(0F, 0F, 1 - FacadePluggable.FACADE_THICKNESS, 1F, 1F, 1F);
            GL11.glTranslatef(translateX, translateY, translateZ);
            RenderHelper.drawBlockItem(blockRenderManager, tessellator, Buildcraft.renderBlock, block.getTexture(0, decodedMeta));
        }

        GL11.glPopMatrix();

        RenderHelper.setGLColorFromInt(0xFFFFFF);

        // Render StructurePipe
        if (!hollow) {
            block = Buildcraft.cobblestoneStructurePipe;
            int textureID = block.getTexture(0);

            Buildcraft.renderBlock.setBoundingBox(PipeWorldRenderer.PIPE_MIN_POS, PipeWorldRenderer.PIPE_MIN_POS, PipeWorldRenderer.PIPE_MIN_POS, PipeWorldRenderer.PIPE_MAX_POS, PipeWorldRenderer.PIPE_MAX_POS, PipeWorldRenderer.PIPE_MAX_POS - 1F / 16F);
            GL11.glTranslatef(translateX, translateY, translateZ + 0.25F);

            tessellator.startQuads();
            tessellator.normal(0.0F, -0F, 0.0F);
            blockRenderManager.renderBottomFace(Buildcraft.renderBlock, 0.0D, 0.0D, 0.0D, textureID);
            tessellator.draw();
            tessellator.startQuads();
            tessellator.normal(0.0F, 1.0F, 0.0F);
            blockRenderManager.renderTopFace(Buildcraft.renderBlock, 0.0D, 0.0D, 0.0D, textureID);
            tessellator.draw();
            tessellator.startQuads();
            tessellator.normal(0.0F, 0.0F, -1F);
            blockRenderManager.renderEastFace(Buildcraft.renderBlock, 0.0D, 0.0D, 0.0D, textureID);
            tessellator.draw();
            tessellator.startQuads();
            tessellator.normal(0.0F, 0.0F, 1.0F);
            blockRenderManager.renderWestFace(Buildcraft.renderBlock, 0.0D, 0.0D, 0.0D, textureID);
            tessellator.draw();
            tessellator.startQuads();
            tessellator.normal(-1F, 0.0F, 0.0F);
            blockRenderManager.renderNorthFace(Buildcraft.renderBlock, 0.0D, 0.0D, 0.0D, textureID);
            tessellator.draw();
            tessellator.startQuads();
            tessellator.normal(1.0F, 0.0F, 0.0F);
            blockRenderManager.renderSouthFace(Buildcraft.renderBlock, 0.0D, 0.0D, 0.0D, textureID);
            tessellator.draw();
        }
    }
}
