package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.entity.TravellingItemEntity;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.render.item.ItemRenderer;
import net.modificationstation.stationapi.api.client.event.render.entity.EntityRendererRegisterEvent;
import net.modificationstation.stationapi.api.event.entity.EntityRegister;
import net.modificationstation.stationapi.api.event.registry.EntityHandlerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

public class EntityListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;
    
    @EventListener
    public void registerEntities(EntityRegister event) {
        event.register(TravellingItemEntity.class, NAMESPACE.id("travelling_item").toString());
    }
    
    @EventListener
    public void registerEntityHandlers(EntityHandlerRegistryEvent event) {
        event.register(NAMESPACE.id("travelling_item"), TravellingItemEntity::new);
    }

    @EventListener
    public void registerEntityRenderers(EntityRendererRegisterEvent event) {
        event.renderers.put(TravellingItemEntity.class, new ItemRenderer());
    }
        
}
