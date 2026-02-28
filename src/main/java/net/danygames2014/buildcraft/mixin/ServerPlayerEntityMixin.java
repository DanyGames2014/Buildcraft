package net.danygames2014.buildcraft.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.danygames2014.buildcraft.block.entity.DelayedBlockEntityUpdate;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Unique
    public Object2IntOpenHashMap<DelayedBlockEntityUpdate> queue = new Object2IntOpenHashMap<>();
    
    @WrapOperation(method = "playerTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ServerPlayerEntity;updateBlockEntity(Lnet/minecraft/block/entity/BlockEntity;)V"))
    public void onBlockEntityUpdatePacket(ServerPlayerEntity player, BlockEntity blockEntity, Operation<Void> original) {
        original.call(player, blockEntity);
        
        if (blockEntity instanceof DelayedBlockEntityUpdate delayedBlockEntityUpdate) {
            queue.put(delayedBlockEntityUpdate, 10);
        }
    }

    @Unique
    ObjectArrayList<DelayedBlockEntityUpdate> removeQueue = new ObjectArrayList<>();
    
    @Inject(method = "playerTick", at = @At("TAIL"))
    public void processQueue(boolean par1, CallbackInfo ci) {
        removeQueue.clear();
        
        for (DelayedBlockEntityUpdate pipe : queue.keySet()) {
            queue.put(pipe, queue.getInt(pipe) - 1);
            
            if (queue.getInt(pipe) <= 0) {
                pipe.onBlockEntityUpdatePacket((ServerPlayerEntity) (Object) this);
                removeQueue.add(pipe);
            }
        }
        
        for (DelayedBlockEntityUpdate blockEntity : removeQueue) {
            queue.removeInt(blockEntity);
        }
    }
}
