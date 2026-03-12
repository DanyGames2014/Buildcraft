package net.danygames2014.buildcraft.client.render.block.entity;

import net.danygames2014.buildcraft.Buildcraft;
import net.danygames2014.buildcraft.block.entity.TankBlockEntity;
import net.danygames2014.buildcraft.client.render.FluidRenderer;
import net.danygames2014.buildcraft.util.RenderHelper;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import org.lwjgl.opengl.GL11;

public class TankBlockEntityRenderer extends BlockEntityRenderer {

    @Override
    public void render(BlockEntity blockEntity, double x, double y, double z, float tickDelta) {
        if(!(blockEntity instanceof TankBlockEntity tankBlockEntity)){
            return;
        }
        if(tankBlockEntity.fluid == null || tankBlockEntity.fluid.amount <= 0){
            return;
        }

        int[] displayList = FluidRenderer.getFluidDisplayLists(tankBlockEntity.fluid, blockEntity.world, false);
        if (displayList == null) {
            return;
        }


        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture();

        float brightness = blockEntity.world.dimension.lightLevelToLuminance[blockEntity.world.getLightLevel(blockEntity.x, blockEntity.y, blockEntity.z)];
        int colorMultiplier = tankBlockEntity.fluid.fluid.getColorMultiplier(blockEntity.world, blockEntity.x, blockEntity.y, blockEntity.z);

        float r = ((colorMultiplier >> 16) & 0xFF) / 255.0f;
        float g = ((colorMultiplier >> 8) & 0xFF) / 255.0f;
        float b = (colorMultiplier & 0xFF) / 255.0f;

        r *= brightness;
        g *= brightness;
        b *= brightness;

        GL11.glColor3f(r, g, b);

        GL11.glTranslatef((float) x + 0.125F, (float) y + 0.5F, (float) z + 0.125F);
        GL11.glScalef(0.75F, 0.999F, 0.75F);
        GL11.glTranslatef(0, -0.5F, 0);

        GL11.glCallList(displayList[(int) ((float) tankBlockEntity.fluid.amount / (float) (TankBlockEntity.CAPACITY) * (FluidRenderer.DISPLAY_STAGES - 1))]);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
