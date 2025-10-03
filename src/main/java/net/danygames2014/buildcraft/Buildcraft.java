package net.danygames2014.buildcraft;

import net.danygames2014.buildcraft.api.transport.statement.ActionInternal;
import net.danygames2014.buildcraft.api.transport.statement.TriggerInternal;
import net.danygames2014.buildcraft.block.*;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeWire;
import net.danygames2014.buildcraft.block.entity.pipe.PoweredPipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.behavior.*;
import net.danygames2014.buildcraft.block.entity.pipe.statement.ActionSignalOutput;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.EnergyPipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.FluidPipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter;
import net.danygames2014.buildcraft.block.material.PipeMaterial;
import net.danygames2014.buildcraft.item.*;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.Logger;

public class Buildcraft {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @Entrypoint.Logger
    public static Logger LOGGER;

    public static Item wrench;
    public static Item woodenGear;
    public static Item stoneGear;
    public static Item ironGear;
    public static Item goldGear;
    public static Item diamondGear;
    public static Item template;
    public static Item blueprint;
    public static Item pipeWaterproof;

    public static Item redPipeWire;
    public static Item bluePipeWire;
    public static Item greenPipeWire;
    public static Item yellowPipeWire;
    
    public static Item redstoneChipset; // NYI
    public static Item redstoneIronChipset; // NYI
    public static Item redstoneGoldenChipset; // NYI
    public static Item redstoneDiamondChipset; // NYI
    public static Item pulsatingChipset; // NYI

    public static Item plug;
    public static Item facade;

    public static Item gateItem;

    public static Material pipeMaterial;

    public static Block chuteBlock;
    public static Block autoWorkbench; // NYI
    public static Block miningWell; // NYI
    public static Block quarry; // NYI
    public static Block pump; // NYI
    public static Block tank;
    public static Block refinery; // NYI
    public static Block laser; // NYI
    public static Block assemblyTable; // NYI
    public static Block filler; // NYI
    public static Block builder; // NYI
    public static Block architectTable; // NYI
    public static Block blueprintLibrary; // NYI
    public static Block landMarker; // NYI
    public static Block pathMarker; // NYI
    public static Block redstoneEngine;
    public static Block stirlingEngine;
    public static Block combustionEngine;
    public static Block creativeEngine;

    public static Block frame;

    public static WoodenPipeBehavior woodenPipeBehavior;
    public static CobblestonePipeBehavior cobblestonePipeBehavior;
    public static StonePipeBehavior stonePipeBehavior;
    public static SandstonePipeBehavior sandstonePipeBehavior;
    public static GoldenPipeBehavior goldenPipeBehavior;
    
    public static Block woodenItemPipe;
    public static Block cobblestoneItemPipe;
    public static Block stoneItemPipe;
    public static Block sandstoneItemPipe;
    public static Block goldenItemPipe;
    
    public static Block woodenFluidPipe;
    public static Block cobblestoneFluidPipe;
    public static Block stoneFluidPipe;
    public static Block sandstoneFluidPipe;
    public static Block goldenFluidPipe;
    
    public static Block woodenEnergyPipe;
    public static Block cobblestoneEnergyPipe;
    public static Block stoneEnergyPipe;
    public static Block sandstoneEnergyPipe;
    public static Block goldenEnergyPipe;

    //TODO: figure out how to avoid registering the renderblock
    public static RenderBlock renderBlock;

    public static TriggerInternal[] triggerPipeWireActive = new TriggerInternal[PipeWire.values().length];
    public static TriggerInternal[] triggerPipeWireInactive = new TriggerInternal[PipeWire.values().length];
    public static ActionInternal[] actionPipeWire = new ActionSignalOutput[PipeWire.values().length];

    @EventListener
    public void registerItems(ItemRegistryEvent event) {
        wrench = new BuildcraftWrenchItem(NAMESPACE.id("wrench")).setTranslationKey(NAMESPACE, "wrench");
        woodenGear = new TemplateItem(NAMESPACE.id("wooden_gear")).setTranslationKey(NAMESPACE, "wooden_gear");
        stoneGear = new TemplateItem(NAMESPACE.id("stone_gear")).setTranslationKey(NAMESPACE, "stone_gear");
        ironGear = new TemplateItem(NAMESPACE.id("iron_gear")).setTranslationKey(NAMESPACE, "iron_gear");
        goldGear = new TemplateItem(NAMESPACE.id("golden_gear")).setTranslationKey(NAMESPACE, "golden_gear");
        diamondGear = new TemplateItem(NAMESPACE.id("diamond_gear")).setTranslationKey(NAMESPACE, "diamond_gear");
        template = new BuilderTemplateItem(NAMESPACE.id("template")).setTranslationKey(NAMESPACE, "template");
        blueprint = new BuilderTemplateItem(NAMESPACE.id("blueprint")).setTranslationKey(NAMESPACE, "blueprint");
        pipeWaterproof = new TemplateItem(NAMESPACE.id("pipe_waterproof")).setTranslationKey(NAMESPACE, "pipe_waterproof");
        
        redPipeWire = new PipeWireItem(NAMESPACE.id("red_pipe_wire")).setTranslationKey(NAMESPACE, "red_pipe_wire");
        bluePipeWire = new PipeWireItem(NAMESPACE.id("blue_pipe_wire")).setTranslationKey(NAMESPACE, "blue_pipe_wire");
        greenPipeWire = new PipeWireItem(NAMESPACE.id("green_pipe_wire")).setTranslationKey(NAMESPACE, "green_pipe_wire");
        yellowPipeWire = new PipeWireItem(NAMESPACE.id("yellow_pipe_wire")).setTranslationKey(NAMESPACE, "yellow_pipe_wire");

        redstoneChipset = new TemplateItem(NAMESPACE.id("redstone_chipset")).setTranslationKey(NAMESPACE, "redstone_chipset");
        redstoneIronChipset = new TemplateItem(NAMESPACE.id("redstone_iron_chipset")).setTranslationKey(NAMESPACE, "redstone_iron_chipset");
        redstoneGoldenChipset = new TemplateItem(NAMESPACE.id("redstone_golden_chipset")).setTranslationKey(NAMESPACE, "redstone_golden_chipset");
        redstoneDiamondChipset = new TemplateItem(NAMESPACE.id("redstone_diamond_chipset")).setTranslationKey(NAMESPACE, "redstone_diamond_chipset");
        pulsatingChipset = new TemplateItem(NAMESPACE.id("pulsating_chipset")).setTranslationKey(NAMESPACE, "pulsating_chipset");

        plug = new PlugItem(NAMESPACE.id("plug")).setTranslationKey(NAMESPACE, "plug");
        facade = new FacadeItem(NAMESPACE.id("facade")).setTranslationKey(NAMESPACE, "facade");

        gateItem = new GateItem(NAMESPACE.id("gate"));
    }

    @EventListener
    public void registerBlocks(BlockRegistryEvent event) {
        pipeMaterial = new PipeMaterial(MapColor.LIGHT_GRAY);

        woodenPipeBehavior = new WoodenPipeBehavior();
        cobblestonePipeBehavior = new CobblestonePipeBehavior();
        stonePipeBehavior = new StonePipeBehavior();
        sandstonePipeBehavior = new SandstonePipeBehavior();
        goldenPipeBehavior = new GoldenPipeBehavior();

        chuteBlock = new ChuteBlock(NAMESPACE.id("chute")).setTranslationKey(NAMESPACE, "chute").setHardness(3.0F).setSoundGroup(Block.METAL_SOUND_GROUP);
        autoWorkbench = new AutocraftingTableBlock(NAMESPACE.id("autocrafting_table"), Material.WOOD).setTranslationKey(NAMESPACE, "auto_workbench").setHardness(2.5F);
        miningWell = new MiningWellBlock(NAMESPACE.id("mining_well"), Material.METAL).setTranslationKey(NAMESPACE, "mining_well").setHardness(3.0F).setSoundGroup(Block.METAL_SOUND_GROUP);
        quarry = new QuarryBlock(NAMESPACE.id("quarry"), Material.METAL).setTranslationKey(NAMESPACE, "quarry").setHardness(3.0F).setSoundGroup(Block.METAL_SOUND_GROUP);
        pump = new PumpBlock(NAMESPACE.id("pump"), Material.METAL).setTranslationKey(NAMESPACE, "pump").setHardness(3.0F).setSoundGroup(Block.METAL_SOUND_GROUP);
        tank = new TankBlock(NAMESPACE.id("tank")).setTranslationKey(NAMESPACE, "tank").setHardness(0.5F);
        refinery = new RefineryBlock(NAMESPACE.id("refinery"), Material.METAL).setTranslationKey(NAMESPACE, "refinery").setHardness(3.0F);
        laser = new LaserBlock(NAMESPACE.id("laser"), Material.METAL).setTranslationKey(NAMESPACE, "laser").setHardness(3.0F).setSoundGroup(Block.METAL_SOUND_GROUP);
        assemblyTable = new AssemblyTableBlock(NAMESPACE.id("assembly_table"), Material.METAL).setTranslationKey(NAMESPACE, "assembly_table").setHardness(3.0F).setSoundGroup(Block.METAL_SOUND_GROUP);
        filler = new FillerBlock(NAMESPACE.id("filler"), Material.METAL).setTranslationKey(NAMESPACE, "filler").setHardness(3.0F).setSoundGroup(Block.METAL_SOUND_GROUP);
        builder = new BuilderBlock(NAMESPACE.id("builder"), Material.METAL).setTranslationKey(NAMESPACE, "builder").setHardness(3.0F).setSoundGroup(Block.METAL_SOUND_GROUP);
        architectTable = new ArchitectTableBlock(NAMESPACE.id("architect_table"), Material.METAL).setTranslationKey(NAMESPACE, "architect_table").setHardness(3.0F).setSoundGroup(Block.METAL_SOUND_GROUP);
        blueprintLibrary = new BlueprintLibraryBlock(NAMESPACE.id("blueprint_library"), Material.METAL).setTranslationKey(NAMESPACE, "blueprint_library").setHardness(3.0F).setSoundGroup(Block.METAL_SOUND_GROUP);
        landMarker = new LandMarkerBlock(NAMESPACE.id("land_marker"), Material.PISTON_BREAKABLE).setTranslationKey(NAMESPACE, "land_marker");
        pathMarker = new PathMarkerBlock(NAMESPACE.id("path_marker"), Material.PISTON_BREAKABLE).setTranslationKey(NAMESPACE, "path_marker");
        redstoneEngine = new RedstoneEngineBlock(NAMESPACE.id("redstone_engine")).setTranslationKey(NAMESPACE, "redstone_engine").setHardness(0.7F).setSoundGroup(Block.WOOD_SOUND_GROUP);
        stirlingEngine = new StirlingEngineBlock(NAMESPACE.id("stirling_engine")).setTranslationKey(NAMESPACE, "stirling_engine").setHardness(1.0F).setSoundGroup(Block.STONE_SOUND_GROUP);
        combustionEngine = new CombustionEngineBlock(NAMESPACE.id("combustion_engine")).setTranslationKey(NAMESPACE, "combustion_engine").setHardness(1.2F).setSoundGroup(Block.METAL_SOUND_GROUP);
        creativeEngine = new CreativeEngineBlock(NAMESPACE.id("creative_engine")).setTranslationKey(NAMESPACE, "creative_engine").setHardness(1.2F).setSoundGroup(Block.METAL_SOUND_GROUP);

        frame = new FrameBlock(NAMESPACE.id("frame"), pipeMaterial).setTranslationKey(NAMESPACE, "frame").setHardness(0.1F).setSoundGroup(Block.METAL_SOUND_GROUP);

        renderBlock = new RenderBlock(NAMESPACE.id("render_block"));

        // Item Pipes
        woodenItemPipe = new PipeBlock(
                NAMESPACE.id("wooden_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/wooden_item_pipe"),
                NAMESPACE.id("block/pipe/wooden_item_pipe_alternative"),
                woodenPipeBehavior,
                ItemPipeTransporter::new,
                PoweredPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "wooden_item_pipe").setHardness(0.1F).setSoundGroup(Block.WOOD_SOUND_GROUP);

        cobblestoneItemPipe = new PipeBlock(
                NAMESPACE.id("cobblestone_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/cobblestone_item_pipe"),
                null,
                cobblestonePipeBehavior,
                ItemPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "cobblestone_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        stoneItemPipe = new PipeBlock(
                NAMESPACE.id("stone_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/stone_item_pipe"),
                null,
                stonePipeBehavior,
                ItemPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "stone_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        sandstoneItemPipe = new PipeBlock(
                NAMESPACE.id("sandstone_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/sandstone_item_pipe"),
                null,
                sandstonePipeBehavior,
                ItemPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "sandstone_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);
        
        goldenItemPipe = new PipeBlock(
                NAMESPACE.id("golden_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/golden_item_pipe"),
                null,
                goldenPipeBehavior,
                ItemPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "golden_item_pipe").setHardness(0.1F).setSoundGroup(Block.METAL_SOUND_GROUP);

        // Fluid Pipes
        woodenFluidPipe = new PipeBlock(
                NAMESPACE.id("wooden_fluid_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/wooden_fluid_pipe"),
                null,
                woodenPipeBehavior,
                FluidPipeTransporter::new,
                PoweredPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "wooden_fluid_pipe").setHardness(0.1F).setSoundGroup(Block.WOOD_SOUND_GROUP);

        cobblestoneFluidPipe = new PipeBlock(
                NAMESPACE.id("cobblestone_fluid_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/cobblestone_fluid_pipe"),
                null,
                cobblestonePipeBehavior,
                FluidPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "cobblestone_fluid_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        stoneFluidPipe = new PipeBlock(
                NAMESPACE.id("stone_fluid_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/stone_fluid_pipe"),
                null,
                stonePipeBehavior,
                FluidPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "stone_fluid_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        sandstoneFluidPipe = new PipeBlock(
                NAMESPACE.id("sandstone_fluid_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/sandstone_fluid_pipe"),
                null,
                sandstonePipeBehavior,
                FluidPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "sandstone_fluid_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        goldenFluidPipe = new PipeBlock(
                NAMESPACE.id("golden_fluid_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/golden_fluid_pipe"),
                null,
                goldenPipeBehavior,
                FluidPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "golden_fluid_pipe").setHardness(0.1F).setSoundGroup(Block.METAL_SOUND_GROUP);

        // Energy Pipes
        woodenEnergyPipe = new PipeBlock(
                NAMESPACE.id("wooden_energy_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/wooden_energy_pipe"),
                null,
                woodenPipeBehavior,
                EnergyPipeTransporter::new,
                PoweredPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "wooden_energy_pipe").setHardness(0.1F).setSoundGroup(Block.WOOD_SOUND_GROUP);

        cobblestoneEnergyPipe = new PipeBlock(
                NAMESPACE.id("cobblestone_energy_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/cobblestone_energy_pipe"),
                null,
                cobblestonePipeBehavior,
                EnergyPipeTransporter::new,
                PoweredPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "cobblestone_energy_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        stoneEnergyPipe = new PipeBlock(
                NAMESPACE.id("stone_energy_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/stone_energy_pipe"),
                null,
                stonePipeBehavior,
                EnergyPipeTransporter::new,
                PoweredPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "stone_energy_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        sandstoneEnergyPipe = new PipeBlock(
                NAMESPACE.id("sandstone_energy_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/sandstone_energy_pipe"),
                null,
                sandstonePipeBehavior,
                EnergyPipeTransporter::new,
                PoweredPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "sandstone_energy_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        goldenEnergyPipe = new PipeBlock(
                NAMESPACE.id("golden_energy_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/golden_energy_pipe"),
                null,
                goldenPipeBehavior,
                EnergyPipeTransporter::new,
                PoweredPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "golden_energy_pipe").setHardness(0.1F).setSoundGroup(Block.METAL_SOUND_GROUP);
    }
}
