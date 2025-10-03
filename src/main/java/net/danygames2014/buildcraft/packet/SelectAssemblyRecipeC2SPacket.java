package net.danygames2014.buildcraft.packet;

import net.danygames2014.buildcraft.screen.handler.AssemblyTableScreenHandler;
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

public class SelectAssemblyRecipeC2SPacket extends Packet implements ManagedPacket<SelectAssemblyRecipeC2SPacket> {
    public static final PacketType<SelectAssemblyRecipeC2SPacket> TYPE = PacketType.builder(false, true, SelectAssemblyRecipeC2SPacket::new).build();
    
    byte index;

    public SelectAssemblyRecipeC2SPacket() {
    }

    public SelectAssemblyRecipeC2SPacket(byte index) {
        this.index = index;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            this.index = stream.readByte();
        } catch (IOException ignored) {
            
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeByte(index);
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
        if (player.currentScreenHandler instanceof AssemblyTableScreenHandler tableHandler) {
            tableHandler.blockEntity.selectRecipe(index);
        }
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public @NotNull PacketType<SelectAssemblyRecipeC2SPacket> getType() {
        return TYPE;
    }
}
