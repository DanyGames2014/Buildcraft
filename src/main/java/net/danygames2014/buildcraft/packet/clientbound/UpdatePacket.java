package net.danygames2014.buildcraft.packet.clientbound;

import net.danygames2014.buildcraft.api.core.Serializable;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class UpdatePacket extends Packet {

    public Serializable payload;
    public byte[] dataBuffer;

    public UpdatePacket(){}

    public UpdatePacket(Serializable payload){
        this.payload = payload;
    }

    @Override
    public void read(DataInputStream stream) {
        readIdentificationData(stream);
        try {
            int dataLength = stream.readInt();
            if(dataLength > 0) {
                dataBuffer = new byte[dataLength];
                stream.readFully(dataBuffer);
            } else {
                dataBuffer = new byte[0];
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void readIdentificationData(DataInputStream stream);

    @Override
    public void write(DataOutputStream stream) {
        writeIdentificationData(stream);

        ByteArrayOutputStream tempByteStream = new ByteArrayOutputStream();
        DataOutputStream tempDos = new DataOutputStream(tempByteStream);

        try {
            payload.writeData(tempDos);

            byte[] bytesToSend = tempByteStream.toByteArray();
            stream.writeInt(bytesToSend.length);
            stream.write(bytesToSend);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void writeIdentificationData(DataOutputStream stream);

    @Override
    public int size() {
        return 0;
    }
}
