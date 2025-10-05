package net.danygames2014.buildcraft.packet.command;

import net.fabricmc.api.EnvType;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface CommandReceiver {
    void receiveCommand(String command, EnvType side, Object sender, DataInputStream stream);
}
