package net.danygames2014.buildcraft.packet;

import net.danygames2014.buildcraft.screen.handler.IntegrationTableScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class IntegrationTablePreviewS2CPacket extends Packet implements ManagedPacket<IntegrationTablePreviewS2CPacket> {
    public static final PacketType<IntegrationTablePreviewS2CPacket> TYPE = PacketType.builder(true, false, IntegrationTablePreviewS2CPacket::new).build();
    
    ItemStack previewStack;

    public IntegrationTablePreviewS2CPacket() {
    }
    
    public IntegrationTablePreviewS2CPacket(ItemStack previewStack) {
        this.previewStack = previewStack;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            NbtCompound stackNbt = new NbtCompound();

            int length = stream.readInt();
            if (length > 0) {
                byte[] bytes = new byte[length];
                stream.readFully(bytes);
                stackNbt = NbtIo.readCompressed(new ByteArrayInputStream(bytes));
            }

            previewStack = new ItemStack(stackNbt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            NbtCompound stackNbt = new NbtCompound();
            if (previewStack == null) {
                stream.writeInt(0);
                return;
            }

            previewStack.writeNbt(stackNbt);

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            NbtIo.writeCompressed(stackNbt, byteStream);
            byte[] bytes = byteStream.toByteArray();
            stream.writeInt(bytes.length); // length
            stream.write(bytes); // nbt data
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        SideUtil.run(() -> handleClient(networkHandler), () -> {});
    }

    @Environment(EnvType.CLIENT)
    public void handleClient(NetworkHandler networkHandler) {
        PlayerEntity player = PlayerHelper.getPlayerFromPacketHandler(networkHandler);
        World world = player.world;
        
        if (player.currentScreenHandler instanceof IntegrationTableScreenHandler integrationTableHandler) {
            integrationTableHandler.blockEntity.previewStack = previewStack;
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull PacketType<IntegrationTablePreviewS2CPacket> getType() {
        return TYPE;
    }
}
