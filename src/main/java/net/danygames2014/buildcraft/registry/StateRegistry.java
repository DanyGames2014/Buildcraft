package net.danygames2014.buildcraft.registry;

import net.danygames2014.buildcraft.api.core.Serializable;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateRegistry {
    private static final Map<Integer, StateRegistry.StateFactory> ID_TO_STATE = new HashMap<>();
    private static final Map<Class<? extends Serializable>, Integer> CLASS_TO_ID = new HashMap<>();
    private static final Map<Integer, Class<? extends Serializable>> ID_TO_CLASS = new HashMap<>();
    private static int NEXT_ID = 0;
    public static void register(Class<? extends Serializable> stateClass){

        ID_TO_STATE.put(NEXT_ID, () -> {
            try {
                return stateClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create state: " + stateClass, e);
            }
        });
        CLASS_TO_ID.put(stateClass, NEXT_ID);
        ID_TO_CLASS.put(NEXT_ID, stateClass);
        NEXT_ID++;
    }

    public interface StateFactory{
        Serializable create();
    }

    public static int getId(Class<? extends Serializable> stateClass) {
        Integer id = CLASS_TO_ID.get(stateClass);
        if (id == null) {
            throw new IllegalArgumentException("State class not registered: " + stateClass);
        }
        return id;
    }

    public static Class<? extends Serializable> getClass(int id){
        Class<? extends Serializable> clazz = ID_TO_CLASS.get(id);
        if(clazz == null){
            throw new IllegalArgumentException("Unknown state id: " + id);
        }
        return clazz;
    }

    public static Serializable create(int id) {
        StateFactory factory = ID_TO_STATE.get(id);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown state id: " + id);
        }
        return factory.create();
    }
}
