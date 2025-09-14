package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.block.entity.ChuteBlockEntity;
import net.danygames2014.buildcraft.client.render.block.entity.ChuteBlockEntityRenderer;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.block.entity.BlockEntityRendererRegisterEvent;

public class BlockEntityRendererListener {
    @EventListener
    public void registerBlockEntityRenderers(BlockEntityRendererRegisterEvent event){
        event.renderers.put(ChuteBlockEntity.class, new ChuteBlockEntityRenderer());
    }
}
