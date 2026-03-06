package net.danygames2014.buildcraft.block.entity;

import net.danygames2014.buildcraft.api.core.Serializable;
import net.danygames2014.buildcraft.packet.clientbound.BlockEntityUpdatePacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;

public abstract class SyncedBlockEntity extends BlockEntity implements Serializable, DelayedBlockEntityUpdate {
    public Packet getUpdatePacket(){
        return new BlockEntityUpdatePacket(this);
    }

    public void sendNetworkUpdate(){
        if(world != null && !world.isRemote){
            Packet updatePacket = getUpdatePacket();
            for(Object o : world.players){
                PlayerEntity player = (PlayerEntity) o;
                if(player.getDistance(x, y, z) < getNetworkUpdateRange()){
                    PacketHelper.sendTo(player, updatePacket);
                }
            }
        }
    }

    private int getNetworkUpdateRange(){
        return 64;
    }

    @Override
    public Packet createUpdatePacket() {
        return getUpdatePacket();
    }

    @Environment(EnvType.SERVER)
    @Override
    public void onBlockEntityUpdatePacket(ServerPlayerEntity player) {
        PacketHelper.sendTo(player, getUpdatePacket());
    }
}
