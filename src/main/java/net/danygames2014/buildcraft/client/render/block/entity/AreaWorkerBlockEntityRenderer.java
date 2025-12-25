package net.danygames2014.buildcraft.client.render.block.entity;

import net.danygames2014.buildcraft.block.entity.AreaWorkerBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.LaserData;
import net.danygames2014.buildcraft.client.render.LaserRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import org.lwjgl.opengl.GL11;

public class AreaWorkerBlockEntityRenderer extends BlockEntityRenderer {
    @Override
    public void render(BlockEntity blockEntity, double x, double y, double z, float tickDelta) {
        if (blockEntity instanceof AreaWorkerBlockEntity areaWorker) {
            if (areaWorker.workingArea == null || !areaWorker.renderWorkingArea()) {
                return;
            }

            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5D, y + 0.5D, z + 0.5D);
            GL11.glTranslated(-areaWorker.x, -areaWorker.y, -areaWorker.z);

            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_LIGHTING);
            
            float luminance = blockEntity.world.dimension.lightLevelToLuminance[blockEntity.world.getLightLevel(areaWorker.workingArea.minX, areaWorker.workingArea.minY, areaWorker.workingArea.minZ)];
            GL11.glColor3f(luminance, luminance, luminance);
            
            for (LaserData laser : areaWorker.workingArea.lasers) {
                LaserRenderer.doRenderLaser(Minecraft.INSTANCE.textureManager, laser, LaserRenderer.LASER_TEXTURES[4]);
            }
            
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();

            GL11.glPopMatrix();
        }
    }
}
