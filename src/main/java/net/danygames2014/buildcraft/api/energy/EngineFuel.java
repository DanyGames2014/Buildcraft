package net.danygames2014.buildcraft.api.energy;

import net.danygames2014.nyalib.fluid.Fluid;

@SuppressWarnings("ClassCanBeRecord")
public class EngineFuel {
    public final Fluid fluid;
    public final float powerPerCycle;
    public final int burnTime;

    public EngineFuel(Fluid fluid, float powerPerCycle, int burnTime) {
        this.fluid = fluid;
        this.powerPerCycle = powerPerCycle;
        this.burnTime = burnTime;
    }
}
