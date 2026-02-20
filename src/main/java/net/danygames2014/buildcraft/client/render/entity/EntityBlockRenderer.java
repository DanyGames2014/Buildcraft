package net.danygames2014.buildcraft.client.render.entity;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.entity.EntityBlock;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public class EntityBlockRenderer extends EntityRenderer {
    public static EntityBlockRenderer INSTANCE = new EntityBlockRenderer();
    protected BlockRenderManager blockRenderManager;

    public static class RenderInfo {
        public float minX = 0.0F;
        public float minY = 0.0F;
        public float minZ = 0.0F;
        public float maxX = 1.0F;
        public float maxY = 1.0F;
        public float maxZ = 1.0F;
        public Block baseBlock = Buildcraft.renderBlock;
        public Integer texture = null;
        public int[] textureArray = null;
        public boolean[] renderSide = new boolean[]{true, true, true, true, true, true};
        public int light = -1;
        public int brightness = -1;

        public RenderInfo() {
        }

        public RenderInfo(Block template, int[] texture) {
            this();
            this.baseBlock = template;
            this.textureArray = texture;
        }

        public RenderInfo(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            this();
            setBounds(minX, minY, minZ, maxX, maxY, maxZ);
        }

        public void setSkyBlockLight(World world, int x, int y, int z, int light) {
            this.brightness = world.getBrightness(LightType.SKY, x, y, z) << 16 | light;
        }

        // TODO: confirm this is correct
        public float getBlockBrightness(World world, int x, int y, int z) {
            return world.getBrightness(x, y, z);
        }

        public final void setBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }

        public final void setRenderSingleSide(int side) {
            Arrays.fill(renderSide, false);
            renderSide[side] = true;
        }

        public final void setRenderAllSides() {
            Arrays.fill(renderSide, true);
        }

        public void rotate() {
            float temp = minX;
            minX = minZ;
            minZ = temp;

            temp = maxX;
            maxX = maxZ;
            maxZ = temp;
        }

        public void reverseX() {
            float temp = minX;
            minX = 1 - maxX;
            maxX = 1 - temp;
        }

        public void reverseZ() {
            float temp = minZ;
            minZ = 1 - maxZ;
            maxZ = 1 - temp;
        }

        public int getBlockTextureFromSide(int i) {
            if (texture != null) {
                return texture;
            }

            int index = i;

            if (textureArray == null || textureArray.length == 0) {
                return baseBlock.getTexture(index);
            } else {
                if (index >= textureArray.length) {
                    index = 0;
                }

                return textureArray[index];
            }
        }
    }

    @Override
    public void render(Entity entity, double x, double y, double z, float yaw, float pitch) {
        renderBlock((EntityBlock)entity, x, y, z);
    }

    public void renderBlock(EntityBlock entity, double x, double y, double z){
        if(blockRenderManager == null){
            this.blockRenderManager = Minecraft.INSTANCE.worldRenderer.blockRenderManager;
        }
        if(entity.dead){
            return;
        }

        shadowRadius = entity.shadowSize;
        RenderInfo util = new RenderInfo();
        util.texture = entity.texture;
        StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture();

        for(int xBase = 0; xBase < entity.xSize; ++xBase){
            for(int yBase = 0; yBase < entity.ySize; ++yBase){
                for(int zBase = 0; zBase < entity.zSize; ++zBase){
                    util.minX = 0;
                    util.minY = 0;
                    util.minZ = 0;

                    double remainX = entity.xSize - xBase;
                    double remainY = entity.ySize - yBase;
                    double remainZ = entity.zSize - zBase;

                    util.maxX = (float) (Math.min(remainX, 1.0));
                    util.maxY = (float) (Math.min(remainY, 1.0));
                    util.maxZ = (float) (Math.min(remainZ, 1.0));

                    GL11.glPushMatrix();
                    GL11.glTranslatef((float) x, (float) y, (float) z);
                    GL11.glRotatef(entity.pitch, 1, 0, 0);
                    GL11.glRotatef(entity.yaw, 0, 1, 0);
                    GL11.glRotatef(entity.roll, 0, 0, 1);
                    GL11.glTranslatef(xBase, yBase, zBase);

                    renderBlock(util);

                    GL11.glPopMatrix();
                }
            }
        }
    }

    public void renderBlock(RenderInfo info){
        if(blockRenderManager == null){
            this.blockRenderManager = Minecraft.INSTANCE.worldRenderer.blockRenderManager;
        }
        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.startQuads();

        info.baseBlock.setBoundingBox(info.minX, info.minY, info.minZ, info.maxX, info.maxY, info.maxZ);

        // TODO: no brightness in tesselator, look for alternative method
//        if(info.light != -1){
//            tessellator.
//        }

        if (info.renderSide[0]) {
            tessellator.normal(0, -1, 0);
            blockRenderManager.renderBottomFace(info.baseBlock, 0, 0, 0, info.getBlockTextureFromSide(0));
        }
        if (info.renderSide[1]) {
            tessellator.normal(0, 1, 0);
            blockRenderManager.renderTopFace(info.baseBlock, 0, 0, 0, info.getBlockTextureFromSide(1));
        }
        if (info.renderSide[2]) {
            tessellator.normal(0, 0, -1);
            blockRenderManager.renderEastFace(info.baseBlock, 0, 0, 0, info.getBlockTextureFromSide(2));
        }
        if (info.renderSide[3]) {
            tessellator.normal(0, 0, 1);
            blockRenderManager.renderWestFace(info.baseBlock, 0, 0, 0, info.getBlockTextureFromSide(3));
        }
        if (info.renderSide[4]) {
            tessellator.normal(-1, 0, 0);
            blockRenderManager.renderNorthFace(info.baseBlock, 0, 0, 0, info.getBlockTextureFromSide(4));
        }
        if (info.renderSide[5]) {
            tessellator.normal(1, 0, 0);
            blockRenderManager.renderSouthFace(info.baseBlock, 0, 0, 0, info.getBlockTextureFromSide(5));
        }
        tessellator.draw();
    }
}
