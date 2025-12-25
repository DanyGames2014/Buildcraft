package net.danygames2014.buildcraft.packet;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.danygames2014.buildcraft.screen.ArchitectTableScreen;
import net.danygames2014.buildcraft.screen.handler.ArchitectTableScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ArchitectTableNameFieldPacket extends Packet implements ManagedPacket<ArchitectTableNameFieldPacket> {
    public static final PacketType<ArchitectTableNameFieldPacket> TYPE = PacketType.builder(true, true, ArchitectTableNameFieldPacket::new).build();
    public static final int MAX_NAME_LENGTH = 16;
    public static final Object2ObjectOpenHashMap<String, PlayerEntity> originators = new Object2ObjectOpenHashMap<>();
    
    public String name = "";
    public boolean requestFlag = false;
    
    public ArchitectTableNameFieldPacket() {
    }

    public ArchitectTableNameFieldPacket(String name) {
        this(name, false);
    }

    public ArchitectTableNameFieldPacket(String name, boolean requestFlag) {
        this.name = name;
        this.requestFlag = requestFlag;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            name = stream.readUTF();
            requestFlag = stream.readBoolean();
        } catch (IOException ignored) {
            
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeUTF(this.name);
            stream.writeBoolean(this.requestFlag);
        } catch (IOException ignored) {
            
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        SideUtil.run(() -> handleClient(networkHandler), () -> handleServer(networkHandler));
    }

    @Environment(EnvType.CLIENT)
    public void handleClient(NetworkHandler networkHandler) {
        if (Minecraft.INSTANCE.currentScreen instanceof ArchitectTableScreen screen) {
            screen.nameField.setText(name.substring(0, Math.min(name.length(), MAX_NAME_LENGTH)));
        }
    }

    @Environment(EnvType.SERVER)
    public void handleServer(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        if (player.currentScreenHandler instanceof ArchitectTableScreenHandler architectTableHandler) {
            if (requestFlag) {
                PacketHelper.sendTo(player, new ArchitectTableNameFieldPacket(architectTableHandler.blockEntity.blueprintName));
            } else {
                architectTableHandler.blockEntity.blueprintName = name.substring(0, Math.min(name.length(), MAX_NAME_LENGTH));
                originators.put(architectTableHandler.blockEntity.blueprintName, player);
            }
        }
    }

    @Override
    public int size() {
        return 1 + name.length();
    }

    @Override
    public @NotNull PacketType<ArchitectTableNameFieldPacket> getType() {
        return TYPE;
    }
}
