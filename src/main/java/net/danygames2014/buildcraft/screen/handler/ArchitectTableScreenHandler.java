package net.danygames2014.buildcraft.screen.handler;

import net.danygames2014.buildcraft.block.entity.ArchitectTableBlockEntity;
import net.danygames2014.buildcraft.packet.ArchitectTableNameFieldPacket;
import net.danygames2014.buildcraft.screen.slot.ArchitectTableInputSlot;
import net.danygames2014.buildcraft.screen.slot.ArchitectTableOutputSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;

public class ArchitectTableScreenHandler extends ScreenHandler {
    public PlayerEntity player;
    public Inventory playerInventory;
    
    public ArchitectTableBlockEntity blockEntity;
    private int progress;
    private String lastName = "";
    
    public ArchitectTableScreenHandler(PlayerEntity player, ArchitectTableBlockEntity blockEntity) {
        this.player = player;
        this.playerInventory = player.inventory;
        this.blockEntity = blockEntity;

        int playerInventoryVerticalOffset = 84;
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
        
        // Input
        this.addSlot(new ArchitectTableInputSlot(blockEntity, 0, 55, 35));
        
        // Output
        this.addSlot(new ArchitectTableOutputSlot(blockEntity, 1, 114, 35));
    }

    @Environment(EnvType.SERVER)
    @Override
    public void addListener(ScreenHandlerListener listener) {
        super.addListener(listener);
        listener.onPropertyUpdate(this, 0, this.blockEntity.progress);
    }
    
    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();

        for (var listenerO : this.listeners) {
            if (listenerO instanceof ScreenHandlerListener listener) {
                if (this.progress != this.blockEntity.progress) {
                    this.progress = this.blockEntity.progress;
                    listener.onPropertyUpdate(this, 0, this.progress);
                }
            }
        }
        
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            if (!this.blockEntity.blueprintName.equals(lastName)) {
                if (ArchitectTableNameFieldPacket.originators.containsKey(this.blockEntity.blueprintName)) {
                    if (ArchitectTableNameFieldPacket.originators.get(this.blockEntity.blueprintName) == this.player) {
                        lastName = this.blockEntity.blueprintName;
                        
                        if (ArchitectTableNameFieldPacket.originators.size() > 50) {
                            ArchitectTableNameFieldPacket.originators.clear();
                        }
                        return;
                    }
                }
                PacketHelper.sendTo(this.player, new ArchitectTableNameFieldPacket(this.blockEntity.blueprintName));
                lastName = this.blockEntity.blueprintName;
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void setProperty(int id, int value) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (id) {
            case 0 -> {
                this.blockEntity.progress = value;
            }
        }
    }

    @Override
    public ItemStack onSlotClick(int index, int button, boolean shift, PlayerEntity player) {
        blockEntity.lastTouchedBy = player.name;
        return super.onSlotClick(index, button, shift, player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
