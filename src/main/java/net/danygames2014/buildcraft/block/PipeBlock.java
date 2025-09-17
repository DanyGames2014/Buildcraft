package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBehavior;
import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;

public class PipeBlock extends TemplateBlockWithEntity {
    private final PipeBehavior behavior;
    
    public PipeBlock(Identifier identifier, Material material, PipeBehavior behavior) {
        super(identifier, material);
        this.behavior = behavior;
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new PipeBlockEntity(behavior);
    }
}
