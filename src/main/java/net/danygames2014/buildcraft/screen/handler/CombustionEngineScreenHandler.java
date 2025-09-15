package net.danygames2014.buildcraft.screen.handler;

import net.danygames2014.buildcraft.block.entity.CombustionEngineBlockEntity;
import net.danygames2014.buildcraft.screen.slot.CombustionEngineFuelSlot;
import net.danygames2014.nyalib.fluid.FluidSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class CombustionEngineScreenHandler extends ScreenHandler {
    private PlayerEntity player;
    private Inventory playerInventory;

    private CombustionEngineBlockEntity engine;

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
                new FluidSlot(
                        engine,
                        0,
                        104,
                        61
                )
        );

        this.addFluidSlot(
                new FluidSlot(
                        engine,
                        1,
                        122,
                        61
                )
        );
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
