package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.event.ControlModeRegisterEvent;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.event.mod.InitEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;

public class InitListener {
    @EventListener(phase = InitEvent.PRE_INIT_PHASE)
    public void preInitListener(InitEvent event) {
        FabricLoader.getInstance().getEntrypointContainers("buildcraft:event_bus", Object.class).forEach(EntrypointManager::setup);
    }
    
    public void initListener(InitEvent event) {
        StationAPI.EVENT_BUS.post(new ControlModeRegisterEvent());
    }
    
}
