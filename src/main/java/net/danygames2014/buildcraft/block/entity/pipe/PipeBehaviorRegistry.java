package net.danygames2014.buildcraft.block.entity.pipe;

import net.modificationstation.stationapi.api.util.Identifier;

import java.util.HashMap;

public class PipeBehaviorRegistry {
    public static final HashMap<Identifier, PipeBehavior> REGISTRY = new HashMap<>();
    
    public static void register(Identifier identifier, PipeBehavior behavior) {
        REGISTRY.put(identifier, behavior);
    }
    
    public static PipeBehavior get(Identifier identifier) {
        return REGISTRY.getOrDefault(identifier, null);
    }
}
