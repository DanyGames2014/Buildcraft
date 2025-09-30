package net.danygames2014.buildcraft.inventory;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;


public class UnhandledAutocraftingTableInventory extends CraftingInventory {
    private final Inventory parent;

    public UnhandledAutocraftingTableInventory(ScreenHandler handler, Inventory parent) {
        super(handler, 3, 3);
        this.parent = parent;
    }

    @Override
    public int size() {
        return 9;
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot <= 9 ? this.parent.getStack(slot) : null;
    }

    @Override
    public ItemStack getStack(int x, int y) {
        if (x >= 0 && x < 3) {
            int index = x + (y * 3);
            return this.getStack(index);
        }
        return null;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return parent.removeStack(slot, amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.parent.setStack(slot, stack);
    }
}
