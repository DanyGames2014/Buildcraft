package net.danygames2014.buildcraft.screen.slot;

import net.danygames2014.buildcraft.item.BuilderBlueprintItem;
import net.danygames2014.buildcraft.item.BuilderTemplateItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ArchitectTableInputSlot extends Slot {
    public ArchitectTableInputSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.getItem() instanceof BuilderTemplateItem || stack.getItem() instanceof BuilderBlueprintItem;
    }
}
