package net.danygames2014.buildcraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;

public abstract class BasePipeBlock extends TemplateBlockWithEntity {
    public BasePipeBlock(Identifier identifier, Material material) {
        super(identifier, material);
    }

    @Override
    protected abstract BlockEntity createBlockEntity();
}
