package net.danygames2014.buildcraft.util;

import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.util.Identifier;

public class TextureUtil {
    public static int getTerrainTextureOffset(Identifier identifier){
        return Atlases.getTerrain().getTexture(identifier).index;
    }

    public static Identifier getTerrainIdentifierFromOffset(int textureOffset){
        return Atlases.getTerrain().getTexture(textureOffset).getId();
    }
}
