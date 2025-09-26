package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.client.render.PipeRenderState;
import net.danygames2014.buildcraft.client.render.block.PipePluggableState;
import net.danygames2014.buildcraft.packet.clientbound.BlockEntityUpdatePacket;
import net.danygames2014.buildcraft.registry.StateRegistry;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.network.packet.PacketRegisterEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.registry.PacketTypeRegistry;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.Namespace;


public class PacketTypeListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerPacketTypes(PacketRegisterEvent event){
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("update_pipe"), BlockEntityUpdatePacket.TYPE);

        StateRegistry.register(PipeRenderState.class);
        StateRegistry.register(PipePluggableState.class);
    }
}
