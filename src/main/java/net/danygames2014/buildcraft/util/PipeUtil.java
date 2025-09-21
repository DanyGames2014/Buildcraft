package net.danygames2014.buildcraft.util;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipeConnectionType;
import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

public class PipeUtil {
    public static boolean checkPipeConnections(BlockEntity blockEntity1, BlockEntity blockEntity2){
        if(blockEntity1 == null || blockEntity2 == null){
            return false;
        }

        if(!(blockEntity1 instanceof PipeBlockEntity) && !(blockEntity2 instanceof PipeBlockEntity)){
            return false;
        }

        Direction direction = null;
        if (blockEntity1.x - 1 == blockEntity2.x) {
            direction = Direction.WEST;
        } else if (blockEntity1.x + 1 == blockEntity2.x) {
            direction = Direction.EAST;
        } else if (blockEntity1.y - 1 == blockEntity2.y) {
            direction = Direction.DOWN;
        } else if (blockEntity1.y + 1 == blockEntity2.y) {
            direction = Direction.UP;
        } else if (blockEntity1.z - 1 == blockEntity2.z) {
            direction = Direction.NORTH;
        } else if (blockEntity1.z + 1 == blockEntity2.z) {
            direction = Direction.SOUTH;
        }

        if(direction == null){
            return false;
        }

        if(blockEntity1 instanceof PipeBlockEntity pipeBlockEntity1 && pipeBlockEntity1.canConnectTo(pipeBlockEntity1.x, pipeBlockEntity1.y, pipeBlockEntity1.z, direction) == PipeConnectionType.NONE){
            return false;
        }

        if(blockEntity2 instanceof PipeBlockEntity pipeBlockEntity2 && pipeBlockEntity2.canConnectTo(pipeBlockEntity2.x, pipeBlockEntity2.y, pipeBlockEntity2.z, direction.getOpposite()) == PipeConnectionType.NONE){
            return false;
        }
        return true;
    }
}
