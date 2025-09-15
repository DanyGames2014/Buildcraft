package net.danygames2014.buildcraft.api.energy;

import net.danygames2014.nyalib.fluid.Fluid;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.HashMap;

public class EngineFuelRegistry {
    public final HashMap<Identifier, EngineFuel> registry;
    private final HashMap<Fluid, EngineFuel> fluidToFuel;
    private static EngineFuelRegistry INSTANCE;

    private static EngineFuelRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EngineFuelRegistry();
        }
        return INSTANCE;
    }

    public EngineFuelRegistry() {
        this.registry = new HashMap<>();
        this.fluidToFuel = new HashMap<>();
    }

    public static void register(Identifier identifier, EngineFuel engineFuel) {
        if (getInstance().registry.containsKey(identifier)) {
            return;
        }

        getInstance().registry.put(identifier, engineFuel);
        getInstance().fluidToFuel.put(engineFuel.fluid, engineFuel);
    }

    public static EngineFuel get(Identifier identifier) {
        return getInstance().registry.getOrDefault(identifier, null);
    }
    
    public static EngineFuel get(Fluid fluid) {
        return getInstance().fluidToFuel.getOrDefault(fluid, null);
    }

    public static HashMap<Identifier, EngineFuel> getRegistry() {
        return getInstance().registry;
    }
}
