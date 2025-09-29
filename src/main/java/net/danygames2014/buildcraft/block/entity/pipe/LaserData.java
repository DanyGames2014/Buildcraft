package net.danygames2014.buildcraft.block.entity.pipe;

import net.danygames2014.buildcraft.api.core.Position;
import net.danygames2014.buildcraft.api.core.Serializable;
import net.minecraft.nbt.NbtCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LaserData implements Serializable {
    public Position head = new Position(0, 0, 0);
    public Position tail = new Position(0, 0, 0);
    public boolean isVisible = true;
    public boolean isGlowing = false;

    public double renderSize = 1.0 / 16.0;
    public double angleY = 0;
    public double angleZ = 0;

    public double wavePosition = 0;
    public int laserTexAnimation = 0;

    // Size of the wave, from 0 to 1
    public float waveSize = 1F;

    public LaserData () {

    }

    public LaserData (Position tail, Position head) {
        this.tail.x = tail.x;
        this.tail.y = tail.y;
        this.tail.z = tail.z;

        this.head.x = head.x;
        this.head.y = head.y;
        this.head.z = head.z;
    }

    public void update () {
        double dx = head.x - tail.x;
        double dy = head.y - tail.y;
        double dz = head.z - tail.z;

        renderSize = Math.sqrt(dx * dx + dy * dy + dz * dz);
        angleZ = 360 - (Math.atan2(dz, dx) * 180.0 / Math.PI + 180.0);
        dx = Math.sqrt(renderSize * renderSize - dy * dy);
        angleY = -Math.atan2(dy, dx) * 180.0 / Math.PI;
    }

    public void iterateTexture () {
        laserTexAnimation = (laserTexAnimation + 1) % 40;
    }

    public void writeNbt(NbtCompound nbt) {
        NbtCompound headNbt = new NbtCompound();
        head.writeNBT(headNbt);
        nbt.put("head", headNbt);

        NbtCompound tailNbt = new NbtCompound();
        tail.writeNBT(tailNbt);
        nbt.put("tail", tailNbt);

        nbt.putBoolean("isVisible", isVisible);
    }

    public void readNbt(NbtCompound nbt) {
        head.readNBT(nbt.getCompound("head"));
        tail.readNBT(nbt.getCompound("tail"));
        isVisible = nbt.getBoolean("isVisible");
    }

    @Override
    public void readData(DataInputStream stream) throws IOException {
        head.readData(stream);
        tail.readData(stream);
        int flags = stream.readUnsignedByte();
        isVisible = (flags & 1) != 0;
        isGlowing = (flags & 2) != 0;
    }

    @Override
    public void writeData(DataOutputStream stream) throws IOException {
        head.writeData(stream);
        tail.writeData(stream);
        int flags = (isVisible ? 1 : 0) | (isGlowing ? 2 : 0);
        stream.writeByte(flags);
    }
}
