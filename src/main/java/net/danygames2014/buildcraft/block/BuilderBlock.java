package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.AreaWorkerBlockEntity;
import net.danygames2014.buildcraft.block.entity.BuilderBlockEntity;
import net.danygames2014.buildcraft.screen.handler.BuilderScreenHandler;
import net.danygames2014.nyalib.block.DropInventoryOnBreak;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.util.Identifier;

public class BuilderBlock extends AreaWorkerBlock implements DropInventoryOnBreak {
    public BuilderBlock(Identifier identifier, Material material) {
        super(identifier, material);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return super.getPlacementState(context).with(Properties.HORIZONTAL_FACING, context.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new BuilderBlockEntity();
    }

    @Override
    public void constructWorkingArea(World world, int x, int y, int z, AreaWorkerBlockEntity areaWorker) {
        
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if (super.onUse(world, x, y, z, player)) {
            return true;
        }

        if (world.getBlockEntity(x,y,z) instanceof BuilderBlockEntity builder) {
            GuiHelper.openGUI(player, Buildcraft.NAMESPACE.id("builder"), builder, new BuilderScreenHandler(player, builder));
            return true;
        }

        return false;
    }
}
