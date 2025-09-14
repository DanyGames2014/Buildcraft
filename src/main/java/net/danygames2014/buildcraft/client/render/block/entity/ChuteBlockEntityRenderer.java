package net.danygames2014.buildcraft.client.render.block.entity;

import net.danygames2014.buildcraft.client.render.block.ChuteModel;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;

public class ChuteBlockEntityRenderer extends BlockEntityRenderer {
    private final ChuteModel chuteModel;

    public ChuteBlockEntityRenderer(){
        chuteModel = new ChuteModel();
    }

    @Override
    public void render(BlockEntity blockEntity, double x, double y, double z, float tickDelta) {
        chuteModel.render(Minecraft.INSTANCE.textureManager, x, y, z);
    }
}
