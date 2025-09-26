package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.block.entity.AssemblyTableBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.util.Identifier;

public class AssemblyTableBlock extends TemplateMachineBlock {
    public AssemblyTableBlock(Identifier identifier, Material material) {
        super(identifier, material);
        this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new AssemblyTableBlockEntity();
    }
}
