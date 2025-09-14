package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.block.entity.ChuteBlockEntity;
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
    }
}
