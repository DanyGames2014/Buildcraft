package net.danygames2014.buildcraft.compat.ami.assemblytable;

import net.danygames2014.buildcraft.Buildcraft;
import net.glasslauncher.mods.alwaysmoreitems.api.gui.*;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeCategory;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeWrapper;
import net.glasslauncher.mods.alwaysmoreitems.gui.DrawableHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.language.TranslationStorage;
import org.jetbrains.annotations.NotNull;

public class AssemblyTableRecipeCategory implements RecipeCategory {


    private TickTimer tickTimer = new net.glasslauncher.mods.alwaysmoreitems.util.TickTimer(20, 70, true);;
    @NotNull
    private final AMIDrawable background = DrawableHelper.createDrawable("/assets/buildcraft/stationapi/textures/gui/assembly_table.png", 7, 35, 162, 72);

    @Override
    public @NotNull String getUid() {
        return Buildcraft.NAMESPACE.id("assembly_table").toString();
    }

    @Override
    public @NotNull String getTitle() {
        return TranslationStorage.getInstance().get("category.buildcraft.assembly_table");
    }

    @Override
    public @NotNull AMIDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        int offset = tickTimer.getValue();
        StaticDrawable bar = DrawableHelper.createDrawable("/assets/buildcraft/stationapi/textures/gui/assembly_table.png", 176, 18, 4, 70 - offset);
        bar.draw(minecraft, 88, 1 + offset);
    }

    @Override
    public void drawAnimations(Minecraft minecraft) {
    }

    @Override
    public void setRecipe(@NotNull RecipeLayout recipeLayout, @NotNull RecipeWrapper recipeWrapper) {
        if(recipeWrapper instanceof AssemblyTableRecipeWrapper wrapper){
            tickTimer = new net.glasslauncher.mods.alwaysmoreitems.util.TickTimer(wrapper.getRecipeTime() / 40, 70, true);
        }

        GuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        for(int row = 0; row <= 4; row++){
            for(int column = 0; column <=3; column++){
                int index = (row * 4) + column;
                guiItemStacks.init(index, true, 18 * column, 18 * row);
                if(index < (long) recipeWrapper.getInputs().size()){
                    guiItemStacks.setFromRecipe(index, recipeWrapper.getInputs().get(index));
                }
            }
        }

        for(int row = 0; row <= 4; row++){
            for(int column = 0; column <=2; column++){
                int index = (row * 4) + column;
                guiItemStacks.init(index + 12, true, 126 + 18 * column, 18 * row);
                if(index < (long) recipeWrapper.getOutputs().size()){
                    guiItemStacks.setFromRecipe(index + 12, recipeWrapper.getOutputs().get(index));
                }
            }
        }
    }
}
