package net.danygames2014.buildcraft.screen.handler;

import net.danygames2014.buildcraft.block.entity.BlueprintLibraryBlockEntity;
import net.danygames2014.buildcraft.item.BlueprintData;
import net.danygames2014.buildcraft.item.BlueprintManager;
import net.danygames2014.buildcraft.item.BlueprintPersistentState;
import net.danygames2014.buildcraft.packet.BlueprintLibraryBlueprintPacket;
import net.danygames2014.buildcraft.screen.slot.BlueprintLibraryInputSlot;
import net.danygames2014.buildcraft.screen.slot.BlueprintLibraryOutputSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.util.SideUtil;

public class BlueprintLibraryScreenHandler extends ScreenHandler {
    public final PlayerEntity player;
    public final World world;
    public final Inventory playerInventory;

    public final BlueprintLibraryBlockEntity blockEntity;

    @Environment(EnvType.CLIENT)
    public String selectedBlueprintName;

    public BlueprintLibraryScreenHandler(PlayerEntity player, BlueprintLibraryBlockEntity blockEntity) {
        this.player = player;
        this.world = player.world;
        this.playerInventory = player.inventory;
        this.blockEntity = blockEntity;

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

        this.addSlot(new BlueprintLibraryInputSlot(this, blockEntity, 0, 153, 61));
        this.addSlot(new BlueprintLibraryOutputSlot(this, blockEntity, 1, 109, 61));
        this.addSlot(new BlueprintLibraryInputSlot(this, blockEntity, 2, 109, 79));
        this.addSlot(new BlueprintLibraryOutputSlot(this, blockEntity, 3, 153, 79));

        SideUtil.run(() -> {
            //noinspection Convert2MethodRef
            BlueprintManager.loadBlueprintList();
        }, () -> {
        });
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onAcknowledgementAccepted(short actionType) {
        super.onAcknowledgementAccepted(actionType);
        onBlueprintUpdate();
    }

    public void onBlueprintUpdate() {
        // Reading from blueprint
        if (blockEntity.getStack(0) != null && blockEntity.getStack(1) == null) {
            BlueprintData blueprintData = getBlueprintData();
            
            if (blueprintData == null) {
                return;
            }
            
            switch (FabricLoader.getInstance().getEnvironmentType()) {
                case CLIENT -> {
                    if (!world.isRemote) {
                        PacketHelper.sendTo(player, new BlueprintLibraryBlueprintPacket(blueprintData, 1));
                    }
                }
                
                case SERVER -> {
                    PacketHelper.sendTo(player, new BlueprintLibraryBlueprintPacket(blueprintData, 1));
                    ItemStack blueprintStack = blockEntity.getStack(0);
                    blockEntity.setStack(1, blueprintStack);
                    onSlotUpdate(blockEntity);
                    blockEntity.setStack(0, null);
                    onSlotUpdate(blockEntity);
                }
            }
        }

        // Writing to blueprint
        if (blockEntity.getStack(2) != null && blockEntity.getStack(3) == null) {
            SideUtil.run(() -> {
                BlueprintData selectedBlueprint;

                if (!BlueprintManager.blueprints.isEmpty()) {
                    selectedBlueprint = BlueprintManager.load(selectedBlueprintName);
                } else {
                    return;
                }

                uploadBlueprint(selectedBlueprint);
            }, () -> {
            });
        }
    }

    @Environment(EnvType.CLIENT)
    public void uploadBlueprint(BlueprintData blueprint) {
        if (blueprint == null) {
            return;
        }

        if (blockEntity.getStack(2) != null && blockEntity.getStack(3) == null) {
            PacketHelper.send(new BlueprintLibraryBlueprintPacket(blueprint, 1));
        }
    }

    public BlueprintData getBlueprintData() {
        ItemStack stack = blockEntity.getStack(0);
        if (stack == null || blockEntity.getStack(1) != null) {
            return null;
        }

        if (!stack.getStationNbt().contains("written")) {
            return null;
        }

        if (stack.getDamage() == 0) {
            return null;
        }

        return BlueprintPersistentState.get(world, stack.getDamage()).data;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
