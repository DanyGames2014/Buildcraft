package net.danygames2014.buildcraft.api.transport.gate;

import com.google.common.collect.HashBiMap;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.*;

public class GateExpansions {
    private static final Map<Identifier, GateExpansion> expansions = new HashMap<>();
    private static final ArrayList<GateExpansion> expansionIDs = new ArrayList<>();
    private static final Map<GateExpansion, ItemStack> recipes = HashBiMap.create();

    private GateExpansions() {
    }

    public static void registerExpansion(GateExpansion expansion) {
        registerExpansion(expansion.getIdentifier(), expansion);
    }

    public static void registerExpansion(Identifier identifier, GateExpansion expansion) {
        expansions.put(identifier, expansion);
        expansionIDs.add(expansion);
    }

    public static void registerExpansion(GateExpansion expansion, ItemStack addedRecipe) {
        registerExpansion(expansion.getIdentifier(), expansion);
        recipes.put(expansion, addedRecipe);
    }

    public static GateExpansion getExpansion(Identifier identifier) {
        return expansions.get(identifier);
    }

    public static Set<GateExpansion> getExpansions() {
        Set<GateExpansion> set = new HashSet<>();
        set.addAll(expansionIDs);
        return set;
    }

    public static Map<GateExpansion, ItemStack> getRecipesForPostInit() {
        return recipes;
    }

    // The code below is used by networking.

    public static GateExpansion getExpansionByID(int id) {
        return expansionIDs.get(id);
    }

    public static int getExpansionID(GateExpansion expansion) {
        return expansionIDs.indexOf(expansion);
    }
}
