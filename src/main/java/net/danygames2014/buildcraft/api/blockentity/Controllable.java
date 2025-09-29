package net.danygames2014.buildcraft.api.blockentity;

public interface Controllable {
    public enum Mode {
        Unknown, On, Off, Mode, Loop
    }

    /**
     * Get the current control mode of the Tile Entity.
     * @return
     */
    Mode getControlMode();

    /**
     * Set the mode of the Tile Entity.
     * @param mode
     */
    void setControlMode(Mode mode);

    /**
     * Check if a given control mode is accepted.
     * If you query IControllable tiles, you MUST check with
     * acceptsControlMode first.
     * @param mode
     * @return True if this control mode is accepted.
     */
    boolean acceptsControlMode(Mode mode);
}
