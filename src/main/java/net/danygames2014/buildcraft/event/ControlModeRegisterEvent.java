package net.danygames2014.buildcraft.event;

import net.danygames2014.buildcraft.api.blockentity.ControlMode;
import net.danygames2014.buildcraft.registry.ControlModeRegistry;
import net.mine_diver.unsafeevents.Event;

public class ControlModeRegisterEvent extends Event {
    public ControlModeRegistry registry;

    public ControlModeRegisterEvent() {
        registry = ControlModeRegistry.getInstance();
    }
    
    public boolean register(ControlMode controlMode) {
        return ControlModeRegistry.register(controlMode);
    }
}
