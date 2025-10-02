package net.danygames2014.buildcraft.block.entity;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.energy.ILaserTarget;
import net.danygames2014.buildcraft.inventory.SimpleInventory;
import net.danygames2014.buildcraft.recipe.AssemblyTableRecipe;
import net.danygames2014.buildcraft.recipe.AssemblyTableRecipeRegistry;
import net.danygames2014.buildcraft.recipe.output.RecipeOutputType;
import net.danygames2014.nyalib.capability.CapabilityHelper;
import net.danygames2014.nyalib.capability.block.itemhandler.ItemHandlerBlockCapability;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.ArrayList;
import java.util.Random;

public class AssemblyTableBlockEntity extends BlockEntity implements ILaserTarget, Inventory {
    protected SimpleInventory inventory = new SimpleInventory(12, "Assembly Table", this::inventoryChanged);
    public ArrayList<RecipeEntry> recipes = new ArrayList<>();
    public ArrayList<RecipeEntry> selectedRecipes = new ArrayList<>();
    public RecipeEntry currentRecipe;
    public double progress;
    public Random random = new Random();
    public boolean hasInit = false;

    @Override
    public void tick() {
        super.tick();

        if (!hasInit) {
            hasInit = true;
            inventoryChanged();
        }

        updateCurrentRecipe();
    }

    public void updateCurrentRecipe() {
        // If there are no avalible recipes, return
        if (recipes.isEmpty()) {
            currentRecipe = null;
            progress = 0;
            return;
        }

        if (currentRecipe == null) {
            switchRecipe();
        }

        // If there is an selected and active recipe, tick the progress
        if (currentRecipe != null) {
            if (progress >= currentRecipe.recipe.recipeTime) {
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

        for (ItemStack output : currentRecipe.recipe.getOutputs(random).get(RecipeOutputType.PRIMARY)) {
            for (var capability : neighbors.entrySet()) {
                output = capability.getValue().insertItem(output, capability.getKey().getOpposite());
                if (output == null) {
                    break;
                }
            }
            
            if (output != null) {
                world.spawnEntity(new ItemEntity(world, x + 0.5D, y + 1.5D, z + 0.5D, output));
            }
        }

        progress = 0;
        if (!currentRecipe.recipe.consume(inventory.stacks)) {
            Buildcraft.LOGGER.warn("Failed to consume items from assembly table inventory!");
        }
        switchRecipe();
    }

    public void switchRecipe() {
        // Construct a list of currently selected recipes
        selectedRecipes.clear();
        for (RecipeEntry entry : recipes) {
            if (entry.selected) {
                selectedRecipes.add(entry);
            } else {
                if (entry.active) {
                    entry.active = false;
                }
            }
        }

        if (selectedRecipes.isEmpty()) {
            currentRecipe = null;
            progress = 0;
            return;
        }

        // If there is only one recipe, there is nothing to talk about
        if (selectedRecipes.size() == 1) {
            currentRecipe = selectedRecipes.get(0);
            selectedRecipes.get(0).active = true;
            return;
        }

        for (int i = 0; i < selectedRecipes.size(); i++) {
            // If the current recipe is the active one, switch to next one
            if (selectedRecipes.get(i).active) {
                // Deactivate the current one
                selectedRecipes.get(i).active = false;

                // Check if we are at the end of the list
                if (i >= selectedRecipes.size() - 1) {
                    // If we are at the end of the list, switch back to first recipe
                    currentRecipe = selectedRecipes.get(0);
                    selectedRecipes.get(0).active = true;
                } else {
                    // If we are not at the end, switch to next recipe
                    currentRecipe = selectedRecipes.get(i + 1);
                    selectedRecipes.get(i + 1).active = true;
                }

                return;
            }
        }

        // If there is not an active recipe, the first one become active
        currentRecipe = selectedRecipes.get(0);
        selectedRecipes.get(0).active = true;
    }

    public void inventoryChanged() {
        if (!world.isRemote) {
            ArrayList<RecipeEntry> newRecipes = new ArrayList<>();

            for (var recipe : AssemblyTableRecipeRegistry.get(inventory.stacks)) {
                ObjectArrayList<ItemStack> outputs = recipe.getOutputs(random).get(RecipeOutputType.PRIMARY);
                ItemStack iconItem;

                if (!outputs.isEmpty()) {
                    iconItem = outputs.get(0);
                } else {
                    iconItem = new ItemStack(Item.RAW_FISH);
                }

                newRecipes.add(new RecipeEntry(recipe, iconItem));
            }

            recipes.removeIf(entry -> {
                boolean remove = true;

                for (var newRecipe : newRecipes) {
                    if (newRecipe.recipe.equals(entry.recipe)) {
                        remove = false;
                        break;
                    }
                }

                return remove;
            });

            for (RecipeEntry newRecipe : newRecipes) {
                boolean add = true;

                for (var recipe : recipes) {
                    if (recipe.recipe.equals(newRecipe.recipe)) {
                        add = false;
                        break;
                    }
                }

                if (add) {
                    recipes.add(newRecipe);
                }
            }
        }

        markDirty();
    }

    public void selectRecipe(int index) {
        if (index < 0 || index >= recipes.size()) {
            return;
        }

        RecipeEntry recipe = recipes.get(index);
        recipe.selected = !recipe.selected;

        if (currentRecipe == null || currentRecipe == recipe) {
            switchRecipe();
        }
    }

    public int getProgressScaled(int scale) {
        if (currentRecipe == null) {
            return 0;
        }

        return (int) ((progress / currentRecipe.recipe.recipeTime) * scale);
    }

    // ILaserTarget
    @Override
    public boolean requiresLaserEnergy() {
        return currentRecipe != null;
    }

    @Override
    public void receiveLaserEnergy(double energy) {
        if (currentRecipe == null) {
            progress = 0;
            return;
        }

        progress = Math.min(progress + energy, currentRecipe.recipe.recipeTime);
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
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        inventory.readNbt(nbt);
    }

    public static class RecipeEntry {
        public AssemblyTableRecipe recipe;
        public ItemStack icon;
        public boolean selected;
        public boolean active;

        public RecipeEntry(AssemblyTableRecipe recipe, ItemStack icon) {
            this.recipe = recipe;
            this.icon = icon;
            this.selected = false;
            this.active = false;
        }
    }
}
