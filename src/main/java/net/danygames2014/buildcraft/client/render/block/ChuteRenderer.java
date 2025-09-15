package net.danygames2014.buildcraft.client.render.block;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.texture.TextureManager;
import org.lwjgl.opengl.GL11;

public class ChuteRenderer {

    private final ModelPart top;
    private final ModelFrustum middle;
    private final ModelPart bottom;

    public ChuteRenderer(){
        top = new ModelPart(0, 0);
        top.addCuboid(-8F, 1F, -8F, 16, 7, 16);
        top.pivotX = 8F;
        top.pivotY = 8F;
        top.pivotZ = 8F;

        middle = new ModelFrustum(top, 32, 0, 0, 3, 0, 8, 8, 16, 16, 7, 1F / 16F);

        bottom = new ModelPart(0, 23);
        bottom.addCuboid(-3F, -8F, -3F, 6, 3, 6);
        bottom.pivotX = 8F;
        bottom.pivotY = 8F;
        bottom.pivotZ = 8F;
    }

    public void render(TextureManager textureManager, double x, double y, double z){
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glTranslated(x, y, z);
        textureManager.bindTexture(textureManager.getTextureId("/assets/buildcraft/stationapi/textures/block/chute_top.png"));
        top.render((float) (1.0 / 16.0));
        bottom.render((float) (1.0 / 16.0));
        textureManager.bindTexture(textureManager.getTextureId("/assets/buildcraft/stationapi/textures/block/chute_side.png"));
        middle.render(Tessellator.INSTANCE, 1F / 16F);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
