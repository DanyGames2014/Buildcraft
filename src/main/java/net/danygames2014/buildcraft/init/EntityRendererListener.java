package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.client.render.entity.EntityBlockRenderer;
import net.danygames2014.buildcraft.entity.EntityBlock;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.render.entity.EntityRendererRegisterEvent;

public class EntityRendererListener {
    @EventListener
    public void registerEntityRenderers(EntityRendererRegisterEvent event){
        event.renderers.put(EntityBlock.class, EntityBlockRenderer.INSTANCE);
    }
}
