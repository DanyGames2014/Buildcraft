package net.danygames2014.buildcraft.api.energy;

import net.danygames2014.nyalib.fluid.Fluid;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.HashMap;

public class EngineCoolantRegistry {
    public final HashMap<Identifier, EngineCoolant> registry;
    public final HashMap<Fluid, EngineCoolant> fluidToCoolant;
    private static EngineCoolantRegistry INSTANCE;

    private static EngineCoolantRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EngineCoolantRegistry();
        }
        return INSTANCE;
    }

    public EngineCoolantRegistry() {
        this.registry = new HashMap<>();
        this.fluidToCoolant = new HashMap<>();
    }

    public static void register(Identifier identifier, EngineCoolant engineCoolant) {
        if (getInstance().registry.containsKey(identifier)) {
            return;
        }

        getInstance().registry.put(identifier, engineCoolant);
    }

    public static EngineCoolant get(Identifier identifier) {
        return getInstance().registry.getOrDefault(identifier, null);
    }
    
    public static EngineCoolant get(Fluid fluid) {
        return getInstance().fluidToCoolant.getOrDefault(fluid, null);
    }

    public static HashMap<Identifier, EngineCoolant> getRegistry() {
        return getInstance().registry;
    }
}
