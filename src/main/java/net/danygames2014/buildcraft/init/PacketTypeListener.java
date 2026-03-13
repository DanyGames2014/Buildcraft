package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.client.render.PipeRenderState;
import net.danygames2014.buildcraft.client.render.block.PipePluggableState;
import net.danygames2014.buildcraft.packet.*;
import net.danygames2014.buildcraft.packet.clientbound.BlockEntityStateUpdatePacket;
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
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("update_pipe"), BlockEntityStateUpdatePacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("update_blockentity"), BlockEntityUpdatePacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("select_assembly_recipe"), SelectAssemblyRecipeC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("assembly_table_recipes_update"), AssemblyTableUpdateS2CPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("integration_table_preview"), IntegrationTablePreviewS2CPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("command"), CommandPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("toggle_diamond_pipe_filter"), ToggleDiamondPipeFilterC2SPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("architect_table_name_field"), ArchitectTableNameFieldPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("builder_command"), BuilderCommandPacket.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("fluid_update"), PacketFluidUpdate.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("power_update"), PacketPowerUpdate.TYPE);
        Registry.register(PacketTypeRegistry.INSTANCE, NAMESPACE.id("blueprint_library_blueprint"), BlueprintLibraryBlueprintPacket.TYPE);

        StateRegistry.register(PipeRenderState.class);
        StateRegistry.register(PipePluggableState.class);
    }
}
