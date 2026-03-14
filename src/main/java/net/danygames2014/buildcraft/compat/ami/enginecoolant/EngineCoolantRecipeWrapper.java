package net.danygames2014.buildcraft.compat.ami.enginecoolant;

import net.danygames2014.buildcraft.api.energy.EngineCoolant;
import net.danygames2014.buildcraft.compat.ami.FluidRecipeWrapper;
import net.glasslauncher.mods.alwaysmoreitems.api.gui.AMIDrawable;
import net.glasslauncher.mods.alwaysmoreitems.gui.DrawableHelper;
import net.glasslauncher.mods.alwaysmoreitems.util.HoverChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.language.TranslationStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class EngineCoolantRecipeWrapper extends FluidRecipeWrapper {
    private final AMIDrawable snowIcon = DrawableHelper.createDrawable("/assets/buildcraft/stationapi/textures/gui/fluidinfo_ami.png", 17, 65, 14, 15);
    private final HoverChecker snowHoverChecker = new HoverChecker(23, 38, 25, 39);

    private final EngineCoolant coolant;
    public EngineCoolantRecipeWrapper(EngineCoolant coolant) {
        super(coolant.fluid);
        this.coolant = coolant;
    }

    @Override
    public void drawInfo(@NotNull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        String degreesCooledPerMb = String.valueOf(coolant.getDegreesCooledPerMb(0));

        snowIcon.draw(minecraft, 25, 23);
        minecraft.textRenderer.drawWithShadow(degreesCooledPerMb, 43, 26, 0xFFFFFF);
    }

    @Override
    public @Nullable ArrayList<Object> getTooltip(int mouseX, int mouseY) {
        if(super.getTooltip(mouseX, mouseY) != null){
            return super.getTooltip(mouseX, mouseY);
        }
        if(snowHoverChecker.isOver(mouseX, mouseY)){
            return new ArrayList<>() {
                {add(TranslationStorage.getInstance().get("gui.buildcraft.engine_coolant.degrees_cooled_per_mb"));}
            };
        }
        return null;
    }
}
