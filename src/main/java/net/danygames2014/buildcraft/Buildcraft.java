package net.danygames2014.buildcraft;

import net.danygames2014.buildcraft.api.transport.gate.GateExpansions;
import net.danygames2014.buildcraft.api.transport.statement.*;
import net.danygames2014.buildcraft.block.*;
import net.danygames2014.buildcraft.block.entity.pipe.*;
import net.danygames2014.buildcraft.block.entity.pipe.behavior.*;
import net.danygames2014.buildcraft.block.entity.pipe.gate.expansion.GateExpansionPulsar;
import net.danygames2014.buildcraft.block.entity.pipe.gate.expansion.GateExpansionRedstoneFader;
import net.danygames2014.buildcraft.block.entity.pipe.gate.expansion.GateExpansionTimer;
import net.danygames2014.buildcraft.block.entity.pipe.parameter.ActionParameterSignal;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.EnergyPipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.FluidPipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter;
import net.danygames2014.buildcraft.block.material.PipeMaterial;
import net.danygames2014.buildcraft.item.*;
import net.danygames2014.buildcraft.statements.*;
import net.danygames2014.buildcraft.util.ColorUtil;
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
    public static Item pipeSealant;

    public static Item redPipeWire;
    public static Item bluePipeWire;
    public static Item greenPipeWire;
    public static Item yellowPipeWire;

    public static Item redstoneChipset;
    public static Item redstoneIronChipset;
    public static Item redstoneGoldenChipset;
    public static Item redstoneDiamondChipset;
    public static Item pulsatingChipset;

    public static Item paintbrush;
    public static Item[] paintbrushes = new Item[16];

    public static Item[] lens = new Item[16];
    public static Item[] filter = new Item[16];

    public static Item plug;
    public static Item facade;

    public static Item gateItem;

    public static Material pipeMaterial;

    public static Block chuteBlock;
    public static Block autoWorkbench;
    public static Block miningWell;
    public static Block quarry; // NYI
    public static Block pump; // NYI
    public static Block tank;
    public static Block refinery; // NYI
    public static Block laser;
    public static Block assemblyTable;
    public static Block filler; // NYI
    public static Block builder; // NYI
    public static Block architectTable; // NYI
    public static Block blueprintLibrary; // NYI
    public static Block landMarker;
    public static Block pathMarker;
    public static Block redstoneEngine;
    public static Block stirlingEngine;
    public static Block combustionEngine;
    public static Block creativeEngine;

    public static Block miningPipe;
    public static Block frame;

    public static WoodenPipeBehavior woodenPipeBehavior;
    public static CobblestonePipeBehavior cobblestonePipeBehavior;
    public static StonePipeBehavior stonePipeBehavior;
    public static IronPipeBehavior ironPipeBehavior;
    public static SandstonePipeBehavior sandstonePipeBehavior;
    public static GoldenPipeBehavior goldenPipeBehavior;
    public static DiamondPipeBehavior diamondPipeBehavior;
    public static ObsidianPipeBehavior obsidianPipeBehavior;
    public static ClayPipeBehavior clayPipeBehavior;
    public static VoidPipeBehavior voidPipeBehavior;

    public static Block woodenItemPipe;
    public static Block cobblestoneItemPipe;
    public static Block stoneItemPipe;
    public static Block ironItemPipe;
    public static Block sandstoneItemPipe;
    public static Block goldenItemPipe;
    public static Block diamondItemPipe;
    public static Block obsidianItemPipe;
    public static Block clayItemPipe;
    public static Block voidItemPipe;

    public static Block woodenFluidPipe;
    public static Block cobblestoneFluidPipe;
    public static Block stoneFluidPipe;
    public static Block ironFluidPipe;
    public static Block sandstoneFluidPipe;
    public static Block goldenFluidPipe;
    public static Block diamondFluidPipe;
    public static Block voidFluidPipe;

    public static Block woodenEnergyPipe;
    public static Block cobblestoneEnergyPipe;
    public static Block stoneEnergyPipe;
    public static Block ironEnergyPipe;
    public static Block sandstoneEnergyPipe;
    public static Block goldenEnergyPipe;
    public static Block diamondEnergyPipe;

    //TODO: figure out how to avoid registering the renderblock
    public static RenderBlock renderBlock;

    public static TriggerInternal triggerRedstoneActive;
    public static TriggerInternal triggerRedstoneInactive;
    public static TriggerInternal[] triggerPipeWireActive = new TriggerInternal[PipeWire.values().length];
    public static TriggerInternal[] triggerPipeWireInactive = new TriggerInternal[PipeWire.values().length];
    public static TriggerInternal[] triggerPipe = new TriggerInternal[TriggerPipeContents.PipeContents.values().length];
    public static TriggerInternal[] triggerTimer = new TriggerInternal[TriggerClockTimer.Time.VALUES.length];
    public static TriggerInternal[] triggerRedstoneLevel = new TriggerInternal[15];

    public static TriggerExternal triggerEmptyInventory;
    public static TriggerExternal triggerContainsInventory;
    public static TriggerExternal triggerSpaceInventory;
    public static TriggerExternal triggerFullInventory;
    public static TriggerExternal triggerInventoryBelow25;
    public static TriggerExternal triggerInventoryBelow50;
    public static TriggerExternal triggerInventoryBelow75;

    public static TriggerExternal triggerEmptyFluid;
    public static TriggerExternal triggerContainsFluid;
    public static TriggerExternal triggerSpaceFluid;
    public static TriggerExternal triggerFullFluid;
    public static TriggerExternal triggerFluidContainerBelow25;
    public static TriggerExternal triggerFluidContainerBelow50;
    public static TriggerExternal triggerFluidContainerBelow75;

    public static TriggerExternal triggerMachineActive;
    public static TriggerExternal triggerMachineInactive;

    public static ActionInternal[] actionPipeWire = new ActionSignalOutput[PipeWire.values().length];
    public static ActionInternal actionEnergyPulser;
    public static ActionInternal actionSingleEnergyPulse;
    public static ActionInternal actionRedstone;
    public static ActionInternal[] actionRedstoneLevel = new ActionInternal[15];

    @EventListener
    public void registerItems(ItemRegistryEvent event) {
        // Register triggers actions and parameters
        // TODO: maybe move to its own event?
        for (PipeWire wire : PipeWire.values()) {
            triggerPipeWireActive[wire.ordinal()] = new TriggerPipeSignal(true, wire);
            triggerPipeWireInactive[wire.ordinal()] = new TriggerPipeSignal(false, wire);
            actionPipeWire[wire.ordinal()] = new ActionSignalOutput(wire);
        }

        actionEnergyPulser = new ActionEnergyPulsar();
        actionSingleEnergyPulse = new ActionSingleEnergyPulse();
        actionRedstone = new ActionRedstoneOutput();

        for (TriggerPipeContents.PipeContents kind : TriggerPipeContents.PipeContents.values()) {
            triggerPipe[kind.ordinal()] = new TriggerPipeContents(kind);
        }

        for (TriggerClockTimer.Time time : TriggerClockTimer.Time.VALUES) {
            triggerTimer[time.ordinal()] = new TriggerClockTimer(time);
        }

        triggerRedstoneActive = new TriggerRedstoneInput(true);
        triggerRedstoneInactive = new TriggerRedstoneInput(false);

        for (int level = 0; level < triggerRedstoneLevel.length; level++) {
            triggerRedstoneLevel[level] = new TriggerRedstoneFaderInput(level + 1);
            actionRedstoneLevel[level] = new ActionRedstoneFaderOutput(level + 1);
        }

        triggerEmptyInventory = new TriggerInventory(TriggerInventory.State.Empty);
        triggerContainsInventory = new TriggerInventory(TriggerInventory.State.Contains);
        triggerSpaceInventory = new TriggerInventory(TriggerInventory.State.Space);
        triggerFullInventory = new TriggerInventory(TriggerInventory.State.Full);
        triggerInventoryBelow25 = new TriggerInventoryLevel(TriggerInventoryLevel.TriggerType.BELOW25);
        triggerInventoryBelow50 = new TriggerInventoryLevel(TriggerInventoryLevel.TriggerType.BELOW50);
        triggerInventoryBelow75 = new TriggerInventoryLevel(TriggerInventoryLevel.TriggerType.BELOW75);

        triggerEmptyFluid = new TriggerFluidContainer(TriggerFluidContainer.State.Empty);
        triggerContainsFluid = new TriggerFluidContainer(TriggerFluidContainer.State.Contains);
        triggerSpaceFluid = new TriggerFluidContainer(TriggerFluidContainer.State.Space);
        triggerFullFluid = new TriggerFluidContainer(TriggerFluidContainer.State.Full);
        triggerFluidContainerBelow25 = new TriggerFluidContainerLevel(TriggerFluidContainerLevel.TriggerType.BELOW25);
        triggerFluidContainerBelow50 = new TriggerFluidContainerLevel(TriggerFluidContainerLevel.TriggerType.BELOW50);
        triggerFluidContainerBelow75 = new TriggerFluidContainerLevel(TriggerFluidContainerLevel.TriggerType.BELOW75);

        triggerMachineActive = new TriggerMachine(true);
        triggerMachineInactive = new TriggerMachine(false);

        StatementManager.registerTriggerProvider(new PipeTriggerProvider());
        StatementManager.registerTriggerProvider(new DefaultTriggerProvider());

        StatementManager.registerActionProvider(new PipeActionProvider());
        StatementManager.registerActionProvider(new DefaultActionProvider());

        StatementManager.registerParameterClass(StatementParameterRedstoneGateSideOnly.class);
        StatementManager.registerParameterClass(StatementParameterItemStack.class);
        StatementManager.registerParameterClass(ActionParameterSignal.class);

        GateExpansions.registerExpansion(GateExpansionPulsar.INSTANCE);
        GateExpansions.registerExpansion(GateExpansionTimer.INSTANCE);
        GateExpansions.registerExpansion(GateExpansionRedstoneFader.INSTANCE);

        wrench = new BuildcraftWrenchItem(NAMESPACE.id("wrench")).setTranslationKey(NAMESPACE, "wrench");
        woodenGear = new TemplateItem(NAMESPACE.id("wooden_gear")).setTranslationKey(NAMESPACE, "wooden_gear");
        stoneGear = new TemplateItem(NAMESPACE.id("stone_gear")).setTranslationKey(NAMESPACE, "stone_gear");
        ironGear = new TemplateItem(NAMESPACE.id("iron_gear")).setTranslationKey(NAMESPACE, "iron_gear");
        goldGear = new TemplateItem(NAMESPACE.id("golden_gear")).setTranslationKey(NAMESPACE, "golden_gear");
        diamondGear = new TemplateItem(NAMESPACE.id("diamond_gear")).setTranslationKey(NAMESPACE, "diamond_gear");
        template = new BuilderTemplateItem(NAMESPACE.id("template")).setTranslationKey(NAMESPACE, "template");
        blueprint = new BuilderBlueprintItem(NAMESPACE.id("blueprint")).setTranslationKey(NAMESPACE, "blueprint");
        pipeSealant = new TemplateItem(NAMESPACE.id("pipe_sealant")).setTranslationKey(NAMESPACE, "pipe_sealant");

        redPipeWire = new PipeWireItem(NAMESPACE.id("red_pipe_wire")).setTranslationKey(NAMESPACE, "red_pipe_wire");
        bluePipeWire = new PipeWireItem(NAMESPACE.id("blue_pipe_wire")).setTranslationKey(NAMESPACE, "blue_pipe_wire");
        greenPipeWire = new PipeWireItem(NAMESPACE.id("green_pipe_wire")).setTranslationKey(NAMESPACE, "green_pipe_wire");
        yellowPipeWire = new PipeWireItem(NAMESPACE.id("yellow_pipe_wire")).setTranslationKey(NAMESPACE, "yellow_pipe_wire");

        redstoneChipset = new TemplateItem(NAMESPACE.id("redstone_chipset")).setTranslationKey(NAMESPACE, "redstone_chipset");
        redstoneIronChipset = new TemplateItem(NAMESPACE.id("redstone_iron_chipset")).setTranslationKey(NAMESPACE, "redstone_iron_chipset");
        redstoneGoldenChipset = new TemplateItem(NAMESPACE.id("redstone_golden_chipset")).setTranslationKey(NAMESPACE, "redstone_golden_chipset");
        redstoneDiamondChipset = new TemplateItem(NAMESPACE.id("redstone_diamond_chipset")).setTranslationKey(NAMESPACE, "redstone_diamond_chipset");
        pulsatingChipset = new TemplateItem(NAMESPACE.id("pulsating_chipset")).setTranslationKey(NAMESPACE, "pulsating_chipset");

        paintbrush = new PaintBrushItem(NAMESPACE.id("clean_paintbrush"), -1).setTranslationKey(NAMESPACE, "clean_paintbrush");
        for (int i = 0; i < ColorUtil.colors.length; i++) {
            paintbrushes[i] = new PaintBrushItem(i).setTranslationKey(NAMESPACE, ColorUtil.getName(i) + "_paintbrush");
        }

        for (int i = 0; i < ColorUtil.colors.length; i++) {
            lens[i] = new LensItem(i, false).setTranslationKey(NAMESPACE, ColorUtil.getName(i) + "_lens");
        }
        for (int i = 0; i < ColorUtil.colors.length; i++) {
            filter[i] = new LensItem(i, true).setTranslationKey(NAMESPACE, ColorUtil.getName(i) + "_filter");
        }

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
        ironPipeBehavior = new IronPipeBehavior();
        sandstonePipeBehavior = new SandstonePipeBehavior();
        goldenPipeBehavior = new GoldenPipeBehavior();
        diamondPipeBehavior = new DiamondPipeBehavior();
        obsidianPipeBehavior = new ObsidianPipeBehavior();
        clayPipeBehavior = new ClayPipeBehavior();
        voidPipeBehavior = new VoidPipeBehavior();

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

        miningPipe = new MiningPipeBlock(NAMESPACE.id("mining_pipe"), pipeMaterial).setTranslationKey(NAMESPACE, "mining_pipe").setHardness(0.1F).setSoundGroup(Block.METAL_SOUND_GROUP);
        frame = new FrameBlock(NAMESPACE.id("frame"), pipeMaterial).setTranslationKey(NAMESPACE, "frame").setHardness(0.1F).setSoundGroup(Block.METAL_SOUND_GROUP);

        renderBlock = new RenderBlock(NAMESPACE.id("render_block"));

        // Item Pipes
        woodenItemPipe = new PipeBlock(
                NAMESPACE.id("wooden_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/wooden_item_pipe"),
                NAMESPACE.id("block/pipe/wooden_item_pipe_alternative"),
                PipeType.ITEM,
                woodenPipeBehavior,
                ItemPipeTransporter::new,
                PoweredPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "wooden_item_pipe").setHardness(0.1F).setSoundGroup(Block.WOOD_SOUND_GROUP);

        cobblestoneItemPipe = new PipeBlock(
                NAMESPACE.id("cobblestone_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/cobblestone_item_pipe"),
                null,
                PipeType.ITEM,
                cobblestonePipeBehavior,
                ItemPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "cobblestone_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        stoneItemPipe = new PipeBlock(
                NAMESPACE.id("stone_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/stone_item_pipe"),
                null,
                PipeType.ITEM,
                stonePipeBehavior,
                ItemPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "stone_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        ironItemPipe = new PipeBlock(
                NAMESPACE.id("iron_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/iron_item_pipe"),
                NAMESPACE.id("block/pipe/iron_item_pipe_alternative"),
                PipeType.ITEM,
                ironPipeBehavior,
                ItemPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "iron_item_pipe").setHardness(0.1F).setSoundGroup(Block.METAL_SOUND_GROUP);

        sandstoneItemPipe = new PipeBlock(
                NAMESPACE.id("sandstone_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/sandstone_item_pipe"),
                null,
                PipeType.ITEM,
                sandstonePipeBehavior,
                ItemPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "sandstone_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        goldenItemPipe = new PipeBlock(
                NAMESPACE.id("golden_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/golden_item_pipe"),
                null,
                PipeType.ITEM,
                goldenPipeBehavior,
                ItemPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "golden_item_pipe").setHardness(0.1F).setSoundGroup(Block.METAL_SOUND_GROUP);

        diamondItemPipe = new DiamondPipeBlock(
                NAMESPACE.id("diamond_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/diamond_item_pipe"),
                null,
                PipeType.ITEM,
                diamondPipeBehavior,
                ItemPipeTransporter::new,
                DiamondPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "diamond_item_pipe").setHardness(0.1F).setSoundGroup(Block.METAL_SOUND_GROUP);

        obsidianItemPipe = new PipeBlock(
                NAMESPACE.id("obsidian_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/obsidian_item_pipe"),
                null,
                PipeType.ITEM,
                obsidianPipeBehavior,
                ItemPipeTransporter::new,
                ObsidianPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "obsidian_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        clayItemPipe = new PipeBlock(
                NAMESPACE.id("clay_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/clay_item_pipe"),
                null,
                PipeType.ITEM,
                clayPipeBehavior,
                ItemPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "clay_item_pipe").setHardness(0.1F).setSoundGroup(Block.DIRT_SOUND_GROUP);

        voidItemPipe = new PipeBlock(
                NAMESPACE.id("void_item_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/void_item_pipe"),
                null,
                PipeType.ITEM,
                voidPipeBehavior,
                ItemPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "void_item_pipe").setHardness(0.1F).setSoundGroup(Block.METAL_SOUND_GROUP);

        // Fluid Pipes
        woodenFluidPipe = new PipeBlock(
                NAMESPACE.id("wooden_fluid_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/wooden_fluid_pipe"),
                NAMESPACE.id("block/pipe/wooden_fluid_pipe_alternative"),
                PipeType.FLUID,
                woodenPipeBehavior,
                FluidPipeTransporter::new,
                PoweredPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "wooden_fluid_pipe").setHardness(0.1F).setSoundGroup(Block.WOOD_SOUND_GROUP);

        cobblestoneFluidPipe = new PipeBlock(
                NAMESPACE.id("cobblestone_fluid_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/cobblestone_fluid_pipe"),
                null,
                PipeType.FLUID,
                cobblestonePipeBehavior,
                FluidPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "cobblestone_fluid_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        stoneFluidPipe = new PipeBlock(
                NAMESPACE.id("stone_fluid_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/stone_fluid_pipe"),
                null,
                PipeType.FLUID,
                stonePipeBehavior,
                FluidPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "stone_fluid_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        ironFluidPipe = new PipeBlock(
                NAMESPACE.id("iron_fluid_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/iron_fluid_pipe"),
                NAMESPACE.id("block/pipe/iron_fluid_pipe_alternative"),
                PipeType.FLUID,
                ironPipeBehavior,
                FluidPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "iron_fluid_pipe").setHardness(0.1F).setSoundGroup(Block.METAL_SOUND_GROUP);

        sandstoneFluidPipe = new PipeBlock(
                NAMESPACE.id("sandstone_fluid_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/sandstone_fluid_pipe"),
                null,
                PipeType.FLUID,
                sandstonePipeBehavior,
                FluidPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "sandstone_fluid_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        goldenFluidPipe = new PipeBlock(
                NAMESPACE.id("golden_fluid_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/golden_fluid_pipe"),
                null,
                PipeType.FLUID,
                goldenPipeBehavior,
                FluidPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "golden_fluid_pipe").setHardness(0.1F).setSoundGroup(Block.METAL_SOUND_GROUP);

        diamondFluidPipe = new DiamondPipeBlock(
                NAMESPACE.id("diamond_fluid_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/diamond_fluid_pipe"),
                null,
                PipeType.FLUID,
                diamondPipeBehavior,
                FluidPipeTransporter::new,
                DiamondPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "diamond_fluid_pipe").setHardness(0.1F).setSoundGroup(Block.METAL_SOUND_GROUP);

        voidFluidPipe = new PipeBlock(
                NAMESPACE.id("void_fluid_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/void_fluid_pipe"),
                null,
                PipeType.FLUID,
                voidPipeBehavior,
                FluidPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "void_fluid_pipe").setHardness(0.1F).setSoundGroup(Block.METAL_SOUND_GROUP);

        // Energy Pipes
        woodenEnergyPipe = new PipeBlock(
                NAMESPACE.id("wooden_energy_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/wooden_energy_pipe"),
                NAMESPACE.id("block/pipe/wooden_energy_pipe_alternative"),
                PipeType.ENERGY,
                woodenPipeBehavior,
                EnergyPipeTransporter::new,
                PoweredPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "wooden_energy_pipe").setHardness(0.1F).setSoundGroup(Block.WOOD_SOUND_GROUP);

        cobblestoneEnergyPipe = new PipeBlock(
                NAMESPACE.id("cobblestone_energy_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/cobblestone_energy_pipe"),
                null,
                PipeType.ENERGY,
                cobblestonePipeBehavior,
                EnergyPipeTransporter::new,
                PoweredPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "cobblestone_energy_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        stoneEnergyPipe = new PipeBlock(
                NAMESPACE.id("stone_energy_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/stone_energy_pipe"),
                null,
                PipeType.ENERGY,
                stonePipeBehavior,
                EnergyPipeTransporter::new,
                PoweredPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "stone_energy_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        sandstoneEnergyPipe = new PipeBlock(
                NAMESPACE.id("sandstone_energy_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/sandstone_energy_pipe"),
                null,
                PipeType.ENERGY,
                sandstonePipeBehavior,
                EnergyPipeTransporter::new,
                PoweredPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "sandstone_energy_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

        goldenEnergyPipe = new PipeBlock(
                NAMESPACE.id("golden_energy_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/golden_energy_pipe"),
                null,
                PipeType.ENERGY,
                goldenPipeBehavior,
                EnergyPipeTransporter::new,
                PoweredPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "golden_energy_pipe").setHardness(0.1F).setSoundGroup(Block.METAL_SOUND_GROUP);

        diamondEnergyPipe = new PipeBlock(
                NAMESPACE.id("diamond_energy_pipe"),
                pipeMaterial,
                NAMESPACE.id("block/pipe/diamond_energy_pipe"),
                null,
                PipeType.ENERGY,
                diamondPipeBehavior,
                EnergyPipeTransporter::new,
                PoweredPipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "diamond_energy_pipe").setHardness(0.1F).setSoundGroup(Block.METAL_SOUND_GROUP);
    }
}
