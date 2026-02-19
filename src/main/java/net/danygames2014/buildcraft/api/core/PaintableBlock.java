package net.danygames2014.buildcraft.api.core;

import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

public interface PaintableBlock {
    boolean recolorBlock(World world, int x, int y, int z, Direction side, int color);

    boolean canRemoveColor(World world, int x, int y, int z, Direction side);

    boolean removeColorFromBlock(World world, int x, int y, int z, Direction side);
}
