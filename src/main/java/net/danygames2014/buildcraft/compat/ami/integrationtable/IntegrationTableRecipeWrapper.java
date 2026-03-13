package net.danygames2014.buildcraft.compat.ami.integrationtable;

import net.danygames2014.buildcraft.api.transport.gate.Gate;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateLogic;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateMaterial;
import net.danygames2014.buildcraft.item.GateItem;
import net.danygames2014.buildcraft.recipe.integration.IntegrationTableRecipe;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeWrapper;
import net.glasslauncher.mods.alwaysmoreitems.util.HoverChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IntegrationTableRecipeWrapper implements RecipeWrapper {
    private final HoverChecker hoverChecker = new HoverChecker(23, 27, 0, 97);
    private final IntegrationTableRecipe recipe;

    public IntegrationTableRecipeWrapper(IntegrationTableRecipe recipe){
        this.recipe = recipe;
    }

    @Override
    public List<?> getInputs() {
        ArrayList<Object> items = new ArrayList<>();
        ArrayList<ItemStack> allGates = new ArrayList<>();
        for(GateItem gate : GateItem.gateItems.values()){
            allGates.add(new ItemStack(gate));
        }
        items.add(allGates);

        items.addAll(recipe.input.getRepresentingStacks());

        return items;
    }

    @Override
    public List<?> getOutputs() {
        ArrayList<Object> items = new ArrayList<>();
        ArrayList<ItemStack> allGates = new ArrayList<>();
        for(GateItem gate : GateItem.gateItems.values()){
            ItemStack g = new ItemStack(gate);
            GateItem.addGateExpansion(g, recipe.expansion);
            allGates.add(g);
        }
        items.add(allGates);

        return items;
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
