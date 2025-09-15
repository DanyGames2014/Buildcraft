package net.danygames2014.buildcraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class SimpleInventory implements Inventory {
    private ItemStack[] stacks;
    private final String name;
    private final MarkDirtyCallback markDirtyCallback;

    public SimpleInventory(int size, String name, MarkDirtyCallback markDirtyCallback) {
        this.stacks = new ItemStack[size];
        this.name = name;
        this.markDirtyCallback = markDirtyCallback;
    }

    @Override
    public int size() {
        return stacks.length;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot < 0 || slot >= stacks.length) {
            return null;
        }

        return stacks[slot];
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot < 0 || slot >= stacks.length) {
            return null;
        }

        if (this.stacks[slot] != null) {
            ItemStack stack;

            if (this.stacks[slot].count <= amount) {
                stack = this.stacks[slot];
                this.stacks[slot] = null;
            } else {
                stack = this.stacks[slot].split(amount);
                if (this.stacks[slot].count == 0) {
                    this.stacks[slot] = null;
                }

            }

            this.markDirty();
            return stack;
        }

        return null;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= stacks.length) {
            return;
        }

        this.stacks[slot] = stack;
        if (stack != null && stack.count > this.getMaxCountPerStack()) {
            stack.count = this.getMaxCountPerStack();
        }
        this.markDirty();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    @Override
    public void markDirty() {
        this.markDirtyCallback.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    public interface MarkDirtyCallback {
        void markDirty();
    }

    // NBT
    public void readNbt(NbtCompound nbt) {
        NbtList items = nbt.getList("Items");
        this.stacks = new ItemStack[this.size()];

        for (int index = 0; index < items.size(); ++index) {
            NbtCompound itemNbt = (NbtCompound) items.get(index);
            int var5 = itemNbt.getByte("Slot") & 255;
            if (var5 < this.stacks.length) {
                this.stacks[var5] = new ItemStack(itemNbt);
            }
        }

    }

    public void writeNbt(NbtCompound nbt) {
        NbtList items = new NbtList();

        for (int index = 0; index < this.stacks.length; ++index) {
            if (this.stacks[index] != null) {
                NbtCompound itemNbt = new NbtCompound();
                itemNbt.putByte("Slot", (byte) index);
                this.stacks[index].writeNbt(itemNbt);
                items.add(itemNbt);
            }
        }

        nbt.put("Items", items);
    }
}
