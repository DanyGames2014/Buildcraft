package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.block.entity.PathMarkerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.BooleanProperty;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;

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
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }
}
