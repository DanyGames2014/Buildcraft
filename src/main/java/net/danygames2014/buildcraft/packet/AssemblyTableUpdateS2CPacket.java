package net.danygames2014.buildcraft.packet;

import net.danygames2014.buildcraft.screen.AssemblyTableScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class AssemblyTableUpdateS2CPacket extends Packet implements ManagedPacket<AssemblyTableUpdateS2CPacket> {
    public static final PacketType<AssemblyTableUpdateS2CPacket> TYPE = PacketType.builder(true, false, AssemblyTableUpdateS2CPacket::new).build();
    
    public int[] resultIds;
    public byte[] resultAmounts;
    public boolean[] selected;
    public boolean[] active;

    public AssemblyTableUpdateS2CPacket() {
    }

    public AssemblyTableUpdateS2CPacket(int[] resultIds, byte[] resultAmounts, boolean[] selected, boolean[] active) {
        this.resultIds = resultIds;
        this.resultAmounts = resultAmounts;
        this.selected = selected;
        this.active = active;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            int length = stream.readByte();
            resultIds = new int[length];
            resultAmounts = new byte[length];
            selected = new boolean[length];
            active = new boolean[length];
            for (int i = 0; i < resultIds.length; i++) {
                resultIds[i] = stream.readInt();
                resultAmounts[i] = stream.readByte();
                selected[i] = stream.readBoolean();
                active[i] = stream.readBoolean();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeByte(resultIds.length);
            for (int i = 0; i < resultIds.length; i++) {
                stream.writeInt(resultIds[i]);
                stream.writeByte(resultAmounts[i]);
                stream.writeBoolean(selected[i]);
                stream.writeBoolean(active[i]);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        SideUtil.run(() -> handleClient(networkHandler), () -> {});
    }

    // This will run on client
    @Environment(EnvType.CLIENT)
    public void handleClient(NetworkHandler networkHandler) {
        if (Minecraft.INSTANCE.currentScreen instanceof AssemblyTableScreen assemblyTableScreen) {
            assemblyTableScreen.resultIds = resultIds;
            assemblyTableScreen.resultAmounts = resultAmounts;
            assemblyTableScreen.selected = selected;
            assemblyTableScreen.active = active;
        }
    }

    @Override
    public int size() {
        return 1 + (resultIds.length * 7);
    }

    @Override
    public @NotNull PacketType<AssemblyTableUpdateS2CPacket> getType() {
        return TYPE;
    }
}
