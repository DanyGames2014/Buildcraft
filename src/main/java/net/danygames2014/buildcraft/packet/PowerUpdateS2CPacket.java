package net.danygames2014.buildcraft.packet;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.EnergyPipeTransporter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PowerUpdateS2CPacket extends Packet implements ManagedPacket<PowerUpdateS2CPacket> {
    public static final PacketType<PowerUpdateS2CPacket> TYPE = PacketType.builder(true, false, PowerUpdateS2CPacket::new).build();

    public int x;
    public int y;
    public int z;
    
    public boolean overload;
    public short[] displayPower;

    public PowerUpdateS2CPacket() {
        
    }

    public PowerUpdateS2CPacket(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void read(DataInputStream stream) {
        displayPower = new short[] { 0, 0, 0, 0, 0, 0 };
        
        try {
            x = stream.readInt();
            y = stream.readInt();
            z = stream.readInt();
            overload = stream.readBoolean();
            for (int i = 0; i < displayPower.length; i++) {
                displayPower[i] = stream.readByte();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            stream.writeInt(x);
            stream.writeInt(y);
            stream.writeInt(z);
            stream.writeBoolean(overload);
            for (short value : displayPower) {
                stream.writeByte(value);
            }
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

        if (world.getBlockState(x,y,z).getBlock() instanceof PipeBlock pipeBlock && world.getBlockEntity(x,y,z) instanceof PipeBlockEntity pipe && pipe.transporter instanceof EnergyPipeTransporter transporter) {
            transporter.handlePowerPacket(this);
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull PacketType<PowerUpdateS2CPacket> getType() {
        return TYPE;
    }
}
