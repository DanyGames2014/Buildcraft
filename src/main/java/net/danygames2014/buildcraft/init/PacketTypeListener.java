package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.packet.clientbound.PipeUpdatePacket;
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
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("update_pipe"), PipeUpdatePacket.TYPE);
    }
}
