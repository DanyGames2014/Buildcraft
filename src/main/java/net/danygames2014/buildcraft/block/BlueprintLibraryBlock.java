package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.ArchitectTableBlockEntity;
import net.danygames2014.buildcraft.block.entity.BlueprintLibraryBlockEntity;
import net.danygames2014.buildcraft.screen.handler.ArchitectTableScreenHandler;
import net.danygames2014.buildcraft.screen.handler.BlueprintLibraryScreenHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;

public class BlueprintLibraryBlock extends TemplateMachineBlock {
    public BlueprintLibraryBlock(Identifier identifier, Material material) {
        super(identifier, material);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new BlueprintLibraryBlockEntity();
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if (super.onUse(world, x, y, z, player)) {
            return true;
        }

        if (world.getBlockEntity(x,y,z) instanceof BlueprintLibraryBlockEntity blueprintLibrary) {
            GuiHelper.openGUI(player, Buildcraft.NAMESPACE.id("blueprint_library"), blueprintLibrary, new BlueprintLibraryScreenHandler(player, blueprintLibrary));
            return true;
        }

        return false;
    }
}
