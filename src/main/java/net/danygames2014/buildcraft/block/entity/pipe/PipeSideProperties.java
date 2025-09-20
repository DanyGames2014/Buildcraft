package net.danygames2014.buildcraft.block.entity.pipe;

import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.math.Direction;

public class PipeSideProperties {
    PipePluggable[] pluggables = new PipePluggable[Direction.values().length];

    public void writeNbt(NbtCompound nbt){
        for(int i = 0; i < Direction.values().length; i++){
            PipePluggable pluggable = pluggables[i];
        }
    }
}
