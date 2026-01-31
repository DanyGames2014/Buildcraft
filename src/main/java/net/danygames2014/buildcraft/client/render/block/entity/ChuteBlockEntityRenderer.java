package net.danygames2014.buildcraft.client.render.block.entity;

import net.danygames2014.buildcraft.client.render.block.ChuteRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;

public class ChuteBlockEntityRenderer extends BlockEntityRenderer {
    private final ChuteRenderer chuteRenderer;

    public ChuteBlockEntityRenderer(String topTexture, String sideTexture){
        chuteRenderer = new ChuteRenderer(topTexture, sideTexture);
    }

    @Override
    public void render(BlockEntity blockEntity, double x, double y, double z, float tickDelta) {
        chuteRenderer.render(Minecraft.INSTANCE.textureManager, x, y, z);
    }
}
