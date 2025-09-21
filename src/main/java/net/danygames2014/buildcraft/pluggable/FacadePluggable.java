package net.danygames2014.buildcraft.pluggable;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.client.render.PipePluggableRenderer;
import net.danygames2014.buildcraft.client.render.pluggable.FacadePluggableRenderer;
import net.danygames2014.buildcraft.util.MatrixTransformation;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public class FacadePluggable extends PipePluggable {
    private Block block;
    private int meta;
    private boolean transparent;
    private boolean hollow;

    public static final float FACADE_THICKNESS = 2F / 16F;

    public FacadePluggable(ItemStack stack){
        NbtCompound nbt = stack.getStationNbt();
        block = BlockRegistry.INSTANCE.get(Identifier.tryParse(nbt.getString("id")));
        meta = nbt.getInt("meta");
        transparent = !block.isOpaque();
        hollow = nbt.getBoolean("hollow");
    }

    public FacadePluggable(){

    }

    public boolean isHollow() {
        return hollow;
    }

    public Block getBlock(){
        return block;
    }

    public int getMeta(){
        return meta;
    }

    public boolean isTransparent(){
        return transparent;
    }

    @Override
    public boolean requiresRenderUpdate(PipePluggable old) {
        FacadePluggable other = (FacadePluggable) old;
        return other.block != block || other.meta != meta || other.transparent != transparent || other.hollow != hollow;
    }

    @Override
    public ItemStack[] getDropItems(PipeBlockEntity pipe) {
        return new ItemStack[0];
    }

    @Override
    public Box getBoundingBox(Direction side) {
        float[][] bounds = new float[3][2];
        // X START - END
        bounds[0][0] = 0.0F;
        bounds[0][1] = 1.0F;
        // Y START - END
        bounds[1][0] = 0.0F;
        bounds[1][1] = FACADE_THICKNESS;
        // Z START - END
        bounds[2][0] = 0.0F;
        bounds[2][1] = 1.0F;

        MatrixTransformation.transform(bounds, side);
        return Box.createCached(bounds[0][0], bounds[1][0], bounds[2][0], bounds[0][1], bounds[1][1], bounds[2][1]);
    }

    @Override
    public PipePluggableRenderer getRenderer() {
        return FacadePluggableRenderer.INSTANCE;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        block = BlockRegistry.INSTANCE.get(Identifier.tryParse(nbt.getString("id")));
        meta = nbt.getInt("meta");
        transparent = !block.isOpaque();
        hollow = nbt.getBoolean("hollow");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("id", BlockRegistry.INSTANCE.getId(block).toString());
        nbt.putInt("meta", meta);
        nbt.putBoolean("hollow", hollow);
    }

    @Override
    public boolean isBlocking(PipeBlockEntity pipe, Direction direction) {
        return !isHollow();
    }

    @Override
    public boolean isSolidOnSide() {
        return !isHollow();
    }
}
