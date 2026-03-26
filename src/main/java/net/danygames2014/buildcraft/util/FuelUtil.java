package net.danygames2014.buildcraft.util;

import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.recipe.FuelRegistry;

public class FuelUtil {
    public static int getEngineFuelTime(ItemStack stack) {
        if (stack != null) {
            return FuelRegistry.getFuelTime(stack);
        }
        return 0;
    }
}
