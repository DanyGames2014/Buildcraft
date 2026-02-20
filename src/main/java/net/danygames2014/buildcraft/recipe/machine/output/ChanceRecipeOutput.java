package net.danygames2014.buildcraft.recipe.machine.output;

import net.minecraft.item.ItemStack;

import java.util.Random;

/**
 * A recipe output with a given chance to "drop"
 */
public class ChanceRecipeOutput extends RecipeOutput {
    public final int chance;

    public ChanceRecipeOutput(ItemStack stack, RecipeOutputType type, int chance) {
        super(stack, type);
        this.chance = chance;
    }

    public ChanceRecipeOutput(ItemStack stack, int chance) {
        super(stack);
        this.chance = chance;
    }

    @Override
    public ItemStack getOutput(Random random) {
        if (random.nextInt(chance) == 0) {
            return super.getOutput(random);
        }
        
        return null;
    }

    @Override
    public String toString() {
        return "RecipeOutput { stack=" + getStackString() + ", type=" + type + ", chance=" + chance + " }";
    }
}
