package net.danygames2014.buildcraft.recipe.machine.input;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.tag.TagKey;

/**
 * Recipe Input which compares to a Item TagKey
 */
public class TagRecipeInput extends RecipeInput {
    public final TagKey<Item> itemTag;
    public final int count;
    
    public TagRecipeInput(TagKey<Item> itemTag, int count) {
        this.itemTag = itemTag;
        this.count = count;
    }
    
    public TagRecipeInput(TagKey<Item> itemTag) {
        this(itemTag, 1);
    }

    @Override
    public boolean matches(ItemStack other) {
        // If item count is lower than required, return false
        if (other.count < count) {
            return false;
        }
        
        return other.isIn(itemTag);
    }

    @Override
    public int getRequiredAmount() {
        return count;
    }

    @Override
    public String toString() {
        return "TagRecipeInput{" +
                "itemTag=" + itemTag +
                ", count=" + count +
                '}';
    }
}
