/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
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
