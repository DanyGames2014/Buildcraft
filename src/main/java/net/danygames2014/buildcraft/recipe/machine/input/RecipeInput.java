package net.danygames2014.buildcraft.recipe.machine.input;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Abstract Recipe Input to be implemented
 */
public abstract class RecipeInput {
    public abstract boolean matches(ItemStack other);
    
    public abstract int getRequiredAmount();

    public abstract List<ItemStack> getRepresentingStacks();
}
