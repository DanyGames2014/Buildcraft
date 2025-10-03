package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.block.entity.TankBlockEntity;
import net.danygames2014.nyalib.fluid.FluidBucket;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.BooleanProperty;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.world.BlockStateView;

public class TankBlock extends TemplateBlockWithEntity {
    public static final BooleanProperty STACKED = BooleanProperty.of("stacked");

    public TankBlock(Identifier identifier) {
        super(identifier, Material.GLASS);
        this.setBoundingBox(0.125F, 0F, 0.125F, 0.875F, 1F, 0.875F);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(STACKED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return super.getPlacementState(context).with(STACKED, false);
    }

    @Override
    public void onPlaced(World world, int x, int y, int z) {
        super.onPlaced(world, x, y, z);

        if (!world.isRemote) {
            updateStacked(world, x, y, z);
        }
    }

    @Override
    public void neighborUpdate(World world, int x, int y, int z, int id) {
        super.neighborUpdate(world, x, y, z, id);

        if (!world.isRemote) {
            updateStacked(world, x, y, z);
        }
    }

    public void updateStacked(World world, int x, int y, int z) {
        BlockState state = world.getBlockState(x, y, z);

        if (world.getBlockState(x, y - 1, z).isOf(this)) {
            if (!state.get(STACKED)) {
                world.setBlockStateWithNotify(x, y, z, state.with(STACKED, true));
            }
        } else {
            if (state.get(STACKED)) {
                world.setBlockStateWithNotify(x, y, z, state.with(STACKED, false));
            }
        }
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new TankBlockEntity();
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public boolean isSideVisible(BlockView blockView, int x, int y, int z, int side) {
        if (side == 1 && blockView instanceof BlockStateView stateView) {
            if (stateView.getBlockState(x, y, z).isOf(this) && stateView.getBlockState(x, y - 1, z).isOf(this)) {
                return false;
            }
        }

        if (side == 0 && blockView instanceof BlockStateView stateView) {
            if (stateView.getBlockState(x, y + 1, z).isOf(this) && stateView.getBlockState(x, y, z).isOf(this)) {
                return false;
            }
        }

        return super.isSideVisible(blockView, x, y, z, side);
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        ItemStack hand = player.getHand();
        if (hand != null && hand.getItem() instanceof FluidBucket fluidBucket) {
            if (fluidBucket.getFluid() != null) {
                FluidStack fluidStack = new FluidStack(fluidBucket.getFluid(), fluidBucket.getFluid().getBucketSize());

                TankBlockEntity tankBlockEntity = (TankBlockEntity) world.getBlockEntity(x, y, z);
                tankBlockEntity.insertFluid(fluidStack, null);
                return true;
            }
        }
        if (world.getBlockEntity(x, y, z) instanceof TankBlockEntity tankBlockEntity && tankBlockEntity.fluid != null) {
            System.out.println(tankBlockEntity.fluid.amount);
        }
        return false;
    }
}
