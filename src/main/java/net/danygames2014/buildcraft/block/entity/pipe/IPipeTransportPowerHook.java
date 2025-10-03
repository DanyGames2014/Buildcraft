package net.danygames2014.buildcraft.block.entity.pipe;

public interface IPipeTransportPowerHook {

	/**
	 * Override default behavior on receiving energy into the pipe.
	 * 
	 * @return The amount of power used, or -1 for default behavior.
	 */
    float receiveEnergy(PipeBlockEntity pipe, ForgeDirection from, float val);

	/**
	 * Override default requested power.
	 */
    float requestEnergy(PipeBlockEntity pipe, ForgeDirection from, float amount);
}
