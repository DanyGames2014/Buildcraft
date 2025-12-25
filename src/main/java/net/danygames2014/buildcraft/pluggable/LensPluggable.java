package net.danygames2014.buildcraft.pluggable;

import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.client.render.PipePluggableRenderer;
import net.danygames2014.buildcraft.client.render.pluggable.LensPluggableRenderer;
import net.danygames2014.buildcraft.util.MatrixTransformation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LensPluggable extends PipePluggable {

    public int color;
    public boolean isFilter;
    protected PipeBlockEntity pipe;
    private Direction side;

    public LensPluggable() {
    }

    public LensPluggable(ItemStack stack){
        color = stack.getDamage() & 15;
        isFilter = stack.getDamage() >= 16;
    }

    @Override
    public void validate(PipeBlockEntity pipe, Direction direction) {
        this.pipe = pipe;
        this.side = direction;
    }

    @Override
    public void invalidate() {
        this.pipe = null;
        this.side = null;
    }

    @Override
    public ItemStack[] getDropItems(PipeBlockEntity pipe) {
        return new ItemStack[0];
    }

    @Override
    public boolean isBlocking(PipeBlockEntity pipe, Direction direction) {
        return false;
    }

    @Override
    public Box getBoundingBox(Direction side) {
        float[][] bounds = new float[3][2];
        // X START - END
        bounds[0][0] = 0.25F - 0.0625F;
        bounds[0][1] = 0.75F + 0.0625F;
        // Y START - END
        bounds[1][0] = 0.000F;
        bounds[1][1] = 0.125F;
        // Z START - END
        bounds[2][0] = 0.25F - 0.0625F;
        bounds[2][1] = 0.75F + 0.0625F;

        MatrixTransformation.transform(bounds, side);
        return Box.createCached(bounds[0][0], bounds[1][0], bounds[2][0], bounds[0][1], bounds[1][1], bounds[2][1]);
    }

    @Override
    public PipePluggableRenderer getRenderer() {
        return LensPluggableRenderer.INSTANCE;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        color = nbt.getByte("c");
        isFilter = nbt.getBoolean("f");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putByte("c", (byte) color);
        nbt.putBoolean("f", isFilter);
    }

    @Override
    public void writeData(DataOutputStream stream) throws IOException {
        stream.writeByte(color | (isFilter ? 0x20 : 0));
    }

    @Override
    public void readData(DataInputStream stream) throws IOException {
        int flags = stream.readUnsignedByte();
        color = flags & 15;
        isFilter = (flags & 0x20) > 0;
    }

    @Override
    public boolean requiresRenderUpdate(PipePluggable old) {
        LensPluggable other = (LensPluggable) old;
        return other.color != color || other.isFilter != isFilter;
    }
}
