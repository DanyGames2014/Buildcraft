package net.danygames2014.buildcraft;

import net.danygames2014.buildcraft.block.*;
import net.danygames2014.buildcraft.block.material.PipeMaterial;
import net.danygames2014.buildcraft.item.BuildcraftWrenchItem;
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

    public static Material pipeMaterial;
    
    public static Block chuteBlock;
    public static Block redstoneEngine;
    public static Block stirlingEngine;
    public static Block combustionEngine;
    public static Block frame;
    
    @EventListener
    public void registerItems(ItemRegistryEvent event) {
        wrench = new BuildcraftWrenchItem(NAMESPACE.id("wrench")).setTranslationKey(NAMESPACE, "wrench");
        woodenGear = new TemplateItem(NAMESPACE.id("wooden_gear")).setTranslationKey(NAMESPACE, "wooden_gear");
        stoneGear = new TemplateItem(NAMESPACE.id("stone_gear")).setTranslationKey(NAMESPACE, "stone_gear");
        ironGear = new TemplateItem(NAMESPACE.id("iron_gear")).setTranslationKey(NAMESPACE, "iron_gear");
        goldGear = new TemplateItem(NAMESPACE.id("golden_gear")).setTranslationKey(NAMESPACE, "golden_gear");
        diamondGear = new TemplateItem(NAMESPACE.id("diamond_gear")).setTranslationKey(NAMESPACE, "diamond_gear");
    }

    @EventListener
    public void registerBlocks(BlockRegistryEvent event){
        pipeMaterial = new PipeMaterial(MapColor.LIGHT_GRAY);
        
        chuteBlock = new ChuteBlock(NAMESPACE.id("chute")).setTranslationKey(NAMESPACE, "chute").setHardness(3.0F).setSoundGroup(Block.METAL_SOUND_GROUP);
        redstoneEngine = new RedstoneEngineBlock(NAMESPACE.id("redstone_engine")).setTranslationKey(NAMESPACE, "redstone_engine").setHardness(0.7F).setSoundGroup(Block.WOOD_SOUND_GROUP);
        stirlingEngine = new StirlingEngineBlock(NAMESPACE.id("stirling_engine")).setTranslationKey(NAMESPACE, "stirling_engine").setHardness(1.0F).setSoundGroup(Block.STONE_SOUND_GROUP);
        combustionEngine = new CombustionEngineBlock(NAMESPACE.id("combustion_engine")).setTranslationKey(NAMESPACE, "combustion_engine").setHardness(1.2F).setSoundGroup(Block.METAL_SOUND_GROUP);
        frame = new FrameBlock(NAMESPACE.id("frame"), pipeMaterial).setTranslationKey(NAMESPACE, "frame").setHardness(0.1F).setSoundGroup(Block.METAL_SOUND_GROUP);
    }
}
