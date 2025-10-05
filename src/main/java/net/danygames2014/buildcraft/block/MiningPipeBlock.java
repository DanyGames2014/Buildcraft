package net.danygames2014.buildcraft.block;

import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.Random;

public class MiningPipeBlock extends TemplateBlock {
    public MiningPipeBlock(Identifier identifier, Material material) {
        super(identifier, material);
        setBoundingBox(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
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
