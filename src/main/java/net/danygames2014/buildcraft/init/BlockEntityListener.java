package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.block.entity.*;
import net.danygames2014.buildcraft.block.entity.pipe.DiamondPipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.ObsidianPipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PoweredPipeBlockEntity;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

public class BlockEntityListener {

    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerBlockEntities(BlockEntityRegisterEvent event) {
        event.register(NAMESPACE.id("chute").toString(), ChuteBlockEntity.class);
        event.register(NAMESPACE.id("golden_chute").toString(), GoldenChuteBlockEntity.class);
        event.register(NAMESPACE.id("autocrafting_table").toString(), AutocraftingTableBlockEntity.class);
        event.register(NAMESPACE.id("mining_well").toString(), MiningWellBlockEntity.class);
        event.register(NAMESPACE.id("quarry").toString(), QuarryBlockEntity.class);
        event.register(NAMESPACE.id("pump").toString(), PumpBlockEntity.class);
        event.register(NAMESPACE.id("flood_gate").toString(), FloodGateBlockEntity.class);
        event.register(NAMESPACE.id("tank").toString(), TankBlockEntity.class);
        event.register(NAMESPACE.id("refinery").toString(), RefineryBlockEntity.class);
        event.register(NAMESPACE.id("laser").toString(), LaserBlockEntity.class);
        event.register(NAMESPACE.id("assembly_table").toString(), AssemblyTableBlockEntity.class);
        event.register(NAMESPACE.id("integration_table").toString(), IntegrationTableBlockEntity.class);
        event.register(NAMESPACE.id("filler").toString(), FillerBlockEntity.class);
        event.register(NAMESPACE.id("builder").toString(), BuilderBlockEntity.class);
        event.register(NAMESPACE.id("architect_table").toString(), ArchitectTableBlockEntity.class);
        event.register(NAMESPACE.id("blueprint_library").toString(), BlueprintLibraryBlockEntity.class);
        event.register(NAMESPACE.id("land_marker").toString(), LandMarkerBlockEntity.class);
        event.register(NAMESPACE.id("path_marker").toString(), PathMarkerBlockEntity.class);
        event.register(NAMESPACE.id("redstone_engine").toString(), RedstoneEngineBlockEntity.class);
        event.register(NAMESPACE.id("stirling_engine").toString(), StirlingEngineBlockEntity.class);
        event.register(NAMESPACE.id("combustion_engine").toString(), CombustionEngineBlockEntity.class);
        event.register(NAMESPACE.id("creative_engine").toString(), CreativeEngineBlockEntity.class);
        event.register(NAMESPACE.id("pipe").toString(), PipeBlockEntity.class);
        event.register(NAMESPACE.id("diamond_pipe").toString(), DiamondPipeBlockEntity.class);
        event.register(NAMESPACE.id("powered_pipe").toString(), PoweredPipeBlockEntity.class);
        event.register(NAMESPACE.id("obsidian_pipe").toString(), ObsidianPipeBlockEntity.class);
    }
}
