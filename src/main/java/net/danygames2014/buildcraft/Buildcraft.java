package net.danygames2014.buildcraft;

import net.danygames2014.buildcraft.block.*;
import net.danygames2014.buildcraft.block.entity.ChuteBlockEntity;
import net.danygames2014.buildcraft.item.BuildcraftWrenchItem;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
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
        chuteBlock = new ChuteBlock(NAMESPACE.id("chute")).setTranslationKey(NAMESPACE, "chute");
        redstoneEngine = new RedstoneEngineBlock(NAMESPACE.id("redstone_engine")).setTranslationKey(NAMESPACE, "redstone_engine");
        stirlingEngine = new StirlingEngineBlock(NAMESPACE.id("stirling_engine")).setTranslationKey(NAMESPACE, "stirling_engine");
        combustionEngine = new CombustionEngineBlock(NAMESPACE.id("combustion_engine")).setTranslationKey(NAMESPACE, "combustion_engine");
        frame = new FrameBlock(NAMESPACE.id("frame"), Material.METAL).setTranslationKey(NAMESPACE, "frame").setHardness(0.5F);
    }
}
