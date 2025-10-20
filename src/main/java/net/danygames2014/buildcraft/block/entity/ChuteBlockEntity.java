package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.config.Config;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Box;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.List;

// TODO: gold hopper
public class ChuteBlockEntity extends BlockEntity implements Inventory {
    private ItemStack[] inventory = new ItemStack[4];
    private Box itemBox;

    @Override
    public void tick() {
        super.tick();
        if(world.isRemote || world.getTime() % Config.MACHINE_CONFIG.chute.tickDelay != 0) return;
        if(itemBox == null){
            itemBox = Box.create(x, y + 1, z, x + 1, y + 1.6D, z + 1);
        }
        ItemHandlerBlockCapability capability = CapabilityHelper.getCapability(world, x, y + 1, z, ItemHandlerBlockCapability.class);
        if(capability != null){
            if (Config.MACHINE_CONFIG.chute.allowInventoryExtraction) {
                extractFromAbove(capability);
            }
        } else {
            if (Config.MACHINE_CONFIG.chute.allowItemPickup) {
                extractItemsFromWorld();
            }
        }
        insertIntoBelow();
    }

    private void extractFromAbove(ItemHandlerBlockCapability capability){
        if(capability.canExtractItem(Direction.DOWN)){
            for(int i = 0; i < size(); i++){
                if(inventory[i] == null){
                    ItemStack stack = capability.extractItem(1, Direction.DOWN);
                    if(stack != null){
                        inventory[i] = stack.copy();
                        break;
                    }
                }
                else if(inventory[i].count < getMaxCountPerStack()){
                    ItemStack stack = capability.extractItem(inventory[i].getItem(), 1, Direction.DOWN);
                    if(stack != null){
                        inventory[i].count++;
                        break;
                    }
                }
            }
        }
    }

    private void extractItemsFromWorld(){
        List<ItemEntity> itemEntities = world.collectEntitiesByClass(ItemEntity.class, itemBox);
        for(int i = 0; i < size(); i++){
            for(ItemEntity itemEntity : itemEntities){
                if(!itemEntity.isAlive()) continue;
                if(inventory[i] == null){
                    inventory[i] = itemEntity.stack.copy();
                    itemEntity.stack.count -= inventory[i].count;
                }
                else if(inventory[i].count < getMaxCountPerStack()){
                    if(inventory[i].itemId == itemEntity.stack.itemId){
                        int spaceInStack = getMaxCountPerStack() - inventory[i].count;
                        if(spaceInStack >= itemEntity.stack.count){
                            inventory[i].count += itemEntity.stack.count;
                            itemEntity.stack.count = 0;
                        }
                        else {
                            inventory[i].count = getMaxCountPerStack();
                            itemEntity.stack.count -= spaceInStack;
                        }
                    }
                }
                if(itemEntity.stack.count <= 0){
                    itemEntity.markDead();
                }
            }
        }
    }

    private void insertIntoBelow(){
        ItemHandlerBlockCapability capability = CapabilityHelper.getCapability(world, x, y - 1, z, ItemHandlerBlockCapability.class);
        if(capability != null){
            if(capability.canInsertItem(Direction.UP)){
                for(int i = 0; i < size(); i++){
                    if(inventory[i] != null){
                        ItemStack stack = inventory[i].copy();
                        stack.count = 1;
                        stack = capability.insertItem(stack, Direction.UP);
                        if(stack == null) inventory[i].count--;
                        if(inventory[i].count <= 0) inventory[i] = null;
                    }
                }
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        NbtList items = nbt.getList("Items");
        this.inventory = new ItemStack[this.size()];

        for(int i = 0; i < items.size(); ++i) {
            NbtCompound var4 = (NbtCompound)items.get(i);
            int var5 = var4.getByte("Slot") & 255;
            if (var5 >= 0 && var5 < this.inventory.length) {
                this.inventory[var5] = new ItemStack(var4);
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NbtList items = new NbtList();

        for(int i = 0; i < this.inventory.length; ++i) {
            if (this.inventory[i] != null) {
                NbtCompound var4 = new NbtCompound();
                var4.putByte("Slot", (byte)i);
                this.inventory[i].writeNbt(var4);
                items.add(var4);
            }
        }
        nbt.put("Items", items);
    }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public ItemStack getStack(int slot) {
        if(slot < 0 || slot > size() - 1) return null;
        return inventory[slot];
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if(slot < 0 || slot > size() - 1) return null;
        if (this.inventory[slot] != null) {
            if (this.inventory[slot].count <= amount) {
                ItemStack var4 = this.inventory[slot];
                this.inventory[slot] = null;
                this.markDirty();
                return var4;
            } else {
                ItemStack var3 = this.inventory[slot].split(amount);
                if (this.inventory[slot].count == 0) {
                    this.inventory[slot] = null;
                }

                this.markDirty();
                return var3;
            }
        } else {
            return null;
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if(slot < 0 || slot > size() - 1) return;
        this.inventory[slot] = stack;
        if (stack != null && stack.count > this.getMaxCountPerStack()) {
            stack.count = this.getMaxCountPerStack();
        }

        this.markDirty();
    }

    @Override
    public String getName() {
        return "Chute";
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.x, this.y, this.z) != this) {
            return false;
        } else {
            return !(player.getSquaredDistance((double)this.x + (double)0.5F, (double)this.y + (double)0.5F, (double)this.z + (double)0.5F) > (double)64.0F);
        }
    }
}
