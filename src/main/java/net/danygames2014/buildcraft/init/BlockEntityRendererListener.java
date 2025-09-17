package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.BaseEngineBlock;
import net.danygames2014.buildcraft.block.entity.*;
import net.danygames2014.buildcraft.client.render.block.entity.ChuteBlockEntityRenderer;
import net.danygames2014.buildcraft.client.render.block.entity.EngineBlockEntityRenderer;
import net.danygames2014.buildcraft.client.render.block.entity.TankBlockEntityRenderer;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.block.entity.BlockEntityRendererRegisterEvent;

public class BlockEntityRendererListener {
    @EventListener
    public void registerBlockEntityRenderers(BlockEntityRendererRegisterEvent event){
        event.renderers.put(ChuteBlockEntity.class, new ChuteBlockEntityRenderer());
        event.renderers.put(RedstoneEngineBlockEntity.class, new EngineBlockEntityRenderer(((BaseEngineBlock)Buildcraft.redstoneEngine).getBaseTexturePath()));
        event.renderers.put(StirlingEngineBlockEntity.class, new EngineBlockEntityRenderer(((BaseEngineBlock)Buildcraft.stirlingEngine).getBaseTexturePath()));
        event.renderers.put(CombustionEngineBlockEntity.class, new EngineBlockEntityRenderer(((BaseEngineBlock)Buildcraft.combustionEngine).getBaseTexturePath()));
        event.renderers.put(TankBlockEntity.class, new TankBlockEntityRenderer());
    }
}
