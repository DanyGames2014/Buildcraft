package net.danygames2014.buildcraft.packet;

import net.danygames2014.buildcraft.screen.handler.DiamondPipeScreenHandler;
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

public class ToggleDiamondPipeFilterC2SPacket extends Packet implements ManagedPacket<ToggleDiamondPipeFilterC2SPacket> {
    public static final PacketType<ToggleDiamondPipeFilterC2SPacket> TYPE = PacketType.builder(false, true, ToggleDiamondPipeFilterC2SPacket::new).build();
    
    // 0 - Meta, 1 - Tags
    byte filterIndex;
    boolean value;
    
    public ToggleDiamondPipeFilterC2SPacket() {
    }

    public ToggleDiamondPipeFilterC2SPacket(byte filterIndex, boolean value) {
        this.filterIndex = filterIndex;
        this.value = value;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            this.filterIndex = stream.readByte();
            this.value = stream.readBoolean();
        } catch (IOException ignored) {

        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeByte(filterIndex);
            stream.writeBoolean(value);
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
        if (player.currentScreenHandler instanceof DiamondPipeScreenHandler diamondPipeHandler) {
            switch (filterIndex) {
                case 0 -> {
                    diamondPipeHandler.pipe.filterMeta = value;
                }
                
                case 1 -> {
                    diamondPipeHandler.pipe.filterTags = value;
                }
            }
        }
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public @NotNull PacketType<ToggleDiamondPipeFilterC2SPacket> getType() {
        return TYPE;
    }
}
