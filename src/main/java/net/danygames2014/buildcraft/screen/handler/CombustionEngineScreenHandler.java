package net.danygames2014.buildcraft.screen.handler;

import net.danygames2014.buildcraft.block.entity.CombustionEngineBlockEntity;
import net.danygames2014.buildcraft.screen.slot.CombustionEngineFuelSlot;
import net.danygames2014.buildcraft.screen.slot.InvisibleFluidSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

@SuppressWarnings("FieldCanBeLocal")
public class CombustionEngineScreenHandler extends ScreenHandler {
    private final PlayerEntity player;
    private final Inventory playerInventory;

    private final CombustionEngineBlockEntity engine;

    public CombustionEngineScreenHandler(PlayerEntity player, CombustionEngineBlockEntity engine) {
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
                new CombustionEngineFuelSlot(
                        engine,
                        0,
                        52,
                        41
                )
        );

        this.addFluidSlot(
                new InvisibleFluidSlot(
                        engine,
                        0,
                        122,
                        19,
                        16,
                        59
                )
        );

        this.addFluidSlot(
                new InvisibleFluidSlot(
                        engine,
                        1,
                        104,
                        19,
                        16,
                        59
                )
        );
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
