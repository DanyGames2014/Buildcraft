package net.danygames2014.buildcraft.packet;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.pipe.ForgeDirection;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.FluidPipeTransporter;
import net.danygames2014.buildcraft.block.entity.pipe.transporter.FluidRenderData;
import net.danygames2014.nyalib.fluid.FluidRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.SideUtil;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.BitSet;

public class FluidUpdateS2CPacket extends Packet implements ManagedPacket<FluidUpdateS2CPacket> {
    public static final PacketType<FluidUpdateS2CPacket> TYPE = PacketType.builder(true, false, FluidUpdateS2CPacket::new).build();

    public static int FLUID_ID_BIT = 0;
    public static int FLUID_AMOUNT_BIT = 1;
    public static int FLUID_DATA_NUM = 2;
    
    public FluidRenderData renderCache = new FluidRenderData();
    public BitSet delta;
    int x;
    int y;
    int z;
    private int packetSize = 0;

    public FluidUpdateS2CPacket() {
        
    }

    public FluidUpdateS2CPacket(int xCoord, int yCoord, int zCoord, boolean worldPacket) {
        this.x = xCoord;
        this.y = yCoord;
        this.z = zCoord;
        this.worldPacket = worldPacket;
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            this.x = stream.readInt();
            this.y = stream.readInt();
            this.z = stream.readInt();
            
            int deltaLength = stream.readInt();
            byte[] deltaBytes = new byte[deltaLength];
            //noinspection ResultOfMethodCallIgnored
            stream.read(deltaBytes);
            this.delta = fromByteArray(deltaBytes);

            // Fluid Id and Color
            if (stream.readBoolean()) {
                String fluidIdString = stream.readUTF();
                int color = stream.readInt();
                
                if (fluidIdString.isBlank()) {
                    renderCache.fluidId = null;
                } else {
                    renderCache.fluidId = Identifier.tryParse(fluidIdString);
                }
                
                renderCache.color = color;
            }
            
            // Fluid Amount
            for (ForgeDirection dir : ForgeDirection.values()) {
                // Fluid Amount 
                if (stream.readBoolean()) {
                    int amount = stream.readInt();
                    renderCache.amount[dir.ordinal()] = amount;
                } else {
                    renderCache.amount[dir.ordinal()] = -1;
                }
            }
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try {
            int initialStreamSize = stream.size();
            
            byte[] deltaBytes = toByteArray(delta);
            stream.writeInt(x);
            stream.writeInt(y);
            stream.writeInt(z);
            
            stream.writeInt(deltaBytes.length);
            stream.write(deltaBytes);

            Identifier fluidId = renderCache.fluidId;

            if (delta.get(FLUID_ID_BIT)) {
                stream.writeBoolean(true);
                if (fluidId != null) {
                    stream.writeUTF(fluidId.toString());
                    stream.writeInt(renderCache.color);
                } else {
                    stream.writeUTF("");
                    stream.writeInt(0xFFFFFF);
                }
            } else {
                stream.writeBoolean(false);
            }
            
            for (ForgeDirection dir : ForgeDirection.values()) {
                if (delta.get(dir.ordinal() + FLUID_AMOUNT_BIT)) {
                    stream.writeBoolean(true);
                    if (fluidId != null) {
                        stream.writeInt(renderCache.amount[dir.ordinal()]);
                    } else {
                        stream.writeInt(0);
                    }
                } else {
                    stream.writeBoolean(false);
                }
            }
            
            packetSize = stream.size() - initialStreamSize;
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
        
        if (world.getBlockState(x,y,z).getBlock() instanceof PipeBlock pipeBlock && world.getBlockEntity(x,y,z) instanceof PipeBlockEntity pipe && pipe.transporter instanceof FluidPipeTransporter transporter) {
            if (renderCache.fluidId != null) {
                transporter.renderCache.fluidId = renderCache.fluidId;
                transporter.fluidType = FluidRegistry.get(renderCache.fluidId);
            }
            transporter.renderCache.color = renderCache.color;
            
            for (ForgeDirection dir : ForgeDirection.values()) {
                if (renderCache.amount[dir.ordinal()] != -1) {
                    transporter.renderCache.amount[dir.ordinal()] = renderCache.amount[dir.ordinal()];
                    transporter.sections[dir.ordinal()].amount = renderCache.amount[dir.ordinal()];
                }
            }
        }
    }

    @Override
    public int size() {
        return packetSize;
    }

    @Override
    public @NotNull PacketType<FluidUpdateS2CPacket> getType() {
        return TYPE;
    }

    public static BitSet fromByteArray(byte[] bytes) {
        BitSet bits = new BitSet();
        for (int i = 0; i < bytes.length * 8; i++) {
            if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
                bits.set(i);
            }
        }
        return bits;
    }

    public static byte[] toByteArray(BitSet bits) {
        byte[] bytes = new byte[2];
        for (int i = 0; i < bits.length(); i++) {
            if (bits.get(i)) {
                bytes[bytes.length - i / 8 - 1] |= (byte) (1 << (i % 8));
            }
        }
        return bytes;
    }
}
