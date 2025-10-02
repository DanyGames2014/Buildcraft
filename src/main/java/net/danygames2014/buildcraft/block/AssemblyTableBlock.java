package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.AssemblyTableBlockEntity;
import net.danygames2014.buildcraft.screen.handler.AssemblyTableScreenHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;

public class AssemblyTableBlock extends TemplateMachineBlock {
    public AssemblyTableBlock(Identifier identifier, Material material) {
        super(identifier, material);
        this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new AssemblyTableBlockEntity();
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if (!world.isRemote) {
            if (world.getBlockEntity(x, y, z) instanceof AssemblyTableBlockEntity table) {
                GuiHelper.openGUI(player, Buildcraft.NAMESPACE.id("assembly_table"), table, new AssemblyTableScreenHandler(player, table));
                return true;
            }
        }

        return super.onUse(world, x, y, z, player);
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }
}
