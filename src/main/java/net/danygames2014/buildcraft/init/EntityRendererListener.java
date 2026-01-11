package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.client.render.entity.EntityBlockRenderer;
import net.danygames2014.buildcraft.client.render.entity.RobotEntityRenderer;
import net.danygames2014.buildcraft.client.render.entity.VoidEntityRenderer;
import net.danygames2014.buildcraft.entity.EntityBlock;
import net.danygames2014.buildcraft.entity.EntityBlockWithParent;
import net.danygames2014.buildcraft.entity.MechanicalArmEntity;
import net.danygames2014.buildcraft.entity.RobotEntity;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.render.entity.EntityRendererRegisterEvent;

public class EntityRendererListener {
    @EventListener
    public void registerEntityRenderers(EntityRendererRegisterEvent event){
        event.renderers.put(EntityBlock.class, EntityBlockRenderer.INSTANCE);
        event.renderers.put(EntityBlockWithParent.class, EntityBlockRenderer.INSTANCE);
        event.renderers.put(RobotEntity.class, RobotEntityRenderer.INSTANCE);
        event.renderers.put(MechanicalArmEntity.class, VoidEntityRenderer.INSTANCE);
    }
}
