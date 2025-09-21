package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.inventory.SimpleInventory;
import net.danygames2014.nyalib.item.ItemHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public abstract class BaseEngineWithInventoryBlockEntity extends BaseEngineBlockEntity implements Inventory, ItemHandler {
    private final SimpleInventory inventory;

    public BaseEngineWithInventoryBlockEntity(int size) {
        this.inventory = new SimpleInventory(size, "Engine", this::markDirty);
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

    // ItemHandler
    @Override
    public boolean canConnectItem(Direction side) {
        return side != facing;
    }

    @Override
    public ItemStack[] getInventory(@Nullable Direction side) {
        return inventory.stacks;
    }

    @Override
    public int getItemSlots(@Nullable Direction side) {
        return inventory.size();
    }

    @Override
    public boolean setItem(ItemStack stack, int slot, @Nullable Direction side) {
        if (slot < 0 || slot >= inventory.size()) {
            return false;
        }
        inventory.stacks[slot] = stack;
        return true;
    }

    @Override
    public ItemStack getItem(int slot, @Nullable Direction side) {
        if (slot < 0 || slot >= inventory.size()) {
            return null;
        }
        return inventory.stacks[slot];
    }

    @Override
    public ItemStack insertItem(ItemStack stack, @Nullable Direction side) {
        ItemStack insertedStack = stack.copy();

        for (int i = 0; i < this.getItemSlots(side); ++i) {
            insertedStack = insertItem(insertedStack, i, side);
            if (insertedStack == null) {
                return insertedStack;
            }
        }

        return insertedStack;
    }

    @Override
    public ItemStack insertItem(ItemStack stack, int slot, @Nullable Direction side) {
        ItemStack slotStack;

        slotStack = inventory.getStack(slot);

        if (slotStack == null) {
            inventory.setStack(slot, stack);
            return null;
        }

        if (slotStack.isItemEqual(stack)) {
            int addedCount = Math.min(slotStack.getItem().getMaxCount() - slotStack.count, stack.count);

            slotStack.count += addedCount;

            if (addedCount >= stack.count) {
                return null;
            } else {
                return new ItemStack(stack.getItem(), stack.count - addedCount, stack.getDamage());
            }
        }

        return stack;
    }

    @Override
    public boolean canInsertItem(@Nullable Direction side) {
        return side != facing;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, @Nullable Direction side) {
        return this.removeStack(slot, amount);
    }

    @Override
    public boolean canExtractItem(@Nullable Direction side) {
        return side != facing;
    }

    // NBT
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        inventory.readNbt(nbt);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        inventory.writeNbt(nbt);
    }
}
