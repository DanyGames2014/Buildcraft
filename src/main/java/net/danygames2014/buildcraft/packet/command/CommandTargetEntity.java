package net.danygames2014.buildcraft.packet.command;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CommandTargetEntity extends CommandTarget{
    @Override
    public Class<?> getHandledClass() {
        return Entity.class;
    }

    @Override
    public CommandReceiver handle(PlayerEntity player, DataInputStream data, World world) {
        try {
            int id = data.readInt();
            Entity entity = (Entity) world.entities.get(id);
            if(entity instanceof CommandReceiver){
                return (CommandReceiver) entity;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void write(DataOutputStream data, Object target) {
        Entity entity = (Entity) target;
        try {
            data.writeInt(entity.id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
