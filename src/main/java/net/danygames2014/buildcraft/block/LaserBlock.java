package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.block.entity.LaserBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.ArrayList;

public class LaserBlock extends TemplateMachineBlock {

    private static final Box[][] collisionBoxes = {
            {Box.create(0.0, 0.75, 0.0, 1.0, 1.0, 1.0), Box.create(0.3125, 0.1875, 0.3125, 0.6875, 0.75, 0.6875)}, // -Y
            {Box.create(0.0, 0.0, 0.0, 1.0, 0.25, 1.0), Box.create(0.3125, 0.25, 0.3125, 0.6875, 0.8125, 0.6875)}, // +Y
            {Box.create(0.0, 0.0, 0.75, 1.0, 1.0, 1.0), Box.create(0.3125, 0.3125, 0.1875, 0.6875, 0.6875, 0.75)}, // -Z
            {Box.create(0.0, 0.0, 0.0, 1.0, 1.0, 0.25), Box.create(0.3125, 0.3125, 0.25, 0.6875, 0.6875, 0.8125)}, // +Z
            {Box.create(0.75, 0.0, 0.0, 1.0, 1.0, 1.0), Box.create(0.1875, 0.3125, 0.3125, 0.75, 0.6875, 0.6875)}, // -X
            {Box.create(0.0, 0.0, 0.0, 0.25, 1.0, 1.0), Box.create(0.25, 0.3125, 0.3125, 0.8125, 0.6875, 0.6875)} // +X
    };

    public LaserBlock(Identifier identifier, Material material) {
        super(identifier, material);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return super.getPlacementState(context).with(Properties.FACING, context.getSide());
    }

    @Override
    public void addIntersectingBoundingBox(World world, int x, int y, int z, Box box, ArrayList boxes) {
        Direction direction = world.getBlockState(x, y, z).get(Properties.FACING);
        Box[] boxArray = collisionBoxes[direction.ordinal()];
        for(Box colBox : boxArray){
            this.setBoundingBox((float) colBox.minX, (float) colBox.minY, (float) colBox.minZ, (float) colBox.maxX, (float) colBox.maxY, (float) colBox.maxZ);
            super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
        }
        this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public HitResult raycast(World world, int x, int y, int z, Vec3d startPos, Vec3d endPos) {
        Direction direction = world.getBlockState(x, y, z).get(Properties.FACING);
        Box[] boxArray = collisionBoxes[direction.ordinal()];
        HitResult closest = null;
        for (Box box : boxArray) {
            HitResult mop = box.offset(x, y, z).raycast(startPos, endPos);
            if (mop != null) {
                if (closest != null && mop.pos.distanceTo(startPos) < closest.pos.distanceTo(startPos)) {
                    closest = mop;
                } else {
                    closest = mop;
                }
            }
        }
        if (closest != null) {
            closest.blockX = x;
            closest.blockY = y;
            closest.blockZ = z;
        }
        return closest;
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new LaserBlockEntity();
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }
}
