package net.danygames2014.buildcraft.screen.handler;

import net.danygames2014.buildcraft.block.entity.pipe.DiamondPipeBlockEntity;
import net.danygames2014.buildcraft.screen.slot.DiamondPipeFilterSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;

public class DiamondPipeScreenHandler extends ScreenHandler {
    public PlayerEntity player;
    public Inventory playerInventory;

    public DiamondPipeBlockEntity pipe;
    private int filterMeta;
    private int filterTags;

    public DiamondPipeScreenHandler(PlayerEntity player, DiamondPipeBlockEntity pipe) {
        this.player = player;
        this.playerInventory = player.inventory;
        this.pipe = pipe;
        if (this.pipe.world == null) {
            this.pipe.world = this.player.world;
        }

        int playerInventoryVerticalOffset = 140;
        int playerInventoryHorizontalOffset = 0;

        int row;
        int column;

        // Player Inventory
        for (row = 0; row < 3; row++) {
            for (column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory,
                                column + (row * 9) + 9,
                                playerInventoryHorizontalOffset + 8 + (column * 18),
                                playerInventoryVerticalOffset + (row * 18)
                        )
                );
            }
        }

        // Player Hotbar
        for (column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory,
                            column,
                            playerInventoryHorizontalOffset + 8 + (column * 18),
                            playerInventoryVerticalOffset + 58
                    )
            );
        }

        // Filter Slots
        for (row = 0; row < 6; row++) {
            for (column = 0; column < 9; column++) {
                this.addSlot(new DiamondPipeFilterSlot(pipe,
                        column + (row * 9),
                        8 + column * 18,
                        18 + row * 18
                ));
            }
        }
    }

    @Environment(EnvType.SERVER)
    @Override
    public void addListener(ScreenHandlerListener listener) {
        super.addListener(listener);
        listener.onPropertyUpdate(this, 0, this.pipe.filterMeta ? 1 : 0);
        listener.onPropertyUpdate(this, 1, this.pipe.filterTags ? 1 : 0);
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();

        for (var listenerO : this.listeners) {
            if (listenerO instanceof ScreenHandlerListener listener) {
                if (this.filterMeta != (this.pipe.filterMeta ? 1 : 0)) {
                    this.filterMeta = (this.pipe.filterMeta ? 1 : 0);
                    listener.onPropertyUpdate(this, 0, this.filterMeta);
                }

                if (this.filterTags != (this.pipe.filterTags ? 1 : 0)) {
                    this.filterTags = (this.pipe.filterTags ? 1 : 0);
                    listener.onPropertyUpdate(this, 1, this.filterTags);
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void setProperty(int id, int value) {
        switch (id) {
            case 0 -> {
                this.pipe.filterMeta = (value == 1);
            }
            case 1 -> {
                this.pipe.filterTags = (value == 1);
            }
        }
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
