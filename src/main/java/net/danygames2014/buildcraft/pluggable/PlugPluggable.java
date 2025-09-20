package net.danygames2014.buildcraft.pluggable;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.client.render.PipePluggableRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.modificationstation.stationapi.api.util.math.Direction;

public class PlugPluggable extends PipePluggable {
    @Override
    public ItemStack[] getDropItems(PipeBlockEntity pipe) {
        return new ItemStack[0];
    }

    @Override
    public Box getBoundingBox(Direction side) {
        return null;
    }

    @Override
    public PipePluggableRenderer getRenderer() {
        return null;
    }

    @Override
    public boolean requiresRenderUpdate(PipePluggable old) {
        return false;
    }
}
