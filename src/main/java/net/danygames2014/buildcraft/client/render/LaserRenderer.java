package net.danygames2014.buildcraft.client.render;

import net.danygames2014.buildcraft.block.entity.pipe.LaserData;
import net.danygames2014.buildcraft.client.render.entity.EntityBlockRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class LaserRenderer {
    public static final float STEP = 0.04F;

    public static final String[] LASER_TEXTURES = new String[]{
            "/assets/buildcraft/stationapi/textures/block/laser/laser_1.png",
            "/assets/buildcraft/stationapi/textures/block/laser/laser_2.png",
            "/assets/buildcraft/stationapi/textures/block/laser/laser_3.png",
            "/assets/buildcraft/stationapi/textures/block/laser/laser_4.png",
            "/assets/buildcraft/stationapi/textures/block/laser/stripes.png",
            "/assets/buildcraft/stationapi/textures/block/laser/blue_stripes.png"
    };

    protected static ModelPart model = new ModelPart(0 ,0) {
    };
    private static ModelPart [] box;

    private static int [][][] scaledBoxes;

    private static ModelPart getBox(int index) {
        if (box == null) {
            box = new ModelPart[40];

            for (int j = 0; j < box.length; ++j) {
                box[j] = new ModelPart(box.length - j, 0);
                box[j].addCuboid(0, -0.5F, -0.5F, 16, 1, 1);
                box[j].pivotX = 0;
                box[j].pivotY = 0;
                box[j].pivotZ = 0;
            }
        }

        return box [index];
    }

    private static void initScaledBoxes () {
        if (scaledBoxes == null) {
            scaledBoxes = new int [2][100][20];

            for (int flags = 0; flags < 2; ++flags) {
                for (int size = 0; size < 100; ++size) {
                    for (int i = 0; i < 20; ++i) {
                        scaledBoxes[flags][size][i] = GL11.glGenLists(1);
                        GL11.glNewList(scaledBoxes[flags][size][i], GL11.GL_COMPILE);

                        EntityBlockRenderer.RenderInfo block = new EntityBlockRenderer.RenderInfo();

                        float minSize = 0.2F * size / 100F;
                        float maxSize = 0.4F * size / 100F;
                        //float minSize = 0.1F;
                        //float maxSize = 0.2F;

                        float range = maxSize - minSize;

                        float diff = MathHelper.cos(i / 20F * 2 * (float) Math.PI)
                                             * range / 2F;

                        block.minX = 0.0F;
                        block.minY = -maxSize / 2F + diff;
                        block.minZ = -maxSize / 2F + diff;

                        block.maxX = STEP;
                        block.maxY = maxSize / 2F - diff;
                        block.maxZ = maxSize / 2F - diff;

                        if (flags == 1) {
                            block.brightness = 15;
                        }

                        EntityBlockRenderer.INSTANCE.renderBlock(block);

                        GL11.glEndList();
                    }
                }
            }
        }
    }

    public static void doRenderLaser(TextureManager textureManager, LaserData laser, String texture) {
        if (!laser.isVisible || texture == null) {
            return;
        }

        GL11.glPushMatrix();

        GL11.glTranslated(laser.head.x, laser.head.y, laser.head.z);
        laser.update();

        GL11.glRotatef((float) laser.angleZ, 0, 1, 0);
        GL11.glRotatef((float) laser.angleY, 0, 0, 1);

        textureManager.bindTexture(textureManager.getTextureId(texture));

        initScaledBoxes();

        if (laser.isGlowing) {
            // TODO: probably use glcolor
            GL11.glColor4f(1f, 1f, 1f, 1f);
//            float lastBrightnessX = OpenGlHelper.lastBrightnessX;
//            float lastBrightnessY = OpenGlHelper.lastBrightnessY;
//
//            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
            GL11.glDisable(GL11.GL_LIGHTING);

            doRenderLaserLine(laser.renderSize, laser.laserTexAnimation);

            GL11.glEnable(GL11.GL_LIGHTING);
            //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        } else {
            doRenderLaserLine(laser.renderSize, laser.laserTexAnimation);
        }

        GL11.glPopMatrix();
    }

    private static void doRenderLaserLine(double len, int texId) {
        float lasti = 0;

        if (len - 1 > 0) {
            for (float i = 0; i <= len - 1; i += 1) {
                getBox(texId).render(1F / 16F);
                GL11.glTranslated(1, 0, 0);
                lasti = i;
            }
            lasti++;
        }

        GL11.glPushMatrix();
        GL11.glScalef((float) len - lasti, 1, 1);
        getBox(texId).render(1F / 16F);
        GL11.glPopMatrix();

        GL11.glTranslated((float) (len - lasti), 0, 0);
    }
}
