package net.danygames2014.buildcraft.api.blockentity;

import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.util.Identifier;

public class ControlMode {
    public static ControlMode ON;
    public static ControlMode OFF;
    
    public final Identifier identifier;
    public final Identifier texture;
    public Atlas.Sprite sprite;

    public ControlMode(Identifier identifier, Identifier texture) {
        this.identifier = identifier;
        this.texture = texture;
    }
}
