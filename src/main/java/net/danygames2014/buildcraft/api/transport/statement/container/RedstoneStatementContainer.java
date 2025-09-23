package net.danygames2014.buildcraft.api.transport.statement.container;

import net.modificationstation.stationapi.api.util.math.Direction;

import javax.annotation.Nullable;

public interface RedstoneStatementContainer {
    /**
     * Get the redstone input from a given side.
     * @param side The side - use "null" for maximum input.
     * @return The redstone input, from 0 to 15.
     */
    int getRedstoneInput(@Nullable Direction side);

    /**
     * Set the redstone input for a given side.
     * @param side The side - use "null" for all sides.
     * @return Whether the set was successful.
     */
    boolean setRedstoneOutput(@Nullable Direction side, int value);
}
