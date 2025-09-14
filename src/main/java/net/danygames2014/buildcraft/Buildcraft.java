package net.danygames2014.buildcraft;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.Item;
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
    
    public static Item woodenGear;
    public static Item stoneGear;
    public static Item ironGear;
    public static Item goldGear;
    public static Item diamondGear;
    
    @EventListener
    public void registerItems(ItemRegistryEvent event) {
        woodenGear = new TemplateItem(NAMESPACE.id("wooden_gear")).setTranslationKey(NAMESPACE, "wooden_gear");
        stoneGear = new TemplateItem(NAMESPACE.id("stone_gear")).setTranslationKey(NAMESPACE, "stone_gear");
        ironGear = new TemplateItem(NAMESPACE.id("iron_gear")).setTranslationKey(NAMESPACE, "iron_gear");
        goldGear = new TemplateItem(NAMESPACE.id("golden_gear")).setTranslationKey(NAMESPACE, "golden_gear");
        diamondGear = new TemplateItem(NAMESPACE.id("diamond_gear")).setTranslationKey(NAMESPACE, "diamond_gear");
    }
}
