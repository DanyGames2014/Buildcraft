package net.danygames2014.buildcraft.api.core;

/**
 * To be implemented by TileEntities able to provide a square area on the world, typically BuildCraft markers.
 */
public interface AreaProvider {
    int xMin();

    int yMin();

    int zMin();

    int xMax();

    int yMax();

    int zMax();

    /**
     * Remove from the world all objects used to define the area.
     */
    void removeFromWorld();
}
