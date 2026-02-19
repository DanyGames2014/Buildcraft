package net.danygames2014.buildcraft.api.core;

import net.danygames2014.buildcraft.util.DirectionUtil;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;

import javax.annotation.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Position implements Serializable {
    public double x, y, z;
    @Nullable
    public Direction orientation;

    public Position() {
        x = 0;
        y = 0;
        z = 0;
        orientation = null;
    }

    public Position(double ci, double cj, double ck) {
        x = ci;
        y = cj;
        z = ck;
        orientation = null;
    }

    public Position(double ci, double cj, double ck, Direction corientation) {
        x = ci;
        y = cj;
        z = ck;
        orientation = corientation;
    }

    public Position(Position p) {
        x = p.x;
        y = p.y;
        z = p.z;
        orientation = p.orientation;
    }

    public Position(NbtCompound nbtCompound) {
        readNBT(nbtCompound);
    }

    public Position(BlockEntity blockEntity) {
        x = blockEntity.x;
        y = blockEntity.y;
        z = blockEntity.z;
        orientation = null;
    }

//    public Position(BlockIndex index) {
//        x = index.x;
//        y = index.y;
//        z = index.z;
//        orientation = ForgeDirection.UNKNOWN;
//    }

    public void moveRight(double step) {
        switch (orientation) {
            case SOUTH:
                x = x - step;
                break;
            case NORTH:
                x = x + step;
                break;
            case EAST:
                z = z + step;
                break;
            case WEST:
                z = z - step;
                break;
            default:
        }
    }

    public void moveLeft(double step) {
        moveRight(-step);
    }

    public void moveForwards(double step) {
        switch (orientation) {
            case UP:
                y = y + step;
                break;
            case DOWN:
                y = y - step;
                break;
            case SOUTH:
                z = z + step;
                break;
            case NORTH:
                z = z - step;
                break;
            case EAST:
                x = x + step;
                break;
            case WEST:
                x = x - step;
                break;
            default:
        }
    }

    public void moveBackwards(double step) {
        moveForwards(-step);
    }

    public void moveUp(double step) {
        switch (orientation) {
            case SOUTH:
            case NORTH:
            case EAST:
            case WEST:
                y = y + step;
                break;
            default:
        }

    }

    public void moveDown(double step) {
        moveUp(-step);
    }

    public void writeNBT(NbtCompound nbt) {
        nbt.putDouble("i", x);
        nbt.putDouble("j", y);
        nbt.putDouble("k", z);
        nbt.putByte("orientation", (byte) DirectionUtil.getOrdinal(orientation));
    }

    public void readNBT(NbtCompound nbt) {
        x = nbt.getDouble("i");
        y = nbt.getDouble("j");
        z = nbt.getDouble("k");
        orientation = DirectionUtil.getById(nbt.getByte("orientation"));
    }

    @Override
    public String toString() {
        return "{" + x + ", " + y + ", " + z + "}";
    }

    public Position min(Position p) {
        return new Position(p.x > x ? x : p.x, p.y > y ? y : p.y, p.z > z ? z : p.z);
    }

    public Position max(Position p) {
        return new Position(p.x < x ? x : p.x, p.y < y ? y : p.y, p.z < z ? z : p.z);
    }

    public boolean isClose(Position newPosition, float f) {
        double dx = x - newPosition.x;
        double dy = y - newPosition.y;
        double dz = z - newPosition.z;

        double sqrDis = dx * dx + dy * dy + dz * dz;

        return !(sqrDis > f * f);
    }

    @Override
    public void writeData(DataOutputStream stream) throws IOException {
        stream.writeDouble(x);
        stream.writeDouble(y);
        stream.writeDouble(z);
        stream.writeByte(DirectionUtil.getOrdinal(orientation));
    }

    @Override
    public void readData(DataInputStream stream) throws IOException {
        x = stream.readDouble();
        y = stream.readDouble();
        z = stream.readDouble();
        orientation = DirectionUtil.getById(stream.readByte());
    }

    @Override
    public int hashCode() {
        return (51 * (int) x) + (13 * (int) y) + (int) z;
    }
}
