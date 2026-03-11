package net.danygames2014.buildcraft.event;

import net.danygames2014.buildcraft.recipe.integration.IntegrationTableRecipe;
import net.danygames2014.buildcraft.recipe.integration.IntegrationTableRecipeRegistry;
import net.mine_diver.unsafeevents.Event;
import net.modificationstation.stationapi.api.util.Identifier;

public class IntegrationTableRecipeRegisterEvent extends Event {
    public IntegrationTableRecipeRegistry registry;

    public IntegrationTableRecipeRegisterEvent() {
        registry = IntegrationTableRecipeRegistry.getInstance();
    }
    
    public boolean register(Identifier identifier, IntegrationTableRecipe recipe) {
        return IntegrationTableRecipeRegistry.register(identifier, recipe);
    }
}
