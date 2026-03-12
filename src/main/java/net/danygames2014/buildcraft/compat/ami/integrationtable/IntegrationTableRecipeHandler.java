package net.danygames2014.buildcraft.compat.ami.integrationtable;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.compat.ami.assemblytable.AssemblyTableRecipeWrapper;
import net.danygames2014.buildcraft.recipe.integration.IntegrationTableRecipe;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeHandler;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

public class IntegrationTableRecipeHandler implements RecipeHandler<IntegrationTableRecipe> {
    @Override
    public @NotNull Class<IntegrationTableRecipe> getRecipeClass() {
        return IntegrationTableRecipe.class;
    }

    @Override
    public @NotNull String getRecipeCategoryUid() {
        return Buildcraft.NAMESPACE.id("integration_table").toString();
    }

    @Override
    public @NotNull RecipeWrapper getRecipeWrapper(@NotNull IntegrationTableRecipe recipe) {
        return new IntegrationTableRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(@NotNull IntegrationTableRecipe recipe) {
        return true;
    }
}
