package net.danygames2014.buildcraft.api.energy;

import net.modificationstation.stationapi.api.util.math.Direction;

/**
 * Essentially only used for Wooden Power Pipe connection rules.
 * <p>
 * This Tile Entity interface allows you to indicate that a block can emit power
 * from a specific side.
 */
public interface IPowerEmitter {
	boolean canEmitPowerFrom(Direction side);
}
