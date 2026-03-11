package net.danygames2014.buildcraft.util;

import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class DirectionUtil {
    public static Direction[] directionsWithInvalid;
    static {
        Direction[] directions = Direction.values();
        directionsWithInvalid = Arrays.copyOf(directions, directions.length + 1);
    }

    public static int getOrdinal(@Nullable Direction direction){
        if(direction == null){
            return 6;
        }
        return direction.ordinal();
    }

    public static @Nullable Direction getById(int id){
        return directionsWithInvalid[Math.max(0, Math.min(id, directionsWithInvalid.length - 1))];
    }

    public static Direction getRealDirectionFromStapiDirection(Direction direction){
        return switch (direction){
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            case NORTH -> Direction.EAST;
            case SOUTH -> Direction.WEST;
            case EAST -> Direction.NORTH;
            case WEST -> Direction.SOUTH;
        };
    }
}
