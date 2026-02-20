package net.danygames2014.buildcraft.packet.command;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class CommandTarget {
    public abstract Class<?> getHandledClass();
    public abstract CommandReceiver handle(PlayerEntity player, DataInputStream data, World world);
    public abstract void write(DataOutputStream data, Object target);
}
