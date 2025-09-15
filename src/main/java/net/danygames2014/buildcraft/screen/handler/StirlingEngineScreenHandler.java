package net.danygames2014.buildcraft.screen.handler;

import net.danygames2014.buildcraft.block.entity.StirlingEngineBlockEntity;
import net.danygames2014.buildcraft.screen.slot.StirlingEngineFuelSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class StirlingEngineScreenHandler extends ScreenHandler {
    private PlayerEntity player;
    private Inventory playerInventory;
    
    private StirlingEngineBlockEntity engine;
    
    private int burnTime;

    public StirlingEngineScreenHandler(PlayerEntity player, StirlingEngineBlockEntity engine) {
        this.player = player;
        this.playerInventory = player.inventory;
        this.engine = engine;

        int playerInventoryVerticalOffset = 168 / 2;
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
        for (row = 0; row < 9; row++) {
            this.addSlot(new Slot(playerInventory,
                            row,
                            playerInventoryHorizontalOffset + 8 + (row * 18),
                            playerInventoryVerticalOffset + 58
                    )
            );
        }

        // The Generator Slot
        this.addSlot(
                new StirlingEngineFuelSlot(
                        engine,
                        0,
                        80,
                        41
                )
        );
    }

    @Environment(EnvType.SERVER)
    @Override
    public void addListener(ScreenHandlerListener listener) {
        super.addListener(listener);
        listener.onPropertyUpdate(this, 0, this.engine.getBurnTime());
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();

        for (var listenerO : this.listeners) {
            if (listenerO instanceof ScreenHandlerListener listener) {
                if (this.burnTime != this.engine.getBurnTime()) {
                    this.burnTime = this.engine.getBurnTime();
                    listener.onPropertyUpdate(this, 0, this.burnTime);
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void setProperty(int id, int value) {
        switch (id) {
            case 0 -> {
                this.burnTime = value;
            }
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
