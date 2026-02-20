package net.danygames2014.buildcraft.packet.command;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class CommandTargetScreenHandler extends CommandTarget{
    @Override
    public Class<?> getHandledClass() {
        return ScreenHandler.class;
    }

    @Override
    public CommandReceiver handle(PlayerEntity player, DataInputStream data, World world) {
        ScreenHandler screenHandler = player.currentScreenHandler;
        if(screenHandler instanceof CommandReceiver){
            return (CommandReceiver) screenHandler;
        }
        return null;
    }

    @Override
    public void write(DataOutputStream data, Object target) {

    }
}
