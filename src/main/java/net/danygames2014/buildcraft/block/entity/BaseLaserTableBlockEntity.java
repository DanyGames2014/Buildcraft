package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.api.energy.ILaserTarget;
import net.danygames2014.buildcraft.inventory.SimpleInventory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public abstract class BaseLaserTableBlockEntity extends BlockEntity implements ILaserTarget, Inventory {
    protected SimpleInventory inventory = new SimpleInventory(12, "Laser Table", this::markDirty);
    
    // ILaserTarget
    @Override
    public boolean requiresLaserEnergy() {
        // I'm setting this to true for testing
        return true;
    }

    @Override
    public void receiveLaserEnergy(double energy) {

    }

    @Override
    public boolean isInvalidTarget() {
        return false;
    }

    @Override
    public int getXCoord() {
        return x;
    }

    @Override
    public int getYCoord() {
        return y;
    }

    @Override
    public int getZCoord() {
        return z;
    }

    // Inventory
    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return inventory.removeStack(slot, amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.setStack(slot, stack);
    }

    @Override
    public String getName() {
        return inventory.getName();
    }

    @Override
    public int getMaxCountPerStack() {
        return inventory.getMaxCountPerStack();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }
    
    // NBT
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        inventory.writeNbt(nbt);
    }
    
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        inventory.readNbt(nbt);
    }
}
