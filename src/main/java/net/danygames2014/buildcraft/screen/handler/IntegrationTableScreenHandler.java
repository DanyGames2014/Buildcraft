package net.danygames2014.buildcraft.screen.handler;

import net.danygames2014.buildcraft.block.entity.IntegrationTableBlockEntity;
import net.danygames2014.buildcraft.packet.IntegrationTablePreviewS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;

public class IntegrationTableScreenHandler extends ScreenHandler {
    public PlayerEntity player;
    public Inventory playerInventory;
    public IntegrationTableBlockEntity blockEntity;
    
    public int scaledProgress;
    
    public IntegrationTableScreenHandler(PlayerEntity player, IntegrationTableBlockEntity blockEntity) {
        this.player = player;
        this.playerInventory = player.inventory;
        this.blockEntity = blockEntity;

        // Player Inventory
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }

        // Hotbar Slots
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
        
        // Gate Input Slot
        this.addSlot(new Slot(blockEntity, 0, 17, 28));
        
        // Chipset Input Slot
        this.addSlot(new Slot(blockEntity, 1, 53, 28));
        
        // Preview Slot
        // x:116 y:44
        
        // Output Slot
        this.addSlot(new Slot(blockEntity, 2, 143, 44));
    }

    @Environment(EnvType.SERVER)
    public void sendTableUpdatePacket() {
        PacketHelper.sendTo(player, new IntegrationTablePreviewS2CPacket(blockEntity.previewStack));
    }

    @Environment(EnvType.SERVER)
    @Override
    public void addListener(ScreenHandlerListener listener) {
        super.addListener(listener);
        listener.onPropertyUpdate(this, 0, this.blockEntity.scaledProgress);
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();
        sendTableUpdatePacket();

        for (var listenerO : this.listeners) {
            if (listenerO instanceof ScreenHandlerListener listener) {
                if (this.scaledProgress != this.blockEntity.scaledProgress) {
                    this.scaledProgress = this.blockEntity.scaledProgress;
                    listener.onPropertyUpdate(this, 0, this.scaledProgress);
                }
            }
        }
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @Environment(EnvType.CLIENT)
    @Override
    public void setProperty(int id, int value) {
        switch (id) {
            case 0 -> {
                this.blockEntity.scaledProgress = value;
            }
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
