package net.danygames2014.buildcraft.api.blockentity;

/**
 * This interface should be implemented by any Blockentity which carries out
 * work (crafting, ore processing, mining, etc).
 */
public interface HasWork {
    /**
     * Check if the Block Entity is performing doing any work.
     *
     * @return <code>true</code> if the Block Entity is performing work.
     */
    boolean hasWork();
}
