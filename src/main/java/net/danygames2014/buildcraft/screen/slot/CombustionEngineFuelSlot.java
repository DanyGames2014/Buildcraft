package net.danygames2014.buildcraft.screen.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class CombustionEngineFuelSlot extends Slot {
    public CombustionEngineFuelSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        
        return stack.getItem() instanceof BucketItem;
    }
}
