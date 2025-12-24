package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.ChuteBlockEntity;
import net.danygames2014.buildcraft.client.render.block.ChuteRenderer;
import net.danygames2014.buildcraft.screen.handler.ChuteScreenHandler;
import net.danygames2014.nyalib.block.DropInventoryOnBreak;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.client.model.block.BlockWithInventoryRenderer;
import net.modificationstation.stationapi.api.client.model.block.BlockWithWorldRenderer;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("deprecation")
public class ChuteBlock extends TemplateMachineBlock implements BlockWithWorldRenderer, BlockWithInventoryRenderer, DropInventoryOnBreak {
    @Environment(EnvType.CLIENT)
    private ChuteRenderer chuteRenderer;

    public ChuteBlock(Identifier identifier) {
        super(identifier, Material.METAL);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            chuteRenderer = new ChuteRenderer();
        }
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new ChuteBlockEntity();
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void renderInventory(BlockRenderManager blockRenderManager, int i) {
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        chuteRenderer.render(Minecraft.INSTANCE.textureManager, 0, 0, 0);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean renderWorld(BlockRenderManager blockRenderManager, BlockView blockView, int i, int i1, int i2) {
        return true;
    }

    // TODO: dont open gui when player is holding pipe
    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        super.onUse(world, x, y, z, player);

        if(player.isSneaking()){
            return false;
        }
        if(world.getBlockEntity(x, y, z) instanceof ChuteBlockEntity chuteBlockEntity){
            GuiHelper.openGUI(player, Buildcraft.NAMESPACE.id("chute_screen"), chuteBlockEntity, new ChuteScreenHandler(player.inventory, chuteBlockEntity));
            return true;
        }
        return false;
    }
}
