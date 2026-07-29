package net.danygames2014.buildcraft.mixin;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.item.FacadeItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingRecipeManager.class)
public class CraftingRecipeManagerMixin {
    @Inject(method = "craft", at = @At("HEAD"), cancellable = true)
    public void craftFacade(CraftingInventory craftingInventory, CallbackInfoReturnable<ItemStack> cir) {
        boolean hasStructurePipe = false;
        ItemStack returnStack = null;
        
        for (ItemStack stack : craftingInventory.stacks) {
            if (stack != null) {
                if (stack.isOf(Buildcraft.cobblestoneStructurePipe.asItem())) {
                    if (hasStructurePipe) {
                        return;
                    }
                    
                    hasStructurePipe = true;
                } else {
                    if (stack.getItem() instanceof BlockItem blockItem) {
                        if (returnStack != null) {
                            return;
                        }
                        
                        returnStack = FacadeItem.createStack(blockItem.getBlock(), stack.getDamage(), false);
                    }
                }
            }
        }

        if (hasStructurePipe && returnStack != null) {
            cir.setReturnValue(returnStack);
        }
    }

    @Inject(method = "craft", at = @At("HEAD"), cancellable = true)
    public void convertHollowFacade(CraftingInventory craftingInventory, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack returnStack = null;
        
        for (ItemStack stack : craftingInventory.stacks) {
            if (stack != null) {
                if (stack.getItem() instanceof FacadeItem) {
                    if (returnStack != null) {
                        return;
                    }
                    
                    if (FacadeItem.isHollow(stack)) {
                        returnStack = FacadeItem.createStack(FacadeItem.getBlock(stack), FacadeItem.getMeta(stack), false);
                    } else {
                        returnStack = FacadeItem.createStack(FacadeItem.getBlock(stack), FacadeItem.getMeta(stack), true);
                    }
                } else {
                    return;
                }
            }
        }

        if (returnStack != null) {
            cir.setReturnValue(returnStack);
        }
    }
}
