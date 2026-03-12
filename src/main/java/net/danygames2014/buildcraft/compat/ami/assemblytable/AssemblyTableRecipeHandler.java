package net.danygames2014.buildcraft.compat.ami.assemblytable;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.recipe.machine.AssemblyTableRecipe;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeHandler;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

public class AssemblyTableRecipeHandler implements RecipeHandler<AssemblyTableRecipe> {
    @Override
    public @NotNull Class<AssemblyTableRecipe> getRecipeClass() {
        return AssemblyTableRecipe.class;
    }

    @Override
    public @NotNull String getRecipeCategoryUid() {
        return Buildcraft.NAMESPACE.id("assembly_table").toString();
    }

    @Override
    public @NotNull RecipeWrapper getRecipeWrapper(@NotNull AssemblyTableRecipe recipe) {
        return new AssemblyTableRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(@NotNull AssemblyTableRecipe recipe) {
        return true;
    }
}
