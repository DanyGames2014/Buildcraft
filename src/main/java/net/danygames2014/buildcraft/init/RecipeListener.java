package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.event.AssemblyTableRecipeRegisterEvent;
import net.danygames2014.buildcraft.recipe.AssemblyTableRecipe;
import net.danygames2014.buildcraft.recipe.input.ItemRecipeInput;
import net.danygames2014.buildcraft.recipe.output.RecipeOutput;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;

public class RecipeListener {
    @Entrypoint.Namespace
    public static Namespace NAMESPACE;

    @EventListener
    public void registerAssemblyTableRecipes(AssemblyTableRecipeRegisterEvent event) {
        event.register(
                NAMESPACE.id("redstone_chipset"),
                (AssemblyTableRecipe) new AssemblyTableRecipe()
                        .addInput(new ItemRecipeInput(Item.REDSTONE))
                        .addOutput(new RecipeOutput(new ItemStack(Buildcraft.redstoneChipset, 1)))
        );
        
        event.register(
                NAMESPACE.id("iron_chipset"),
                (AssemblyTableRecipe) new AssemblyTableRecipe()
                        .addInput(new ItemRecipeInput(Item.REDSTONE))
                        .addInput(new ItemRecipeInput(Item.IRON_INGOT))
                        .addOutput(new RecipeOutput(new ItemStack(Buildcraft.redstoneIronChipset, 1)))
        );

        event.register(
                NAMESPACE.id("golden_chipset"),
                (AssemblyTableRecipe) new AssemblyTableRecipe()
                        .addInput(new ItemRecipeInput(Item.REDSTONE))
                        .addInput(new ItemRecipeInput(Item.GOLD_INGOT))
                        .addOutput(new RecipeOutput(new ItemStack(Buildcraft.redstoneGoldenChipset, 1)))
        );

        event.register(
                NAMESPACE.id("diamond_chipset"),
                (AssemblyTableRecipe) new AssemblyTableRecipe()
                        .addInput(new ItemRecipeInput(Item.REDSTONE))
                        .addInput(new ItemRecipeInput(Item.DIAMOND))
                        .addOutput(new RecipeOutput(new ItemStack(Buildcraft.redstoneDiamondChipset, 1)))
        );

        event.register(
                NAMESPACE.id("pulsating_chipset"),
                (AssemblyTableRecipe) new AssemblyTableRecipe()
                        .addInput(new ItemRecipeInput(Item.REDSTONE))
                        .addInput(new ItemRecipeInput(Item.DYE, 1, 5))
                        .addOutput(new RecipeOutput(new ItemStack(Buildcraft.pulsatingChipset, 2)))
        );
    }
}
