package net.danygames2014.buildcraft.compat.ami.refinery;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.recipe.refinery.RefineryRecipe;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeHandler;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

public class RefineryRecipeHandler implements RecipeHandler<RefineryRecipe> {
    @Override
    public @NotNull Class getRecipeClass() {
        return RefineryRecipe.class;
    }

    @Override
    public @NotNull String getRecipeCategoryUid() {
        return Buildcraft.NAMESPACE.id("refinery").toString();
    }

    @Override
    public @NotNull RecipeWrapper getRecipeWrapper(@NotNull RefineryRecipe recipe) {
        return new RefineryRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(@NotNull RefineryRecipe recipe) {
        return true;
    }
}
