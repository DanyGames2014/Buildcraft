package net.danygames2014.buildcraft.packet.clientbound;

import net.danygames2014.buildcraft.api.core.Serializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.States;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BlockEntityUpdatePacket extends UpdatePacket implements ManagedPacket<BlockEntityUpdatePacket> {
    public int x;
    public int y;
    public int z;

    public static final PacketType<BlockEntityUpdatePacket> TYPE = PacketType.builder(true, false, BlockEntityUpdatePacket::new).build();

    public BlockEntityUpdatePacket(){}
    public BlockEntityUpdatePacket(Serializable serializable){
        super(serializable);
        BlockEntity blockEntity = (BlockEntity) serializable;
        x = blockEntity.x;
        y = blockEntity.y;
        z = blockEntity.z;
    }
    @Override
    public void readIdentificationData(DataInputStream stream) {
        try {
            x = stream.readInt();
            y = stream.readInt();
            z = stream.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeIdentificationData(DataOutputStream stream) {
        try {
            stream.writeInt(x);
            stream.writeInt(y);
            stream.writeInt(z);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromGame();
        if(targetExists(player.world) && getTarget(player.world) instanceof Serializable serializable){
            DataInputStream playbackStream = new DataInputStream(new ByteArrayInputStream(dataBuffer));
            try {
                serializable.readData(playbackStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public @NotNull PacketType<BlockEntityUpdatePacket> getType() {
        return TYPE;
    }

    public boolean targetExists(World world) {
        return world.getBlockState(x, y, z) != States.AIR.get();
    }

    public BlockEntity getTarget(World world) {
        return world.getBlockEntity(x, y, z);
    }
}
