package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.block.entity.AreaWorkerBlockEntity;
import net.danygames2014.buildcraft.block.entity.LandMarkerBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public abstract class AreaWorkerBlock extends TemplateMachineBlock {
    public AreaWorkerBlock(Identifier identifier, Material material) {
        super(identifier, material);
    }

    @Override
    public void onPlaced(World world, int x, int y, int z) {
        super.onPlaced(world, x, y, z);

        if (world.getBlockEntity(x, y, z) instanceof AreaWorkerBlockEntity areaWorker) {
            constructWorkingArea(world, x, y, z, areaWorker);
        }
    }

    public void constructWorkingArea(World world, int x, int y, int z, AreaWorkerBlockEntity areaWorker) {
        for (Direction side : Direction.values()) {
            if (world.getBlockEntity(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ()) instanceof LandMarkerBlockEntity landMarker) {
                if (!landMarker.origin.isSet()) {
                    return;
                }

                areaWorker.constructWorkingArea(landMarker.origin);
                landMarker.removeFromWorld();
            }
        }
    }
}
