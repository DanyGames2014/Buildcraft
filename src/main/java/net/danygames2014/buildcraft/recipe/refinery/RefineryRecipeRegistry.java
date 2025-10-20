package net.danygames2014.buildcraft.recipe.refinery;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.nyalib.fluid.Fluid;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.HashMap;

public class RefineryRecipeRegistry {
    public final HashMap<Identifier, RefineryRecipe> registry = new HashMap<>();
    public final HashMap<Fluid, RefineryRecipe> fluidToRecipe = new HashMap<>();
    public static RefineryRecipeRegistry INSTANCE;

    public static RefineryRecipeRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RefineryRecipeRegistry();
        }

        return INSTANCE;
    }

    public static HashMap<Identifier, RefineryRecipe> getRegistry() {
        return getInstance().registry;
    }

    public static boolean register(Identifier identifier, RefineryRecipe recipe) {
        if (getInstance().registry.containsKey(identifier)) {
            Buildcraft.LOGGER.warn("Recipe " + identifier + " already exists!");
            return false;
        }

        if (getInstance().fluidToRecipe.containsKey(recipe.inputFluid)) {
            Buildcraft.LOGGER.warn("There is already a refinery recipe with " + recipe.inputFluid.getIdentifier() + " as input when registering " + identifier + "!");
        }

        getInstance().registry.put(identifier, recipe);
        getInstance().fluidToRecipe.put(recipe.inputFluid, recipe);
        return true;
    }

    public static RefineryRecipe get(Identifier identifier) {
        return getInstance().registry.getOrDefault(identifier, null);
    }
}
