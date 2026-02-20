package net.danygames2014.buildcraft.client.render.block.entity;

import net.danygames2014.buildcraft.block.entity.RefineryBlockEntity;
import net.danygames2014.buildcraft.client.render.FluidRenderer;
import net.danygames2014.buildcraft.util.RenderHelper;
import net.danygames2014.nyalib.fluid.FluidStack;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.modificationstation.stationapi.api.client.StationRenderAPI;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import org.lwjgl.opengl.GL11;

public class RefineryBlockEntityRenderer extends BlockEntityRenderer {

    public static RefineryBlockEntityRenderer INSTANCE = new RefineryBlockEntityRenderer();

    private static final String TEXTURE = "/assets/buildcraft/stationapi/textures/block/refinery_magnet.png";
    private static final float pixel = (float) (1.0 / 16.0);
    private final ModelPart tank;
    private final ModelPart[] magnet = new ModelPart[4];

    public RefineryBlockEntityRenderer() {
        tank = new ModelPart(0, 0);
        tank.addCuboid(-4F, -8F, -4F, 8, 16, 8);
        tank.pivotX = 8;
        tank.pivotY = 8;
        tank.pivotZ = 8;

        for (int i = 0; i < 4; ++i) {
            magnet[i] = new ModelPart(32, i * 8);
            magnet[i].addCuboid(0, -8F, -8F, 8, 4, 4);
            magnet[i].pivotX = 8;
            magnet[i].pivotY = 8;
            magnet[i].pivotZ = 8;
        }

    }

    @Override
    public void render(BlockEntity blockEntity, double x, double y, double z, float tickDelta) {
        FluidStack fluid1 = null, fluid2 = null, fluidResult = null;
        int color1 = 0xFFFFFF, color2 = 0xFFFFFF, colorResult = 0xFFFFFF;

        float anim;
        int angle;
        ModelPart theMagnet;
        if(blockEntity instanceof RefineryBlockEntity refineryBlockEntity){
            if(refineryBlockEntity.getFluid(0, null) != null){
                fluid1 = refineryBlockEntity.getFluid(0, null);
                color1 = fluid1.fluid.getColorMultiplier(blockEntity.world, blockEntity.x, blockEntity.y, blockEntity.z);
            }

            if(refineryBlockEntity.getFluid(1, null) != null){
                fluid2 = refineryBlockEntity.getFluid(1, null);
                color2 = fluid2.fluid.getColorMultiplier(blockEntity.world, blockEntity.x, blockEntity.y, blockEntity.z);
            }

            if(refineryBlockEntity.getFluid(2, null) != null){
                fluidResult = refineryBlockEntity.getFluid(2, null);
                colorResult = fluidResult.fluid.getColorMultiplier(blockEntity.world, blockEntity.x, blockEntity.y, blockEntity.z);
            }

            anim = refineryBlockEntity.getAnimationStage();

            angle = switch(refineryBlockEntity.facing){
                case NORTH -> 180;
                case EAST -> 90;
                case WEST -> 270;
                default -> 0;
            };

            if (refineryBlockEntity.animationSpeed <= 1) {
                theMagnet = magnet[0];
            } else if (refineryBlockEntity.animationSpeed <= 2.5) {
                theMagnet = magnet[1];
            } else if (refineryBlockEntity.animationSpeed <= 4.5) {
                theMagnet = magnet[2];
            } else {
                theMagnet = magnet[3];
            }

            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);

            GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
            GL11.glScalef(0.99F, 0.99F, 0.99F);

            GL11.glRotatef(angle, 0, 1, 0);

            bindTexture(TEXTURE);

//            GL11.glPushMatrix();
//            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
//            GL11.glTranslatef(-4F * pixel, 0, -4F * pixel);
//            tank.render(pixel);
//            GL11.glTranslatef(4F * pixel, 0, 4F * pixel);
//
//            GL11.glTranslatef(-4F * pixel, 0, 4F * pixel);
//            tank.render(pixel);
//            GL11.glTranslatef(4F * pixel, 0, -4F * pixel);
//
//            GL11.glTranslatef(4F * pixel, 0, 0);
//            tank.render(pixel);
//            GL11.glTranslatef(-4F * pixel, 0, 0);
//            GL11.glPopMatrix();

            float trans1, trans2;

            if (anim <= 100) {
                trans1 = 12F * pixel * anim / 100F;
                trans2 = 0;
            } else if (anim <= 200) {
                trans1 = 12F * pixel - (12F * pixel * (anim - 100F) / 100F);
                trans2 = 12F * pixel * (anim - 100F) / 100F;
            } else {
                trans1 = 12F * pixel * (anim - 200F) / 100F;
                trans2 = 12F * pixel - (12F * pixel * (anim - 200F) / 100F);
            }

            GL11.glPushMatrix();
            GL11.glScalef(0.99F, 0.99F, 0.99F);
            GL11.glTranslatef(-0.51F, trans1 - 0.5F, -0.5F);
            theMagnet.render(pixel);
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glScalef(0.99F, 0.99F, 0.99F);
            GL11.glTranslatef(-0.51F, trans2 - 0.5F, 12F * pixel - 0.5F);
            theMagnet.render(pixel);
            GL11.glPopMatrix();

            // fluid rendering

            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            GL11.glScalef(0.5F, 1, 0.5F);

            StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture();

            if (fluid1 != null && fluid1.amount > 0) {
                int[] list1 = FluidRenderer.getFluidDisplayLists(fluid1, blockEntity.world, false);

                if (list1 != null) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(0, 0, 1);
                    RenderHelper.setGLColorFromInt(color1);
                    GL11.glCallList(list1[getDisplayListIndex(refineryBlockEntity, 0)]);
                    GL11.glPopMatrix();
                }
            }

            if (fluid2 != null && fluid2.amount > 0) {
                int[] list2 = FluidRenderer.getFluidDisplayLists(fluid2, blockEntity.world, false);

                if (list2 != null) {
                    RenderHelper.setGLColorFromInt(color2);
                    GL11.glCallList(list2[getDisplayListIndex(refineryBlockEntity, 1)]);
                }
            }


            if (fluidResult != null && fluidResult.amount > 0) {
                int[] list3 = FluidRenderer.getFluidDisplayLists(fluidResult, blockEntity.world, false);

                if (list3 != null) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(1, 0, 0.5F);
                    RenderHelper.setGLColorFromInt(colorResult);
                    GL11.glCallList(list3[getDisplayListIndex(refineryBlockEntity, 2)]);
                    GL11.glPopMatrix();
                }
            }
            GL11.glPopAttrib();

            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    private int getDisplayListIndex(RefineryBlockEntity refineryBlockEntity, int slot) {
        return Math.min((int) ((float) refineryBlockEntity.getFluid(slot, null).amount / (float) refineryBlockEntity.getFluidCapacity(slot, null) * (FluidRenderer.DISPLAY_STAGES - 1)), FluidRenderer.DISPLAY_STAGES - 1);
    }
}
