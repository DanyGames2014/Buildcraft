package net.danygames2014.buildcraft.packet.clientbound;

import net.danygames2014.buildcraft.api.core.Serializable;
import net.danygames2014.buildcraft.api.core.SynchedBlockEntity;
import net.danygames2014.buildcraft.packet.CoordinatesPacket;
import net.danygames2014.buildcraft.registry.StateRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class BlockEntityStateUpdatePacket extends CoordinatesPacket implements ManagedPacket<BlockEntityStateUpdatePacket> {
    public static final PacketType<BlockEntityStateUpdatePacket> TYPE = PacketType.builder(true, false, BlockEntityStateUpdatePacket::new).build();

    private final List<Serializable> stateList = new LinkedList<>();

    int stateCount;
    public byte[] dataBuffer;

    public BlockEntityStateUpdatePacket(){
    }

    public BlockEntityStateUpdatePacket(int x, int y, int z){
        super(x, y, z);
    }

    public void addStateForSerialization(Serializable state){
        stateList.add(state);
    }

    @Override
    public void read(DataInputStream stream) {
        super.read(stream);
        try {
            stateCount = stream.readByte();
            int dataLength = stream.readInt();
            if(dataLength > 0) {
                dataBuffer = new byte[dataLength];
                stream.readFully(dataBuffer);
            } else {
                dataBuffer = new byte[0];
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

            ByteArrayOutputStream tempByteStream = new ByteArrayOutputStream();
            DataOutputStream tempDoc = new DataOutputStream(tempByteStream);

            for (Serializable state : stateList) {
                tempDoc.writeByte(StateRegistry.getId(state.getClass()));
                state.writeData(tempDoc);
            }

            byte[] bytesToSend = tempByteStream.toByteArray();
            stream.writeInt(bytesToSend.length);
            stream.write(bytesToSend);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        
        PlayerEntity player = PlayerHelper.getPlayerFromGame();
        
        if(player.world.getBlockEntity(x, y, z) instanceof SynchedBlockEntity synchedBlockEntity){
            DataInputStream playbackStream = new DataInputStream(new ByteArrayInputStream(dataBuffer));
            try{
                for (int i = 0; i < stateCount; i++) {
                    byte id = playbackStream.readByte();
                    Serializable instance = synchedBlockEntity.getStateInstance(id);
                    instance.readData(playbackStream);
                    synchedBlockEntity.afterStateUpdated(id);
                }
            }
            catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }
    
    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull PacketType<BlockEntityStateUpdatePacket> getType() {
        return TYPE;
    }
}
