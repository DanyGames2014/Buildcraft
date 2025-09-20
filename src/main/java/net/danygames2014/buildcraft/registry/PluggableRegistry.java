package net.danygames2014.buildcraft.registry;

import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.modificationstation.stationapi.api.util.Identifier;

import javax.annotation.Nullable;
import java.util.HashMap;

public class PluggableRegistry{
    private static final HashMap<Identifier, PluggableFactory> IDENTIFIER_TO_PLUGGABLE = new HashMap<>();
    private static final HashMap<Class, Identifier> CLASS_TO_IDENTIFIER = new HashMap<>();

    public static void register(Identifier identifier, Class plugableClass, PluggableFactory pluggable){
//        if(REGISTRY.containsKey(identifier)){
//            throw new RuntimeException("Duplicate identifier: " + identifier.toString());
//        }
        IDENTIFIER_TO_PLUGGABLE.put(identifier, pluggable);
        CLASS_TO_IDENTIFIER.put(plugableClass, identifier);
    }

    @Nullable
    public static PluggableFactory getPluggableFactory(Identifier identifier){
//        if(!REGISTRY.containsKey(identifier)){
//            return null;
//        }
        return IDENTIFIER_TO_PLUGGABLE.get(identifier);
    }

    public static Identifier getIdentifier(Class pluggableClass){
        return CLASS_TO_IDENTIFIER.get(pluggableClass);
    }

    public interface PluggableFactory{
        PipePluggable create();
    }
}
