package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class PipeBlockEntityInventory extends PipeBlockEntity implements Inventory {
    @Override
    public int size() {
        return 0;
    }

    @Override
    public ItemStack getStack(int slot) {
        return null;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return null;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {

    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public int getMaxCountPerStack() {
        return 0;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }
}
