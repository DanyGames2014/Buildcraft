package net.danygames2014.buildcraft.compat.ami;

import net.danygames2014.nyalib.fluid.Fluid;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeWrapper;
import net.glasslauncher.mods.alwaysmoreitems.gui.screen.OverlayScreen;
import net.glasslauncher.mods.alwaysmoreitems.gui.screen.RecipesGui;
import net.glasslauncher.mods.alwaysmoreitems.recipe.Focus;
import net.glasslauncher.mods.alwaysmoreitems.util.HoverChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FluidRecipeWrapper implements RecipeWrapper {
    private final Fluid fluid;

    private final HoverChecker fluidHoverChecker = new HoverChecker(0, 61, 0, 17);

    public FluidRecipeWrapper(Fluid fluid){
        this.fluid = fluid;
    }

    @Override
    public List<?> getInputs() {
        if(fluid == null){
            return null;
        }
        ArrayList<Object> fluids = new ArrayList<>();

        ArrayList<ItemStack> stacks = new ArrayList<>();
        Item bucketItem = fluid.getBucketItem();
        if(bucketItem != null){
            stacks.add(new ItemStack(bucketItem));
        }
        if(fluid.getStillBlock() != null){
            stacks.add(new ItemStack(fluid.getStillBlock().asItem()));
        }
        fluids.add(stacks);
        return fluids;
    }

    @Override
    public List<?> getOutputs() {
        return List.of();
    }

    public Fluid getFluid(){
        return this.fluid;
    }

    @Override
    public void drawInfo(@NotNull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }

    @Override
    public void drawAnimations(@NotNull Minecraft minecraft, int recipeWidth, int recipeHeight) {

    }

    @Override
    public @Nullable ArrayList<Object> getTooltip(int mouseX, int mouseY) {
        if(fluidHoverChecker.isOver(mouseX, mouseY) && fluid != null){
            return new ArrayList<>() {
                {
                    add(fluid.getTranslatedName());
                    add("§9§o" + fluid.getIdentifier().namespace.getName());
                }
            };
        }
        return null;
    }

    @Override
    public boolean handleClick(@NotNull Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        RecipesGui recipesGui = OverlayScreen.INSTANCE.recipesGui;
        if(fluidHoverChecker.isOver(mouseX, mouseY) && fluid != null){
            ItemStack stack = getStackForFluid(fluid);
            if(stack == null){
                return false;
            }
            if(mouseButton == 0){
                recipesGui.showRecipes(Focus.create(stack));
                return true;
            }
            if(mouseButton == 1){
                recipesGui.showUses(Focus.create(stack));
                return true;
            }
        }
        return false;
    }

    private ItemStack getStackForFluid(Fluid fluid){
        if(fluid.getStillBlock() != null){
            return new ItemStack(fluid.getStillBlock().asItem());
        }
        if(fluid.getBucketItem() != null){
            return new ItemStack(fluid.getBucketItem());
        }
        return null;
    }
}
