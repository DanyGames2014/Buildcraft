package net.danygames2014.buildcraft.recipe.input;

import net.minecraft.item.ItemStack;

/**
 * Abstract Recipe Input to be implemented
 */
public abstract class RecipeInput {
    public abstract boolean matches(ItemStack other);
    
    public abstract int getRequiredAmount();
}
