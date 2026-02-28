package net.danygames2014.buildcraft.block.entity;

import net.minecraft.entity.player.ServerPlayerEntity;

public interface DelayedBlockEntityUpdate {
    void onBlockEntityUpdatePacket(ServerPlayerEntity player);
}
