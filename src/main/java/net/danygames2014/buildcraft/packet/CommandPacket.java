package net.danygames2014.buildcraft.packet;

import net.danygames2014.buildcraft.packet.command.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;

public class CommandPacket extends Packet implements ManagedPacket<CommandPacket> {
    public static final PacketType<CommandPacket> TYPE = PacketType.builder(true, true, CommandPacket::new).build();
    
    public static final ArrayList<CommandTarget> targets = new ArrayList<>() {
        {
            add(new CommandTargetBlockEntity());
            add(new CommandTargetEntity());
            add(new CommandTargetScreenHandler());
        }
    };
    
    public byte[] dataBuffer;
    public String command;
    public Object target;
    public CommandTarget handler;
    private CommandWriter writer;

    public CommandPacket(){}
    public CommandPacket(Object target, String command, CommandWriter writer){
        this.target = target;
        this.command = command;
        this.writer = writer;

        for (CommandTarget c : targets) {
            if (c.getHandledClass().isAssignableFrom(target.getClass())) {
                this.handler = c;
                break;
            }
        }
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            command = stream.readUTF();
            handler = targets.get(stream.readUnsignedByte());

            int dataLength = stream.readInt();
            if (dataLength > 0) {
                dataBuffer = new byte[dataLength];
                stream.readFully(dataBuffer); // Only reads exactly what was written
            } else {
                dataBuffer = new byte[0];
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeUTF(command);
            stream.writeByte(targets.indexOf(handler));

            ByteArrayOutputStream tempByteStream = new ByteArrayOutputStream();
            DataOutputStream tempDoc = new DataOutputStream(tempByteStream);

            handler.write(tempDoc, target);
            if (writer != null) {
                writer.write(tempDoc);
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
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(handler != null && dataBuffer != null){
            DataInputStream playbackStream = new DataInputStream(new ByteArrayInputStream(dataBuffer));

            CommandReceiver receiver = handler.handle(player, playbackStream, player.world);
            if(receiver != null){
                receiver.receiveCommand(command, FabricLoader.getInstance().getEnvironmentType(), player, playbackStream);
            }
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull PacketType<CommandPacket> getType() {
        return TYPE;
    }
}
