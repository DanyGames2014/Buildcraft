package net.danygames2014.buildcraft.block.entity.pipe.transporter;

import net.modificationstation.stationapi.api.util.Identifier;

public class FluidRenderData {
    public Identifier fluidId;
    public int color;
    public int[] amount = new int[7];

    public FluidRenderData duplicate() {
        FluidRenderData n = new FluidRenderData();
        n.fluidId = fluidId;
        n.color = color;
        System.arraycopy(this.amount, 0, n.amount, 0, 7);
        return n;
    }
}
