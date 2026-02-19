package net.danygames2014.buildcraft.api.energy;

import net.danygames2014.nyalib.fluid.Fluid;

import java.util.HashMap;

public class EngineFuelRegistry {
    private final HashMap<Fluid, EngineFuel> registry;
    private static EngineFuelRegistry INSTANCE;

    private static EngineFuelRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EngineFuelRegistry();
        }
        return INSTANCE;
    }

    public EngineFuelRegistry() {
        this.registry = new HashMap<>();
    }

    public static void register(Fluid fluid, EngineFuel engineFuel) {
        if (getInstance().registry.containsKey(fluid)) {
            return;
        }

        getInstance().registry.put(fluid, engineFuel);
    }


    public static EngineFuel get(Fluid fluid) {
        return getInstance().registry.getOrDefault(fluid, null);
    }

    public static HashMap<Fluid, EngineFuel> getRegistry() {
        return getInstance().registry;
    }
}
