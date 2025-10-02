package net.danygames2014.buildcraft.block.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.api.energy.ILaserTarget;
import net.danygames2014.buildcraft.inventory.SimpleInventory;
import net.danygames2014.buildcraft.recipe.AssemblyTableRecipe;
import net.danygames2014.buildcraft.recipe.AssemblyTableRecipeRegistry;
import net.danygames2014.buildcraft.recipe.output.RecipeOutputType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.Random;

public class AssemblyTableBlockEntity extends BlockEntity implements ILaserTarget, Inventory {
    protected SimpleInventory inventory = new SimpleInventory(12, "Assembly Table", this::inventoryChanged);
    public ArrayList<RecipeEntry> recipes = new ArrayList<>();
    public Random random = new Random();
    public boolean hasInit = false;
    
    @Override
    public void tick() {
        super.tick();
        
        if (!hasInit) {
            hasInit = true;
            inventoryChanged();
        }
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
    }

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
