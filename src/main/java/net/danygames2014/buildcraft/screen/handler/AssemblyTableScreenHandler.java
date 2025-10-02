package net.danygames2014.buildcraft.screen.handler;

import net.danygames2014.buildcraft.block.entity.AssemblyTableBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class AssemblyTableScreenHandler extends ScreenHandler {
    public PlayerEntity player;
    public Inventory playerInventory;
    public AssemblyTableBlockEntity blockEntity;
    
    public AssemblyTableScreenHandler(PlayerEntity player, AssemblyTableBlockEntity blockEntity) {
        this.player = player;
        this.playerInventory = player.inventory;
        this.blockEntity = blockEntity;

        // Assembly Table Slots
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 3; column++) {
                this.addSlot(new Slot(blockEntity, column + row * 3, 8 + column * 18, 36 + row * 18));
            }
        }
        
        // Player Inventory
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 123 + row * 18));
            }
        }

        // Hotbar Slots
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 181));
        }
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
