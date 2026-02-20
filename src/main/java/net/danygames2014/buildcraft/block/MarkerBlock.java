package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.block.entity.LandMarkerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.BooleanProperty;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.world.BlockStateView;

public abstract class MarkerBlock extends TemplateBlockWithEntity {
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");
    
    public MarkerBlock(Identifier identifier, Material material) {
        super(identifier, material);
        this.setBoundingBox(0.4375F, 0.0F, 0.4375F, 0.5625F, 0.5625F, 0.5625F);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ACTIVE);
        builder.add(Properties.FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return super.getPlacementState(context).with(ACTIVE, false).with(Properties.FACING, context.getSide());
    }

    @Override
    public Box getCollisionShape(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public void updateBoundingBox(BlockView blockView, int x, int y, int z) {
        Direction facing = ((BlockStateView)blockView).getBlockState(x, y, z).get(Properties.FACING);
        Box box = getBoundingBox(facing);
        setBoundingBox((float) box.minX, (float) box.minY, (float) box.minZ, (float) box.maxX, (float) box.maxY, (float) box.maxZ);
    }

    private Box getBoundingBox(Direction facing) {
        double w = 0.10;
        double h = 0.65;

        return switch (facing) {
            case DOWN -> Box.createCached(0.5F - w, 1F - h, 0.5F - w, 0.5F + w, 1F, 0.5F + w);
            case UP -> Box.createCached(0.5F - w, 0F, 0.5F - w, 0.5F + w, h, 0.5F + w);
            case WEST -> Box.createCached(0.5F - w, 0.5F - w, 0F, 0.5F + w, 0.5F + w, h);
            case EAST -> Box.createCached(0.5F - w, 0.5F - w, 1 - h, 0.5F + w, 0.5F + w, 1);
            case SOUTH -> Box.createCached(0F, 0.5F - w, 0.5F - w, h, 0.5F + w, 0.5F + w);
            default -> Box.createCached(1 - h, 0.5F - w, 0.5F - w, 1F, 0.5F + w, 0.5F + w);
        };
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    public static boolean canPlaceTorch(World world, int x, int y, int z, Direction side) {
        Block block = world.getBlockState(x, y, z).getBlock();
        return block != null && (block.isFullCube() && block.isOpaque());
    }

    @Override
    public boolean canPlaceAt(World world, int x, int y, int z, int side) {
        Direction direction = Direction.byId(side);
        return canPlaceTorch(world, x - direction.getOffsetX(), y - direction.getOffsetY(), z - direction.getOffsetZ(), direction);
    }

    private void dropTorchIfCantStay(World world, int x, int y, int z) {
        BlockState blockState = world.getBlockState(x, y, z);
        if (!canPlaceAt(world, x, y, z, blockState.get(Properties.FACING).getId())) {
            dropStacks(world, x, y, z, 0, 0);
            world.setBlock(x, y, z, 0);
        }
    }

    @Override
    public void neighborUpdate(World world, int x, int y, int z, int id) {
        super.neighborUpdate(world, x, y, z, id);
        if(world.getBlockEntity(x, y, z) instanceof LandMarkerBlockEntity blockEntity){
            blockEntity.updateSignals();
            blockEntity.switchSignals();
        }
        dropTorchIfCantStay(world, x, y, z);
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if (super.onUse(world, x, y, z, player)) {
            return true;
        }

//        if (player.getHand() != null
//                    && player.getHand().getItem() instanceof IMapLocation) {
//            return false;
//        }

        if (player.isSneaking()) {
            return false;
        }

        BlockEntity tile = world.getBlockEntity(x, y, z);
        if (tile instanceof LandMarkerBlockEntity landMarkerBlockEntity) {
            landMarkerBlockEntity.tryConnection();
            return true;
        }
        return false;
    }
}
