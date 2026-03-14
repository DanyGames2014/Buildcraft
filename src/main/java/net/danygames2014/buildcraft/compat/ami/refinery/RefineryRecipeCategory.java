package net.danygames2014.buildcraft.compat.ami.refinery;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.util.ScreenUtil;
import net.danygames2014.nyalib.fluid.Fluid;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.glasslauncher.mods.alwaysmoreitems.api.gui.AMIDrawable;
import net.glasslauncher.mods.alwaysmoreitems.api.gui.RecipeLayout;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeCategory;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeWrapper;
import net.glasslauncher.mods.alwaysmoreitems.gui.DrawableHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.language.TranslationStorage;
import org.jetbrains.annotations.NotNull;

public class RefineryRecipeCategory implements RecipeCategory {
    private final AMIDrawable background = DrawableHelper.createDrawable("/assets/buildcraft/stationapi/textures/gui/refinery_ami.png", 0, 0, 87, 60);
    private final AMIDrawable tankLines = DrawableHelper.createDrawable("/assets/buildcraft/stationapi/textures/gui/refinery_ami.png", 87, 1, 16, 58);
    private Fluid input1;
    private Fluid input2;
    private Fluid output;

    @Override
    public @NotNull String getUid() {
        return Buildcraft.NAMESPACE.id("refinery").toString();
    }

    @Override
    public @NotNull String getTitle() {
        return TranslationStorage.getInstance().get("category.buildcraft.refinery");
    }

    @Override
    public @NotNull AMIDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        if(input1 != null) {
            ScreenUtil.drawFluid(new FluidStack(input1), 100, 1, 1, 16, 58, 0);
            tankLines.draw(minecraft, 1, 1);
        }
        if(input2 != null) {
            ScreenUtil.drawFluid(new FluidStack(input2), 100, 19, 1, 16, 58, 0);
            tankLines.draw(minecraft, 19, 1);
        }
        if(output != null) {
            ScreenUtil.drawFluid(new FluidStack(output), 100, 70, 1, 16, 58, 0);
            tankLines.draw(minecraft, 70, 1);
        }
    }

    @Override
    public void drawAnimations(Minecraft minecraft) {

    }

    @Override
    public void setRecipe(@NotNull RecipeLayout recipeLayout, @NotNull RecipeWrapper recipeWrapper) {


        if(recipeWrapper instanceof RefineryRecipeWrapper wrapper){
            input1 = wrapper.getInputFluid1();
            input2 = wrapper.getInputFluid2();
            output = wrapper.getOutputFluid();
        }
    }
}
