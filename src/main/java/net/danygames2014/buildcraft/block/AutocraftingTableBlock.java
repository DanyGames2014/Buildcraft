package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.block.entity.AutocraftingTableBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.util.Identifier;

public class AutocraftingTableBlock extends TemplateMachineBlock {
    public AutocraftingTableBlock(Identifier identifier, Material material) {
        super(identifier, material);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new AutocraftingTableBlockEntity();
    }
}
