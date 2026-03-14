package net.danygames2014.buildcraft.compat.ami.refinery;

import net.danygames2014.buildcraft.recipe.refinery.RefineryRecipe;
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

public class RefineryRecipeWrapper implements RecipeWrapper {
    private final RefineryRecipe recipe;

    private final HoverChecker input1HoverChecker = new HoverChecker(0, 61, 0, 17);
    private final HoverChecker input2HoverChecker = new HoverChecker(0, 61, 18, 35);
    private final HoverChecker outputHoverChecker = new HoverChecker(0, 61, 69, 86);

    private final HoverChecker energyCostHoverChecker = new HoverChecker(23, 38, 42, 63);

    public RefineryRecipeWrapper(RefineryRecipe recipe){
        this.recipe = recipe;
    }

    @Override
    public List<?> getInputs() {
        if(recipe.inputFluids == null){
            return null;
        }
        ArrayList<Object> fluids = new ArrayList<>();

        for(int i = 0; i < recipe.inputFluids.length; i++) {
            ArrayList<ItemStack> stacks = new ArrayList<>();
            if(recipe.inputFluids[i] != null){
                Item bucketItem = recipe.inputFluids[i].getBucketItem();
                if(bucketItem != null){
                    stacks.add(new ItemStack(bucketItem));
                }
                if(recipe.inputFluids[i].getStillBlock() != null){
                    stacks.add(new ItemStack(recipe.inputFluids[i].getStillBlock().asItem()));
                }
            }
            fluids.add(stacks);
        }
        return fluids;
    }

    @Override
    public List<?> getOutputs() {
        if(recipe.outputFluid == null){
            return null;
        }
        ArrayList<Object> fluids = new ArrayList<>();

        ArrayList<ItemStack> stacks = new ArrayList<>();
        Item bucketItem = recipe.outputFluid.getBucketItem();
        if(bucketItem != null){
            stacks.add(new ItemStack(bucketItem));
        }
        if(recipe.outputFluid.getStillBlock() != null){
            stacks.add(new ItemStack(recipe.outputFluid.getStillBlock().asItem()));
        }
        fluids.add(stacks);
        return fluids;
    }

    public Fluid getInputFluid1(){
        if(recipe.inputFluids == null || recipe.inputFluids.length == 0){
            return null;
        }
        return recipe.inputFluids[0];
    }

    public int getInput1Amount(){
        if(recipe.inputAmounts == null || recipe.inputAmounts.length == 0){
            return 0;
        }
        return recipe.inputAmounts[0];
    }

    public Fluid getInputFluid2(){
        if(recipe.inputFluids == null || recipe.inputFluids.length < 2){
            return null;
        }
        return recipe.inputFluids[1];
    }

    public int getInput2Amount(){
        if(recipe.inputAmounts == null || recipe.inputAmounts.length < 2){
            return 0;
        }
        return recipe.inputAmounts[1];
    }

    public Fluid getOutputFluid(){
        return recipe.outputFluid;
    }

    public int getOutputAmount(){
        return recipe.outputAmount;
    }

    @Override
    public void drawInfo(@NotNull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }

    @Override
    public void drawAnimations(@NotNull Minecraft minecraft, int recipeWidth, int recipeHeight) {

    }

    @Override
    public @Nullable ArrayList<Object> getTooltip(int mouseX, int mouseY) {
        if(energyCostHoverChecker.isOver(mouseX, mouseY)){
            return new ArrayList<>() {
                {
                    add(recipe.energyCost + " MJ");
                }
            };
        }
        if(input1HoverChecker.isOver(mouseX, mouseY) && getInputFluid1() != null){
            return new ArrayList<>() {
                {
                    add(getInputFluid1().getTranslatedName());
                    add("§7" + getInput1Amount() + " mB");
                    add("§9§o" + getInputFluid1().getIdentifier().namespace.getName());
                }
            };
        }
        if(input2HoverChecker.isOver(mouseX, mouseY) && getInputFluid2() != null){
            return new ArrayList<>() {
                {
                    add(getInputFluid2().getTranslatedName());
                    add("§7" + getInput2Amount() + " mB");
                    add("§9§o" + getInputFluid2().getIdentifier().namespace.getName());
                }
            };
        }
        if(outputHoverChecker.isOver(mouseX, mouseY) && getOutputFluid() != null){
            return new ArrayList<>() {
                {
                    add(getOutputFluid().getTranslatedName());
                    add("§7" + getOutputAmount() + " mB");
                    add("§9§o" + getOutputFluid().getIdentifier().namespace.getName());
                }
            };
        }
        return null;
    }

    @Override
    public boolean handleClick(@NotNull Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        RecipesGui recipesGui = OverlayScreen.INSTANCE.recipesGui;
        if(input1HoverChecker.isOver(mouseX, mouseY) && getInputFluid1() != null){
            ItemStack stack = getStackForFluid(getInputFluid1());
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
        if(input2HoverChecker.isOver(mouseX, mouseY) && getInputFluid2() != null){
            ItemStack stack = getStackForFluid(getInputFluid2());
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
        if(outputHoverChecker.isOver(mouseX, mouseY) && getOutputFluid() != null){
            ItemStack stack = getStackForFluid(getOutputFluid());
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

    @Override
    public boolean handleKeyPress(@NotNull Minecraft minecraft, char character, int keyCode) {
        return RecipeWrapper.super.handleKeyPress(minecraft, character, keyCode);
    }
}
