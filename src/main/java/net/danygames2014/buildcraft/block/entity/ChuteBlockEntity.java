package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.config.Config;
import net.danygames2014.buildcraft.inventory.SimpleInventory;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.List;

public class ChuteBlockEntity extends BlockEntity implements Inventory {
    private final SimpleInventory inventory = new SimpleInventory(4, "Chute", this::markDirty);
    private Box itemBox;

    @Override
    public void tick() {
        super.tick();
        if (world.isRemote || world.getTime() % getDelay() != 0) {
            return;
        }
        
        tickItemMovement();
    }
    
    public int getDelay() {
        return Config.MACHINE_CONFIG.chute.tickDelay;
    }
    
    public boolean canExtractFromAbove() {
        return Config.MACHINE_CONFIG.chute.allowInventoryExtraction;
    }
    
    public boolean canPickupItemsFromWorld() {
        return Config.MACHINE_CONFIG.chute.allowItemPickup;
    }
    
    public void tickItemMovement() {
        if (itemBox == null) {
            itemBox = Box.create(x, y + 1, z, x + 1, y + 1.6D, z + 1);
        }
        
        ItemHandlerBlockCapability capability = CapabilityHelper.getCapability(world, x, y + 1, z, ItemHandlerBlockCapability.class);
        if (capability != null) {
            if (canExtractFromAbove()) {
                extractFromAbove(capability);
            }
        } else {
            if (canPickupItemsFromWorld()) {
                extractItemsFromWorld();
            }
        }
        
        insertIntoBelow();
    }

    private void extractFromAbove(ItemHandlerBlockCapability capability) {
        if (capability.canExtractItem(Direction.DOWN)) {
            ItemStack[] stacks = inventory.stacks;
            
            for (int i = 0; i < size(); i++) {
                if (stacks[i] == null) {
                    ItemStack stack = capability.extractItem(1, Direction.DOWN);
                    if (stack != null) {
                        stacks[i] = stack.copy();
                        break;
                    }
                } else if (stacks[i].count < getMaxCountPerStack()) {
                    ItemStack stack = capability.extractItem(stacks[i].getItem(), 1, Direction.DOWN);
                    if (stack != null) {
                        stacks[i].count++;
                        break;
                    }
                }
            }
        }
    }

    private void extractItemsFromWorld() {
        List<ItemEntity> itemEntities = world.collectEntitiesByClass(ItemEntity.class, itemBox);
        ItemStack[] stacks = inventory.stacks;
        
        for (int i = 0; i < size(); i++) {
            for (ItemEntity itemEntity : itemEntities) {
                if (!itemEntity.isAlive()) continue;
                if (stacks[i] == null) {
                    stacks[i] = itemEntity.stack.copy();
                    itemEntity.stack.count -= stacks[i].count;
                } else if (stacks[i].count < getMaxCountPerStack()) {
                    if (stacks[i].itemId == itemEntity.stack.itemId) {
                        int spaceInStack = getMaxCountPerStack() - stacks[i].count;
                        if (spaceInStack >= itemEntity.stack.count) {
                            stacks[i].count += itemEntity.stack.count;
                            itemEntity.stack.count = 0;
                        } else {
                            stacks[i].count = getMaxCountPerStack();
                            itemEntity.stack.count -= spaceInStack;
                        }
                    }
                }
                if (itemEntity.stack.count <= 0) {
                    itemEntity.markDead();
                }
            }
        }
    }

    private void insertIntoBelow() {
        ItemHandlerBlockCapability capability = CapabilityHelper.getCapability(world, x, y - 1, z, ItemHandlerBlockCapability.class);
        if (capability != null) {
            ItemStack[] stacks = inventory.stacks;
            
            if (capability.canInsertItem(Direction.UP)) {
                for (int i = 0; i < size(); i++) {
                    if (stacks[i] != null) {
                        ItemStack stack = stacks[i].copy();
                        stack.count = 1;
                        stack = capability.insertItem(stack, Direction.UP);
                        if (stack == null) {
                            stacks[i].count--;
                            
                            if (stacks[i].count <= 0) {
                                stacks[i] = null;
                            }
                            
                            return;
                        }
                    }
                }
            }
        }
    }

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
        if (this.world.getBlockEntity(this.x, this.y, this.z) != this) {
            return false;
        } else {
            return !(player.getSquaredDistance((double) this.x + (double) 0.5F, (double) this.y + (double) 0.5F, (double) this.z + (double) 0.5F) > (double) 64.0F);
        }
    }
}
