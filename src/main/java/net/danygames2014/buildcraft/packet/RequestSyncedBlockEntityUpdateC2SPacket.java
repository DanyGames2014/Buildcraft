package net.danygames2014.buildcraft.packet;

import net.danygames2014.buildcraft.block.entity.SyncedBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class RequestSyncedBlockEntityUpdateC2SPacket extends CoordinatesPacket implements ManagedPacket<RequestSyncedBlockEntityUpdateC2SPacket> {
    public static final PacketType<RequestSyncedBlockEntityUpdateC2SPacket> TYPE = PacketType.builder(false, true, RequestSyncedBlockEntityUpdateC2SPacket::new).build();

    public RequestSyncedBlockEntityUpdateC2SPacket() {
    }

    public RequestSyncedBlockEntityUpdateC2SPacket(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    public @NotNull PacketType<RequestSyncedBlockEntityUpdateC2SPacket> getType() {
        return TYPE;
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(player.world.getBlockEntity(x, y, z) instanceof SyncedBlockEntity blockEntity) {
            blockEntity.sendNetworkUpdate();
        }
    }
}
