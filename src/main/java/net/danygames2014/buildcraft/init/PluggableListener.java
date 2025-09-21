package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.pluggable.FacadePluggable;
import net.danygames2014.buildcraft.pluggable.PlugPluggable;
import net.danygames2014.buildcraft.registry.PluggableRegisterEvent;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

public class PluggableListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerPluggables(PluggableRegisterEvent event){
        event.register(NAMESPACE.id("plug"), PlugPluggable.class, PlugPluggable::new);
        event.register(NAMESPACE.id("facade"), FacadePluggable.class, FacadePluggable::new);
    }
}
