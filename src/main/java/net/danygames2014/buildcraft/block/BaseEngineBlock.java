package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.api.energy.EnergyStage;
import net.danygames2014.buildcraft.block.entity.BaseEngineBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.BooleanProperty;
import net.modificationstation.stationapi.api.state.property.EnumProperty;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;

public abstract class BaseEngineBlock extends TemplateBlockWithEntity {
    public static final EnumProperty<EnergyStage> ENERGY_STAGE_PROPERTY = EnumProperty.of("energy_stage", EnergyStage.class);
    public static final BooleanProperty PUMPING_PROPERTY = BooleanProperty.of("pumping");

    public BaseEngineBlock(Identifier identifier, Material material) {
        super(identifier, material);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ENERGY_STAGE_PROPERTY);
        builder.add(Properties.FACING);
        builder.add(PUMPING_PROPERTY);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return super.getPlacementState(context)
                .with(ENERGY_STAGE_PROPERTY, EnergyStage.BLUE)
                .with(Properties.FACING, context.getPlayerLookDirection().getOpposite())
                .with(PUMPING_PROPERTY, false);
    }

    @Override
    protected abstract BlockEntity createBlockEntity();
}
