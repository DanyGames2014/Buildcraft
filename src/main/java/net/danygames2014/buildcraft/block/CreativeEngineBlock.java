package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.block.entity.CreativeEngineBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.Random;

public class CreativeEngineBlock extends BaseEngineBlock {
    public CreativeEngineBlock(Identifier identifier) {
        super(identifier);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new CreativeEngineBlockEntity();
    }

    @Override
    public String getBaseTexturePath() {
        return "/assets/buildcraft/stationapi/textures/block/engine_base_creative.png";
    }

    @Override
    public String getChamberTexturePath() {
        return "/assets/buildcraft/stationapi/textures/block/engine_chamber_creative.png";
    }

    @Override
    public String getTrunkTexturePath() {
        return "/assets/buildcraft/stationapi/textures/block/engine_trunk_creative.png";
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if (!world.isRemote && player.getHand() == null) {
            if (world.getBlockEntity(x, y, z) instanceof CreativeEngineBlockEntity creativeEngineBlockEntity) {
                creativeEngineBlockEntity.switchPowerMode(false, player);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBlockBreakStart(World world, int x, int y, int z, PlayerEntity player) {
        if (!world.isRemote && player.getHand() == null) {
            if (world.getBlockEntity(x, y, z) instanceof CreativeEngineBlockEntity creativeEngineBlockEntity) {
                creativeEngineBlockEntity.switchPowerMode(true, player);
            }
        }
    }
}
