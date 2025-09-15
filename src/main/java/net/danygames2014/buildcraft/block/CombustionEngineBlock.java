package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.CombustionEngineBlockEntity;
import net.danygames2014.buildcraft.block.entity.StirlingEngineBlockEntity;
import net.danygames2014.buildcraft.screen.handler.CombustionEngineScreenHandler;
import net.danygames2014.buildcraft.screen.handler.StirlingEngineScreenHandler;
import net.danygames2014.nyalib.block.DropInventoryOnBreak;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;

public class CombustionEngineBlock extends BaseEngineBlock implements DropInventoryOnBreak {
    public CombustionEngineBlock(Identifier identifier) {
        super(identifier);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new CombustionEngineBlockEntity();
    }

    @Override
    public String getBaseTexturePath() {
        return "/assets/buildcraft/stationapi/textures/block/engine_base_combustion.png";
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if (world.getBlockEntity(x, y, z) instanceof CombustionEngineBlockEntity engine) {
            GuiHelper.openGUI(player, Buildcraft.NAMESPACE.id("combustion_engine"), engine, new CombustionEngineScreenHandler(player, engine));
            return true;
        }

        return false;
    }

    @Override
    public boolean shouldDropInventory(World world, int x, int y, int z) {
        return true;
    }
}
