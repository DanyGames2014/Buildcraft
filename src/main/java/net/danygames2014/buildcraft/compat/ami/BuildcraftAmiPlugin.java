package net.danygames2014.buildcraft.compat.ami;

import net.danygames2014.buildcraft.Buildcraft;
import net.glasslauncher.mods.alwaysmoreitems.api.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.Identifier;

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
