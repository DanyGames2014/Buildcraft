package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.block.entity.RedstoneEngineBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.Identifier;

public class RedstoneEngineBlock extends BaseEngineBlock{
    public RedstoneEngineBlock(Identifier identifier) {
        super(identifier);
    }

    @Override
    public String getBaseTexturePath() {
        return "/assets/buildcraft/stationapi/textures/block/engine_base_redstone.png";
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new RedstoneEngineBlockEntity();
    }
}
