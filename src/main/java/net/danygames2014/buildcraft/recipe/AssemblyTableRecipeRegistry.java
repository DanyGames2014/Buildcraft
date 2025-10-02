package net.danygames2014.buildcraft.recipe;

import net.danygames2014.buildcraft.Buildcraft;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("StringConcatenationArgumentToLogCall")
public class AssemblyTableRecipeRegistry {
    public final HashMap<Identifier, AssemblyTableRecipe> registry;
    public static AssemblyTableRecipeRegistry INSTANCE;

    public static AssemblyTableRecipeRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AssemblyTableRecipeRegistry();
        }

        return INSTANCE;
    }

    public AssemblyTableRecipeRegistry() {
        this.registry = new HashMap<>();
    }

    public static HashMap<Identifier, AssemblyTableRecipe> getRegistry() {
        return getInstance().registry;
    }

    public static boolean register(Identifier identifier, AssemblyTableRecipe recipe) {
        if (getInstance().registry.containsKey(identifier)) {
            Buildcraft.LOGGER.warn("Recipe " + identifier + " already exists!");
            return false;
        }

        getInstance().registry.put(identifier, recipe);
        return true;
    }

    public static AssemblyTableRecipe get(Identifier identifier) {
        return getInstance().registry.getOrDefault(identifier, null);
    }

    public static ArrayList<AssemblyTableRecipe> get(ItemStack[] input) {
        var r = getInstance();

        ArrayList<AssemblyTableRecipe> recipes = new ArrayList<>();
        
        for (var recipe : r.registry.entrySet()) {
            if (recipe.getValue().matches(input)) {
                recipes.add(recipe.getValue());
            }
        }

        return recipes;
    }
}
