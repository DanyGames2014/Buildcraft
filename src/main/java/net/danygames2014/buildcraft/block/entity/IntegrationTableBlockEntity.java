package net.danygames2014.buildcraft.block.entity;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.danygames2014.buildcraft.api.energy.ILaserTarget;
import net.danygames2014.buildcraft.inventory.SimpleInventory;
import net.danygames2014.buildcraft.item.GateItem;
import net.danygames2014.buildcraft.recipe.integration.IntegrationTableRecipe;
import net.danygames2014.buildcraft.recipe.integration.IntegrationTableRecipeRegistry;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.Random;

public class IntegrationTableBlockEntity extends BlockEntity implements ILaserTarget, Inventory {
    protected SimpleInventory inventory = new SimpleInventory(3, "Integration Table", this::inventoryChanged);
    public ItemStack previewStack = null;
    public IntegrationTableRecipe currentRecipe;
    public double progress;
    public double lastProgress = 0;
    public int scaledProgress = 0;
    public Random random = new Random();

    private ItemStack lastInput1 = null;
    private ItemStack lastInput2 = null;
    
    @Override
    public void tick() {
        super.tick();

        if (world.isRemote) {
            return;
        }
        
        if (this.getStack(0) != lastInput1 || this.getStack(1) != lastInput2) {
            lastInput1 = this.getStack(0);
            lastInput2 = this.getStack(1);
            inventoryChanged();
            this.progress = 0.0D;
            this.lastProgress = 0.0D;
            return;
        }

        updateCurrentRecipe();

        scaledProgress = getProgressScaled(98);
        
        if (progress == 0) {
            lastProgress = 0;
        }
    }

    public void updateCurrentRecipe() {
        if (currentRecipe == null) {
            fetchRecipe();
        }

        // If there is an selected and active recipe, tick the progress
        if (currentRecipe != null) {
            if (progress >= currentRecipe.recipeTime) {
                craftRecipe();
            }
        }
    }

    public void craftRecipe() {
        Object2ObjectOpenHashMap<Direction, ItemHandlerBlockCapability> neighbors = new Object2ObjectOpenHashMap<>();

        for (Direction side : Direction.values()) {
            var capability = CapabilityHelper.getCapability(world, x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ(), ItemHandlerBlockCapability.class);
            if (capability != null) {
                neighbors.put(side, capability);
            }
        }

        ItemStack output = this.getStack(0).copy();
        output.count = 1;
        currentRecipe.integrateExpansion(output);

        // Output into the internal output slot
        if (getStack(2) == null) {
            setStack(2, output);
            output = null;
        }
        
        if (output != null) {
            ItemStack existingOutput = this.getStack(2);
            if (existingOutput != null && existingOutput.isItemEqual(output) && existingOutput.count < existingOutput.getMaxCount()) {
                existingOutput.count++;
                output = null;
            }
        }
        
        
        // Try to push into surrounding inventories
        if (output != null) {
            for (var capability : neighbors.entrySet()) {
                output = capability.getValue().insertItem(output, capability.getKey().getOpposite());
                if (output == null) {
                    break;
                }
            }
        }

        // As last resort drop it
        if (output != null) {
            ItemEntity itemEntity = new ItemEntity(world, x + 0.5D, y + 0.7D, z + 0.5D, output);
            itemEntity.pickupDelay = 10;
            world.spawnEntity(itemEntity);
        }

        progress = 0;
        this.getStack(0).count--;
        this.getStack(1).count--;
        inventoryChanged();
    }

    public void fetchRecipe() {
        if (!isGate(this.getStack(0))) {
            currentRecipe = null;
            previewStack = null;
            return;
        }
        
        IntegrationTableRecipe recipe = IntegrationTableRecipeRegistry.get(this.getStack(1));
        if (recipe == null || GateItem.hasGateExpansion(this.getStack(0), recipe.expansion)) {
            currentRecipe = null;
            previewStack = null;
            return;
        }
        
        ItemStack preview = this.getStack(0).copy();
        preview.count = 1;
        GateItem.addGateExpansion(preview, recipe.expansion);
        previewStack = preview;
        
        currentRecipe = recipe;
    }
    
    public boolean isGate(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        
        return stack.getItem() instanceof GateItem;
    }

    public void inventoryChanged() {
        if (world != null && world.isRemote) {
            return;
        }

        validateInventory();
        markDirty();
        fetchRecipe();
    }

    public void validateInventory() {
        for (int slot = 0; slot < inventory.size(); slot++) {
            if (inventory.stacks[slot] != null && inventory.stacks[slot].count <= 0) {
                inventory.stacks[slot] = null;
            }
        }
    }

    public int getProgressScaled(int scale) {
        if (currentRecipe == null) {
            return 0;
        }

        return (int) ((progress / currentRecipe.recipeTime) * scale);
    }

    // ILaserTarget
    @Override
    public boolean requiresLaserEnergy() {
        return currentRecipe != null;
    }

    @Override
    public void receiveLaserEnergy(double energy) {
        if (world == null || world.isRemote) {
            return;
        }

        if (currentRecipe == null) {
            progress = 0;
            lastProgress = 0;
            return;
        }

        lastProgress = progress;
        progress = Math.min(progress + energy, currentRecipe.recipeTime);
    }

    @Override
    public boolean isInvalidTarget() {
        return this.isRemoved();
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

        nbt.putDouble("progress", this.progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        inventory.readNbt(nbt);
        
        inventoryChanged();
        
        progress = nbt.getDouble("progress");
    }
}
