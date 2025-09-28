package net.danygames2014.buildcraft.inventory;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;


public class AutocraftingTableInventory extends CraftingInventory {
    private ScreenHandler handler;
    private Inventory parent;
    
    public AutocraftingTableInventory(ScreenHandler handler, Inventory parent) {
        super(handler, 3, 3);
        this.handler = handler;
        this.parent = parent;
    }
}
