package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.block.entity.LandMarkerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;

public class LandMarkerBlock extends MarkerBlock {
    public LandMarkerBlock(Identifier identifier, Material material) {
        super(identifier, material);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new LandMarkerBlockEntity();
    }
}
