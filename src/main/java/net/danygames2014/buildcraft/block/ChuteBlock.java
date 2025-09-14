package net.danygames2014.buildcraft.block;

import net.danygames2014.buildcraft.block.entity.ChuteBlockEntity;
import net.danygames2014.buildcraft.client.render.block.ChuteModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.world.BlockView;
import net.modificationstation.stationapi.api.client.model.block.BlockWithInventoryRenderer;
import net.modificationstation.stationapi.api.client.model.block.BlockWithWorldRenderer;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import org.lwjgl.opengl.GL11;

public class ChuteBlock extends TemplateBlockWithEntity implements BlockWithWorldRenderer, BlockWithInventoryRenderer {
    private final ChuteModel chuteModel;

    public ChuteBlock(Identifier identifier) {
        super(identifier, Material.METAL);
        chuteModel = new ChuteModel();
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

    @Override
    public void renderInventory(BlockRenderManager blockRenderManager, int i) {
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        chuteModel.render(Minecraft.INSTANCE.textureManager, 0, 0, 0);
    }

    @Override
    public boolean renderWorld(BlockRenderManager blockRenderManager, BlockView blockView, int i, int i1, int i2) {
        return true;
    }
}
