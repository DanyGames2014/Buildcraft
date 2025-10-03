package net.danygames2014.buildcraft.screen.handler;

import net.danygames2014.buildcraft.block.entity.AssemblyTableBlockEntity;
import net.danygames2014.buildcraft.packet.AssemblyTableUpdateS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;

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
    public void sendContentUpdates() {
        super.sendContentUpdates();
        sendTableUpdatePacket();
    }

    public void sendTableUpdatePacket() {
        int recipeCount = blockEntity.recipes.size();
        
        int[] resultIds = new int[recipeCount];
        byte[] resultAmounts = new byte[recipeCount];
        boolean[] selected = new boolean[recipeCount];
        boolean[] active = new boolean[recipeCount];
        
        for (var i = 0; i < recipeCount; i++) {
            var recipe = blockEntity.recipes.get(i);
            
            resultIds[i] = recipe.icon.itemId;
            resultAmounts[i] = (byte) recipe.icon.count;
            selected[i] = recipe.selected;
            active[i] = recipe.active;
        }

        PacketHelper.sendTo(player, new AssemblyTableUpdateS2CPacket(resultIds, resultAmounts, selected, active));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
