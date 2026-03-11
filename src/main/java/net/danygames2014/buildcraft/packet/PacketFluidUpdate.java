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

public class PacketFluidUpdate extends Packet implements ManagedPacket<PacketFluidUpdate> {
    public static final PacketType<PacketFluidUpdate> TYPE = PacketType.builder(true, false, PacketFluidUpdate::new).build();

    public static int FLUID_ID_BIT = 0;
    public static int FLUID_AMOUNT_BIT = 1;
    public static int FLUID_DATA_NUM = 2;
    
    public FluidRenderData renderCache = new FluidRenderData();
    public BitSet delta;
    int x;
    int y;
    int z;
    boolean isChunkDataPacket;

    public PacketFluidUpdate(int xCoord, int yCoord, int zCoord) {
        this.x = xCoord;
        this.y = yCoord;
        this.z = zCoord;
    }

    public PacketFluidUpdate(int xCoord, int yCoord, int zCoord, boolean chunkPacket) {
        this.x = xCoord;
        this.y = yCoord;
        this.z = zCoord;
        this.isChunkDataPacket = chunkPacket;
    }

    public PacketFluidUpdate() {
    }

    @Override
    public void read(DataInputStream stream) {
        try {
            this.x = stream.readInt();
            this.y = stream.readInt();
            this.z = stream.readInt();
            this.isChunkDataPacket = stream.readBoolean();
            
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
        byte[] deltaBytes = toByteArray(delta);
        // System.out.printf("write %d, %d, %d = %s, %s%n", posX, posY, posZ, Arrays.toString(deltaBytes), delta);
        try {
            stream.writeInt(x);
            stream.writeInt(y);
            stream.writeInt(z);
            stream.writeBoolean(isChunkDataPacket);
            
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
        return 0;
    }

    @Override
    public @NotNull PacketType<PacketFluidUpdate> getType() {
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
