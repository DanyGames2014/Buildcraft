package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.block.entity.RedstoneEngineBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.Identifier;

public class RedstoneEngineBlock extends BaseEngineBlock{
    public RedstoneEngineBlock(Identifier identifier) {
        super(identifier);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new RedstoneEngineBlockEntity();
    }
}
