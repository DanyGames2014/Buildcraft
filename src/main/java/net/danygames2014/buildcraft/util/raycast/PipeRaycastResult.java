package net.danygames2014.buildcraft.util.raycast;

import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.modificationstation.stationapi.api.util.math.Direction;

public class PipeRaycastResult extends RaycastResult{
    public final Part hitPart;

    public enum Part {
        Pipe,
        Pluggable
    }

    public PipeRaycastResult(Part hitPart, HitResult hit, Box box, Direction sideHit){
        super(hit, box, sideHit);
        this.hitPart = hitPart;
    }
}
