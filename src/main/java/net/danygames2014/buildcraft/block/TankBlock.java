package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.block.entity.TankBlockEntity;
import net.danygames2014.nyalib.fluid.FluidBucket;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.model.block.BlockWithWorldRenderer;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;

public class TankBlock extends TemplateBlockWithEntity {
    public TankBlock(Identifier identifier) {
        super(identifier, Material.GLASS);
        this.setBoundingBox(0.125F, 0F, 0.125F, 0.875F, 1F, 0.875F);
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
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        ItemStack hand = player.getHand();
        if(hand != null && hand.getItem() instanceof FluidBucket fluidBucket){
            if(fluidBucket.getFluid() != null){
                FluidStack fluidStack = new FluidStack(fluidBucket.getFluid(), fluidBucket.getFluid().getBucketSize());

                TankBlockEntity tankBlockEntity = (TankBlockEntity) world.getBlockEntity(x, y, z);
                tankBlockEntity.insertFluid(fluidStack, null);
                return true;
            }
        }
        if(world.getBlockEntity(x, y, z) instanceof TankBlockEntity tankBlockEntity && tankBlockEntity.fluid != null){
            System.out.println(tankBlockEntity.fluid.amount);
        }
        return false;
    }
}
