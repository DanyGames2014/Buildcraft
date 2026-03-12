package net.danygames2014.buildcraft.compat.ami;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.compat.ami.assemblytable.AssemblyTableRecipeCategory;
import net.danygames2014.buildcraft.compat.ami.assemblytable.AssemblyTableRecipeHandler;
import net.danygames2014.buildcraft.compat.ami.integrationtable.IntegrationTableRecipeCategory;
import net.danygames2014.buildcraft.compat.ami.integrationtable.IntegrationTableRecipeHandler;
import net.danygames2014.buildcraft.recipe.integration.IntegrationTableRecipeRegistry;
import net.danygames2014.buildcraft.recipe.machine.AssemblyTableRecipeRegistry;
import net.glasslauncher.mods.alwaysmoreitems.api.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.Arrays;

public class BuildcraftAmiPlugin implements ModPluginProvider {
    @Override
    public String getName() {
        return "Buildcraft";
    }

    @Override
    public Identifier getId() {
        return Buildcraft.NAMESPACE.id("buildcraft");
    }

    @Override
    public void onAMIHelpersAvailable(AMIHelpers amiHelpers) {

    }

    @Override
    public void onItemRegistryAvailable(ItemRegistry itemRegistry) {

    }

    @Override
    public void register(ModRegistry registry) {
        registry.addRecipeCategories(new AssemblyTableRecipeCategory());
        registry.addRecipeCategories(new IntegrationTableRecipeCategory());

        registry.addRecipeHandlers(new AssemblyTableRecipeHandler());
        registry.addRecipeHandlers(new IntegrationTableRecipeHandler());

        registry.addRecipes(Arrays.asList(AssemblyTableRecipeRegistry.getInstance().registry.values().toArray()));
        registry.addRecipes(Arrays.asList(IntegrationTableRecipeRegistry.getInstance().registry.values().toArray()));
    }

    @Override
    public void onRecipeRegistryAvailable(RecipeRegistry recipeRegistry) {

    }

    @Override
    public SyncableRecipe deserializeRecipe(NbtCompound recipe) {
        return null;
    }

    @Override
    public void updateBlacklist(AMIHelpers amiHelpers) {
        amiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(Buildcraft.renderBlock));
    }
}
