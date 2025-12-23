package net.danygames2014.buildcraft.screen.slot;

import net.danygames2014.buildcraft.block.entity.pipe.DiamondPipeBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class DiamondPipeFilterSlot extends Slot {
    public final Inventory inventory;
    public final int index;
    
    public DiamondPipeFilterSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.inventory = inventory;
        this.index = index;
    }

    @Override
    public void onTakeItem(ItemStack stack) {
        inventory.setStack(index, null);
        if (inventory instanceof DiamondPipeBlockEntity pipe) {
            pipe.filterChanged();
        }
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        inventory.setStack(index, new ItemStack(stack.itemId, 1, stack.getDamage()));
        if (inventory instanceof DiamondPipeBlockEntity pipe) {
            pipe.filterChanged();
        }
        return false;
    }
}
