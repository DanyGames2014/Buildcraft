package net.danygames2014.buildcraft.inventory.slot;

import net.danygames2014.buildcraft.item.BuilderBlueprintItem;
import net.danygames2014.buildcraft.item.BuilderTemplateItem;
import net.danygames2014.buildcraft.screen.handler.BlueprintLibraryScreenHandler;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class BlueprintLibraryInputSlot extends Slot {
    BlueprintLibraryScreenHandler handler;
    
    public BlueprintLibraryInputSlot(BlueprintLibraryScreenHandler handler, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.handler = handler;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.getItem() instanceof BuilderTemplateItem || stack.getItem() instanceof BuilderBlueprintItem;
    }

    @Override
    public void onTakeItem(ItemStack stack) {
        super.onTakeItem(stack);
        handler.onBlueprintUpdate();
    }

    @Override
    public void setStack(ItemStack stack) {
        super.setStack(stack);
        handler.onBlueprintUpdate();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        handler.onBlueprintUpdate();
    }
}
