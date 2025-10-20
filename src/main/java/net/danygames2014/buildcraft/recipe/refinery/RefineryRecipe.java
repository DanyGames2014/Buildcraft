package net.danygames2014.buildcraft.recipe.refinery;

import net.danygames2014.nyalib.fluid.Fluid;

public class RefineryRecipe {
    public Fluid inputFluid;
    public int inputAmount;
    public Fluid outputFluid;
    public int outputAmount;

    public RefineryRecipe(Fluid inputFluid, int inputAmount, Fluid outputFluid, int outputAmount) {
        this.inputFluid = inputFluid;
        this.inputAmount = inputAmount;
        this.outputFluid = outputFluid;
        this.outputAmount = outputAmount;
    }
}
