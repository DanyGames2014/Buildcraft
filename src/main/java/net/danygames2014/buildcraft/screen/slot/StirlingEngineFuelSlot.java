package net.danygames2014.buildcraft.screen.slot;

import net.danygames2014.buildcraft.util.FuelUtil;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class StirlingEngineFuelSlot extends Slot {
    public StirlingEngineFuelSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return FuelUtil.getEngineFuelTime(stack) > 0;
    }
}
