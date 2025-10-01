package net.danygames2014.buildcraft.util;

import net.danygames2014.buildcraft.api.core.LaserKind;
import net.danygames2014.buildcraft.api.core.Position;
import net.danygames2014.buildcraft.entity.EntityBlock;
import net.minecraft.world.World;

public class LaserUtil {
    public static EntityBlock createLaser(World world, Position p1, Position p2) {
        if (p1.equals(p2)) {
            return null;
        }

        double iSize = p2.x - p1.x;
        double jSize = p2.y - p1.y;
        double kSize = p2.z - p1.z;

        double i = p1.x;
        double j = p1.y;
        double k = p1.z;

        if (iSize != 0) {
            i += 0.5;
            j += 0.45;
            k += 0.45;

            jSize = 0.10;
            kSize = 0.10;
        } else if (jSize != 0) {
            i += 0.45;
            j += 0.5;
            k += 0.45;

            iSize = 0.10;
            kSize = 0.10;
        } else if (kSize != 0) {
            i += 0.45;
            j += 0.45;
            k += 0.5;

            iSize = 0.10;
            jSize = 0.10;
        }

        EntityBlock block = new EntityBlock(world, i, j, k, iSize, jSize, kSize);
        block.setBrightness(210);

        world.spawnEntity(block);

        return block;
    }

    public static EntityBlock[] createLaserBox(World world, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
        EntityBlock[] lasers = new EntityBlock[12];
        Position[] p = new Position[8];

        p[0] = new Position(xMin, yMin, zMin);
        p[1] = new Position(xMax, yMin, zMin);
        p[2] = new Position(xMin, yMax, zMin);
        p[3] = new Position(xMax, yMax, zMin);
        p[4] = new Position(xMin, yMin, zMax);
        p[5] = new Position(xMax, yMin, zMax);
        p[6] = new Position(xMin, yMax, zMax);
        p[7] = new Position(xMax, yMax, zMax);

        lasers[0] = LaserUtil.createLaser(world, p[0], p[1]);
        lasers[1] = LaserUtil.createLaser(world, p[0], p[2]);
        lasers[2] = LaserUtil.createLaser(world, p[2], p[3]);
        lasers[3] = LaserUtil.createLaser(world, p[1], p[3]);
        lasers[4] = LaserUtil.createLaser(world, p[4], p[5]);
        lasers[5] = LaserUtil.createLaser(world, p[4], p[6]);
        lasers[6] = LaserUtil.createLaser(world, p[5], p[7]);
        lasers[7] = LaserUtil.createLaser(world, p[6], p[7]);
        lasers[8] = LaserUtil.createLaser(world, p[0], p[4]);
        lasers[9] = LaserUtil.createLaser(world, p[1], p[5]);
        lasers[10] = LaserUtil.createLaser(world, p[2], p[6]);
        lasers[11] = LaserUtil.createLaser(world, p[3], p[7]);

        return lasers;
    }
}
