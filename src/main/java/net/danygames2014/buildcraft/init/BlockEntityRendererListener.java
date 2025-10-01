package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.BaseEngineBlock;
import net.danygames2014.buildcraft.block.entity.*;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.client.render.block.entity.*;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.block.entity.BlockEntityRendererRegisterEvent;

public class BlockEntityRendererListener {
    @EventListener
    public void registerBlockEntityRenderers(BlockEntityRendererRegisterEvent event){
        event.renderers.put(ChuteBlockEntity.class, new ChuteBlockEntityRenderer());
        event.renderers.put(RedstoneEngineBlockEntity.class, new EngineBlockEntityRenderer(((BaseEngineBlock)Buildcraft.redstoneEngine).getBaseTexturePath(), ((BaseEngineBlock)Buildcraft.redstoneEngine).getChamberTexturePath(), ((BaseEngineBlock)Buildcraft.redstoneEngine).getTrunkTexturePath()));
        event.renderers.put(StirlingEngineBlockEntity.class, new EngineBlockEntityRenderer(((BaseEngineBlock)Buildcraft.stirlingEngine).getBaseTexturePath(), ((BaseEngineBlock)Buildcraft.stirlingEngine).getChamberTexturePath(), ((BaseEngineBlock)Buildcraft.stirlingEngine).getTrunkTexturePath()));
        event.renderers.put(CombustionEngineBlockEntity.class, new EngineBlockEntityRenderer(((BaseEngineBlock)Buildcraft.combustionEngine).getBaseTexturePath(), ((BaseEngineBlock)Buildcraft.combustionEngine).getChamberTexturePath(), ((BaseEngineBlock)Buildcraft.combustionEngine).getTrunkTexturePath()));
        event.renderers.put(CreativeEngineBlockEntity.class, new EngineBlockEntityRenderer(((BaseEngineBlock)Buildcraft.creativeEngine).getBaseTexturePath(), ((BaseEngineBlock)Buildcraft.creativeEngine).getChamberTexturePath(), ((BaseEngineBlock)Buildcraft.creativeEngine).getTrunkTexturePath()));
        event.renderers.put(TankBlockEntity.class, new TankBlockEntityRenderer());
        event.renderers.put(PipeBlockEntity.class, PipeBlockEntityRenderer.INSTANCE);
        event.renderers.put(LaserBlockEntity.class, LaserBlockEntityRenderer.INSTANCE);
        event.renderers.put(PathMarkerBlockEntity.class, new PathMarkerBlockEntityRenderer());
    }
}
