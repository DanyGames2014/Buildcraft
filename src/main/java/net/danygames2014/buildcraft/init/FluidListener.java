package net.danygames2014.buildcraft.init;

import net.danygames2014.nyalib.event.FluidRegistryEvent;
import net.danygames2014.nyalib.fluid.Fluid;
import net.danygames2014.nyalib.fluid.FluidBuilder;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

public class FluidListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    public static Fluid oil;
    public static Fluid fuel;

    @EventListener
    public void registerFluids(FluidRegistryEvent event){
        event.register(oil = new FluidBuilder(NAMESPACE.id("oil"), NAMESPACE.id("block/oil_still"), NAMESPACE.id("block/oil_flowing"))
                               .color(0x000000)
                               .tickRate(20)
                               .build()
        );
        event.register(fuel = new FluidBuilder(NAMESPACE.id("fuel"), NAMESPACE.id("block/fuel_still"), NAMESPACE.id("block/fuel_flowing"))
                               .color(0xC4C00E)
                               .build()
        );
    }
}
