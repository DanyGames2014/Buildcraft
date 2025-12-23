package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.client.render.PipeRenderState;
import net.danygames2014.buildcraft.client.render.block.PipePluggableState;
import net.danygames2014.buildcraft.packet.AssemblyTableUpdateS2CPacket;
import net.danygames2014.buildcraft.packet.CommandPacket;
import net.danygames2014.buildcraft.packet.SelectAssemblyRecipeC2SPacket;
import net.danygames2014.buildcraft.packet.ToggleDiamondPipeFilterC2SPacket;
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
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("select_assembly_recipe"), SelectAssemblyRecipeC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("assembly_table_recipes_update"), AssemblyTableUpdateS2CPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("command"), CommandPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("toggle_diamond_pipe_filter"), ToggleDiamondPipeFilterC2SPacket.TYPE);

        StateRegistry.register(PipeRenderState.class);
        StateRegistry.register(PipePluggableState.class);
    }
}
