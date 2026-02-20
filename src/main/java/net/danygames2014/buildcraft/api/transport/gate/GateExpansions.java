package net.danygames2014.buildcraft.api.transport.gate;

import com.google.common.collect.HashBiMap;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.*;

public class GateExpansions {
    private static final Map<Identifier, GateExpansion> IDENTIFIER_TO_EXPANSION = new HashMap<>();
    private static final ArrayList<GateExpansion> expansions = new ArrayList<>();
    private static final Map<GateExpansion, ItemStack> recipes = HashBiMap.create();
    public static final Map<GateExpansion, Integer> indexMap = new HashMap<>();

    private static int nextId = 0;

    private GateExpansions() {
    }

    public static void registerExpansion(GateExpansion expansion) {
        registerExpansion(expansion.getIdentifier(), expansion);
    }

    public static void registerExpansion(Identifier identifier, GateExpansion expansion) {
        IDENTIFIER_TO_EXPANSION.put(identifier, expansion);
        expansions.add(expansion);
        indexMap.computeIfAbsent(expansion, c -> nextId++);
    }

    public static void registerExpansion(GateExpansion expansion, ItemStack addedRecipe) {
        registerExpansion(expansion.getIdentifier(), expansion);
        recipes.put(expansion, addedRecipe);
    }

    public static GateExpansion getExpansion(Identifier identifier) {
        return IDENTIFIER_TO_EXPANSION.get(identifier);
    }

    public static Set<GateExpansion> getExpansions() {
        return new HashSet<>(expansions);
    }

    public static Map<GateExpansion, ItemStack> getRecipesForPostInit() {
        return recipes;
    }

    // The code below is used by networking.

    public static GateExpansion getExpansionByID(int id) {
        return expansions.get(id);
    }

    public static int getExpansionID(GateExpansion expansion) {
        return expansions.indexOf(expansion);
    }
}
