package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.block.entity.ChuteBlockEntity;
import net.danygames2014.buildcraft.client.gui.screen.ChuteScreen;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.modificationstation.stationapi.api.client.gui.screen.GuiHandler;
import net.modificationstation.stationapi.api.client.registry.GuiHandlerRegistry;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.Namespace;

public class ScreenHandlerListener {

    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerScreenHandlers(GuiHandlerRegistryEvent event){
        GuiHandlerRegistry registry = event.registry;
        Registry.register(registry, NAMESPACE.id("chute_screen"), new GuiHandler((GuiHandler.ScreenFactoryNoMessage)this::openChuteScreen, ChuteBlockEntity::new));
    }

    public Screen openChuteScreen(PlayerEntity playerEntity, Inventory inventory){
        return new ChuteScreen(playerEntity.inventory, (ChuteBlockEntity)inventory);
    }
}
