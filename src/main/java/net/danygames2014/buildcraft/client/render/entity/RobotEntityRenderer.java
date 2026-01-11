package net.danygames2014.buildcraft.client.render.entity;

import net.danygames2014.buildcraft.client.render.LaserRenderer;
import net.danygames2014.buildcraft.entity.RobotEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class RobotEntityRenderer extends EntityRenderer {
    public static RobotEntityRenderer INSTANCE = new RobotEntityRenderer();
    private ModelPart box;

    public RobotEntityRenderer(){
        box = new ModelPart(0, 0);
        box.addCuboid(-4F, -4F, -4F, 8, 8, 8);
        box.pivotX = 0;
        box.pivotY = 0;
        box.pivotZ = 0;
    }

    @Override
    public void render(Entity entity, double x, double y, double z, float yaw, float pitch) {
        if(!(entity instanceof RobotEntity robotEntity)){
            return;
        }

        float yOffset = MathHelper.sin(((float)robotEntity.age) / 10.0F) * 0.1F + 0.1F;

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glDisable(GL11.GL_LIGHTING);

        bindTexture("/assets/buildcraft/stationapi/textures/entity/robot.png");

        float factor = (float) (1.0 / 16.0);

        GL11.glPushMatrix();
        GL11.glTranslatef(0, yOffset, 0);
        box.render(factor);
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_LIGHTING);

        robotEntity.laserData.head.x = 0;
        robotEntity.laserData.head.y = yOffset;
        robotEntity.laserData.head.z = 0;

        robotEntity.laserData.tail.x = robotEntity.getLaserTargetX() + 0.5D - robotEntity.x;
        robotEntity.laserData.tail.y = robotEntity.getLaserTargetY() + 0.5D - robotEntity.y;
        robotEntity.laserData.tail.z = robotEntity.getLaserTargetZ() + 0.5D - robotEntity.z;

        LaserRenderer.doRenderLaser(Minecraft.INSTANCE.textureManager, robotEntity.laserData, robotEntity.getLaserTexture());

        GL11.glPopMatrix();
    }
}
