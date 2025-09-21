package net.danygames2014.buildcraft.util;

import net.modificationstation.stationapi.api.util.math.Direction;

import javax.annotation.Nullable;
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
}
