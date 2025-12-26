package net.danygames2014.buildcraft.screen.handler;

import net.danygames2014.buildcraft.block.entity.BuilderBlockEntity;
import net.danygames2014.buildcraft.screen.slot.ArchitectTableInputSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;

public class BuilderScreenHandler extends ScreenHandler {
    public BuilderBlockEntity blockEntity;
    public PlayerEntity player;
    public Inventory playerInventory;
    
    int builderStatus = 0;
    int remainingBlocks = 0;

    public BuilderScreenHandler(PlayerEntity player, BuilderBlockEntity blockEntity) {
        this.player = player;
        this.playerInventory = player.inventory;
        this.blockEntity = blockEntity;

        int playerInventoryVerticalOffset = 140;
        int playerInventoryHorizontalOffset = 0;

        // Player Inventory
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory,
                                column + (row * 9) + 9,
                                playerInventoryHorizontalOffset + 8 + (column * 18),
                                playerInventoryVerticalOffset + (row * 18)
                        )
                );
            }
        }

        // Player Hotbar
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory,
                            column,
                            playerInventoryHorizontalOffset + 8 + (column * 18),
                            playerInventoryVerticalOffset + 58
                    )
            );
        }
        
        this.addSlot(new ArchitectTableInputSlot(blockEntity, 0, 80, 27));
        
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(blockEntity, 
                        column + (row * 9) + 1, 
                        8 + column * 18, 
                        72 + row * 18
                ));
            }
        }
    }

    @Environment(EnvType.SERVER)
    @Override
    public void addListener(ScreenHandlerListener listener) {
        super.addListener(listener);
        listener.onPropertyUpdate(this, 0, this.blockEntity.state.ordinal());
        listener.onPropertyUpdate(this, 1, this.blockEntity.remainingBlocks);
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();

        for (var listenerO : this.listeners) {
            if (listenerO instanceof ScreenHandlerListener listener) {
                if (this.builderStatus != this.blockEntity.state.ordinal()) {
                    this.builderStatus = this.blockEntity.state.ordinal();
                    listener.onPropertyUpdate(this, 0, this.blockEntity.state.ordinal());
                }
                
                if (this.remainingBlocks != this.blockEntity.remainingBlocks) {
                    this.remainingBlocks = this.blockEntity.remainingBlocks;
                    listener.onPropertyUpdate(this, 1, this.blockEntity.remainingBlocks);
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void setProperty(int id, int value) {
        switch (id) {
            case 0 -> {
                this.blockEntity.state = BuilderBlockEntity.BuilderState.values()[value];
            }
            case 1 -> {
                this.blockEntity.remainingBlocks = value;
            }
        }
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
