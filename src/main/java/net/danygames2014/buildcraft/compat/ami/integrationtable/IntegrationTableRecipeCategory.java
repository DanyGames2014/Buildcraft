package net.danygames2014.buildcraft.compat.ami.integrationtable;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateLogic;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateMaterial;
import net.danygames2014.buildcraft.item.GateItem;
import net.glasslauncher.mods.alwaysmoreitems.api.gui.AMIDrawable;
import net.glasslauncher.mods.alwaysmoreitems.api.gui.GuiItemStackGroup;
import net.glasslauncher.mods.alwaysmoreitems.api.gui.RecipeLayout;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeCategory;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeWrapper;
import net.glasslauncher.mods.alwaysmoreitems.gui.DrawableHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class IntegrationTableRecipeCategory implements RecipeCategory {

    @NotNull
    private final AMIDrawable background = DrawableHelper.createDrawable("/assets/buildcraft/stationapi/textures/gui/integration_table.png", 13, 27, 151, 38);

    @Override
    public @NotNull String getUid() {
        return Buildcraft.NAMESPACE.id("integration_table").toString();
    }

    @Override
    public @NotNull String getTitle() {
        return TranslationStorage.getInstance().get("category.buildcraft.integration_table");
    }

    @Override
    public @NotNull AMIDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {

    }

    @Override
    public void drawAnimations(Minecraft minecraft) {

    }

    @Override
    public void setRecipe(@NotNull RecipeLayout recipeLayout, @NotNull RecipeWrapper recipeWrapper) {
        GuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 3, 0);
        guiItemStacks.setFromRecipe(0, new ItemStack(GateItem.getGateItem(GateMaterial.REDSTONE, GateLogic.AND)));
        guiItemStacks.init(1, true, 39, 0);
        guiItemStacks.setFromRecipe(1, recipeWrapper.getInputs().get(0));
        guiItemStacks.init(2, false, 102, 16);
        guiItemStacks.setFromRecipe(2, recipeWrapper.getOutputs().get(0));
        guiItemStacks.init(3, false, 129, 16);
        guiItemStacks.setFromRecipe(3, recipeWrapper.getOutputs().get(0));
    }
}
