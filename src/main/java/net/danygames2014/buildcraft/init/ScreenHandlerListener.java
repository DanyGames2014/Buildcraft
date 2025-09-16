package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.block.entity.ChuteBlockEntity;
import net.danygames2014.buildcraft.block.entity.CombustionEngineBlockEntity;
import net.danygames2014.buildcraft.block.entity.StirlingEngineBlockEntity;
import net.danygames2014.buildcraft.screen.ChuteScreen;
import net.danygames2014.buildcraft.screen.CombustionEngineScreen;
import net.danygames2014.buildcraft.screen.StirlingEngineScreen;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.modificationstation.stationapi.api.client.gui.screen.GuiHandler;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

public class ScreenHandlerListener {

    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerScreenHandlers(GuiHandlerRegistryEvent event) {
        event.register(NAMESPACE.id("chute_screen"), new GuiHandler((GuiHandler.ScreenFactoryNoMessage) this::openChuteScreen, ChuteBlockEntity::new));
        event.register(NAMESPACE.id("stirling_engine"), new GuiHandler((GuiHandler.ScreenFactoryNoMessage) this::openStirlingEngineScreen, StirlingEngineBlockEntity::new));
        event.register(NAMESPACE.id("combustion_engine"), new GuiHandler((GuiHandler.ScreenFactoryNoMessage) this::openCombustionEngineScreen, CombustionEngineBlockEntity::new));
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
}
