package net.danygames2014.buildcraft.packet.clientbound;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.client.render.PipeRenderState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.math.StationBlockPos;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PipeUpdatePacket extends Packet implements ManagedPacket<PipeUpdatePacket> {
    public static final PacketType<PipeUpdatePacket> TYPE = PacketType.builder(true, false, PipeUpdatePacket::new).build();

    private PipeRenderState state;
    private BlockPos blockPos;

    public PipeUpdatePacket(){
    }

    public PipeUpdatePacket(PipeRenderState state, BlockPos blockPos){
        this.state = state;
        this.blockPos = blockPos;
    }

    @Override
    public void read(DataInputStream stream) {
        PlayerEntity player = PlayerHelper.getPlayerFromGame();
        try {
            blockPos = StationBlockPos.fromLong(stream.readLong());
            if(player.world.getBlockEntity(blockPos.getX(), blockPos.getY(), blockPos.getZ()) instanceof PipeBlockEntity pipe){
                pipe.renderState.read(stream);
            } else {
                state = new PipeRenderState();
                state.read(stream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream stream) {
        try{
            stream.writeLong(blockPos.asLong());
            state.write(stream);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull PacketType<PipeUpdatePacket> getType() {
        return TYPE;
    }
}
