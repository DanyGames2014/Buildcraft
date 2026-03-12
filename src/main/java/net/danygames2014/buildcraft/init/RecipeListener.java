package net.danygames2014.buildcraft.init;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.gate.GateExpansions;
import net.danygames2014.buildcraft.event.AssemblyTableRecipeRegisterEvent;
import net.danygames2014.buildcraft.event.IntegrationTableRecipeRegisterEvent;
import net.danygames2014.buildcraft.event.RefineryRecipeRegisterEvent;
import net.danygames2014.buildcraft.recipe.integration.IntegrationTableRecipe;
import net.danygames2014.buildcraft.recipe.machine.AssemblyTableRecipe;
import net.danygames2014.buildcraft.recipe.machine.input.ItemRecipeInput;
import net.danygames2014.buildcraft.recipe.machine.output.RecipeOutput;
import net.danygames2014.buildcraft.recipe.refinery.RefineryRecipe;
import net.danygames2014.buildcraft.util.ColorUtil;
import net.danygames2014.nyalib.fluid.Fluid;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
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
                NAMESPACE.id("emerald_chipset"),
                (AssemblyTableRecipe) new AssemblyTableRecipe(120000)
                        .addInput(new ItemRecipeInput(Item.REDSTONE))
                        .addInput(new ItemRecipeInput(Item.DIAMOND))
                        .addInput(new ItemRecipeInput(Item.DYE, 1, 10))
                        .addOutput(new RecipeOutput(new ItemStack(Buildcraft.redstoneEmeraldChipset, 1)))
        );

        event.register(
                NAMESPACE.id("glowstone_chipset"),
                (AssemblyTableRecipe) new AssemblyTableRecipe(60000)
                                              .addInput(new ItemRecipeInput(Item.REDSTONE))
                                              .addInput(new ItemRecipeInput(Item.GLOWSTONE_DUST))
                                              .addOutput(new RecipeOutput(new ItemStack(Buildcraft.redstoneGlowstoneChipset, 1)))
        );

        event.register(
                NAMESPACE.id("comp_chipset"),
                (AssemblyTableRecipe) new AssemblyTableRecipe(60000)
                                              .addInput(new ItemRecipeInput(Item.REDSTONE))
                                              .addInput(new ItemRecipeInput(Item.REPEATER))
                                              .addOutput(new RecipeOutput(new ItemStack(Buildcraft.redstoneCompChipset, 1)))
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
        
        event.register(
                NAMESPACE.id("pipe_plug"),
                (AssemblyTableRecipe) new AssemblyTableRecipe(1000)
                        .addInput(new ItemRecipeInput(Buildcraft.cobblestoneStructurePipe.asItem()))
                        .addOutput(new RecipeOutput(new ItemStack(Buildcraft.plug, 8)))
        );

        for (int i = 0; i < 16; i++) {
            event.register(
                    NAMESPACE.id(ColorUtil.getName(i) + "_lens"),
                    (AssemblyTableRecipe) new AssemblyTableRecipe(1000)
                            .addInput(new ItemRecipeInput(Block.GLASS.asItem()))
                            .addInput(new ItemRecipeInput(Item.DYE, 1, i))
                            .addOutput(new RecipeOutput(new ItemStack(Buildcraft.lens[i], 2)))
            );
        }

        for (int i = 0; i < 16; i++) {
            event.register(
                    NAMESPACE.id(ColorUtil.getName(i) + "_filter"),
                    (AssemblyTableRecipe) new AssemblyTableRecipe(1000)
                            .addInput(new ItemRecipeInput(Item.IRON_INGOT))
                            .addInput(new ItemRecipeInput(Block.GLASS.asItem()))
                            .addInput(new ItemRecipeInput(Item.DYE, 1, i))
                            .addOutput(new RecipeOutput(new ItemStack(Buildcraft.filter[i], 2)))
            );
        }
    }
    
    @EventListener
    public void registerIntegrationTableRecipes(IntegrationTableRecipeRegisterEvent event) {
        event.register(
                NAMESPACE.id("autarchic_pulsar"),
                new IntegrationTableRecipe(
                        new ItemRecipeInput(Buildcraft.pulsatingChipset),
                        GateExpansions.getExpansion(Buildcraft.NAMESPACE.id("pulsar")),
                        2000
                )
        );
        event.register(
                NAMESPACE.id("redstone_fader"),
                new IntegrationTableRecipe(
                        new ItemRecipeInput(Buildcraft.redstoneCompChipset),
                        GateExpansions.getExpansion(Buildcraft.NAMESPACE.id("fader")),
                        2000
                )
        );
        event.register(
                NAMESPACE.id("timer"),
                new IntegrationTableRecipe(
                        new ItemRecipeInput(Buildcraft.redstoneGlowstoneChipset),
                        GateExpansions.getExpansion(Buildcraft.NAMESPACE.id("timer")),
                        2000
                )
        );
        event.register(
                NAMESPACE.id("light_sensor"),
                new IntegrationTableRecipe(
                        new ItemRecipeInput(Block.TORCH.asItem()),
                        GateExpansions.getExpansion(Buildcraft.NAMESPACE.id("light_sensor")),
                        2000
                )
        );
    }
    
    @EventListener
    public void registerRefineryRecipes(RefineryRecipeRegisterEvent event) {
        event.register(
                NAMESPACE.id("oil_to_fuel"),
                new RefineryRecipe(new Fluid[]{FluidListener.oil, null}, new int[]{1, 0}, FluidListener.fuel, 1, 10, 1)
        );
    }
}
