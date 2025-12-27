package net.danygames2014.buildcraft.api.blockentity;

import net.modificationstation.stationapi.api.util.Identifier;

public class ControlMode {
    public static ControlMode ON;
    public static ControlMode OFF;
    
    public final Identifier identifier;
    public final Identifier texture;
    public final int u;
    public final int v;

    public ControlMode(Identifier identifier, Identifier texture, int u, int v) {
        this.identifier = identifier;
        this.texture = texture;
        this.u = u;
        this.v = v;
    }
    
    public ControlMode(Identifier identifier, Identifier texture) {
        this(identifier, texture, 0, 0);
    }
}
