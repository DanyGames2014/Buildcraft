package net.danygames2014.buildcraft.registry;

import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.mine_diver.unsafeevents.Event;
import net.modificationstation.stationapi.api.util.Identifier;

public class PluggableRegisterEvent extends Event {
    public void register(Identifier identifier, Class<? extends PipePluggable> pluggableClass, PluggableRegistry.PluggableFactory pluggable){
        PluggableRegistry.register(identifier, pluggableClass, pluggable);
    }
}
