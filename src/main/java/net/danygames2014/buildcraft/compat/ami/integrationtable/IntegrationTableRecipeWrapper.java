package net.danygames2014.buildcraft.compat.ami.integrationtable;

import net.danygames2014.buildcraft.api.transport.gate.Gate;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateLogic;
import net.danygames2014.buildcraft.block.entity.pipe.gate.GateMaterial;
import net.danygames2014.buildcraft.item.GateItem;
import net.danygames2014.buildcraft.recipe.integration.IntegrationTableRecipe;
import net.glasslauncher.mods.alwaysmoreitems.api.recipe.RecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IntegrationTableRecipeWrapper implements RecipeWrapper {
    private final IntegrationTableRecipe recipe;

    public IntegrationTableRecipeWrapper(IntegrationTableRecipe recipe){
        this.recipe = recipe;
    }

    @Override
    public List<?> getInputs() {
        return recipe.input.getRepresentingStacks();
    }

    @Override
    public List<?> getOutputs() {
        ItemStack output = new ItemStack(GateItem.getGateItem(GateMaterial.REDSTONE, GateLogic.AND));
        GateItem.addGateExpansion(output, recipe.expansion);

        return List.of(output);
    }

    @Override
    public void drawInfo(@NotNull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }

    @Override
    public void drawAnimations(@NotNull Minecraft minecraft, int recipeWidth, int recipeHeight) {

    }

    @Override
    public @Nullable ArrayList<Object> getTooltip(int mouseX, int mouseY) {
        return null;
    }

    @Override
    public boolean handleClick(@NotNull Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
