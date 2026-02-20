package net.danygames2014.buildcraft.util;

import net.danygames2014.buildcraft.init.TextureListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.util.Identifier;

public class TextureUtil {
    public static int getTerrainTextureOffset(Identifier identifier){
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER || identifier == null){
            return 0;
        }
        Atlas.Sprite texture = Atlases.getTerrain().getTexture(identifier);
        if(texture != null){
            return texture.index;
        }
        return Atlases.getTerrain().getTexture(TextureListener.missingTexture).index;
    }

    public static Identifier getTerrainIdentifierFromOffset(int textureOffset){
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER){
            return TextureListener.missingTexture;
        }
        return Atlases.getTerrain().getTexture(textureOffset).getId();
    }
}
