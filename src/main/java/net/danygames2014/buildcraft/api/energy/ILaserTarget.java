package net.danygames2014.buildcraft.api.energy;

public interface ILaserTarget {
    /**
     * Returns true if the target currently needs power. For example, if the Advanced
     * Crafting Table has work to do.
     *
     * @return true if needs power
     */
    boolean requiresLaserEnergy();

    /**
     * Transfers energy from the laser to the target.
     */
    void receiveLaserEnergy(double energy);

    /**
     * Return true if the Tile Entity object is no longer a valid target. For
     * example, if its been invalidated.
     *
     * @return true if no longer a valid target object
     */
    boolean isInvalidTarget();

    int getXCoord();

    int getYCoord();

    int getZCoord();
}
