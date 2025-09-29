package net.danygames2014.buildcraft.client.render.block.entity;

import net.danygames2014.buildcraft.block.entity.LaserBlockEntity;
import net.danygames2014.buildcraft.client.render.LaserRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import org.lwjgl.opengl.GL11;

public class LaserBlockEntityRenderer extends BlockEntityRenderer {
    public static LaserBlockEntityRenderer INSTANCE = new LaserBlockEntityRenderer();
    @Override
    public void render(BlockEntity blockEntity, double x, double y, double z, float tickDelta) {
        if(blockEntity instanceof LaserBlockEntity laser){
            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);
            GL11.glTranslated(-laser.x, -laser.y, -laser.z);

            GL11.glPushMatrix();
            LaserRenderer.doRenderLaser(Minecraft.INSTANCE.textureManager, laser.laser, laser.getTexture());
            GL11.glPopMatrix();

            GL11.glPopMatrix();
        }
    }
}
