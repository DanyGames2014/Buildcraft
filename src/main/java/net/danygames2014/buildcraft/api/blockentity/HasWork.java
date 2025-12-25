package net.danygames2014.buildcraft.api.blockentity;

/**
 * This interface should be implemented by any Blockentity which carries out
 * work (crafting, ore processing, mining, et cetera).
 */
public interface HasWork {
    /**
     * Check if the Tile Entity is currently doing any work.
     * @return True if the Tile Entity is doing work.
     */
    boolean hasWork();
}
