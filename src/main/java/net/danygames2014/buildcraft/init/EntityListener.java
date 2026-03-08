package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.entity.*;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.entity.EntityRegister;
import net.modificationstation.stationapi.api.event.registry.EntityHandlerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

@SuppressWarnings("unused")
public class EntityListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerEntities(EntityRegister event){
        event.register(EntityBlock.class, NAMESPACE.id("block").toString());
        event.register(EntityBlockWithParent.class, NAMESPACE.id("block_with_parent").toString());
        event.register(TravellingItemEntity.class, NAMESPACE.id("travelling_item").toString());
        event.register(MechanicalArmEntity.class, NAMESPACE.id("mechanical_arm").toString());
        event.register(RobotEntity.class, NAMESPACE.id("robot").toString());
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
