package net.danygames2014.buildcraft.mixin;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.minecraft.client.InteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InteractionManager.class)
public class InteractionManagerMixin {
    @Inject(method = "interactBlock", at = @At("HEAD"))
    public void getSide(PlayerEntity player, World world, ItemStack stack, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir){
        PipeBlock.lastSideUsed = side;
    }
}
