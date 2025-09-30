package net.danygames2014.buildcraft.inventory.slot;

import net.danygames2014.buildcraft.block.entity.AutocraftingTableBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;

public class AutocraftingTableResultSlot extends CraftingResultSlot {
    public AutocraftingTableBlockEntity blockEntity;
    public PlayerEntity player;
    
    public AutocraftingTableResultSlot(PlayerEntity player, Inventory input, Inventory inventory, int index, int x, int y) {
        super(player, input, inventory, index, x, y);
        this.player = player;
        this.blockEntity = (AutocraftingTableBlockEntity) inventory;
    }

    @Override
    public void onTakeItem(ItemStack stack) {
        if (player != null) {
            stack.onCraft(this.player.world, this.player);
            
            player.inventory.setCursorStack(blockEntity.craft(false).copy());
        }
    }
}
