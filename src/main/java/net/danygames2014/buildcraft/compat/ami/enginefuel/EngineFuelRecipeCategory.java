package net.danygames2014.buildcraft.compat.ami.enginefuel;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.compat.ami.FluidInfoRecipeCategory;
import net.minecraft.client.resource.language.TranslationStorage;
import org.jetbrains.annotations.NotNull;

public class EngineFuelRecipeCategory extends FluidInfoRecipeCategory {
    @Override
    public @NotNull String getUid() {
        return Buildcraft.NAMESPACE.id("engine_fuel").toString();
    }

    @Override
    public @NotNull String getTitle() {
        return TranslationStorage.getInstance().get("category.buildcraft.engine_fuel");
    }
}
