package net.danygames2014.buildcraft.recipe.output;

import net.minecraft.item.ItemStack;

import java.util.Random;

/**
 * A recipe output with a given possible range of output count
 */
public class RangeRecipeOutput extends RecipeOutput {
    public final int minCount;
    public final int maxCount;

    public RangeRecipeOutput(ItemStack stack, RecipeOutputType type, int minCount, int maxCount) {
        super(stack, type);
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    public RangeRecipeOutput(ItemStack stack, int minCount, int maxCount) {
        super(stack);
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    @Override
    public ItemStack getOutput(Random random) {
        int count = random.nextInt(minCount, maxCount + 1);

        if (count == 0) {
            return null;
        }

        ItemStack stack = super.getOutput(random);
        stack.count = count;
        return stack;
    }

    @Override
    public ItemStack getMaxOutput() {
        ItemStack stack = super.getMaxOutput();
        stack.count = maxCount;
        return stack;
    }

    @Override
    public String toString() {
        return "RangeRecipeOutput { stack=" + getStackString() + ", type=" + type + ", minCount= " + minCount + ", maxCount= " + maxCount + " }";
    }
}
