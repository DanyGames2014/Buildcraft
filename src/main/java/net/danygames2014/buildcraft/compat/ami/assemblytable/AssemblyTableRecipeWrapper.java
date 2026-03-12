package net.danygames2014.buildcraft.compat.ami.assemblytable;

import net.danygames2014.buildcraft.recipe.machine.AssemblyTableRecipe;
import net.danygames2014.buildcraft.recipe.machine.input.RecipeInput;
import net.danygames2014.buildcraft.recipe.machine.output.RecipeOutput;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeWrapper;
import net.glasslauncher.mods.alwaysmoreitems.util.HoverChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AssemblyTableRecipeWrapper implements RecipeWrapper {
    private final AssemblyTableRecipe recipe;
    private final HoverChecker hoverChecker = new HoverChecker(0, 74, 87, 92);

    public AssemblyTableRecipeWrapper(AssemblyTableRecipe recipe){
        this.recipe = recipe;
    }

    @Override
    public List<?> getInputs() {
        ArrayList<ItemStack> inputs = new ArrayList<>();
        for(RecipeInput input : recipe.inputs){
            inputs.addAll(input.getRepresentingStacks());
        }
        return inputs;
    }

    @Override
    public List<?> getOutputs() {
        ArrayList<ItemStack> outputs = new ArrayList<>();
        for(RecipeOutput output : recipe.outputs){
            outputs.add(output.getMaxOutput());
        }
        return outputs;
    }

    @Override
    public void drawInfo(@NotNull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }

    @Override
    public void drawAnimations(@NotNull Minecraft minecraft, int recipeWidth, int recipeHeight) {

    }

    @Override
    public @Nullable ArrayList<Object> getTooltip(int mouseX, int mouseY) {
        if(hoverChecker.isOver(mouseX, mouseY)){
            return new ArrayList<>() {
                {add(recipe.recipeTime + " MJ");}
            };
        }
        return null;
    }

    @Override
    public boolean handleClick(@NotNull Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }

    public int getRecipeTime(){
        return recipe.recipeTime;
    }
}
