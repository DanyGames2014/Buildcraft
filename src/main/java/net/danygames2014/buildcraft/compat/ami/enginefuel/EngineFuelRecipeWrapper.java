package net.danygames2014.buildcraft.compat.ami.enginefuel;

import net.danygames2014.buildcraft.api.energy.EngineFuel;
import net.danygames2014.buildcraft.compat.ami.FluidRecipeWrapper;
import net.danygames2014.nyalib.fluid.Fluid;
import net.glasslauncher.mods.alwaysmoreitems.api.gui.AMIDrawable;
import net.glasslauncher.mods.alwaysmoreitems.gui.DrawableHelper;
import net.glasslauncher.mods.alwaysmoreitems.util.AMIHelpers;
import net.glasslauncher.mods.alwaysmoreitems.util.HoverChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.language.TranslationStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class EngineFuelRecipeWrapper extends FluidRecipeWrapper {
    private final EngineFuel fuel;

    private final AMIDrawable powerIcon = DrawableHelper.createDrawable("/assets/buildcraft/stationapi/textures/gui/fluidinfo_ami.png", 33, 65, 14, 14);
    private final AMIDrawable flameIcon = DrawableHelper.createDrawable("/assets/buildcraft/stationapi/textures/gui/fluidinfo_ami.png", 1, 65, 14, 14);

    private final HoverChecker powerHoverChecker = new HoverChecker(5, 19, 25, 39);
    private final HoverChecker flameHoverChecker = new HoverChecker(41, 55, 25, 39);

    public EngineFuelRecipeWrapper(EngineFuel fuel) {
        super(fuel.fluid);
        this.fuel = fuel;
    }

    @Override
    public void drawInfo(@NotNull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        String powerPerCycle = String.valueOf(fuel.powerPerCycle);
        String burnTime = String.valueOf(fuel.burnTime);



        powerIcon.draw(minecraft, 25, 5);
        minecraft.textRenderer.drawWithShadow(powerPerCycle, 43, 8, 0xFFFFFF);
        flameIcon.draw(minecraft, 25, 41);
        minecraft.textRenderer.drawWithShadow(burnTime, 43, 44, 0xFFFFFF);
    }

    @Override
    public @Nullable ArrayList<Object> getTooltip(int mouseX, int mouseY) {
        if(super.getTooltip(mouseX, mouseY) != null){
            return super.getTooltip(mouseX, mouseY);
        }
        if(powerHoverChecker.isOver(mouseX, mouseY)){
            return new ArrayList<>() {
                {add(TranslationStorage.getInstance().get("gui.buildcraft.engine_fuel.power_per_cycle"));}
            };
        }
        if(flameHoverChecker.isOver(mouseX, mouseY)){
            return new ArrayList<>() {
                {add(TranslationStorage.getInstance().get("gui.buildcraft.engine_fuel.burn_time"));}
            };
        }
        return null;
    }
}
