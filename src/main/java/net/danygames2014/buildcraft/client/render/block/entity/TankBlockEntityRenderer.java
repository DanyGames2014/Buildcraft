package net.danygames2014.buildcraft.client.render.block.entity;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.TankBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import org.lwjgl.opengl.GL11;

public class TankBlockEntityRenderer extends BlockEntityRenderer {

    @Override
    public void render(BlockEntity blockEntity, double x, double y, double z, float tickDelta) {
        if(!(blockEntity instanceof TankBlockEntity tankBlockEntity)){
            return;
        }
        if(tankBlockEntity.fluid == null || tankBlockEntity.fluid.amount <= 0){
            return;
        }
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glPushMatrix();
        BlockRenderManager blockRenderManager = Minecraft.INSTANCE.worldRenderer.blockRenderManager;
        GL11.glTranslated(x, y, z);

        float waterMaxY = (float) tankBlockEntity.fluid.amount / TankBlockEntity.CAPACITY;

        Buildcraft.tank.setBoundingBox(0.126F, 0.01F, 0.126F, 0.874F, waterMaxY - 0.01F, 0.874F);

        StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture();

        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.startQuads();

        int fluidTextureIndex = tankBlockEntity.fluid.fluid.getStillBlock().getTexture(0);

        blockRenderManager.renderTopFace(Buildcraft.tank, 0, 0, 0, fluidTextureIndex);
        blockRenderManager.renderNorthFace(Buildcraft.tank, 0, 0, 0, fluidTextureIndex);
        blockRenderManager.renderEastFace(Buildcraft.tank, 0, 0, 0, fluidTextureIndex);
        blockRenderManager.renderSouthFace(Buildcraft.tank, 0, 0, 0, fluidTextureIndex);
        blockRenderManager.renderWestFace(Buildcraft.tank, 0, 0, 0, fluidTextureIndex);
        blockRenderManager.renderBottomFace(Buildcraft.tank, 0, 0, 0, fluidTextureIndex);
        tessellator.draw();
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);

        Buildcraft.tank.setBoundingBox(0.125F, 0F, 0.125F, 0.875F, 1F, 0.875F);
    }
}
