package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.block.entity.PumpBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.util.Identifier;

public class PumpBlock extends TemplateMachineBlock {
    public PumpBlock(Identifier identifier, Material material) {
        super(identifier, material);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new PumpBlockEntity();
    }
}
