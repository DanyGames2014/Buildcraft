package net.danygames2014.buildcraft.util;

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
}
