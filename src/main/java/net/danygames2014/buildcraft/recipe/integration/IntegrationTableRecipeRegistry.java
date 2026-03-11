package net.danygames2014.buildcraft.recipe.integration;

import net.danygames2014.buildcraft.Buildcraft;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("StringConcatenationArgumentToLogCall")
public class IntegrationTableRecipeRegistry {
    public final HashMap<Identifier, IntegrationTableRecipe> registry;
    public static IntegrationTableRecipeRegistry INSTANCE;

    public static IntegrationTableRecipeRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new IntegrationTableRecipeRegistry();
        }

        return INSTANCE;
    }

    public IntegrationTableRecipeRegistry() {
        this.registry = new HashMap<>();
    }

    public static HashMap<Identifier, IntegrationTableRecipe> getRegistry() {
        return getInstance().registry;
    }

    public static boolean register(Identifier identifier, IntegrationTableRecipe recipe) {
        if (getInstance().registry.containsKey(identifier)) {
            Buildcraft.LOGGER.warn("Recipe " + identifier + " already exists!");
            return false;
        }

        getInstance().registry.put(identifier, recipe);
        return true;
    }

    public static IntegrationTableRecipe get(Identifier identifier) {
        return getInstance().registry.getOrDefault(identifier, null);
    }

    public static IntegrationTableRecipe get(ItemStack input) {
        var r = getInstance();

        for (Map.Entry<Identifier, IntegrationTableRecipe> recipe : r.registry.entrySet()) {
            if (recipe.getValue().matches(input)) {
                return recipe.getValue();
            }
        }

        return null;
    }
    
    public static Identifier getIdentifier(IntegrationTableRecipe recipe) {
        IntegrationTableRecipeRegistry r = getInstance();
        
        if (r.registry.containsValue(recipe)) {
            for (Map.Entry<Identifier, IntegrationTableRecipe> entry : r.registry.entrySet()) {
                if (entry.getValue().equals(recipe)) {
                    return entry.getKey();
                }
            }
        }

        return null;
    }
}
