package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.block.entity.*;
import net.danygames2014.buildcraft.block.entity.TankBlockEntity;
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
        event.register(ChuteBlockEntity.class, NAMESPACE.id("chute").toString());
        event.register(RedstoneEngineBlockEntity.class, NAMESPACE.id("redstone_engine").toString());
        event.register(StirlingEngineBlockEntity.class, NAMESPACE.id("stirling_engine").toString());
        event.register(CombustionEngineBlockEntity.class, NAMESPACE.id("combustion_engine").toString());
        event.register(TankBlockEntity.class, NAMESPACE.id("tank").toString());
        event.register(PipeBlockEntity.class, NAMESPACE.id("pipe").toString());
    }
}
