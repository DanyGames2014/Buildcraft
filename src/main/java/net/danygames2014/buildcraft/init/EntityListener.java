package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.entity.EntityBlock;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.entity.EntityRegister;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

public class EntityListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerEntities(EntityRegister event){
        event.register(EntityBlock.class, NAMESPACE.id("block").toString());
    }
}
