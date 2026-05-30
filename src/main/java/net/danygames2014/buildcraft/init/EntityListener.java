package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.entity.*;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.entity.EntityRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.EntityHandlerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

@SuppressWarnings("unused")
public class EntityListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerEntities(EntityRegisterEvent event){
        event.register(NAMESPACE.id("block"), EntityBlock.class);
        event.register(NAMESPACE.id("block_with_parent"), EntityBlockWithParent.class);
        event.register(NAMESPACE.id("travelling_item"), TravellingItemEntity.class);
        event.register(NAMESPACE.id("mechanical_arm"), MechanicalArmEntity.class);
        event.register(NAMESPACE.id("robot"), RobotEntity.class);
    }
    
    @EventListener
    public void registerEntityHandlers(EntityHandlerRegistryEvent event) {
        event.register(NAMESPACE.id("travelling_item"), TravellingItemEntity::new);
        event.register(NAMESPACE.id("robot"), RobotEntity::new);
        event.register(NAMESPACE.id("mechanical_arm"), MechanicalArmEntity::new);
        event.register(NAMESPACE.id("entity_block"), EntityBlock::new);
        event.register(NAMESPACE.id("entity_block_with_parent"), EntityBlockWithParent::new);
    }
}
