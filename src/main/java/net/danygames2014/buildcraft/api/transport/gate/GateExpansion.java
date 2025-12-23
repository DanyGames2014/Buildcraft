package net.danygames2014.buildcraft.api.transport.gate;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.util.Identifier;

public interface GateExpansion {
    Identifier getIdentifier();

    String getDisplayName();

    GateExpansionController makeController(PipeBlockEntity pipe);

    void registerBlockTexture();

    void registerItemTexture();

    Atlas.Sprite getOverlayBlockSprite();

    Atlas.Sprite getOverlayItemSprite();
}
