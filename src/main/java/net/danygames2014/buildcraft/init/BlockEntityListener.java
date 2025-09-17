package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.block.entity.ChuteBlockEntity;
import net.danygames2014.buildcraft.block.entity.CombustionEngineBlockEntity;
import net.danygames2014.buildcraft.block.entity.RedstoneEngineBlockEntity;
import net.danygames2014.buildcraft.block.entity.StirlingEngineBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

public class BlockEntityListener {

    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerBlockEntities(BlockEntityRegisterEvent event){
        event.register(ChuteBlockEntity.class, NAMESPACE.id("chute_block_entity").toString());
        event.register(RedstoneEngineBlockEntity.class, NAMESPACE.id("redstone_engine").toString());
        event.register(StirlingEngineBlockEntity.class, NAMESPACE.id("stirling_engine").toString());
        event.register(CombustionEngineBlockEntity.class, NAMESPACE.id("combustion_engine").toString());
        event.register(PipeBlockEntity.class, NAMESPACE.id("pipe").toString());
    }
}
