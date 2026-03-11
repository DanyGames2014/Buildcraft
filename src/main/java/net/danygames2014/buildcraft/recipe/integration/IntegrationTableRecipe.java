package net.danygames2014.buildcraft.recipe.integration;

import net.danygames2014.buildcraft.api.transport.gate.GateExpansion;
import net.danygames2014.buildcraft.item.GateItem;
import net.danygames2014.buildcraft.recipe.machine.input.RecipeInput;
import net.minecraft.item.ItemStack;

public class IntegrationTableRecipe {
    public final RecipeInput input;
    public final GateExpansion expansion;
    public final int recipeTime;
    
    public IntegrationTableRecipe(RecipeInput input, GateExpansion expansion, int recipeTime) {
        this.input = input;
        this.expansion = expansion;
        this.recipeTime = recipeTime;
    }
    
    public boolean matches(ItemStack stack) {
        if (stack == null || input == null) {
            return false;
        }
        
        return input.matches(stack);
    }
    
    public boolean integrateExpansion(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        
        if (stack.getItem() instanceof GateItem) {
            GateItem.addGateExpansion(stack, expansion);
            return true;
        }
        
        return false;
    }
}
