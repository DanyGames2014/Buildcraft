package net.danygames2014.buildcraft.event;

import net.danygames2014.buildcraft.recipe.machine.AssemblyTableRecipe;
import net.danygames2014.buildcraft.recipe.machine.AssemblyTableRecipeRegistry;
import net.mine_diver.unsafeevents.Event;
import net.modificationstation.stationapi.api.util.Identifier;

public class AssemblyTableRecipeRegisterEvent extends Event {
    public AssemblyTableRecipeRegistry registry;

    public AssemblyTableRecipeRegisterEvent() {
        registry = AssemblyTableRecipeRegistry.getInstance();
    }
    
    public boolean register(Identifier identifier, AssemblyTableRecipe recipe) {
        return AssemblyTableRecipeRegistry.register(identifier, recipe);
    }
}
