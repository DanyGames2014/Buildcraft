package net.danygames2014.buildcraft.recipe.refinery;

import net.danygames2014.nyalib.fluid.Fluid;

public class RefineryRecipe {
    public Fluid[] inputFluids;
    public int[] inputAmounts;
    public Fluid outputFluid;
    public int outputAmount;

    public int energyCost;
    public long craftingTime;

    public RefineryRecipe(Fluid[] inputFluids, int[] inputAmounts, Fluid outputFluid, int outputAmount, int energyCost, long craftingTime) {
        this.inputFluids = inputFluids;
        this.inputAmounts = inputAmounts;
        this.outputFluid = outputFluid;
        this.outputAmount = outputAmount;

        this.energyCost = energyCost;
        this.craftingTime = craftingTime;
    }
}
