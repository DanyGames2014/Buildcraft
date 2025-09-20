package net.danygames2014.buildcraft.registry;

import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.modificationstation.stationapi.api.util.Identifier;

import javax.annotation.Nullable;
import java.util.HashMap;

public class PluggableRegistry{
    private static final HashMap<Identifier, PipePluggable> REGISTRY = new HashMap<>();

    public static void register(Identifier identifier, PipePluggable pluggable){
        if(REGISTRY.containsKey(identifier)){
            throw new RuntimeException("Duplicate identifier: " + identifier.toString());
        }
        REGISTRY.put(identifier, pluggable);
    }

    @Nullable
    public static PipePluggable getPluggable(Identifier identifier){
        if(!REGISTRY.containsKey(identifier)){
            return null;
        }
        return REGISTRY.get(identifier);
    }
}
