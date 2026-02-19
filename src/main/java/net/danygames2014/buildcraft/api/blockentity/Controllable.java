package net.danygames2014.buildcraft.api.blockentity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Defines a block entity as being controllable by logic gates on pipes
 */
public interface Controllable {
    /**
     * Get the current control mode of the Block Entity.
     */
    ControlMode getControlMode();

    /**
     * Set the mode of the Block Entity.
     */
    void setControlMode(ControlMode mode);

    /**
     * Check if a given control mode is accepted.
     * <p>You MUST check this if you are querying or setting control modes
     *
     * @return <code>true</code> if this control mode is accepted.
     */
    default boolean supportsControlMode(ControlMode mode) {
        return getSupportedControlModes().contains(mode);
    }

    /**
     * @return A list of all the control modes this Block Entity supports
     */
    ObjectArrayList<ControlMode> getSupportedControlModes();
}
