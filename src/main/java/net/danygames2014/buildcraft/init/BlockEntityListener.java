package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.block.entity.*;
import net.danygames2014.buildcraft.block.entity.TankBlockEntity;
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
    public void registerBlockEntities(BlockEntityRegisterEvent event){
        event.register(ChuteBlockEntity.class, NAMESPACE.id("chute").toString());
        event.register(AutocraftingTableBlockEntity.class, NAMESPACE.id("autocrafting_table").toString());
        event.register(MiningWellBlockEntity.class, NAMESPACE.id("mining_well").toString());
        event.register(QuarryBlockEntity.class, NAMESPACE.id("quarry").toString());
        event.register(PumpBlockEntity.class, NAMESPACE.id("pump").toString());
        event.register(TankBlockEntity.class, NAMESPACE.id("tank").toString());
        event.register(RefineryBlockEntity.class, NAMESPACE.id("refinery").toString());
        event.register(LaserBlockEntity.class, NAMESPACE.id("laser").toString());
        event.register(AssemblyTableBlockEntity.class, NAMESPACE.id("assembly_table").toString());
        event.register(FillerBlockEntity.class, NAMESPACE.id("filler").toString());
        event.register(BuilderBlockEntity.class, NAMESPACE.id("builder").toString());
        event.register(ArchitectTableBlockEntity.class, NAMESPACE.id("architect_table").toString());
        event.register(BlueprintLibraryBlockEntity.class, NAMESPACE.id("blueprint_library").toString());
        event.register(LandMarkerBlockEntity.class, NAMESPACE.id("land_marker").toString());
        event.register(PathMarkerBlockEntity.class, NAMESPACE.id("path_marker").toString());
        event.register(RedstoneEngineBlockEntity.class, NAMESPACE.id("redstone_engine").toString());
        event.register(StirlingEngineBlockEntity.class, NAMESPACE.id("stirling_engine").toString());
        event.register(CombustionEngineBlockEntity.class, NAMESPACE.id("combustion_engine").toString());
        event.register(CreativeEngineBlockEntity.class, NAMESPACE.id("creative_engine").toString());
        event.register(PipeBlockEntity.class, NAMESPACE.id("pipe").toString());
        event.register(DiamondPipeBlockEntity.class, NAMESPACE.id("diamond_pipe").toString());
        event.register(PoweredPipeBlockEntity.class, NAMESPACE.id("powered_pipe").toString());
        event.register(ObsidianPipeBlockEntity.class, NAMESPACE.id("obsidian_pipe").toString());
    }
}
