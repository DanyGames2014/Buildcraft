package net.danygames2014.buildcraft.util.raycast;

import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.modificationstation.stationapi.api.util.math.Direction;

public class IndexRaycastResult extends RaycastResult{
    public final int index;
    public IndexRaycastResult(int index, HitResult hit, Box box, Direction sideHit) {
        super(hit, box, sideHit);
        this.index = index;
    }
}
