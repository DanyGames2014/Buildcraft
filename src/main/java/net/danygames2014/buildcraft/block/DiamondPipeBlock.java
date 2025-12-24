package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.pipe.*;
import net.danygames2014.buildcraft.block.entity.pipe.behavior.PipeBehavior;
import net.danygames2014.buildcraft.screen.handler.DiamondPipeScreenHandler;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class DiamondPipeBlock extends PipeBlock {
    public DiamondPipeBlock(Identifier identifier, Material material, Identifier texture, @Nullable Identifier alternativeTexture, PipeType type, PipeBehavior behavior, PipeTransporter.PipeTransporterFactory transporter, PipeBlockEntityFactory blockEntityFactory) {
        super(identifier, material, texture, alternativeTexture, type, behavior, transporter, blockEntityFactory);
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if (super.onUse(world, x, y, z, player)) {
            return true;
        }
        
        if (world.getBlockEntity(x,y,z) instanceof DiamondPipeBlockEntity blockEntity) {
            GuiHelper.openGUI(player, Buildcraft.NAMESPACE.id("diamond_pipe"), blockEntity, new DiamondPipeScreenHandler(player, blockEntity));
            return true;
        }
        
        return false;
    }

    @Override
    public Identifier getTextureIdentifierForSide(@Nullable Direction direction, @Nullable PipeConnectionType connectionType) {
        if (direction != null) {
            return Buildcraft.NAMESPACE.id("block/pipe/diamond_" + type.name + "_pipe_" + direction.getName());
        }
        
        return super.getTextureIdentifierForSide(null, connectionType);
    }
}
