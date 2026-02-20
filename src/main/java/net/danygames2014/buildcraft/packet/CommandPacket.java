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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
    
    public DataInputStream stream;
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
            this.stream = stream;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeUTF(command);
            stream.writeByte(targets.indexOf(handler));
            handler.write(stream, target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if(handler != null){
            CommandReceiver receiver = handler.handle(player, stream, player.world);
            if(receiver != null){
                receiver.receiveCommand(command, FabricLoader.getInstance().getEnvironmentType(), player, stream);
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
