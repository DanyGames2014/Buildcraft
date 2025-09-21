package net.danygames2014.buildcraft.mixin;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @Inject(method = "interactBlock", at = @At("HEAD"))
    public void getSide(PlayerEntity world, World item, ItemStack x, int y, int z, int side, int par7, CallbackInfoReturnable<Boolean> cir){
        PipeBlock.lastSideUsed = side;
    }
}
