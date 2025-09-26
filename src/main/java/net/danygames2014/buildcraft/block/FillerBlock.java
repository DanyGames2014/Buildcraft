package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.api.core.Debuggable;
import net.danygames2014.buildcraft.block.entity.FillerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.EnumProperty;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.StringIdentifiable;

public class FillerBlock extends TemplateMachineBlock implements Debuggable {
    public static final EnumProperty<FillerPattern> FILLER_PATTERN = EnumProperty.of("pattern", FillerPattern.class);
    
    public FillerBlock(Identifier identifier, Material material) {
        super(identifier, material);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FILLER_PATTERN);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return super.getPlacementState(context).with(FILLER_PATTERN, FillerPattern.NONE);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new FillerBlockEntity();
    }

    @Override
    public void debug(ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side) {
        BlockState state = world.getBlockState(x,y,z);
        
        world.setBlockStateWithNotify(x,y,z, state.cycle(FILLER_PATTERN));
    }

    public enum FillerPattern implements StringIdentifiable {
        NONE("none"),
        CLEAR("clear"),
        FILL("fill"),
        FLATTEN("flatten"),
        HORIZON("horizon"),
        PYRAMID("pyramid"),
        STAIRS("stairs"),
        WALLS("walls");

        private final String id;
        
        FillerPattern(String id) {
            this.id = id;
        }

        @Override
        public String asString() {
            return id;
        }
    }
}
