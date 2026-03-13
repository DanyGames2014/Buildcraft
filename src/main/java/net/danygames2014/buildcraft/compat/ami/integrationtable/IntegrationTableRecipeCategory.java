package net.danygames2014.buildcraft.compat.ami.integrationtable;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateLogic;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateMaterial;
import net.danygames2014.buildcraft.compat.ami.assemblytable.AssemblyTableRecipeWrapper;
import net.danygames2014.buildcraft.item.GateItem;
import net.danygames2014.buildcraft.recipe.integration.IntegrationTableRecipe;
import net.glasslauncher.mods.alwaysmoreitems.api.gui.*;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeCategory;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeWrapper;
import net.glasslauncher.mods.alwaysmoreitems.gui.DrawableHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class IntegrationTableRecipeCategory implements RecipeCategory {

    private TickTimer tickTimer = new net.glasslauncher.mods.alwaysmoreitems.util.TickTimer(40, 98, false);
    boolean renderAlt = false;
    long lastTime = -1;

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
        if(lastTime == -1){
            lastTime = System.currentTimeMillis();
        }
        if(System.currentTimeMillis() - lastTime > 150){
            renderAlt = !renderAlt;
            lastTime = System.currentTimeMillis();
        }

        StaticDrawable staticArrow = DrawableHelper.createDrawable("/assets/buildcraft/stationapi/textures/gui/integration_table.png", 0, 189, tickTimer.getValue(), 24);

        StaticDrawable staticArrowAlt = DrawableHelper.createDrawable("/assets/buildcraft/stationapi/textures/gui/integration_table.png", 0, 213, tickTimer.getValue(), 24);

        if(!renderAlt){
            staticArrow.draw(minecraft, 0, 12);
        } else {
            staticArrowAlt.draw(minecraft, 0, 12);
        }
    }

    @Override
    public void setRecipe(@NotNull RecipeLayout recipeLayout, @NotNull RecipeWrapper recipeWrapper) {
        GuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        if(recipeWrapper instanceof IntegrationTableRecipeWrapper wrapper){
            tickTimer = new net.glasslauncher.mods.alwaysmoreitems.util.TickTimer(wrapper.getRecipeTime() / 40, 98, false);
        }

        guiItemStacks.init(0, true, 3, 0);
        guiItemStacks.setFromRecipe(0, recipeWrapper.getInputs().get(0));
        guiItemStacks.init(1, true, 39, 0);
        guiItemStacks.setFromRecipe(1, recipeWrapper.getInputs().get(1));
        guiItemStacks.init(2, false, 102, 16);
        guiItemStacks.setFromRecipe(2, recipeWrapper.getOutputs().get(0));
        guiItemStacks.init(3, false, 129, 16);
        guiItemStacks.setFromRecipe(3, recipeWrapper.getOutputs().get(0));
    }
}
