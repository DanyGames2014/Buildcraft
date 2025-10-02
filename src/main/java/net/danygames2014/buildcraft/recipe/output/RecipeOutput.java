package net.danygames2014.buildcraft.recipe.output;

import net.minecraft.item.ItemStack;

import java.util.Random;

/**
 * A base recipe output
 */
public class RecipeOutput {
    /**
     * The stack of the output definining what will actually be gotten from this output
     */
    private final ItemStack stack;

    /**
     * The type of the output
     */
    public final RecipeOutputType type;

    public RecipeOutput(ItemStack stack, RecipeOutputType type) {
        this.stack = stack;
        this.type = type;
    }
    
    public RecipeOutput(ItemStack stack) {
        this(stack, RecipeOutputType.PRIMARY);
    }

    /**
     * Returns a stack which is safe to be used directly
     */
    public ItemStack getOutput(Random random) {
        return stack.copy();
    }

    /**
     * Returns a stack which is safe to be used directly which contains the maximum possible output
     */
    public ItemStack getMaxOutput() {
        return stack.copy();
    }

    public String getStackString() {
        return stack.toString();
    }
    
    @Override
    public String toString() {
        return "RecipeOutput { stack=" + getStackString() + ", type=" + type + " }";
    }
}
