package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.api.energy.EnergyStage;
import net.danygames2014.buildcraft.block.entity.BaseEngineBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.BooleanProperty;
import net.modificationstation.stationapi.api.state.property.EnumProperty;
import net.modificationstation.stationapi.api.state.property.Properties;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.world.BlockStateView;

import java.util.Random;

public abstract class BaseEngineBlock extends TemplateBlockWithEntity {
    public static final EnumProperty<EnergyStage> ENERGY_STAGE_PROPERTY = EnumProperty.of("energy_stage", EnergyStage.class);
    public static final BooleanProperty PUMPING_PROPERTY = BooleanProperty.of("pumping");

    public BaseEngineBlock(Identifier identifier) {
        super(identifier, Material.METAL);
    }

    @Override
    public int getRenderType() {
        return -1;
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

    @Override
    public void onPlaced(World world, int x, int y, int z) {
        super.onPlaced(world, x, y, z);
        if (world.getBlockEntity(x, y, z) instanceof BaseEngineBlockEntity engine) {
            engine.init();
        }
    }

    @Override
    public void neighborUpdate(World world, int x, int y, int z, int id) {
        if (world.getBlockEntity(x, y, z) instanceof BaseEngineBlockEntity engine) {
            engine.checkRedstonePower();
        }
    }

    @Override
    public boolean isSolidFace(BlockView blockView, int x, int y, int z, int face) {
        if (blockView.getBlockEntity(x, y, z) instanceof BaseEngineBlockEntity engine) {
            return engine.getFacing().getOpposite().getId() == face;
        }
        
        return super.isSolidFace(blockView, x, y, z, face);
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if (world.getBlockEntity(x, y, z) instanceof BaseEngineBlockEntity engine) {
            player.sendMessage("Energy:" + engine.energy + ", Heat: " + engine.heat + ", RS:" + engine.isRedstonePowered + ", Prog:" + engine.progress);
        }
        
        return super.onUse(world, x, y, z, player);
    }

    // Rendering
    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if (world.getBlockEntity(x, y, z) instanceof BaseEngineBlockEntity engine) {
            if (!engine.isBurning()) {
                return;
            }

            float particleX = (float) x + 0.5F;
            float particleY = (float) y + 0.0F + (random.nextFloat() * 6F) / 16F;
            float particleZ = (float) z + 0.5F;
            float particleOffset = 0.52F;
            float randomOffset = random.nextFloat() * 0.6F - 0.3F;

            world.addParticle("reddust", particleX - particleOffset, particleY, particleZ + randomOffset, 0.0D, 0.0D, 0.0D);
            world.addParticle("reddust", particleX + particleOffset, particleY, particleZ + randomOffset, 0.0D, 0.0D, 0.0D);
            world.addParticle("reddust", particleX + randomOffset, particleY, particleZ - particleOffset, 0.0D, 0.0D, 0.0D);
            world.addParticle("reddust", particleX + randomOffset, particleY, particleZ + particleOffset, 0.0D, 0.0D, 0.0D);
        }
    }
}
