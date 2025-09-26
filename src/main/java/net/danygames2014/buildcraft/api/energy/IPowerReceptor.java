package net.danygames2014.buildcraft.api.energy;

import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

/**
 * This interface should be implemented by any Tile Entity that wishes to be
 * able to receive power.
 */
public interface IPowerReceptor {
	/**
	 * Get the PowerReceiver for this side of the block. You can return the same
	 * PowerReceiver for all sides or one for each side.
	 * <p>
	 * You should NOT return null to this method unless you mean to NEVER
	 * receive power from that side. Returning null, after previous returning a
	 * PowerReceiver, will most likely cause pipe connections to derp out and
	 * engines to eventually explode.
	 */
    PowerHandler.PowerReceiver getPowerReceiver(Direction side);

	/**
	 * Call back from the PowerHandler that is called when the stored power
	 * exceeds the activation power.
	 * <p>
	 * It can be triggered by update() calls or power modification calls.
	 */
    void doWork(PowerHandler workProvider);

	World getWorld();
}
