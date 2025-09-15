package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.StirlingEngineBlockEntity;
import net.danygames2014.buildcraft.screen.handler.StirlingEngineScreenHandler;
import net.danygames2014.nyalib.block.DropInventoryOnBreak;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;

public class StirlingEngineBlock extends BaseEngineBlock implements DropInventoryOnBreak {
    public StirlingEngineBlock(Identifier identifier) {
        super(identifier);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new StirlingEngineBlockEntity();
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if (world.getBlockEntity(x, y, z) instanceof StirlingEngineBlockEntity engine) {
            GuiHelper.openGUI(player, Buildcraft.NAMESPACE.id("stirling_engine"), engine, new StirlingEngineScreenHandler(player, engine));
            return true;
        }
        
        return false;
    }

    @Override
    public boolean shouldDropInventory(World world, int x, int y, int z) {
        return true;
    }
}
