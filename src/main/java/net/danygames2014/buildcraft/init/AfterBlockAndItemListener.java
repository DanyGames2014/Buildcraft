package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.event.AssemblyTableRecipeRegisterEvent;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.event.registry.AfterBlockAndItemRegisterEvent;

public class AfterBlockAndItemListener {
    @EventListener
    public void afterBlockAndItemListener(AfterBlockAndItemRegisterEvent event) {
        StationAPI.EVENT_BUS.post(new AssemblyTableRecipeRegisterEvent());
    }
}
