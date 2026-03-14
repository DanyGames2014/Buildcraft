package net.danygames2014.buildcraft.util;

import net.danygames2014.nyalib.fluid.FluidStack;
import net.minecraft.client.render.Tessellator;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.texture.SpriteAtlasTexture;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import org.lwjgl.opengl.GL11;

public class ScreenUtil {
    public static void drawSprite(Atlas.Sprite sprite, int x, int y, int width, int height, float zOffset) {
        SpriteAtlasTexture atlas = StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE);
        atlas.bindTexture();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);

        float uScale = 1.0F / atlas.getWidth();
        float vScale = 1.0F / atlas.getHeight();
        Tessellator var9 = Tessellator.INSTANCE;
        var9.startQuads();
        var9.vertex(x, y + height, zOffset, (float)sprite.getX() * uScale, (float)(sprite.getY() + height) * vScale);
        var9.vertex(x + width, y + height, zOffset, ((float)(sprite.getX() + width) * uScale), (float)(sprite.getY() + height) * vScale);
        var9.vertex(x + width, y, zOffset, (float)(sprite.getX() + width) * uScale, (float)sprite.getY() * vScale);
        var9.vertex(x, y, zOffset, (float)sprite.getX() * uScale, (float)sprite.getY() * vScale);
        var9.draw();
    }

    @SuppressWarnings("SameParameterValue")
    public static void drawFluid(FluidStack fluidStack, int level, int x, int y, int width, int height, float zOffset){
        if(fluidStack == null || fluidStack.fluid == null) {
            return;
        }
        int textureId = fluidStack.fluid.getStillBlock().getTexture(0);
        Atlas.Sprite sprite = Atlases.getTerrain().getTexture(textureId);
        StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture();

        // TODO: Implement a default color multiplier in NyaLib
        int color = 0xFFFFFF;

        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        GL11.glColor4f(red, green, blue, 1.0F);

        int fullX = width / 16;
        int fullY = height / 16;
        int lastX = width - fullX * 16;
        int lastY = height - fullY * 16;
        int fullLvl = (height - level) / 16;
        int lastLvl = (height - level) - fullLvl * 16;
        for(int i = 0; i < fullX; i++) {
            for(int j = 0; j < fullY; j++) {
                if(j >= fullLvl) {
                    drawCutSprite(sprite, x + i * 16, y + j * 16, 16, 16, j == fullLvl ? lastLvl : 0, zOffset);
                }
            }
        }
        for(int i = 0; i < fullX; i++) {
            drawCutSprite(sprite, x + i * 16, y + fullY * 16, 16, lastY, fullLvl == fullY ? lastLvl : 0, zOffset);
        }
        for(int i = 0; i < fullY; i++) {
            if(i >= fullLvl) {
                drawCutSprite(sprite, x + fullX * 16, y + i * 16, lastX, 16, i == fullLvl ? lastLvl : 0, zOffset);
            }
        }
        drawCutSprite(sprite, x + fullX * 16, y + fullY * 16, lastX, lastY, fullLvl == fullY ? lastLvl : 0, zOffset);
    }

    public static void drawCutSprite(Atlas.Sprite sprite, int x, int y, int width, int height, int cut, float zOffset){
        Tessellator tess = Tessellator.INSTANCE;
        tess.startQuads();
        tess.vertex(x, y + height, zOffset, sprite.getStartU(), getInterpolatedV(sprite, height));
        tess.vertex(x + width, y + height, zOffset, getInterpolatedU(sprite, width), getInterpolatedV(sprite, height));
        tess.vertex(x + width, y + cut, zOffset, getInterpolatedU(sprite, width), getInterpolatedV(sprite, cut));
        tess.vertex(x, y + cut, zOffset, sprite.getStartU(), getInterpolatedV(sprite, cut));
        tess.draw();
    }

    public static double getInterpolatedU(Atlas.Sprite sprite, double delta){
        double var3 = sprite.getEndU() - sprite.getStartU();
        return sprite.getStartU() + var3 * (float)delta / 16.0F;
    }

    public static double getInterpolatedV(Atlas.Sprite sprite, double delta){
        double var3 = sprite.getEndV() - sprite.getStartV();
        return sprite.getStartV() + var3 * ((float)delta / 16.0F);
    }
}
