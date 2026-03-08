package net.danygames2014.buildcraft.inventory.slot;

import net.danygames2014.buildcraft.screen.handler.BlueprintLibraryScreenHandler;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class BlueprintLibraryOutputSlot extends Slot {
    BlueprintLibraryScreenHandler handler;
    
    public BlueprintLibraryOutputSlot(BlueprintLibraryScreenHandler handler, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.handler = handler;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public void onTakeItem(ItemStack stack) {
        handler.onBlueprintUpdate();
        super.onTakeItem(stack);
    }

    @Override
    public void setStack(ItemStack stack) {
        handler.onBlueprintUpdate();
        super.setStack(stack);
    }

    @Override
    public ItemStack takeStack(int amount) {
        handler.onBlueprintUpdate();
        return super.takeStack(amount);
    }
}
