package net.danygames2014.buildcraft.packet.command;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CommandTargetBlockEntity extends CommandTarget{
    @Override
    public Class<?> getHandledClass() {
        return BlockEntity.class;
    }

    @Override
    public CommandReceiver handle(PlayerEntity player, DataInputStream data, World world) {
        try {
            int x = data.readInt();
            int y = data.readInt();
            int z = data.readInt();
            if(world.getBlockEntity(x, y, z) instanceof CommandReceiver commandReceiver){
                return commandReceiver;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void write(DataOutputStream data, Object target) {
        BlockEntity blockEntity = (BlockEntity) target;
        try {
            data.writeInt(blockEntity.x);
            data.writeInt(blockEntity.y);
            data.writeInt(blockEntity.z);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
