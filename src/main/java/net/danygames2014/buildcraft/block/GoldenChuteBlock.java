package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.block.entity.GoldenChuteBlockEntity;
import net.danygames2014.buildcraft.client.render.block.ChuteRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.Identifier;

public class GoldenChuteBlock extends ChuteBlock {
    public GoldenChuteBlock(Identifier identifier) {
        super(identifier);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            chuteRenderer = new ChuteRenderer("/assets/buildcraft/stationapi/textures/block/golden_chute_top.png", "/assets/buildcraft/stationapi/textures/block/golden_chute_side.png");
        }
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new GoldenChuteBlockEntity();
    }
}
