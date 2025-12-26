package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.inventory.SimpleInventory;
import net.danygames2014.buildcraft.item.BlueprintData;
import net.danygames2014.buildcraft.item.BlueprintPersistentState;
import net.danygames2014.buildcraft.item.BuilderBlueprintItem;
import net.danygames2014.buildcraft.item.BuilderTemplateItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.state.property.Properties;

public class ArchitectTableBlockEntity extends AreaWorkerBlockEntity implements Inventory {
    public SimpleInventory inventory = new SimpleInventory(2, "Architect Table", this::markDirty);
    public int progress = 0;
    public int maxProgress = 20;
    public String blueprintName = "";
    public String lastTouchedBy = "";
    
    public ArchitectTableBlockEntity() {
        
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (canOperate()) {
            if (progress >= maxProgress) {
                writeBlueprint();
                progress = 0;
            }
            progress++;
        } else {
            progress = 0;
        }
    }

    public boolean canOperate() {
        ItemStack inputStack = inventory.getStack(0);
        
        if (inputStack == null) {
            return false;
        }
        
        return inputStack.getItem() instanceof BuilderBlueprintItem || inputStack.getItem() instanceof BuilderTemplateItem;
    }
    
    public void writeBlueprint() {
        ItemStack inputStack = inventory.getStack(0);
        
        if (inputStack == null) {
            return;
        }
        
        BlueprintPersistentState blueprint;
        if (inputStack.getDamage() == 0) {
            blueprint = BlueprintPersistentState.get(world);
        } else {
            blueprint = BlueprintPersistentState.get(world, inputStack.getDamage());
        }
        BlueprintData blueprintData = blueprint.data;
        blueprintData.entries.clear();
        
        int sizeX = (workingArea.maxX - workingArea.minX) + 1;
        int sizeY = (workingArea.maxY - workingArea.minY) + 1;
        int sizeZ = (workingArea.maxZ - workingArea.minZ) + 1;
        
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    BlockState state = world.getBlockState(x + workingArea.minX,y + workingArea.minY,z + workingArea.minZ);
                    
                    if (state.isAir()) {
                        continue;
                    }
                    
                    blueprintData.addEntry(x,y,z, state, world.getBlockMeta(x + workingArea.minX,y + workingArea.minY,z + workingArea.minZ));
                }
            }
        }
        
        blueprintData.name = blueprintName;
        blueprintData.author = lastTouchedBy;
        blueprintData.sizeX = sizeX;
        blueprintData.sizeY = sizeY;
        blueprintData.sizeZ = sizeZ;
        blueprintData.facing = world.getBlockState(x,y,z).get(Properties.HORIZONTAL_FACING);
        blueprintData.written = true;
        blueprint.markDirty();

        NbtCompound nbt = inputStack.getStationNbt();
        nbt.putBoolean("written", true);
        nbt.putString("name", blueprintData.name);
        nbt.putString("author", blueprintData.author);
        nbt.putInt("sizeX", blueprintData.sizeX);
        nbt.putInt("sizeY", blueprintData.sizeY);
        nbt.putInt("sizeZ", blueprintData.sizeZ);
        
        inputStack.setDamage(blueprint.rawId);
        
        inventory.setStack(1, inputStack);
        inventory.setStack(0, null);
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

    @Override
    public void markDirty() {
        super.markDirty();
    }
    
    // Nbt
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
