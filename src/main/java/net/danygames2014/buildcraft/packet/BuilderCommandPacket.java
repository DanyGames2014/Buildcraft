package net.danygames2014.buildcraft.packet;

import net.danygames2014.buildcraft.screen.handler.BuilderScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BuilderCommandPacket extends Packet implements ManagedPacket<BuilderCommandPacket> {
    public static final PacketType<BuilderCommandPacket> TYPE = PacketType.builder(false, true, BuilderCommandPacket::new).build();

    /**
     * <p><bold>Client -> Server</bold>
     * <p>0 = Start
     * <p>1 = Pause
     * <p>2 = Stop
     */
    public int data;
    
    public BuilderCommandPacket() {
    }

    public BuilderCommandPacket(int data) {
        this.data = data;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            data = stream.readInt();
        } catch (IOException ignored) {
            
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeInt(data);
        } catch (IOException ignored) {
            
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        SideUtil.run(() -> {}, () -> handleServer(networkHandler));
    }

    @Environment(EnvType.SERVER)
    public void handleServer(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if (player.currentScreenHandler instanceof BuilderScreenHandler builderHandler) {
            switch (data) {
                case 0 -> {
                    builderHandler.blockEntity.startConstruction();   
                }
                
                case 1 -> {
                    builderHandler.blockEntity.pauseConstruction();
                }
                
                case 2 -> {
                    builderHandler.blockEntity.stopConstruction();
                }
            }
        }
    }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public @NotNull PacketType<BuilderCommandPacket> getType() {
        return TYPE;
    }
}
