package net.danygames2014.buildcraft.compat.ami;

import net.danygames2014.buildcraft.util.ScreenUtil;
import net.danygames2014.nyalib.fluid.Fluid;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.glasslauncher.mods.alwaysmoreitems.api.gui.AMIDrawable;
import net.glasslauncher.mods.alwaysmoreitems.api.gui.RecipeLayout;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeCategory;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeWrapper;
import net.glasslauncher.mods.alwaysmoreitems.gui.DrawableHelper;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public abstract class FluidInfoRecipeCategory implements RecipeCategory {
    private final AMIDrawable background = DrawableHelper.createDrawable("/assets/buildcraft/stationapi/textures/gui/fluidinfo_ami.png", 0, 0, 87, 60);
    private final AMIDrawable tankLines = DrawableHelper.createDrawable("/assets/buildcraft/stationapi/textures/gui/fluidinfo_ami.png", 87, 1, 16, 58);

    private Fluid fluid;

    @Override
    public @NotNull AMIDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        if(fluid != null) {
            ScreenUtil.drawFluid(new FluidStack(fluid), 100, 1, 1, 16, 58, 0);
            tankLines.draw(minecraft, 1, 1);
        }
    }

    @Override
    public void drawAnimations(Minecraft minecraft) {

    }

    @Override
    public void setRecipe(@NotNull RecipeLayout recipeLayout, @NotNull RecipeWrapper recipeWrapper) {
        if(recipeWrapper instanceof FluidRecipeWrapper wrapper){
            setFluid(wrapper.getFluid());
        }
    }

    protected void setFluid(Fluid fluid){
        this.fluid = fluid;
    }
}
