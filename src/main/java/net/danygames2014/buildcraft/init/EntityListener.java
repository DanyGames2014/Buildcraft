package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.entity.EntityBlock;
import net.danygames2014.buildcraft.entity.MechanicalArmEntity;
import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.entity.EntityRegister;
import net.modificationstation.stationapi.api.event.registry.EntityHandlerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

public class EntityListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerEntities(EntityRegister event){
        event.register(EntityBlock.class, NAMESPACE.id("block").toString());
        event.register(TravellingItemEntity.class, NAMESPACE.id("travelling_item").toString());
        event.register(MechanicalArmEntity.class, NAMESPACE.id("mechanical_arm").toString());
    }
    
    @EventListener
    public void registerEntityHandlers(EntityHandlerRegistryEvent event) {
        event.register(NAMESPACE.id("travelling_item"), TravellingItemEntity::new);
    }
}
