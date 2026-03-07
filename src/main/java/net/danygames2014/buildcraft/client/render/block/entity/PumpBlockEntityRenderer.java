package net.danygames2014.buildcraft.client.render.block.entity;

import net.danygames2014.buildcraft.block.entity.PumpBlockEntity;
import net.danygames2014.buildcraft.client.render.entity.EntityBlockRenderer;
import net.danygames2014.nyalib.util.PlayerUtil;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.modificationstation.stationapi.api.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class PumpBlockEntityRenderer extends BlockEntityRenderer {
    @Override
    public void render(BlockEntity blockEntity, double x, double y, double z, float tickDelta) {
        if(blockEntity instanceof PumpBlockEntity pump){
            if(pump.tube != null){
                Vec3d playerPos = PlayerUtil.getRenderPosition(Minecraft.INSTANCE.player, tickDelta);
                float light = blockEntity.world.dimension.lightLevelToLuminance[blockEntity.world.getLightLevel(blockEntity.x, blockEntity.y - 1, blockEntity.z)];
                GL11.glColor3f(light, light, light);
                EntityBlockRenderer.INSTANCE.renderBlock(pump.tube, pump.tube.x - playerPos.x, pump.tube.y - playerPos.y, pump.tube.z - playerPos.z);
            }
        }
    }
}
