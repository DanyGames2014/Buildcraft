package net.danygames2014.buildcraft.util;

import net.modificationstation.stationapi.api.util.math.Direction;

public class MatrixTransformation {

    public static void mirrorY(float[][] matrix){
        float temp = matrix[1][0];
        matrix[1][0] = (matrix[1][1] - 0.5F) * -1F + 0.5F; // 1 -> 0.5F -> -0.5F -> 0F
        matrix[1][1] = (temp - 0.5F) * -1F + 0.5F; // 0 -> -0.5F -> 0.5F -> 1F
    }

    // This might have to be changed for beta directions
    public static void transform(float[][] matrix, Direction direction) {
        if ((direction.ordinal() & 0x1) == 1) {
            mirrorY(matrix);
        }

        for (int i = 0; i < (direction.ordinal() >> 1); i++) {
            rotate(matrix);
        }
    }

    public static void transformHorizontalFacing(float[][] matrix, Direction direction){
        switch (direction){
            case NORTH -> {
                rotateYClockwise(matrix);
                rotateYClockwise(matrix);
                rotateYClockwise(matrix);
            }
            case EAST -> {
                rotateYClockwise(matrix);
                rotateYClockwise(matrix);
            }
            case SOUTH -> {
                rotateYClockwise(matrix);
            }
        }
    }

    public static void rotate(float[][] matrix) {
        for (int i = 0; i < 2; i++) {
            float temp = matrix[2][i];
            matrix[2][i] = matrix[1][i];
            matrix[1][i] = matrix[0][i];
            matrix[0][i] = temp;
        }
    }

    public static void rotateYClockwise(float[][] matrix) {
        float minX = matrix[0][0];
        float maxX = matrix[0][1];

        float minZ = matrix[2][0];
        float maxZ = matrix[2][1];

        matrix[0][0] = minZ;
        matrix[0][1] = maxZ;

        matrix[2][0] = 1-maxX;
        matrix[2][1] = 1-minX;
    }

    public static float[][] deepClone(float[][] source) {
        float[][] target = source.clone();
        for (int i = 0; i < target.length; i++) {
            target[i] = source[i].clone();
        }
        return target;
    }
}
