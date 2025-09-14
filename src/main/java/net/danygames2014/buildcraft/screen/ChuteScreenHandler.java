package net.danygames2014.buildcraft.screen;

import net.danygames2014.buildcraft.block.entity.ChuteBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class ChuteScreenHandler extends ScreenHandler {

    Inventory playerInventory;
    ChuteBlockEntity chuteBlockEntity;

    public ChuteScreenHandler(PlayerInventory playerInventory, ChuteBlockEntity blockEntity){
        this.playerInventory = playerInventory;
        this.chuteBlockEntity = blockEntity;

        this.addSlot(new Slot(chuteBlockEntity, 0, 62, 18));
        this.addSlot(new Slot(chuteBlockEntity, 1, 80, 18));
        this.addSlot(new Slot(chuteBlockEntity, 2, 98, 18));
        this.addSlot(new Slot(chuteBlockEntity, 3, 80, 36));

        for (int slotRow = 0; slotRow < 3; slotRow++) {
            for (int slotCol = 0; slotCol < 9; slotCol++) {
                addSlot(new Slot(playerInventory, slotCol + slotRow * 9 + 9, 8 + slotCol * 18, 71 + slotRow * 18));
            }
        }


        for (int slotCol = 0; slotCol < 9; slotCol++) {
            addSlot(new Slot(playerInventory, slotCol, 8 + slotCol * 18, 129));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return chuteBlockEntity.canPlayerUse(player);
    }
}
