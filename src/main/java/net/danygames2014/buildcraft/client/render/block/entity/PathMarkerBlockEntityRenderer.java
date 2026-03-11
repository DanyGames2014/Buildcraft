package net.danygames2014.buildcraft.client.render.block.entity;

import net.danygames2014.buildcraft.block.entity.PathMarkerBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.LaserData;
import net.danygames2014.buildcraft.client.render.LaserRenderer;
import net.danygames2014.buildcraft.util.Constants;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import org.lwjgl.opengl.GL11;

public class PathMarkerBlockEntityRenderer extends BlockEntityRenderer {
    @Override
    public void render(BlockEntity blockEntity, double x, double y, double z, float tickDelta) {
        if(!(blockEntity instanceof PathMarkerBlockEntity marker)){
            return;
        }
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glTranslated(x, y, z);
        GL11.glTranslated(-marker.x, -marker.y, -marker.z);

        for (LaserData laser : marker.lasers) {
            if (laser != null) {
                GL11.glPushMatrix();
                LaserRenderer
                        .doRenderLaser(
                                Minecraft.INSTANCE.textureManager,
                                laser, Constants.LASER_TEXTURES[3]);
                GL11.glPopMatrix();
            }
        }

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
