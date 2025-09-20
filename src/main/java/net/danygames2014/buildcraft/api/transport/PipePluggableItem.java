package net.danygames2014.buildcraft.api.transport;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.util.math.Direction;

public interface PipePluggableItem {
    PipePluggable createPipePluggable(PipeBlockEntity pipe, Direction side, ItemStack stack);
}
