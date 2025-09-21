package net.danygames2014.buildcraft.pluggable;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.client.render.PipePluggableRenderer;
import net.danygames2014.buildcraft.client.render.pluggable.PlugPluggableRenderer;
import net.danygames2014.buildcraft.util.MatrixTransformation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.modificationstation.stationapi.api.util.math.Direction;

public class PlugPluggable extends PipePluggable {

    @Override
    public ItemStack[] getDropItems(PipeBlockEntity pipe) {
        return new ItemStack[]{new ItemStack(Buildcraft.plug)};
    }

    @Override
    public Box getBoundingBox(Direction side) {
        float[][] bounds = new float[3][2];
        // X START - END
        bounds[0][0] = 0.25F;
        bounds[0][1] = 0.75F;
        // Y START - END
        bounds[1][0] = 0.125F;
        bounds[1][1] = 0.251F;
        // Z START - END
        bounds[2][0] = 0.25F;
        bounds[2][1] = 0.75F;

        MatrixTransformation.transform(bounds, side);
        return Box.createCached(bounds[0][0], bounds[1][0], bounds[2][0], bounds[0][1], bounds[1][1], bounds[2][1]);
    }

    @Override
    public PipePluggableRenderer getRenderer() {
        return PlugPluggableRenderer.INSTANCE;
    }

    @Override
    public boolean requiresRenderUpdate(PipePluggable old) {
        return false;
    }

    @Override
    public boolean isSolidOnSide() {
        return true;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
    }
}
