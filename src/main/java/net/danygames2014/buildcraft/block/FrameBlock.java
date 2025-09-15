package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.Buildcraft;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.BooleanProperty;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.ArrayList;
import java.util.HashMap;

public class FrameBlock extends TemplateBlock {
    public static final HashMap<Direction, BooleanProperty> PROPERTY_LOOKUP = new HashMap<>();

    static {
        PROPERTY_LOOKUP.put(Direction.UP, Properties.UP);
        PROPERTY_LOOKUP.put(Direction.DOWN, Properties.DOWN);
        PROPERTY_LOOKUP.put(Direction.NORTH, Properties.NORTH);
        PROPERTY_LOOKUP.put(Direction.SOUTH, Properties.SOUTH);
        PROPERTY_LOOKUP.put(Direction.EAST, Properties.EAST);
        PROPERTY_LOOKUP.put(Direction.WEST, Properties.WEST);
    }
    
    public FrameBlock(Identifier identifier, Material material) {
        super(identifier, material);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.UP, Properties.DOWN, Properties.NORTH, Properties.SOUTH, Properties.EAST, Properties.WEST);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return getDefaultState()
                .with(Properties.UP, false)
                .with(Properties.DOWN, false)
                .with(Properties.NORTH, false)
                .with(Properties.SOUTH, false)
                .with(Properties.EAST, false)
                .with(Properties.WEST, false);
    }

    @Override
    public void neighborUpdate(World world, int x, int y, int z, int id) {
        super.neighborUpdate(world, x, y, z, id);
        updateConnections(world, x, y, z);
    }

    @Override
    public void onPlaced(World world, int x, int y, int z) {
        super.onPlaced(world, x, y, z);
        updateConnections(world, x, y, z);
    }

    public void updateConnections(World world, int x, int y, int z) {
        BlockState state = world.getBlockState(x, y, z);

        for (Direction side : Direction.values()) {
            state = state.with(PROPERTY_LOOKUP.get(side), this.canConnectTo(world, x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ()));
        }

        world.setBlockState(x, y, z, state);
    }
    
    public boolean canConnectTo(World world, int x, int y, int z) {
        return world.getBlockState(x, y, z).isOf(Buildcraft.frame);
    }

    @Override
    public Box getBoundingBox(World world, int x, int y, int z) {
        BlockState state = world.getBlockState(x, y, z);

        float minX = 0.34375F;
        float minY = 0.34375F;
        float minZ = 0.34375F;

        float maxX = 0.65625F;
        float maxY = 0.65625F;
        float maxZ = 0.65625F;

        if (state.get(Properties.UP)) {
            maxY = 1.0F;
        }

        if (state.get(Properties.DOWN)) {
            minY = 0.0F;
        }

        if (state.get(Properties.WEST)) {
            maxZ = 1.0F;
        }

        if (state.get(Properties.EAST)) {
            minZ = 0.0F;
        }

        if (state.get(Properties.NORTH)) {
            minX = 0.0F;
        }

        if (state.get(Properties.SOUTH)) {
            maxX = 1.0F;
        }

        return Box.createCached(x + minX, y + minY, z + minZ, x + maxX, y + maxY, z + maxZ);
    }

    @Override
    public void addIntersectingBoundingBox(World world, int x, int y, int z, Box box, ArrayList boxes) {
        BlockState state = world.getBlockState(x, y, z);

        if (state.get(Properties.UP)) {
            this.setBoundingBox(0.34375F, 0.34375F, 0.34375F, 0.65625F, 1.0F, 0.65625F);
            super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
        }

        if (state.get(Properties.DOWN)) {
            this.setBoundingBox(0.34375F, 0.0F, 0.34375F, 0.65625F, 0.65625F, 0.65625F);
            super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
        }

        if (state.get(Properties.WEST)) {
            this.setBoundingBox(0.34375F, 0.34375F, 0.34375F, 0.65625F, 0.65625F, 1.0F);
            super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
        }

        if (state.get(Properties.EAST)) {
            this.setBoundingBox(0.34375F, 0.34375F, 0.0F, 0.65625F, 0.65625F, 0.65625F);
            super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
        }

        if (state.get(Properties.SOUTH)) {
            this.setBoundingBox(0.34375F, 0.34375F, 0.34375F, 1.0F, 0.65625F, 0.65625F);
            super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
        }

        if (state.get(Properties.NORTH)) {
            this.setBoundingBox(0.0F, 0.34375F, 0.34375F, 0.65625F, 0.65625F, 0.65625F);
            super.addIntersectingBoundingBox(world, x, y, z, box, boxes);
        }

        this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
