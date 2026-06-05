package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.Buildcraft;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.block.States;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.Random;

public class MiningPipeBlock extends TemplateBlock {
    public MiningPipeBlock(Identifier identifier, Material material) {
        super(identifier, material);
        setBoundingBox(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
        setTickRandomly(true);
    }

    @Override
    public void onTick(World world, int x, int y, int z, Random random) {
        super.onTick(world, x, y, z, random);

        BlockState above = world.getBlockState(x, y + 1, z);
        if (!above.isOf(Buildcraft.miningPipe) && !above.isOf(Buildcraft.miningWell)) {
            world.setBlockState(x, y, z, States.AIR.get());
        }
    }

    @Override
    public void neighborUpdate(World world, int x, int y, int z, int id) {
        super.neighborUpdate(world, x, y, z, id);
        world.scheduleBlockUpdate(x, y, z, this.id, 10);
    }

    @Override
    public int getDroppedItemId(int blockMeta, Random random) {
        return 0;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }
}
