package net.danygames2014.buildcraft.mixin;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.item.FacadeItem;
import net.danygames2014.whatsthis.WhatsThis;
import net.danygames2014.whatsthis.item.ProbeUtil;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingRecipeManager.class)
public class CraftingRecipeManagerMixin {
    @Inject(method = "craft", at = @At("HEAD"), cancellable = true)
    public void craftFacade(CraftingInventory craftingInventory, CallbackInfoReturnable<ItemStack> cir){
        boolean hasStructurePipe = false;
        ItemStack returnStack = null;
        for(ItemStack stack : craftingInventory.stacks) {
            if (stack != null) {
                if (stack.isOf(Item.DIAMOND_HOE)) {
                    hasStructurePipe = true;
                } else {
                    Item var8 = stack.getItem();
                    if (var8 instanceof BlockItem blockItem) {
                        returnStack = FacadeItem.createStack(blockItem.getBlock(), stack.getDamage(), false);
                    }
                }
            }
        }

        if (hasStructurePipe && returnStack != null) {
            cir.setReturnValue(returnStack);
        }
    }
}
