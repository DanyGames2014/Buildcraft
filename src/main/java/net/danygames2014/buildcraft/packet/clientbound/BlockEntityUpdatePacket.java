package net.danygames2014.buildcraft.packet.clientbound;

import net.danygames2014.buildcraft.api.core.Serializable;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.client.render.PipeRenderState;
import net.danygames2014.buildcraft.packet.CoordinatesPacket;
import net.danygames2014.buildcraft.registry.StateRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.math.StationBlockPos;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class BlockEntityUpdatePacket extends CoordinatesPacket implements ManagedPacket<BlockEntityUpdatePacket> {
    public static final PacketType<BlockEntityUpdatePacket> TYPE = PacketType.builder(true, false, BlockEntityUpdatePacket::new).build();

    private List<Serializable> stateList = new LinkedList<>();

    public BlockEntityUpdatePacket(){
    }

    public BlockEntityUpdatePacket(int x, int y, int z){
        super(x, y, z);
    }

    public void addStateForSerialization(Serializable state){
        stateList.add(state);
    }

    @Override
    public void read(DataInputStream stream) {
        super.read(stream);
        try {
            int stateCount = stream.readByte();

            for (int i = 0; i < stateCount; i++) {
                int id = stream.readByte();
                Serializable state = StateRegistry.create(id);
                state.readData(stream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        super.write(stream);

        try {
            stream.writeByte(stateList.size());
            for (Serializable state : stateList) {
                stream.writeByte(StateRegistry.getId(state.getClass()));
                state.writeData(stream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull PacketType<BlockEntityUpdatePacket> getType() {
        return TYPE;
    }
}
