package net.danygames2014.buildcraft.api.energy;

import net.danygames2014.nyalib.fluid.Fluid;

import java.util.HashMap;

public class EngineCoolantRegistry {
    public final HashMap<Fluid, EngineCoolant> registry;
    private static EngineCoolantRegistry INSTANCE;

    private static EngineCoolantRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EngineCoolantRegistry();
        }
        return INSTANCE;
    }

    public EngineCoolantRegistry() {
        this.registry = new HashMap<>();
    }

    public static void register(Fluid fluid, EngineCoolant engineCoolant) {
        if (getInstance().registry.containsKey(fluid)) {
            return;
        }

        getInstance().registry.put(fluid, engineCoolant);
    }

    public static EngineCoolant get(Fluid fluid) {
        return getInstance().registry.getOrDefault(fluid, null);
    }

    public static HashMap<Fluid, EngineCoolant> getRegistry() {
        return getInstance().registry;
    }
}
