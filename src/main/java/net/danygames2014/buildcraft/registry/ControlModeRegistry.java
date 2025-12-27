package net.danygames2014.buildcraft.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.danygames2014.buildcraft.api.blockentity.ControlMode;
import net.modificationstation.stationapi.api.util.Identifier;

public class ControlModeRegistry {
    private static final ControlModeRegistry INSTANCE = new ControlModeRegistry();
    private final Object2ObjectOpenHashMap<Identifier, ControlMode> registry;

    public ControlModeRegistry() {
        registry = new Object2ObjectOpenHashMap<>();
    }
    
    public static ControlModeRegistry getInstance() {
        return INSTANCE;
    }
    
    public static Object2ObjectOpenHashMap<Identifier, ControlMode> getRegistry() {
        return getInstance().registry;
    }
    
    public static boolean register(ControlMode controlMode) {
        if (getInstance().registry.containsKey(controlMode.identifier)) {
            return false;
        }
        
        getInstance().registry.put(controlMode.identifier, controlMode);
        return true;
    }
    
    public static ControlMode get(Identifier identifier) {
        return getInstance().registry.get(identifier);
    }
}
