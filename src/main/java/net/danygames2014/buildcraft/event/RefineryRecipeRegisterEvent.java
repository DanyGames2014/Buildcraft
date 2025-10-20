package net.danygames2014.buildcraft.event;

import net.danygames2014.buildcraft.recipe.refinery.RefineryRecipe;
import net.danygames2014.buildcraft.recipe.refinery.RefineryRecipeRegistry;
import net.mine_diver.unsafeevents.Event;
import net.modificationstation.stationapi.api.util.Identifier;

public class RefineryRecipeRegisterEvent extends Event {
    public RefineryRecipeRegistry registry;

    public RefineryRecipeRegisterEvent() {
        registry = RefineryRecipeRegistry.getInstance();
    }

    public boolean register(Identifier identifier, RefineryRecipe recipe) {
        return RefineryRecipeRegistry.register(identifier, recipe);
    }
}
