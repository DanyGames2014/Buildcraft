package net.danygames2014.buildcraft.recipe.input;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemRecipeInput extends RecipeInput {
    /**
     * The item required
     */
    public final Item item;
    /**
     * The item count required
     */
    public final int count;
    /**
     * The item meta required, -1 for any meta.
     */
    public final int meta;

    public ItemRecipeInput(Item item, int count, int meta) {
        this.item = item;
        this.count = count;
        this.meta = meta;
    }
    
    public ItemRecipeInput(Item item, int count) {
        this(item, count, -1);
    }
    
    public ItemRecipeInput(Item item) {
        this(item, 1);
    }

    @Override
    public boolean matches(ItemStack other) {
        // If item doesn't match, return false
        if (item != other.getItem()) {
            return false;
        }
        
        // If item count is lower than required, return false
        if (other.count < count) {
            return false;
        }
        
        // If meta is not -1 and item meta doesn't match, return false
        if (meta != -1 && meta != other.getDamage()){
            return false;
        }

        return true;
    }

    @Override
    public int getRequiredAmount() {
        return count;
    }

    @Override
    public String toString() {
        return "ItemRecipeInput{" +
                "item=" + item +
                ", count=" + count +
                ", meta=" + meta +
                '}';
    }
}
