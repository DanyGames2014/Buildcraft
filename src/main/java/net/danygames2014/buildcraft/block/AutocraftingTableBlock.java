package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.AutocraftingTableBlockEntity;
import net.danygames2014.buildcraft.screen.handler.AutocraftingTableScreenHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;

public class AutocraftingTableBlock extends TemplateMachineBlock {
    public AutocraftingTableBlock(Identifier identifier, Material material) {
        super(identifier, material);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new AutocraftingTableBlockEntity();
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if (!world.isRemote) {
            if (world.getBlockEntity(x, y, z) instanceof AutocraftingTableBlockEntity table) {
                GuiHelper.openGUI(player, Buildcraft.NAMESPACE.id("autocrafting_table"), table, new AutocraftingTableScreenHandler(player, table));
            }
        }
        
        return super.onUse(world, x, y, z, player);
    }
}
