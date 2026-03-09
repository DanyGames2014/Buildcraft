package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.block.entity.FloodGateBlockEntity;
import net.danygames2014.uniwrench.api.WrenchMode;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

public class FloodGateBlock extends TemplateMachineBlock{
    public FloodGateBlock(Identifier identifier, Material material) {
        super(identifier, material);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new FloodGateBlockEntity();
    }

    @Override
    public boolean wrenchRightClick(ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side, WrenchMode wrenchMode) {
        if(world.getBlockEntity(x, y, z) instanceof FloodGateBlockEntity blockEntity){
            if(side == 1){
                blockEntity.rebuildQueue();
            } else {
                blockEntity.switchSide(Direction.byId(side));
            }
            return true;
        }
        return false;
    }

    @Override
    public void neighborUpdate(World world, int x, int y, int z, int id) {
        if(world.getBlockEntity(x, y, z) instanceof FloodGateBlockEntity blockEntity){
            blockEntity.neighborUpdate();
        }
    }
}
