package net.danygames2014.buildcraft.api.blockentity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public interface Controllable {
    /**
     * Get the current control mode of the Tile Entity.
     */
    ControlMode getControlMode();

    /**
     * Set the mode of the Tile Entity.
     */
    void setControlMode(ControlMode mode);

    /**
     * Check if a given control mode is accepted.
     * <p><bold>You MUST check this if you are querying or setting control modes</bold>
     * @return <code>true</code> if this control mode is accepted.
     */
    default boolean supportsControlMode(ControlMode mode) {
        return getSupportedControlModes().contains(mode);
    }
    
    ObjectArrayList<ControlMode> getSupportedControlModes();
}
