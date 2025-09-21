package net.danygames2014.buildcraft;

import net.danygames2014.buildcraft.block.*;
import net.danygames2014.buildcraft.block.entity.pipe.*;
import net.danygames2014.buildcraft.block.material.PipeMaterial;
import net.danygames2014.buildcraft.item.BuildcraftWrenchItem;
import net.danygames2014.buildcraft.item.PipeWireItem;
import net.danygames2014.buildcraft.item.PlugItem;
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

    public static Item redPipeWire;
    public static Item bluePipeWire;
    public static Item greenPipeWire;
    public static Item yellowPipeWire;

    public static Item plug;

    public static Material pipeMaterial;

    public static WoodenPipeBehavior woodenPipeBehavior;
    public static StonePipeBehavior stonePipeBehavior;
    public static GoldenPipeBehavior goldenPipeBehavior;

    public static Block chuteBlock;
    public static Block redstoneEngine;
    public static Block stirlingEngine;
    public static Block combustionEngine;
    public static Block tank;
    public static Block frame;

    public static Block woodenItemPipe;
    public static Block cobblestoneItemPipe;
    public static Block goldenItemPipe;
    public static Block woodenFluidPipe;
    public static Block cobblestoneFluidPipe;
    public static Block woodenEnergyPipe;
    public static Block cobblestoneEnergyPipe;

    //TODO: figure out how to avoid registering the renderblock
    public static RenderBlock renderBlock;

    @EventListener
    public void registerItems(ItemRegistryEvent event) {
        wrench = new BuildcraftWrenchItem(NAMESPACE.id("wrench")).setTranslationKey(NAMESPACE, "wrench");
        woodenGear = new TemplateItem(NAMESPACE.id("wooden_gear")).setTranslationKey(NAMESPACE, "wooden_gear");
        stoneGear = new TemplateItem(NAMESPACE.id("stone_gear")).setTranslationKey(NAMESPACE, "stone_gear");
        ironGear = new TemplateItem(NAMESPACE.id("iron_gear")).setTranslationKey(NAMESPACE, "iron_gear");
        goldGear = new TemplateItem(NAMESPACE.id("golden_gear")).setTranslationKey(NAMESPACE, "golden_gear");
        diamondGear = new TemplateItem(NAMESPACE.id("diamond_gear")).setTranslationKey(NAMESPACE, "diamond_gear");

        redPipeWire = new PipeWireItem(NAMESPACE.id("red_pipe_wire")).setTranslationKey(NAMESPACE, "red_pipe_wire");
        bluePipeWire = new PipeWireItem(NAMESPACE.id("blue_pipe_wire")).setTranslationKey(NAMESPACE, "blue_pipe_wire");
        greenPipeWire = new PipeWireItem(NAMESPACE.id("green_pipe_wire")).setTranslationKey(NAMESPACE, "green_pipe_wire");
        yellowPipeWire = new PipeWireItem(NAMESPACE.id("yellow_pipe_wire")).setTranslationKey(NAMESPACE, "yellow_pipe_wire");

        plug = new PlugItem(NAMESPACE.id("plug")).setTranslationKey(NAMESPACE, "plug");
    }

    @EventListener
    public void registerBlocks(BlockRegistryEvent event) {
        pipeMaterial = new PipeMaterial(MapColor.LIGHT_GRAY);

        woodenPipeBehavior = new WoodenPipeBehavior();
        stonePipeBehavior = new StonePipeBehavior();
        goldenPipeBehavior = new GoldenPipeBehavior();

        chuteBlock = new ChuteBlock(NAMESPACE.id("chute")).setTranslationKey(NAMESPACE, "chute").setHardness(3.0F).setSoundGroup(Block.METAL_SOUND_GROUP);
        redstoneEngine = new RedstoneEngineBlock(NAMESPACE.id("redstone_engine")).setTranslationKey(NAMESPACE, "redstone_engine").setHardness(0.7F).setSoundGroup(Block.WOOD_SOUND_GROUP);
        stirlingEngine = new StirlingEngineBlock(NAMESPACE.id("stirling_engine")).setTranslationKey(NAMESPACE, "stirling_engine").setHardness(1.0F).setSoundGroup(Block.STONE_SOUND_GROUP);
        combustionEngine = new CombustionEngineBlock(NAMESPACE.id("combustion_engine")).setTranslationKey(NAMESPACE, "combustion_engine").setHardness(1.2F).setSoundGroup(Block.METAL_SOUND_GROUP);
        tank = new TankBlock(NAMESPACE.id("tank")).setTranslationKey(NAMESPACE, "tank").setHardness(0.5F);
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
                stonePipeBehavior,
                ItemPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "cobblestone_item_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);
        
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
                stonePipeBehavior,
                FluidPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "cobblestone_fluid_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);

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
                stonePipeBehavior,
                EnergyPipeTransporter::new,
                PipeBlockEntity::new
        ).setTranslationKey(NAMESPACE, "cobblestone_energy_pipe").setHardness(0.1F).setSoundGroup(Block.STONE_SOUND_GROUP);
    }
}
