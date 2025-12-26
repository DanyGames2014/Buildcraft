package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.event.AssemblyTableRecipeRegisterEvent;
import net.danygames2014.buildcraft.event.RefineryRecipeRegisterEvent;
import net.danygames2014.buildcraft.recipe.machine.AssemblyTableRecipe;
import net.danygames2014.buildcraft.recipe.machine.input.ItemRecipeInput;
import net.danygames2014.buildcraft.recipe.machine.output.RecipeOutput;
import net.danygames2014.buildcraft.recipe.refinery.RefineryRecipe;
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
                (AssemblyTableRecipe) new AssemblyTableRecipe(10000)
                        .addInput(new ItemRecipeInput(Item.REDSTONE))
                        .addOutput(new RecipeOutput(new ItemStack(Buildcraft.redstoneChipset, 1)))
        );
        
        event.register(
                NAMESPACE.id("iron_chipset"),
                (AssemblyTableRecipe) new AssemblyTableRecipe(20000)
                        .addInput(new ItemRecipeInput(Item.REDSTONE))
                        .addInput(new ItemRecipeInput(Item.IRON_INGOT))
                        .addOutput(new RecipeOutput(new ItemStack(Buildcraft.redstoneIronChipset, 1)))
        );

        event.register(
                NAMESPACE.id("golden_chipset"),
                (AssemblyTableRecipe) new AssemblyTableRecipe(40000)
                        .addInput(new ItemRecipeInput(Item.REDSTONE))
                        .addInput(new ItemRecipeInput(Item.GOLD_INGOT))
                        .addOutput(new RecipeOutput(new ItemStack(Buildcraft.redstoneGoldenChipset, 1)))
        );

        event.register(
                NAMESPACE.id("diamond_chipset"),
                (AssemblyTableRecipe) new AssemblyTableRecipe(80000)
                        .addInput(new ItemRecipeInput(Item.REDSTONE))
                        .addInput(new ItemRecipeInput(Item.DIAMOND))
                        .addOutput(new RecipeOutput(new ItemStack(Buildcraft.redstoneDiamondChipset, 1)))
        );

        event.register(
                NAMESPACE.id("pulsating_chipset"),
                (AssemblyTableRecipe) new AssemblyTableRecipe(40000)
                        .addInput(new ItemRecipeInput(Item.REDSTONE))
                        .addInput(new ItemRecipeInput(Item.DYE, 1, 5))
                        .addOutput(new RecipeOutput(new ItemStack(Buildcraft.pulsatingChipset, 2)))
        );

        event.register(
                NAMESPACE.id("red_pipe_wire"),
                (AssemblyTableRecipe) new AssemblyTableRecipe(500)
                        .addInput(new ItemRecipeInput(Item.REDSTONE))
                        .addInput(new ItemRecipeInput(Item.IRON_INGOT))
                        .addInput(new ItemRecipeInput(Item.DYE, 1, 1))
                        .addOutput(new RecipeOutput(new ItemStack(Buildcraft.redPipeWire, 8)))
        );

        event.register(
                NAMESPACE.id("blue_pipe_wire"),
                (AssemblyTableRecipe) new AssemblyTableRecipe(500)
                        .addInput(new ItemRecipeInput(Item.REDSTONE))
                        .addInput(new ItemRecipeInput(Item.IRON_INGOT))
                        .addInput(new ItemRecipeInput(Item.DYE, 1, 4))
                        .addOutput(new RecipeOutput(new ItemStack(Buildcraft.bluePipeWire, 8)))
        );

        event.register(
                NAMESPACE.id("green_pipe_wire"),
                (AssemblyTableRecipe) new AssemblyTableRecipe(500)
                        .addInput(new ItemRecipeInput(Item.REDSTONE))
                        .addInput(new ItemRecipeInput(Item.IRON_INGOT))
                        .addInput(new ItemRecipeInput(Item.DYE, 1, 2))
                        .addOutput(new RecipeOutput(new ItemStack(Buildcraft.greenPipeWire, 8)))
        );

        event.register(
                NAMESPACE.id("yellow_pipe_wire"),
                (AssemblyTableRecipe) new AssemblyTableRecipe(500)
                        .addInput(new ItemRecipeInput(Item.REDSTONE))
                        .addInput(new ItemRecipeInput(Item.IRON_INGOT))
                        .addInput(new ItemRecipeInput(Item.DYE, 1, 11))
                        .addOutput(new RecipeOutput(new ItemStack(Buildcraft.yellowPipeWire, 8)))
        );
    }
    
    @EventListener
    public void registerRefineryRecipes(RefineryRecipeRegisterEvent event) {
        event.register(
                NAMESPACE.id("oil_to_fuel"),
                new RefineryRecipe(FluidListener.oil, 1, FluidListener.fuel, 1)
        );
    }
}
