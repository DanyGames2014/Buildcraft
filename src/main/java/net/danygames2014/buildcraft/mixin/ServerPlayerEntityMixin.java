package net.danygames2014.buildcraft.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.danygames2014.buildcraft.block.entity.DelayedBlockEntityUpdate;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @WrapOperation(method = "playerTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ServerPlayerEntity;updateBlockEntity(Lnet/minecraft/block/entity/BlockEntity;)V"))
    public void onBlockEntityUpdatePacket(ServerPlayerEntity player, BlockEntity blockEntity, Operation<Void> original) {
        original.call(player, blockEntity);
        
        if (blockEntity instanceof DelayedBlockEntityUpdate delayedBlockEntityUpdate) {
            delayedBlockEntityUpdate.onBlockEntityUpdatePacket(player);
        }
    }
}
