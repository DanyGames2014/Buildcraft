package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.blockentity.ControlMode;
import net.danygames2014.buildcraft.event.ControlModeRegisterEvent;
import net.mine_diver.unsafeevents.listener.EventListener;

public class ControlModeListener {
    @EventListener
    public void registerControlModes(ControlModeRegisterEvent event) {
        event.register(ControlMode.ON = new ControlMode(Buildcraft.NAMESPACE.id("on"), null));
        event.register(ControlMode.OFF = new ControlMode(Buildcraft.NAMESPACE.id("off"), null));
    }
}
