package net.danygames2014.buildcraft.item;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.api.transport.PipePluggableItem;
import net.danygames2014.buildcraft.block.PipeBlock;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.pluggable.FacadePluggable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.template.item.TemplateItem;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class FacadeItem extends TemplateItem implements PipePluggableItem {
    public FacadeItem(Identifier identifier) {
        super(identifier);
    }

    @Override
    public PipePluggable createPipePluggable(PipeBlockEntity pipe, Direction side, ItemStack stack) {
        return new FacadePluggable(stack);
    }

    public static ItemStack createStack(Block block, int meta, boolean hollow){
        ItemStack stack = new ItemStack(Buildcraft.facade);
        stack.getStationNbt().putString("id", BlockRegistry.INSTANCE.getId(block).toString());
        stack.getStationNbt().putInt("meta", meta);
        stack.getStationNbt().putBoolean("hollow", hollow);
        return stack;
    }

    @Override
    public boolean useOnBlock(ItemStack stack, PlayerEntity user, World world, int x, int y, int z, int side) {
        BlockState blockState = world.getBlockState(x, y, z);
        if(blockState.getBlock() instanceof PipeBlock pipeBlock){
            pipeBlock.onUseItem(stack, user, world, x, y, z, side);
            return true;
        }
        return false;
    }
}
