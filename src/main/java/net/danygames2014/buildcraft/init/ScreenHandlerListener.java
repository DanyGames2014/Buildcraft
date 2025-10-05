package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.block.entity.*;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.screen.*;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.modificationstation.stationapi.api.client.gui.screen.GuiHandler;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

import java.nio.channels.Pipe;

public class ScreenHandlerListener {

    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerScreenHandlers(GuiHandlerRegistryEvent event) {
        event.register(NAMESPACE.id("chute_screen"), new GuiHandler((GuiHandler.ScreenFactoryNoMessage) this::openChuteScreen, ChuteBlockEntity::new));
        event.register(NAMESPACE.id("stirling_engine"), new GuiHandler((GuiHandler.ScreenFactoryNoMessage) this::openStirlingEngineScreen, StirlingEngineBlockEntity::new));
        event.register(NAMESPACE.id("combustion_engine"), new GuiHandler((GuiHandler.ScreenFactoryNoMessage) this::openCombustionEngineScreen, CombustionEngineBlockEntity::new));
        event.register(NAMESPACE.id("autocrafting_table"), new GuiHandler((GuiHandler.ScreenFactoryNoMessage) this::openAutocraftingTableScreen, AutocraftingTableBlockEntity::new));
        event.register(NAMESPACE.id("assembly_table"), new GuiHandler((GuiHandler.ScreenFactoryNoMessage) this::openAssemblyTableScreen, AssemblyTableBlockEntity::new));
        event.register(NAMESPACE.id("gate"), new GuiHandler((GuiHandler.ScreenFactoryNoMessage) this::openGateScreen, PipeBlockEntityInventory::new));
    }

    public Screen openChuteScreen(PlayerEntity playerEntity, Inventory inventory) {
        return new ChuteScreen(playerEntity.inventory, (ChuteBlockEntity) inventory);
    }

    public Screen openStirlingEngineScreen(PlayerEntity playerEntity, Inventory inventory) {
        return new StirlingEngineScreen(playerEntity, (StirlingEngineBlockEntity) inventory);
    }

    public Screen openCombustionEngineScreen(PlayerEntity playerEntity, Inventory inventory) {
        return new CombustionEngineScreen(playerEntity, (CombustionEngineBlockEntity) inventory);
    }
    
    public Screen openAutocraftingTableScreen(PlayerEntity playerEntity, Inventory inventory) {
        return new AutocraftingTableScreen(playerEntity, (AutocraftingTableBlockEntity) inventory);
    }
    
    public Screen openAssemblyTableScreen(PlayerEntity playerEntity, Inventory inventory) {
        return new AssemblyTableScreen(playerEntity, (AssemblyTableBlockEntity) inventory);
    }

    public Screen openGateScreen(PlayerEntity playerEntity, Inventory inventory) {
        return new GateInterfaceScreen(playerEntity.inventory, (PipeBlockEntity) inventory);
    }
}
